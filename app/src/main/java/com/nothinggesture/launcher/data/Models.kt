package com.nothinggesture.launcher.data

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Immutable

@Immutable
data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val icon: Drawable?,
    val lastUsed: Long = 0L,
    val useCount: Int = 0
) {
    val uniqueId: String get() = "$packageName/$activityName"
}

@Immutable
data class LauncherSettings(
    val accentColor: Long = 0xFFFF0022,
    val iconSizeDp: Int = 56,
    val gridColumns: Int = 4,
    val clockStyle: ClockStyle = ClockStyle.LARGE_DIGITAL,
    val clockSizeScale: Float = 1.0f,
    val showDate: Boolean = true,
    val showWeatherPlaceholder: Boolean = true,
    val animationSpeed: Float = 1.0f,
    val doubleTapAction: DoubleTapAction = DoubleTapAction.NONE,
    val searchShowRecent: Boolean = true,
    val searchShowFrequent: Boolean = true,
    val iconShape: IconShape = IconShape.ROUNDED_SQUARE,
    val homePages: Int = 2,
    val wallpaperUri: String? = null
)

enum class ClockStyle {
    LARGE_DIGITAL,
    MINIMAL,
    DOT_MATRIX
}

enum class DoubleTapAction {
    NONE,
    LOCK_SCREEN,
    OPEN_SEARCH,
    OPEN_SETTINGS
}

enum class IconShape {
    CIRCLE,
    SQUIRCLE,
    ROUNDED_SQUARE,
    SQUARE
}
