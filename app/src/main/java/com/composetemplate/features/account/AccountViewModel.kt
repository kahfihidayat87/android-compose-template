package com.composetemplate.features.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composetemplate.core.data.repositories.UserRepository
import com.composetemplate.core.data.storage.UserPreferenceStore
import com.composetemplate.core.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferenceStore: UserPreferenceStore,
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut: StateFlow<Boolean> = _loggedOut.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            try {
                val users = userPreferenceStore.getAll()
                _user.value = users.lastOrNull()
            } catch (e: Throwable) {
                _user.value = null
            }
        }
    }

    fun refresh() = loadUser()

    fun logout() {
        viewModelScope.launch {
            try {
                userPreferenceStore.clear()
                userRepository.logout()
            } catch (e: Throwable) { }
            _loggedOut.value = true
        }
    }
}
