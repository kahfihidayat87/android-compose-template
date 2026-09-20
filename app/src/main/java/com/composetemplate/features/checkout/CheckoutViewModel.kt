package com.composetemplate.features.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composetemplate.core.data.network.ShippingItemRequest
import com.composetemplate.core.data.network.dtos.CourierRequest
import com.composetemplate.core.data.network.dtos.OrderCreateRequest
import com.composetemplate.core.data.network.dtos.OrderItemRequest
import com.composetemplate.core.data.network.dtos.PaymentMethodDto
import com.composetemplate.core.data.repositories.CartRepository
import com.composetemplate.core.data.repositories.OrderRepository
import com.composetemplate.core.data.repositories.PaymentRepository
import com.composetemplate.core.data.repositories.ShippingRepository
import com.composetemplate.core.domain.model.ShippingRate
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
) : ViewModel() {

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

    // Order yang sedang diproses (dibuat setelah rates dipilih)
    private var currentOrderId: Int? = null

    val items = cartRepository.items
    val totalItems: Int get() = cartRepository.totalItems
    val totalPrice: Int get() = cartRepository.totalPrice

    fun onName(v: String) { _form.value = _form.value.copy(name = v) }
    fun onPhone(v: String) { _form.value = _form.value.copy(phone = v) }
    fun onAddress(v: String) { _form.value = _form.value.copy(address = v) }
    fun onPostalCode(v: String) { _form.value = _form.value.copy(postalCode = v.filter { it.isDigit() }.take(5)) }

    fun clearError() { _error.value = null }
    fun consumePaymentUrl() { _paymentUrl.value = null }

    fun isAddressValid(): Boolean {
        val f = _form.value
        return f.name.isNotBlank() &&
               f.phone.isNotBlank() &&
               f.address.isNotBlank() &&
               f.postalCode.length == 5
    }

    fun currentStep(): Step {
        return when {
            _rates.value.isEmpty() -> Step.ADDRESS
            _paymentMethods.value.isEmpty() -> Step.RATES
            else -> Step.PAYMENT
        }
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
                if (rates.isEmpty()) _error.value = "Tidak ada layanan kurir tersedia untuk kode pos ini."
            } catch (e: Exception) {
                _error.value = "Gagal cek ongkir: ${e.message ?: "coba lagi"}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun selectRate(rate: ShippingRate) {
        _selectedRate.value = rate
    }

    fun selectMethod(code: String) {
        _selectedMethod.value = code
    }

    /**
     * Dipanggil saat user tap tombol utama.
     * Otomatis pilih aksi berdasarkan step saat ini.
     */
    fun onPrimaryAction() {
        when (currentStep()) {
            Step.ADDRESS -> checkRates()
            Step.RATES -> {
                if (_selectedRate.value == null) {
                    _error.value = "Pilih kurir pengiriman dulu."
                } else {
                    submitOrderAndFetchMethods()
                }
            }
            Step.PAYMENT -> {
                if (_selectedMethod.value.isNullOrEmpty()) {
                    _error.value = "Pilih metode pembayaran dulu."
                } else {
                    createPayment()
                }
            }
        }
    }

    private fun submitOrderAndFetchMethods() {
        val f = _form.value
        val cartItems = cartRepository.items.value
        val courier = _selectedRate.value ?: return

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
                        courier = CourierRequest(
                            courierCode = courier.courierCode,
                            serviceCode = courier.serviceCode,
                        ),
                    )
                )
                currentOrderId = order.id

                val methods = paymentRepository.getMethods(order.id)
                if (methods.isEmpty()) {
                    _error.value = "Tidak ada metode pembayaran tersedia. Hubungi admin."
                } else {
                    _paymentMethods.value = methods
                    _selectedMethod.value = methods.first().code
                }
            } catch (e: Exception) {
                _error.value = "Gagal buat pesanan: ${e.message}"
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
                _error.value = "Gagal buat pembayaran: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getPrimaryButtonLabel(): String {
        return when (currentStep()) {
            Step.ADDRESS -> "Cek Ongkir"
            Step.RATES -> if (_selectedRate.value == null) "Pilih Kurir Dulu" else "Lanjut Bayar"
            Step.PAYMENT -> "Bayar Sekarang"
        }
    }

    fun isPrimaryButtonEnabled(): Boolean {
        return when (currentStep()) {
            Step.ADDRESS -> isAddressValid()
            Step.RATES -> _selectedRate.value != null
            Step.PAYMENT -> !_selectedMethod.value.isNullOrEmpty()
        }
    }
}
