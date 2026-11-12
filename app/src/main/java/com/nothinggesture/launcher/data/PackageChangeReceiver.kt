package com.nothinggesture.launcher.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nothinggesture.launcher.LauncherApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PackageChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val app = context.applicationContext as? LauncherApplication ?: return
        CoroutineScope(Dispatchers.IO).launch {
            app.appRepository.loadApps()
        }
    }
}
