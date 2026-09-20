package com.composetemplate.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composetemplate.core.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()

    fun register(name: String, email: String, password: String, phone: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank()) {
            _error.value = "Semua field wajib diisi"
            return
        }
        if (password.length < 8) {
            _error.value = "Password minimal 8 karakter"
            return
        }
        if (!email.contains("@")) {
            _error.value = "Format email tidak valid"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                userRepository.register(name, email, password, phone)
                _success.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Registrasi gagal"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() { _error.value = null }
}
