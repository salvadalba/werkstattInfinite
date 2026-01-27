package com.gift.werkstatt.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * A circular hue selector (360 degree color wheel)
 * Tap or drag to select hue value (0-360)
 */
@Composable
fun ColorWheel(
    selectedHue: Float,
    onHueSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var center by remember { mutableStateOf(Offset.Zero) }
    var radius by remember { mutableStateOf(0f) }

    Canvas(
        modifier = modifier
            .size(200.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val hue = calculateHue(offset, center)
                    onHueSelected(hue)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val hue = calculateHue(change.position, center)
                    onHueSelected(hue)
                }
            }
    ) {
        center = Offset(size.width / 2, size.height / 2)
        radius = minOf(size.width, size.height) / 2 - 16f

        // Draw color wheel using arcs
        val ringWidth = 32f
        for (i in 0 until 360) {
            val startAngle = i.toFloat()
            drawArc(
                color = Color.hsv(startAngle, 1f, 1f),
                startAngle = startAngle - 90, // Adjust so 0 degrees is at top
                sweepAngle = 1.5f,
                useCenter = false,
                style = Stroke(width = ringWidth),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
        }

        // Draw selection indicator
        val indicatorAngle = Math.toRadians((selectedHue - 90).toDouble())
        val indicatorX = center.x + radius * cos(indicatorAngle).toFloat()
        val indicatorY = center.y + radius * sin(indicatorAngle).toFloat()

        // White circle with black border (14px radius, 3px stroke)
        drawCircle(
            color = Color.White,
            radius = 14f,
            center = Offset(indicatorX, indicatorY)
        )
        drawCircle(
            color = Color.Black,
            radius = 14f,
            center = Offset(indicatorX, indicatorY),
            style = Stroke(width = 3f)
        )
        // Filled center with selected color (8px radius)
        drawCircle(
            color = Color.hsv(selectedHue, 1f, 1f),
            radius = 8f,
            center = Offset(indicatorX, indicatorY)
        )
    }
}

/**
 * Calculate hue (0-360) from touch position relative to center
 * Uses atan2 and adjusts so 0 degrees is at top
 */
private fun calculateHue(position: Offset, center: Offset): Float {
    val dx = position.x - center.x
    val dy = position.y - center.y
    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    angle += 90 // Adjust so 0 is at top
    if (angle < 0) angle += 360
    return angle
}

/**
 * A horizontal slider for adjusting saturation or brightness
 * Draws a gradient based on isSaturation flag
 * @param label The label to display above the slider
 * @param value Current value (0-1)
 * @param onValueChange Callback when value changes
 * @param hue Current hue value for gradient colors
 * @param isSaturation true for saturation slider, false for brightness slider
 */
@Composable
fun SaturationBrightnessSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    hue: Float,
    isSaturation: Boolean,
    modifier: Modifier = Modifier
) {
    val gradientColors = if (isSaturation) {
        listOf(
            Color.hsv(hue, 0f, 1f),
            Color.hsv(hue, 1f, 1f)
        )
    } else {
        listOf(
            Color.hsv(hue, 1f, 0f),
            Color.hsv(hue, 1f, 1f)
        )
    }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val newValue = (offset.x / size.width).coerceIn(0f, 1f)
                            onValueChange(newValue)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val newValue = (change.position.x / size.width).coerceIn(0f, 1f)
                            onValueChange(newValue)
                        }
                    }
            ) {
                // Draw gradient background with rounded corners
                drawRoundRect(
                    brush = Brush.horizontalGradient(gradientColors),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // Draw indicator at current position
                val indicatorX = value * size.width
                drawCircle(
                    color = Color.White,
                    radius = 12f,
                    center = Offset(indicatorX, size.height / 2)
                )
                drawCircle(
                    color = Color.Black,
                    radius = 12f,
                    center = Offset(indicatorX, size.height / 2),
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}
