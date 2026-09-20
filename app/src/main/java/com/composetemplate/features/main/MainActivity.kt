package com.composetemplate.features.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.composetemplate.core.data.storage.OnboardingStore
import com.composetemplate.core.data.storage.ThemePreferenceStore
import com.composetemplate.core.ui.AndroidTemplateApp
import com.composetemplate.core.ui.AppBackground
import com.composetemplate.core.ui.AppTheme
import com.composetemplate.core.ui.rememberAppState
import com.composetemplate.features.onboarding.OnboardingScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var themePreferenceStore: ThemePreferenceStore
    @Inject lateinit var onboardingStore: OnboardingStore

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { false }
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val onboardingCompleted by onboardingStore.completedFlow.collectAsState(initial = null)
            val darkModePref by themePreferenceStore.darkModeFlow.collectAsState(initial = null)
            val systemDark = isSystemInDarkTheme()
            val isDark = darkModePref ?: systemDark

            val scope = rememberCoroutineScope()
            val systemUiController = rememberSystemUiController()

            DisposableEffect(systemUiController, isDark) {
                systemUiController.systemBarsDarkContentEnabled = !isDark
                onDispose {}
            }

            AppTheme(useDarkTheme = isDark) {
                AppBackground {
                    when (onboardingCompleted) {
                        null -> {
                            // Loading state — splash tetap tampil
                        }
                        false -> {
                            OnboardingScreen(
                                onFinish = {
                                    scope.launch { onboardingStore.markCompleted() }
                                }
                            )
                        }
                        else -> {
                            AndroidTemplateApp(
                                appState = rememberAppState(
                                    windowSizeClass = calculateWindowSizeClass(this)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
