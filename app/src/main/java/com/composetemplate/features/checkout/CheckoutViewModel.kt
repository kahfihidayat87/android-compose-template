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

    val items = cartRepository.items
    val totalItems = cartRepository.totalItems
    val totalPrice = cartRepository.totalPrice

    fun onName(v: String) { _form.value = _form.value.copy(name = v) }
    fun onPhone(v: String) { _form.value = _form.value.copy(phone = v) }
    fun onAddress(v: String) { _form.value = _form.value.copy(address = v) }
    fun onPostalCode(v: String) { _form.value = _form.value.copy(postalCode = v.filter { it.isDigit() }.take(5)) }

    fun clearError() { _error.value = null }
    fun consumePaymentUrl() { _paymentUrl.value = null }

    fun checkRates() {
        val f = _form.value
        if (f.address.isBlank() || f.phone.isBlank() || f.postalCode.length != 5) {
            _error.value = "Lengkapi alamat, telepon, dan kode pos (5 digit)."
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
                if (rates.isEmpty()) _error.value = "Tidak ada layanan kurir tersedia."
            } catch (e: Exception) {
                _error.value = "Gagal cek ongkir: ${e.message}"
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

    fun createOrderAndPayment() {
        val f = _form.value
        val cartItems = cartRepository.items.value
        val courier = _selectedRate.value
        val method = _selectedMethod.value

        if (f.name.isBlank() || f.phone.isBlank() || f.address.isBlank() || f.postalCode.length != 5) {
            _error.value = "Lengkapi semua data alamat."
            return
        }
        if (courier == null) {
            _error.value = "Pilih kurir pengiriman dulu."
            return
        }
        if (cartItems.isEmpty()) {
            _error.value = "Keranjang kosong."
            return
        }

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

                if (method.isNullOrEmpty()) {
                    val methods = paymentRepository.getMethods(order.id)
                    _paymentMethods.value = methods
                    _error.value = "Pilih metode pembayaran lalu tap Bayar."
                    if (methods.isNotEmpty()) _selectedMethod.value = methods.first().code
                    _loading.value = false
                    return@launch
                }

                val url = paymentRepository.createPayment(order.id, method)
                _paymentUrl.value = url
                cartRepository.clear()
            } catch (e: Exception) {
                _error.value = "Gagal buat pesanan: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}
