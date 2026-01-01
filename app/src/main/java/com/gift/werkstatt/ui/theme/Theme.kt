package com.gift.werkstatt.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BauhausBlue,
    onPrimary = BauhausWhite,
    secondary = BauhausGray,
    onSecondary = BauhausWhite,
    background = CanvasBackground,
    onBackground = BauhausBlack,
    surface = BauhausWhite,
    onSurface = BauhausBlack,
    surfaceVariant = BauhausLightGray,
    onSurfaceVariant = BauhausBlack
)

private val DarkColorScheme = darkColorScheme(
    primary = BauhausBlue,
    onPrimary = BauhausWhite,
    secondary = BauhausGray,
    onSecondary = BauhausWhite,
    background = BauhausDarkGray,
    onBackground = BauhausWhite,
    surface = BauhausBlack,
    onSurface = BauhausWhite,
    surfaceVariant = BauhausDarkGray,
    onSurfaceVariant = BauhausLightGray
)

@Composable
fun WerkstattInfiniteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
