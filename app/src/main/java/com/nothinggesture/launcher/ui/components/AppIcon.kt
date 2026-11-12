package com.nothinggesture.launcher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.nothinggesture.launcher.data.AppInfo
import com.nothinggesture.launcher.data.IconShape
import com.nothinggesture.launcher.ui.theme.LocalNothingColors

@Composable
fun AppIcon(
    app: AppInfo,
    size: Dp = 56.dp,
    shape: IconShape = IconShape.ROUNDED_SQUARE,
    monochrome: Boolean = true,
    showLabel: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalNothingColors.current
    val density = LocalDensity.current

    val iconShape = when (shape) {
        IconShape.CIRCLE -> CircleShape
        IconShape.SQUIRCLE -> RoundedCornerShape(size * 0.32f)
        IconShape.ROUNDED_SQUARE -> RoundedCornerShape(14.dp)
        IconShape.SQUARE -> RoundedCornerShape(4.dp)
    }

    val tileColor = Color(0xFF1A1A1A)

    Column(
        modifier = modifier
            .width(size + 12.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(iconShape)
                .background(tileColor),
            contentAlignment = Alignment.Center
        ) {
            val drawable = app.icon
            if (drawable != null) {
                val px = with(density) { size.roundToPx() }
                val bitmap = try {
                    drawable.toBitmap(px, px)
                } catch (_: Exception) {
                    null
                }
                if (bitmap != null) {
                    val colorFilter = if (monochrome) {
                        ColorFilter.colorMatrix(nothingStyleMatrix())
                    } else null

                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = app.label,
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = colorFilter
                    )
                } else {
                    FallbackIcon(app.label, colors.accent)
                }
            } else {
                FallbackIcon(app.label, colors.accent)
            }
        }

        if (showLabel) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = app.label,
                color = colors.onBackground,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun nothingStyleMatrix(): ColorMatrix {
    val matrix = ColorMatrix().apply { setToSaturation(0.15f) }
    val contrast = 1.35f
    val translate = (-0.15f * 255f) * (contrast - 1f) / contrast
    val contrastMatrix = ColorMatrix(
        floatArrayOf(
            contrast, 0f, 0f, 0f, translate,
            0f, contrast, 0f, 0f, translate,
            0f, 0f, contrast, 0f, translate,
            0f, 0f, 0f, 1f, 0f
        )
    )
    matrix.timesAssign(contrastMatrix)
    val redLift = ColorMatrix(
        floatArrayOf(
            1.15f, 0f, 0f, 0f, 10f,
            0f, 0.95f, 0f, 0f, 0f,
            0f, 0f, 0.95f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    )
    matrix.timesAssign(redLift)
    return matrix
}

@Composable
private fun FallbackIcon(label: String, accent: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.take(1).uppercase(),
            color = accent,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
