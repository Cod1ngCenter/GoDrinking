package com.godrinking.app.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Dark Color Scheme ─────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary            = PrimaryRed,
    onPrimary          = ForegroundDark,
    primaryContainer   = PrimaryRedDarker,
    onPrimaryContainer = ForegroundDark,
    secondary          = PrimaryRedDark,
    onSecondary        = ForegroundDark,
    background         = BackgroundDark,
    onBackground       = ForegroundDark,
    surface            = CardDark,
    onSurface          = ForegroundDark,
    surfaceVariant     = MutedDark,
    onSurfaceVariant   = MutedForegroundDark,
    outline            = BorderDark,
    error              = RedDanger,
    onError            = ForegroundDark,
)

// ── Light Color Scheme ────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary            = PrimaryRed,
    onPrimary          = ForegroundDark,
    primaryContainer   = RedDangerBg,
    onPrimaryContainer = PrimaryRedDarker,
    secondary          = PrimaryRedDark,
    onSecondary        = ForegroundDark,
    background         = BackgroundLight,
    onBackground       = ForegroundLight,
    surface            = CardLight,
    onSurface          = ForegroundLight,
    surfaceVariant     = MutedLight,
    onSurfaceVariant   = MutedForegroundLight,
    outline            = BorderLight,
    error              = RedDanger,
    onError            = ForegroundDark,
)

@Composable
fun GoDrinkingTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = GoDrinkingTypography,
        content     = content
    )
}
