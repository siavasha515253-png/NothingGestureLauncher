package com.nothinggesture.launcher

import android.app.Application
import com.nothinggesture.launcher.data.AppRepository
import com.nothinggesture.launcher.data.SettingsRepository

class LauncherApplication : Application() {
    lateinit var appRepository: AppRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        appRepository = AppRepository(this)
        settingsRepository = SettingsRepository(this)
    }
}
