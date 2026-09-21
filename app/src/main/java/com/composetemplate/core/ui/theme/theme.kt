package com.composetemplate.core.ui

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.composetemplate.core.ui.theme.Blue10
import com.composetemplate.core.ui.theme.Blue20
import com.composetemplate.core.ui.theme.Blue30
import com.composetemplate.core.ui.theme.Blue40
import com.composetemplate.core.ui.theme.Blue80
import com.composetemplate.core.ui.theme.Blue90
import com.composetemplate.core.ui.theme.MuhGold40
import com.composetemplate.core.ui.theme.MuhGold80
import com.composetemplate.core.ui.theme.MuhGold90
import com.composetemplate.core.ui.theme.MuhGold10
import com.composetemplate.core.ui.theme.MuhGray10
import com.composetemplate.core.ui.theme.MuhGray20
import com.composetemplate.core.ui.theme.MuhGray50
import com.composetemplate.core.ui.theme.MuhGray80
import com.composetemplate.core.ui.theme.MuhGray90
import com.composetemplate.core.ui.theme.MuhGray99
import com.composetemplate.core.ui.theme.MuhGreen10
import com.composetemplate.core.ui.theme.MuhGreen20
import com.composetemplate.core.ui.theme.MuhGreen40
import com.composetemplate.core.ui.theme.MuhGreen80
import com.composetemplate.core.ui.theme.MuhGreen90
import com.composetemplate.core.ui.theme.ErrorRed10
import com.composetemplate.core.ui.theme.ErrorRed40
import com.composetemplate.core.ui.theme.ErrorRed80
import com.composetemplate.core.ui.theme.ErrorRed90

/**
 * Light theme — Muhammadiyah Green
 */
@VisibleForTesting
val LightColors = lightColorScheme(
    primary = MuhGreen40,
    onPrimary = Color.White,
    primaryContainer = MuhGreen90,
    onPrimaryContainer = MuhGreen10,

    secondary = MuhGold40,
    onSecondary = Color.White,
    secondaryContainer = MuhGold90,
    onSecondaryContainer = MuhGold10,

    tertiary = MuhGreen80,
    onTertiary = MuhGreen20,
    tertiaryContainer = MuhGreen90,
    onTertiaryContainer = MuhGreen10,

    error = ErrorRed40,
    onError = Color.White,
    errorContainer = ErrorRed90,
    onErrorContainer = ErrorRed10,

    background = MuhGray99,
    onBackground = MuhGray10,
    surface = Color.White,
    onSurface = MuhGray10,
    surfaceVariant = MuhGray90,
    onSurfaceVariant = MuhGray20,
    outline = MuhGray50,
)

/**
 * Dark theme — Muhammadiyah Green
 */
@VisibleForTesting
val DarkColors = darkColorScheme(
    primary = MuhGreen80,
    onPrimary = MuhGreen20,
    primaryContainer = MuhGreen40,
    onPrimaryContainer = MuhGreen90,

    secondary = MuhGold80,
    onSecondary = MuhGold10,
    secondaryContainer = MuhGold40,
    onSecondaryContainer = MuhGold90,

    tertiary = Blue80,
    onTertiary = Blue20,
    tertiaryContainer = Blue30,
    onTertiaryContainer = Blue90,

    error = ErrorRed80,
    onError = ErrorRed10,
    errorContainer = ErrorRed40,
    onErrorContainer = ErrorRed90,

    background = MuhGray10,
    onBackground = MuhGray90,
    surface = MuhGray20,
    onSurface = MuhGray90,
    surfaceVariant = MuhGray20,
    onSurfaceVariant = MuhGray80,
    outline = MuhGray50,
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (!useDarkTheme) LightColors else DarkColors
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
