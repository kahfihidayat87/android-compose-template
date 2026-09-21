package com.composetemplate.features.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composetemplate.core.data.network.ShippingItemRequest
import com.composetemplate.core.data.network.dtos.CourierRequest
import com.composetemplate.core.data.network.dtos.OrderCreateRequest
import com.composetemplate.core.data.network.dtos.OrderItemRequest
import com.composetemplate.core.data.network.dtos.PaymentMethodDto
import com.composetemplate.core.data.repositories.CartRepository
import com.composetemplate.core.data.repositories.CouponRepository
import com.composetemplate.core.data.repositories.OrderRepository
import com.composetemplate.core.data.repositories.PantiRepository
import com.composetemplate.core.data.repositories.PaymentRepository
import com.composetemplate.core.data.repositories.ShippingRepository
import com.composetemplate.core.domain.model.Panti
import com.composetemplate.core.domain.model.ShippingRate
import com.composetemplate.core.util.ErrorExtractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val shippingRepository: ShippingRepository,
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val couponRepository: CouponRepository,
    private val pantiRepository: PantiRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "CheckoutVM"
    }

    enum class Step { ADDRESS, RATES, PAYMENT }

    data class AddressForm(
        val name: String = "",
        val phone: String = "",
        val address: String = "",
        val postalCode: String = "",
    )

    private val _form = MutableStateFlow(AddressForm())
    val form: StateFlow<AddressForm> = _form.asStateFlow()

    private val _rates = MutableStateFlow<List<ShippingRate>>(emptyList())
    val rates: StateFlow<List<ShippingRate>> = _rates.asStateFlow()

    private val _selectedRate = MutableStateFlow<ShippingRate?>(null)
    val selectedRate: StateFlow<ShippingRate?> = _selectedRate.asStateFlow()

    private val _paymentMethods = MutableStateFlow<List<PaymentMethodDto>>(emptyList())
    val paymentMethods: StateFlow<List<PaymentMethodDto>> = _paymentMethods.asStateFlow()

    private val _selectedMethod = MutableStateFlow<String?>(null)
    val selectedMethod: StateFlow<String?> = _selectedMethod.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _paymentUrl = MutableStateFlow<String?>(null)
    val paymentUrl: StateFlow<String?> = _paymentUrl.asStateFlow()

    private val _couponInput = MutableStateFlow("")
    val couponInput: StateFlow<String> = _couponInput.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<String?>(null)
    val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

    private val _discount = MutableStateFlow(0)
    val discount: StateFlow<Int> = _discount.asStateFlow()

    private val _wakafEnabled = MutableStateFlow(false)
    val wakafEnabled: StateFlow<Boolean> = _wakafEnabled.asStateFlow()

    private val _pantiList = MutableStateFlow<List<Panti>>(emptyList())
    val pantiList: StateFlow<List<Panti>> = _pantiList.asStateFlow()

    private val _selectedPantiId = MutableStateFlow<Int?>(null)
    val selectedPantiId: StateFlow<Int?> = _selectedPantiId.asStateFlow()

    private var currentOrderId: Int? = null

    val items = cartRepository.items

    init {
        loadPantiList()
    }

    fun onName(v: String) { _form.value = _form.value.copy(name = v) }
    fun onPhone(v: String) { _form.value = _form.value.copy(phone = v) }
    fun onAddress(v: String) { _form.value = _form.value.copy(address = v) }
    fun onPostalCode(v: String) { _form.value = _form.value.copy(postalCode = v.filter { it.isDigit() }.take(5)) }
    fun onCouponInput(v: String) { _couponInput.value = v.uppercase() }

    fun onWakafToggled(enabled: Boolean) {
        _wakafEnabled.value = enabled
        if (!enabled) _selectedPantiId.value = null
    }

    fun onPantiSelected(id: Int) {
        _selectedPantiId.value = id
    }

    fun clearError() { _error.value = null }
    fun consumePaymentUrl() { _paymentUrl.value = null }

    fun isAddressValid(): Boolean {
        val f = _form.value
        return f.name.isNotBlank() && f.phone.isNotBlank() &&
               f.address.isNotBlank() && f.postalCode.length == 5
    }

    fun isWakafValid(): Boolean {
        if (!_wakafEnabled.value) return true
        return _selectedPantiId.value != null
    }

    fun currentStep(): Step = when {
        _rates.value.isEmpty() -> Step.ADDRESS
        _paymentMethods.value.isEmpty() -> Step.RATES
        else -> Step.PAYMENT
    }

    private fun loadPantiList() {
        viewModelScope.launch {
            try {
                _pantiList.value = pantiRepository.getPantiList()
            } catch (e: Exception) {
                Log.e(TAG, "Load panti error", e)
            }
        }
    }

    fun applyCoupon() {
        val code = _couponInput.value.trim()
        if (code.isEmpty()) {
            _error.value = "Masukkan kode kupon"
            return
        }
        val subtotal = cartRepository.totalPrice
        if (subtotal <= 0) {
            _error.value = "Keranjang kosong"
            return
        }
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = couponRepository.validate(code, subtotal)
                if (result.valid) {
                    _appliedCoupon.value = result.code
                    _discount.value = result.discount
                } else {
                    _error.value = result.error ?: "Kode tidak valid"
                    _appliedCoupon.value = null
                    _discount.value = 0
                }
            } catch (e: Exception) {
                Log.e(TAG, "Coupon error", e)
                _error.value = "Gagal validasi kupon: ${ErrorExtractor.extract(e)}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun removeCoupon() {
        _couponInput.value = ""
        _appliedCoupon.value = null
        _discount.value = 0
    }

    fun checkRates() {
        val f = _form.value
        if (!isAddressValid()) {
            _error.value = "Lengkapi nama, telepon, alamat, dan kode pos (5 digit)."
            return
        }
        val cartItems = cartRepository.items.value
        if (cartItems.isEmpty()) {
            _error.value = "Keranjang kosong."
            return
        }
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _rates.value = emptyList()
            _selectedRate.value = null
            try {
                val rates = shippingRepository.getRates(
                    destinationPostalCode = f.postalCode,
                    items = cartItems.map { ShippingItemRequest(productId = it.productId, quantity = it.quantity) }
                )
                _rates.value = rates
                if (rates.isEmpty()) _error.value = "Tidak ada layanan kurir untuk kode pos ini."
            } catch (e: Exception) {
                Log.e(TAG, "Rates error", e)
                _error.value = "Gagal cek ongkir: ${ErrorExtractor.extract(e)}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun selectRate(rate: ShippingRate) { _selectedRate.value = rate }
    fun selectMethod(code: String) { _selectedMethod.value = code }

    fun onPrimaryAction() {
        when (currentStep()) {
            Step.ADDRESS -> checkRates()
            Step.RATES -> {
                if (_selectedRate.value == null) {
                    _error.value = "Pilih kurir pengiriman dulu."
                } else if (!isWakafValid()) {
                    _error.value = "Pilih panti asuhan tujuan wakaf dulu."
                } else {
                    submitOrderAndFetchMethods()
                }
            }
            Step.PAYMENT -> {
                if (_selectedMethod.value.isNullOrEmpty()) _error.value = "Pilih metode pembayaran dulu."
                else createPayment()
            }
        }
    }

    private fun submitOrderAndFetchMethods() {
        val f = _form.value
        val cartItems = cartRepository.items.value
        val courier = _selectedRate.value ?: return

        val wakafTarget = if (_wakafEnabled.value && _selectedPantiId.value != null) {
            val panti = _pantiList.value.find { it.id == _selectedPantiId.value }
            if (panti != null) "Wakaf untuk ${panti.nama} (${panti.alamat})" else null
        } else null

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val order = orderRepository.createOrder(
                    OrderCreateRequest(
                        items = cartItems.map {
                            OrderItemRequest(productId = it.productId, qty = it.quantity, size = it.size)
                        },
                        shippingAddress = "${f.name}\n${f.address}",
                        shippingPhone = f.phone,
                        shippingPostalCode = f.postalCode,
                        courier = CourierRequest(courier.courierCode, courier.serviceCode),
                        couponCode = _appliedCoupon.value,
                        wakafTarget = wakafTarget,
                    )
                )
                currentOrderId = order.id
                val methods = paymentRepository.getMethods(order.id)
                if (methods.isEmpty()) _error.value = "Tidak ada metode pembayaran tersedia. Hubungi admin."
                else {
                    _paymentMethods.value = methods
                    _selectedMethod.value = methods.first().effectiveCode
                }
            } catch (e: Exception) {
                Log.e(TAG, "Create order error", e)
                _error.value = ErrorExtractor.extract(e)
            } finally {
                _loading.value = false
            }
        }
    }

    private fun createPayment() {
        val orderId = currentOrderId
        val method = _selectedMethod.value
        if (orderId == null || method.isNullOrEmpty()) {
            _error.value = "Data pesanan tidak lengkap. Coba ulang dari awal."
            return
        }
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val url = paymentRepository.createPayment(orderId, method)
                _paymentUrl.value = url
                cartRepository.clear()
            } catch (e: Exception) {
                Log.e(TAG, "Create payment error", e)
                _error.value = "Gagal buat pembayaran: ${ErrorExtractor.extract(e)}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getPrimaryButtonLabel(): String = when (currentStep()) {
        Step.ADDRESS -> "Cek Ongkir"
        Step.RATES -> {
            when {
                _selectedRate.value == null -> "Pilih Kurir Dulu"
                !isWakafValid() -> "Pilih Panti Dulu"
                else -> "Lanjut Bayar"
            }
        }
        Step.PAYMENT -> "Bayar Sekarang"
    }

    fun isPrimaryButtonEnabled(): Boolean = when (currentStep()) {
        Step.ADDRESS -> isAddressValid()
        Step.RATES -> _selectedRate.value != null && isWakafValid()
        Step.PAYMENT -> !_selectedMethod.value.isNullOrEmpty()
    }
}
