package com.nothinggesture.launcher.gestures

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

enum class GestureAction {
    SWIPE_UP,
    SWIPE_DOWN,
    SWIPE_LEFT,
    SWIPE_RIGHT,
    DOUBLE_TAP,
    LONG_PRESS,
    PINCH
}

fun Modifier.launcherGestures(
    onGesture: (GestureAction) -> Unit,
    enabled: Boolean = true
): Modifier {
    if (!enabled) return this

    return this
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    onGesture(GestureAction.DOUBLE_TAP)
                },
                onLongPress = {
                    onGesture(GestureAction.LONG_PRESS)
                }
            )
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragEnd = { },
                onDrag = { change, dragAmount ->
                    change.consume()
                }
            )
        }
        .pointerInput(Unit) {
            var totalDrag = Offset.Zero
            detectDragGestures(
                onDragStart = { totalDrag = Offset.Zero },
                onDragEnd = {
                    val absX = abs(totalDrag.x)
                    val absY = abs(totalDrag.y)
                    val threshold = 120f
                    when {
                        absY > absX && absY > threshold -> {
                            if (totalDrag.y < 0) onGesture(GestureAction.SWIPE_UP)
                            else onGesture(GestureAction.SWIPE_DOWN)
                        }
                        absX > absY && absX > threshold -> {
                            if (totalDrag.x < 0) onGesture(GestureAction.SWIPE_LEFT)
                            else onGesture(GestureAction.SWIPE_RIGHT)
                        }
                    }
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    totalDrag += dragAmount
                }
            )
        }
        .pointerInput(Unit) {
            detectTransformGestures { _, _, zoom, _ ->
                if (zoom < 0.85f) {
                    onGesture(GestureAction.PINCH)
                }
            }
        }
}
