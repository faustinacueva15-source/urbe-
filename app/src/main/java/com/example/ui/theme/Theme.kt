package com.example.ui.theme

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

private val UrbeDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = ObsidianDark,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = EmeraldTrust,
    onSecondary = ObsidianDark,
    secondaryContainer = EmeraldContainer,
    onSecondaryContainer = OnEmeraldContainer,
    tertiary = CobaltBlue,
    background = ObsidianDark,
    onBackground = TextPrimaryDark,
    surface = SlateNavyDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceCardDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderSubtleDark,
    outlineVariant = SurfaceElevatedDark
)

private val UrbeLightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = ObsidianDark,
    primaryContainer = GoldLight,
    onPrimaryContainer = ObsidianDark,
    secondary = EmeraldTrust,
    onSecondary = TextPrimaryDark,
    secondaryContainer = OnEmeraldContainer,
    onSecondaryContainer = EmeraldContainer,
    tertiary = CobaltBlue,
    background = ObsidianDark, // Use dark-mode luxury baseline for premium feel
    onBackground = TextPrimaryDark,
    surface = SlateNavyDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceCardDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderSubtleDark,
    outlineVariant = SurfaceElevatedDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep custom curated luxury palette for branded experience
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) UrbeDarkColorScheme else UrbeDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ObsidianDark.toArgb()
                window.navigationBarColor = ObsidianDark.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
