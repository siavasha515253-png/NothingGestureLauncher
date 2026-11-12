package com.nothinggesture.launcher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "launcher_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val ACCENT = longPreferencesKey("accent_color")
        val ICON_SIZE = intPreferencesKey("icon_size")
        val GRID_COLUMNS = intPreferencesKey("grid_columns")
        val CLOCK_STYLE = stringPreferencesKey("clock_style")
        val CLOCK_SCALE = floatPreferencesKey("clock_scale")
        val SHOW_DATE = booleanPreferencesKey("show_date")
        val SHOW_WEATHER = booleanPreferencesKey("show_weather")
        val ANIM_SPEED = floatPreferencesKey("anim_speed")
        val DOUBLE_TAP = stringPreferencesKey("double_tap")
        val SEARCH_RECENT = booleanPreferencesKey("search_recent")
        val SEARCH_FREQUENT = booleanPreferencesKey("search_frequent")
        val ICON_SHAPE = stringPreferencesKey("icon_shape")
        val HOME_PAGES = intPreferencesKey("home_pages")
        val WALLPAPER = stringPreferencesKey("wallpaper_uri")
    }

    val defaultSettings = LauncherSettings()

    val settings: Flow<LauncherSettings> = context.dataStore.data.map { prefs ->
        LauncherSettings(
            accentColor = prefs[Keys.ACCENT] ?: defaultSettings.accentColor,
            iconSizeDp = prefs[Keys.ICON_SIZE] ?: defaultSettings.iconSizeDp,
            gridColumns = prefs[Keys.GRID_COLUMNS] ?: defaultSettings.gridColumns,
            clockStyle = try {
                ClockStyle.valueOf(prefs[Keys.CLOCK_STYLE] ?: ClockStyle.LARGE_DIGITAL.name)
            } catch (_: Exception) {
                ClockStyle.LARGE_DIGITAL
            },
            clockSizeScale = prefs[Keys.CLOCK_SCALE] ?: defaultSettings.clockSizeScale,
            showDate = prefs[Keys.SHOW_DATE] ?: defaultSettings.showDate,
            showWeatherPlaceholder = prefs[Keys.SHOW_WEATHER] ?: defaultSettings.showWeatherPlaceholder,
            animationSpeed = prefs[Keys.ANIM_SPEED] ?: defaultSettings.animationSpeed,
            doubleTapAction = try {
                DoubleTapAction.valueOf(prefs[Keys.DOUBLE_TAP] ?: DoubleTapAction.NONE.name)
            } catch (_: Exception) {
                DoubleTapAction.NONE
            },
            searchShowRecent = prefs[Keys.SEARCH_RECENT] ?: defaultSettings.searchShowRecent,
            searchShowFrequent = prefs[Keys.SEARCH_FREQUENT] ?: defaultSettings.searchShowFrequent,
            iconShape = try {
                IconShape.valueOf(prefs[Keys.ICON_SHAPE] ?: IconShape.ROUNDED_SQUARE.name)
            } catch (_: Exception) {
                IconShape.ROUNDED_SQUARE
            },
            homePages = prefs[Keys.HOME_PAGES] ?: defaultSettings.homePages,
            wallpaperUri = prefs[Keys.WALLPAPER]
        )
    }

    suspend fun update(transform: (LauncherSettings) -> LauncherSettings) {
        context.dataStore.edit { prefs ->
            val current = LauncherSettings(
                accentColor = prefs[Keys.ACCENT] ?: defaultSettings.accentColor,
                iconSizeDp = prefs[Keys.ICON_SIZE] ?: defaultSettings.iconSizeDp,
                gridColumns = prefs[Keys.GRID_COLUMNS] ?: defaultSettings.gridColumns,
                clockStyle = try {
                    ClockStyle.valueOf(prefs[Keys.CLOCK_STYLE] ?: ClockStyle.LARGE_DIGITAL.name)
                } catch (_: Exception) {
                    ClockStyle.LARGE_DIGITAL
                },
                clockSizeScale = prefs[Keys.CLOCK_SCALE] ?: defaultSettings.clockSizeScale,
                showDate = prefs[Keys.SHOW_DATE] ?: defaultSettings.showDate,
                showWeatherPlaceholder = prefs[Keys.SHOW_WEATHER] ?: defaultSettings.showWeatherPlaceholder,
                animationSpeed = prefs[Keys.ANIM_SPEED] ?: defaultSettings.animationSpeed,
                doubleTapAction = try {
                    DoubleTapAction.valueOf(prefs[Keys.DOUBLE_TAP] ?: DoubleTapAction.NONE.name)
                } catch (_: Exception) {
                    DoubleTapAction.NONE
                },
                searchShowRecent = prefs[Keys.SEARCH_RECENT] ?: defaultSettings.searchShowRecent,
                searchShowFrequent = prefs[Keys.SEARCH_FREQUENT] ?: defaultSettings.searchShowFrequent,
                iconShape = try {
                    IconShape.valueOf(prefs[Keys.ICON_SHAPE] ?: IconShape.ROUNDED_SQUARE.name)
                } catch (_: Exception) {
                    IconShape.ROUNDED_SQUARE
                },
                homePages = prefs[Keys.HOME_PAGES] ?: defaultSettings.homePages,
                wallpaperUri = prefs[Keys.WALLPAPER]
            )
            val updated = transform(current)
            prefs[Keys.ACCENT] = updated.accentColor
            prefs[Keys.ICON_SIZE] = updated.iconSizeDp
            prefs[Keys.GRID_COLUMNS] = updated.gridColumns
            prefs[Keys.CLOCK_STYLE] = updated.clockStyle.name
            prefs[Keys.CLOCK_SCALE] = updated.clockSizeScale
            prefs[Keys.SHOW_DATE] = updated.showDate
            prefs[Keys.SHOW_WEATHER] = updated.showWeatherPlaceholder
            prefs[Keys.ANIM_SPEED] = updated.animationSpeed
            prefs[Keys.DOUBLE_TAP] = updated.doubleTapAction.name
            prefs[Keys.SEARCH_RECENT] = updated.searchShowRecent
            prefs[Keys.SEARCH_FREQUENT] = updated.searchShowFrequent
            prefs[Keys.ICON_SHAPE] = updated.iconShape.name
            prefs[Keys.HOME_PAGES] = updated.homePages
            if (updated.wallpaperUri != null) {
                prefs[Keys.WALLPAPER] = updated.wallpaperUri
            } else {
                prefs.remove(Keys.WALLPAPER)
            }
        }
    }
}
