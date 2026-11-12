package com.nothinggesture.launcher.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nothinggesture.launcher.data.ClockStyle
import com.nothinggesture.launcher.ui.theme.LocalNothingColors
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NothingClock(
    style: ClockStyle = ClockStyle.LARGE_DIGITAL,
    scale: Float = 1f,
    showDate: Boolean = true,
    modifier: Modifier = Modifier
) {
    var now by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val calendar = Calendar.getInstance().apply { timeInMillis = now }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val timeStr = String.format("%02d:%02d", hour, minute)

    val dateFormat = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
    val dateStr = dateFormat.format(Date(now)).uppercase(Locale.getDefault())

    val colors = LocalNothingColors.current

    when (style) {
        ClockStyle.LARGE_DIGITAL -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = timeStr,
                    color = colors.onBackground,
                    fontSize = (72 * scale).sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-2).sp
                )
                if (showDate) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = dateStr,
                        color = colors.muted,
                        fontSize = (14 * scale).sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }
        ClockStyle.MINIMAL -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = timeStr,
                    color = colors.onBackground,
                    fontSize = (42 * scale).sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Normal
                )
                if (showDate) {
                    Text(
                        text = dateStr,
                        color = colors.muted,
                        fontSize = (12 * scale).sp
                    )
                }
            }
        }
        ClockStyle.DOT_MATRIX -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = timeStr,
                    color = colors.accent,
                    fontSize = (56 * scale).sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
                if (showDate) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = dateStr,
                        color = colors.muted,
                        fontSize = (11 * scale).sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}
