package com.nothinggesture.launcher.ui.search

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nothinggesture.launcher.data.AppInfo
import com.nothinggesture.launcher.data.AppRepository
import com.nothinggesture.launcher.data.LauncherSettings
import com.nothinggesture.launcher.ui.components.AppIcon
import com.nothinggesture.launcher.ui.theme.LocalNothingColors
import kotlinx.coroutines.delay

@Composable
fun SearchOverlay(
    visible: Boolean,
    appRepository: AppRepository,
    settings: LauncherSettings,
    onDismiss: () -> Unit,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalNothingColors.current
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    val allApps by appRepository.apps.collectAsState()
    val recent = remember(allApps) { appRepository.getRecent(8) }
    val frequent = remember(allApps) { appRepository.getFrequent(8) }
    val searchResults = remember(query, allApps) {
        if (query.isBlank()) emptyList() else appRepository.search(query)
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(220)) + slideInVertically(tween(280)) { it / 3 },
        exit = fadeOut(tween(180)) + slideOutVertically(tween(220)) { it / 4 },
        modifier = modifier
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(colors.background.copy(alpha = 0.97f))
                .clickable(enabled = false) {}
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(colors.elevated, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Search, null, tint = colors.muted, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.weight(1f).focusRequester(focusRequester),
                        textStyle = TextStyle(color = colors.onBackground, fontSize = 17.sp),
                        cursorBrush = SolidColor(colors.accent),
                        singleLine = true,
                        decorationBox = { inner ->
                            if (query.isEmpty()) {
                                Text("Search apps…", color = colors.muted, fontSize = 17.sp)
                            }
                            inner()
                        }
                    )
                    if (query.isNotEmpty()) {
                        Icon(
                            Icons.Rounded.Close, "Clear", tint = colors.muted,
                            modifier = Modifier.size(22.dp).clickable { query = "" }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                if (query.isBlank()) {
                    if (settings.searchShowRecent && recent.isNotEmpty()) {
                        SectionTitle("Recent")
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(recent) { app ->
                                AppIcon(app = app, size = 52.dp, shape = settings.iconShape, onClick = { onAppClick(app) })
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    if (settings.searchShowFrequent && frequent.isNotEmpty()) {
                        SectionTitle("Frequent")
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(frequent) { app ->
                                AppIcon(app = app, size = 52.dp, shape = settings.iconShape, onClick = { onAppClick(app) })
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    SectionTitle("All Apps")
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(settings.gridColumns),
                        contentPadding = PaddingValues(bottom = 48.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(allApps, key = { it.uniqueId }) { app ->
                            AppIcon(
                                app = app,
                                size = settings.iconSizeDp.dp,
                                shape = settings.iconShape,
                                onClick = { onAppClick(app) }
                            )
                        }
                    }
                } else {
                    if (searchResults.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No apps found", color = colors.muted)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(settings.gridColumns),
                            contentPadding = PaddingValues(bottom = 48.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(searchResults, key = { it.uniqueId }) { app ->
                                AppIcon(
                                    app = app,
                                    size = settings.iconSizeDp.dp,
                                    shape = settings.iconShape,
                                    onClick = { onAppClick(app) }
                                )
                            }
                        }
                    }
                }
            }

            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
                    .clickable { onDismiss() }
            ) {
                Text("Swipe down or tap to close", color = colors.muted.copy(alpha = 0.6f), fontSize = 12.sp)
            }
        }

        LaunchedEffect(visible) {
            if (visible) {
                delay(100)
                focusRequester.requestFocus()
                keyboard?.show()
            } else {
                query = ""
                keyboard?.hide()
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    val colors = LocalNothingColors.current
    Text(
        text = text.uppercase(),
        color = colors.muted,
        fontSize = 12.sp,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}
