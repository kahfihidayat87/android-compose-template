package com.composetemplate.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composetemplate.core.data.repositories.OnboardingRepository
import com.composetemplate.core.data.storage.OnboardingStore
import com.composetemplate.core.domain.model.OnboardingPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val onboardingStore: OnboardingStore,
) : ViewModel() {

    private val _pages = MutableStateFlow<List<OnboardingPage>>(emptyList())
    val pages: StateFlow<List<OnboardingPage>> = _pages.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        loadPages()
    }

    private fun loadPages() {
        viewModelScope.launch {
            try {
                val fresh = onboardingRepository.getPages()
                if (fresh.isNotEmpty()) {
                    _pages.value = fresh
                    onboardingStore.cachePages(fresh)
                    _loading.value = false
                    return@launch
                }
            } catch (e: Exception) {
                // Lanjut ke cache
            }

            val cached = onboardingStore.getCachedPages()
            if (cached.isNotEmpty()) {
                _pages.value = cached
                _loading.value = false
                return@launch
            }

            _pages.value = DEFAULT_PAGES
            _loading.value = false
        }
    }

    fun markCompleted() {
        viewModelScope.launch {
            try {
                onboardingStore.markCompleted()
            } catch (e: Exception) { }
        }
    }

    companion object {
        private val DEFAULT_PAGES = listOf(
            OnboardingPage(
                id = 1,
                emoji = "👟",
                title = "Sepatu Kanvas Lokal",
                description = "A-DHL — sepatu kanvas breathable buatan warga Muhammadiyah Gunungkidul-Yogyakarta."
            ),
            OnboardingPage(
                id = 2,
                emoji = "✨",
                title = "100% Original",
                description = "Material pilihan, jahitan rapi, sol empuk anti-slip. Kualitas terjamin, harga merakyat."
            ),
            OnboardingPage(
                id = 3,
                emoji = "🤝",
                title = "Dukung Ekonomi Umat",
                description = "Setiap pasang yang kamu beli turut menggerakkan roda ekonomi umat."
            ),
            OnboardingPage(
                id = 4,
                emoji = "🚚",
                title = "Belanja Mudah",
                description = "Pilih model, ukuran, bayar via VA/QRIS, dan lacak pengiriman real-time."
            ),
        )
    }
}
