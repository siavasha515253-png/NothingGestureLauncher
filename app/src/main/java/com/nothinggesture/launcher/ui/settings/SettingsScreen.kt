package com.nothinggesture.launcher.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nothinggesture.launcher.data.*
import com.nothinggesture.launcher.ui.theme.AccentPresets
import com.nothinggesture.launcher.ui.theme.LocalNothingColors
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    onClose: () -> Unit
) {
    val colors = LocalNothingColors.current
    val scope = rememberCoroutineScope()
    val settings by settingsRepository.settings.collectAsState(
        initial = settingsRepository.defaultSettings
    )

    Box(Modifier.fillMaxSize().background(colors.background)) {
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
        ) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.ArrowBack, "Back", tint = colors.onBackground,
                    modifier = Modifier.size(28.dp).clickable { onClose() }
                )
                Spacer(Modifier.width(16.dp))
                Text("Settings", color = colors.onBackground, fontSize = 22.sp)
            }

            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
            ) {
                SectionHeader("Appearance")

                var selectedFamily by remember {
                    mutableStateOf(AccentPresets.familyOf(settings.accentColor))
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF111111), RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Box(
                        Modifier.fillMaxWidth().height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(settings.accentColor))
                    )
                    Spacer(Modifier.height(18.dp))
                    Text("Accent", color = colors.onBackground, fontSize = 15.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Choose a family, then a shade", color = colors.muted, fontSize = 12.sp)
                    Spacer(Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AccentPresets.allFamilies.forEach { family ->
                            val isSelected = selectedFamily.name == family.name
                            val chipColor = Color(family.shades.first().color)
                            Box(
                                Modifier
                                    .background(
                                        if (isSelected) chipColor.copy(alpha = 0.18f) else Color.Transparent,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { selectedFamily = family }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    family.name,
                                    color = if (isSelected) chipColor else colors.muted,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        selectedFamily.shades.forEach { shade ->
                            val isSelected = settings.accentColor == shade.color
                            val shadeColor = Color(shade.color)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        settingsRepository.update { it.copy(accentColor = shade.color) }
                                    }
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(52.dp)) {
                                    if (isSelected) {
                                        Box(
                                            Modifier.size(52.dp).clip(CircleShape)
                                                .background(shadeColor.copy(alpha = 0.25f))
                                        )
                                    }
                                    Box(
                                        Modifier
                                            .size(if (isSelected) 38.dp else 36.dp)
                                            .clip(CircleShape)
                                            .background(shadeColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                Icons.Rounded.Check, null,
                                                tint = if (shade.color == 0xFFFFFFFFL || shade.color == 0xFFFFE57F || shade.color == 0xFFFFEA00)
                                                    Color.Black else Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    shade.name,
                                    color = if (isSelected) colors.onBackground else colors.muted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                SettingSlider("Icon size", settings.iconSizeDp.toFloat(), 40f..72f) { v ->
                    scope.launch { settingsRepository.update { it.copy(iconSizeDp = v.toInt()) } }
                }
                SettingSlider("Grid columns", settings.gridColumns.toFloat(), 3f..6f, 2) { v ->
                    scope.launch { settingsRepository.update { it.copy(gridColumns = v.toInt()) } }
                }

                Text("Clock style", color = colors.muted, fontSize = 13.sp, modifier = Modifier.padding(top = 16.dp))
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ClockStyle.entries.forEach { style ->
                        val selected = settings.clockStyle == style
                        Box(
                            Modifier
                                .background(if (selected) colors.accent else colors.elevated, RoundedCornerShape(12.dp))
                                .clickable {
                                    scope.launch { settingsRepository.update { it.copy(clockStyle = style) } }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(style.name.replace('_', ' '), color = colors.onBackground, fontSize = 12.sp)
                        }
                    }
                }

                SettingSlider("Clock scale", settings.clockSizeScale, 0.7f..1.4f) { v ->
                    scope.launch { settingsRepository.update { it.copy(clockSizeScale = v) } }
                }

                Text("Icon shape", color = colors.muted, fontSize = 13.sp, modifier = Modifier.padding(top = 16.dp))
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconShape.entries.forEach { shape ->
                        val selected = settings.iconShape == shape
                        Box(
                            Modifier
                                .background(if (selected) colors.accent else colors.elevated, RoundedCornerShape(12.dp))
                                .clickable {
                                    scope.launch { settingsRepository.update { it.copy(iconShape = shape) } }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(shape.name.replace('_', ' '), color = colors.onBackground, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))
                SectionHeader("Gestures")

                Text("Double-tap action", color = colors.muted, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DoubleTapAction.entries.forEach { action ->
                        val selected = settings.doubleTapAction == action
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(if (selected) colors.elevated else Color.Transparent, RoundedCornerShape(10.dp))
                                .clickable {
                                    scope.launch { settingsRepository.update { it.copy(doubleTapAction = action) } }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(action.name.replace('_', ' '), color = colors.onBackground, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))
                SectionHeader("Search")

                ToggleRow("Show recent apps", settings.searchShowRecent) {
                    scope.launch { settingsRepository.update { s -> s.copy(searchShowRecent = it) } }
                }
                ToggleRow("Show frequent apps", settings.searchShowFrequent) {
                    scope.launch { settingsRepository.update { s -> s.copy(searchShowFrequent = it) } }
                }

                Spacer(Modifier.height(28.dp))
                SectionHeader("About")
                Text(
                    "Nothing Gesture Launcher\nInspired by Nothing OS.\nNo app drawer. Pure gestures.\n\nFor POCO M6 Pro / HyperOS / Android 16.\nLightweight · Offline · No tracking.",
                    color = colors.muted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    val colors = LocalNothingColors.current
    Text(
        title.uppercase(),
        color = colors.accent,
        fontSize = 12.sp,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun SettingSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChange: (Float) -> Unit
) {
    val colors = LocalNothingColors.current
    Column(Modifier.padding(vertical = 8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = colors.muted, fontSize = 13.sp)
            Text(
                if (value == value.toInt().toFloat()) value.toInt().toString()
                else String.format("%.1f", value),
                color = colors.onBackground,
                fontSize = 13.sp
            )
        }
        Slider(value = value, onValueChange = onValueChange, valueRange = range, steps = steps, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = LocalNothingColors.current
    Row(
        Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) }.padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = colors.onBackground, fontSize = 15.sp)
        Box(
            Modifier
                .size(width = 48.dp, height = 28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (checked) colors.accent else colors.elevated),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                Modifier.padding(3.dp).size(22.dp).clip(CircleShape).background(colors.onBackground)
            )
        }
    }
}
