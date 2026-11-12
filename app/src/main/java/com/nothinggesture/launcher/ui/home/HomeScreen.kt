package com.nothinggesture.launcher.ui.home

import android.content.Context
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nothinggesture.launcher.data.*
import com.nothinggesture.launcher.gestures.GestureAction
import com.nothinggesture.launcher.gestures.launcherGestures
import com.nothinggesture.launcher.ui.components.AppIcon
import com.nothinggesture.launcher.ui.components.NothingClock
import com.nothinggesture.launcher.ui.search.SearchOverlay
import com.nothinggesture.launcher.ui.settings.SettingsScreen
import com.nothinggesture.launcher.ui.theme.LocalNothingColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    appRepository: AppRepository,
    settingsRepository: SettingsRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = LocalNothingColors.current
    val scope = rememberCoroutineScope()

    val settings by settingsRepository.settings.collectAsState(
        initial = settingsRepository.defaultSettings
    )
    val apps by appRepository.apps.collectAsState()

    var showSearch by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        appRepository.loadApps()
    }

    val pagerState = rememberPagerState(pageCount = { settings.homePages })

    val dockApps = remember(apps) {
        appRepository.getFrequent(4).ifEmpty { apps.take(4) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .launcherGestures(
                enabled = !showSearch && !showSettings,
                onGesture = { action ->
                    when (action) {
                        GestureAction.SWIPE_UP -> showSearch = true
                        GestureAction.SWIPE_DOWN -> expandNotificationShade(context)
                        GestureAction.SWIPE_LEFT -> {
                            scope.launch {
                                if (pagerState.currentPage < settings.homePages - 1) {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        }
                        GestureAction.SWIPE_RIGHT -> {
                            scope.launch {
                                if (pagerState.currentPage > 0) {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        }
                        GestureAction.DOUBLE_TAP -> {
                            when (settings.doubleTapAction) {
                                DoubleTapAction.OPEN_SEARCH -> showSearch = true
                                DoubleTapAction.OPEN_SETTINGS -> showSettings = true
                                DoubleTapAction.LOCK_SCREEN -> {}
                                DoubleTapAction.NONE -> {}
                            }
                        }
                        GestureAction.LONG_PRESS -> editMode = !editMode
                        GestureAction.PINCH -> showSettings = true
                    }
                }
            )
    ) {
        Box(Modifier.fillMaxSize().background(colors.background))

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 32.dp)
        ) {
            Box(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                NothingClock(
                    style = settings.clockStyle,
                    scale = settings.clockSizeScale,
                    showDate = settings.showDate
                )
            }

            Spacer(Modifier.height(12.dp))

            if (settings.showWeatherPlaceholder) {
                Text(
                    text = "—  °  ·  Clear",
                    color = colors.muted,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(Modifier.height(24.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) { page ->
                val pageApps = if (page == 0) {
                    apps.filter { it !in dockApps }.take(settings.gridColumns * 3)
                } else {
                    apps.drop(settings.gridColumns * 3 + 4).take(settings.gridColumns * 4)
                }

                Column(
                    Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    val rows = pageApps.chunked(settings.gridColumns)
                    rows.forEach { rowApps ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowApps.forEach { app ->
                                AppIcon(
                                    app = app,
                                    size = settings.iconSizeDp.dp,
                                    shape = settings.iconShape,
                                    onClick = {
                                        if (!editMode) appRepository.launchApp(app)
                                    }
                                )
                            }
                            repeat(settings.gridColumns - rowApps.size) {
                                Spacer(Modifier.width(settings.iconSizeDp.dp + 16.dp))
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                    }
                }
            }

            if (dockApps.isNotEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .background(colors.elevated.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                        .padding(vertical = 12.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        dockApps.forEach { app ->
                            AppIcon(
                                app = app,
                                size = (settings.iconSizeDp * 0.95f).dp,
                                shape = settings.iconShape,
                                showLabel = false,
                                onClick = { appRepository.launchApp(app) }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.navigationBarsPadding())
        }

        if (editMode) {
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 8.dp)
                    .background(colors.accent, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    "EDIT MODE  ·  Long press to exit",
                    color = colors.onBackground,
                    fontSize = 12.sp
                )
            }
        }

        SearchOverlay(
            visible = showSearch,
            appRepository = appRepository,
            settings = settings,
            onDismiss = { showSearch = false },
            onAppClick = { app ->
                appRepository.launchApp(app)
                showSearch = false
            }
        )

        if (showSettings) {
            SettingsScreen(
                settingsRepository = settingsRepository,
                onClose = { showSettings = false }
            )
        }
    }
}

private fun expandNotificationShade(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val statusBarService = context.getSystemService("statusbar")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val expand = statusBarManager.getMethod("expandNotificationsPanel")
            expand.invoke(statusBarService)
        }
    } catch (_: Exception) {
        // Expected on non-system apps / HyperOS
    }
}
