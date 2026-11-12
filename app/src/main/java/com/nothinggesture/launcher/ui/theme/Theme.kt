package com.nothinggesture.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class NothingColors(
    val accent: Color = NothingRed,
    val background: Color = NothingBlack,
    val surface: Color = NothingSurface,
    val elevated: Color = NothingElevated,
    val onBackground: Color = NothingWhite,
    val onSurface: Color = NothingWhite,
    val muted: Color = NothingGrey500,
    val border: Color = NothingGrey700
)

val LocalNothingColors = staticCompositionLocalOf { NothingColors() }

@Composable
fun NothingGestureTheme(
    accentColor: Long = 0xFFFF0022,
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val accent = Color(accentColor)
    val nothingColors = NothingColors(accent = accent)

    val colorScheme = darkColorScheme(
        primary = accent,
        onPrimary = NothingWhite,
        secondary = NothingGrey300,
        onSecondary = NothingBlack,
        background = NothingBlack,
        onBackground = NothingWhite,
        surface = NothingSurface,
        onSurface = NothingWhite,
        surfaceVariant = NothingElevated,
        onSurfaceVariant = NothingGrey300,
        outline = NothingGrey700,
        error = accent
    )

    CompositionLocalProvider(LocalNothingColors provides nothingColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NothingTypography,
            content = content
        )
    }
}
