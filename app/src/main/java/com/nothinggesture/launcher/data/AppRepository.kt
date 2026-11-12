package com.nothinggesture.launcher.data

import android.content.Context
import android.content.pm.LauncherApps
import android.os.Process
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class AppRepository(private val context: Context) {

    private val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val usageMap = ConcurrentHashMap<String, Pair<Long, Int>>()

    suspend fun loadApps() = withContext(Dispatchers.IO) {
        val result = mutableListOf<AppInfo>()
        val profiles = launcherApps.profiles

        for (profile in profiles) {
            val activities = launcherApps.getActivityList(null, profile)
            for (info in activities) {
                val component = info.componentName
                val label = info.label?.toString() ?: component.packageName
                val icon = try {
                    info.getBadgedIcon(0)
                } catch (_: Exception) {
                    null
                }
                val unique = "${component.packageName}/${component.className}"
                val (last, count) = usageMap[unique] ?: (0L to 0)

                result.add(
                    AppInfo(
                        packageName = component.packageName,
                        activityName = component.className,
                        label = label,
                        icon = icon,
                        lastUsed = last,
                        useCount = count
                    )
                )
            }
        }

        result.sortBy { it.label.lowercase() }
        _apps.value = result
    }

    fun launchApp(app: AppInfo) {
        try {
            val component = android.content.ComponentName(app.packageName, app.activityName)
            val user = Process.myUserHandle()
            launcherApps.startMainActivity(component, user, null, null)

            val key = app.uniqueId
            val (last, count) = usageMap[key] ?: (0L to 0)
            usageMap[key] = System.currentTimeMillis() to (count + 1)

            val updated = _apps.value.map {
                if (it.uniqueId == key) it.copy(lastUsed = System.currentTimeMillis(), useCount = count + 1)
                else it
            }
            _apps.value = updated
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getRecent(limit: Int = 8): List<AppInfo> {
        return _apps.value
            .filter { it.lastUsed > 0 }
            .sortedByDescending { it.lastUsed }
            .take(limit)
    }

    fun getFrequent(limit: Int = 8): List<AppInfo> {
        return _apps.value
            .filter { it.useCount > 0 }
            .sortedByDescending { it.useCount }
            .take(limit)
    }

    fun search(query: String): List<AppInfo> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()
        return _apps.value.filter {
            it.label.lowercase().contains(q) || it.packageName.lowercase().contains(q)
        }
    }

    fun onPackageChanged() {}
}
