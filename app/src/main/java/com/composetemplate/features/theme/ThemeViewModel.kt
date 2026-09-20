package com.composetemplate.features.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composetemplate.core.data.storage.ThemePreferenceStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val store: ThemePreferenceStore
) : ViewModel() {

    val darkMode: StateFlow<Boolean?> = store.darkModeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun toggle(currentlyDark: Boolean) {
        viewModelScope.launch {
            store.setDarkMode(!currentlyDark)
        }
    }
}
