package com.gift.werkstatt.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.gift.werkstatt.data.models.BrushType
import com.gift.werkstatt.ui.theme.AppAccent
import com.gift.werkstatt.ui.theme.AppBackground
import com.gift.werkstatt.ui.theme.AppPrimary
import com.gift.werkstatt.ui.theme.AppSubtle

/**
 * Slide-up panel combining brush picker, size slider, and color picker.
 *
 * @param isVisible Whether the panel is visible
 * @param onDismiss Callback when the panel should be dismissed
 * @param selectedBrush The currently selected brush type
 * @param brushSize The current brush size
 * @param currentColor The currently selected color as ARGB Long
 * @param recentColors List of recently used colors
 * @param onBrushSelected Callback when a brush is selected
 * @param onSizeChanged Callback when the brush size changes
 * @param onColorSelected Callback when a color is selected
 * @param modifier Modifier for the composable
 */
@Composable
fun SlideUpPanel(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    selectedBrush: BrushType,
    brushSize: Float,
    currentColor: Long,
    recentColors: List<Long>,
    onBrushSelected: (BrushType) -> Unit,
    onSizeChanged: (Float) -> Unit,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(AppBackground)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount > 50) {
                            onDismiss()
                        }
                    }
                }
        ) {
            // 1. Drag handle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AppSubtle)
                )
            }

            // 2. BrushPicker component
            BrushPicker(
                selectedBrush = selectedBrush,
                onBrushSelected = onBrushSelected
            )

            // 3. Spacer (16.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // 4. SIZE section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Row with "SIZE" label and "{brushSize.toInt()}px"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SIZE",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppPrimary
                    )
                    Text(
                        text = "${brushSize.toInt()}px",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppPrimary.copy(alpha = 0.6f)
                    )
                }

                // Slider
                Slider(
                    value = brushSize,
                    onValueChange = onSizeChanged,
                    valueRange = 1f..50f,
                    colors = SliderDefaults.colors(
                        thumbColor = AppAccent,
                        activeTrackColor = AppAccent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 5. Spacer (8.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // 6. Divider
            Divider(color = AppSubtle)

            // 7. ColorPicker component
            ColorPicker(
                currentColor = currentColor,
                recentColors = recentColors,
                onColorSelected = onColorSelected
            )

            // 8. Bottom Spacer (16.dp)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
