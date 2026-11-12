package com.nothinggesture.launcher

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.nothinggesture.launcher.ui.home.HomeScreen
import com.nothinggesture.launcher.ui.theme.NothingGestureTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            val app = application as LauncherApplication
            val settings by app.settingsRepository.settings.collectAsState(
                initial = app.settingsRepository.defaultSettings
            )

            NothingGestureTheme(
                accentColor = settings.accentColor,
                darkTheme = true
            ) {
                HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    appRepository = app.appRepository,
                    settingsRepository = app.settingsRepository
                )
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onBackPressed() {
        // Launchers should not finish on back
    }
}
