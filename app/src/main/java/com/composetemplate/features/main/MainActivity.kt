package com.composetemplate.features.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.composetemplate.core.data.storage.OnboardingStore
import com.composetemplate.core.data.storage.ThemePreferenceStore
import com.composetemplate.core.ui.AndroidTemplateApp
import com.composetemplate.core.ui.AppBackground
import com.composetemplate.core.ui.AppTheme
import com.composetemplate.core.ui.rememberAppState
import com.composetemplate.core.util.NotificationHelper
import com.composetemplate.features.notification.OrderPollingViewModel
import com.composetemplate.features.onboarding.OnboardingScreen
import com.composetemplate.features.onboarding.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var themePreferenceStore: ThemePreferenceStore
    @Inject lateinit var onboardingStore: OnboardingStore

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { false }
        WindowCompat.setDecorFitsSystemWindows(window, false)

        try {
            NotificationHelper.ensureChannel(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } catch (e: Throwable) { }

        setContent {
            MainApp(activity = this@MainActivity)
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun MainApp(activity: MainActivity) {
    val onboardingCompleted by activity.onboardingStore.completedFlow
        .collectAsState(initial = null)

    val darkModePref by activity.themePreferenceStore.darkModeFlow
        .collectAsState(initial = null)

    val systemDark = isSystemInDarkTheme()
    val isDark = darkModePref ?: systemDark

    val systemUiController = rememberSystemUiController()

    // Polling notifikasi
    val pollingViewModel: OrderPollingViewModel = hiltViewModel()
    LaunchedEffect(Unit) {
        try {
            pollingViewModel.startPolling()
        } catch (e: Throwable) { }
    }

    DisposableEffect(systemUiController, isDark) {
        systemUiController.systemBarsDarkContentEnabled = !isDark
        onDispose {}
    }

    AppTheme(useDarkTheme = isDark) {
        AppBackground {
            when (onboardingCompleted) {
                null -> { }

                false -> {
                    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
                    val pages by onboardingViewModel.pages.collectAsState()
                    OnboardingScreen(
                        pages = pages,
                        onFinish = {
                            onboardingViewModel.markCompleted()
                        }
                    )
                }

                true -> {
                    AndroidTemplateApp(
                        appState = rememberAppState(
                            windowSizeClass = calculateWindowSizeClass(activity)
                        )
                    )
                }
            }
        }
    }
}
