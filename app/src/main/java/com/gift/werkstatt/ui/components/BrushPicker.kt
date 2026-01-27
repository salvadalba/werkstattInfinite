package com.gift.werkstatt.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.gift.werkstatt.data.models.BrushType
import com.gift.werkstatt.ui.theme.AppAccent
import com.gift.werkstatt.ui.theme.AppPrimary
import com.gift.werkstatt.ui.theme.AppSubtle
import com.gift.werkstatt.ui.theme.AppSurface

/**
 * Brush picker component displaying all 8 brush types in a horizontal row.
 *
 * @param selectedBrush The currently selected brush type
 * @param onBrushSelected Callback when a brush is selected
 * @param modifier Modifier for the composable
 */
@Composable
fun BrushPicker(
    selectedBrush: BrushType,
    onBrushSelected: (BrushType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // 1. "BRUSHES" label
        Text(
            text = "BRUSHES",
            style = MaterialTheme.typography.labelMedium,
            color = AppPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. LazyRow with 8 BrushButton items
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            items(BrushType.entries.toList()) { brushType ->
                BrushButton(
                    brushType = brushType,
                    isSelected = brushType == selectedBrush,
                    onClick = { onBrushSelected(brushType) }
                )
            }
        }
    }
}

/**
 * Individual brush button showing brush preview and name.
 *
 * @param brushType The brush type to display
 * @param isSelected Whether this brush is currently selected
 * @param onClick Callback when clicked
 */
@Composable
private fun BrushButton(
    brushType: BrushType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) AppAccent else AppSurface
    val borderColor = if (isSelected) AppAccent else AppSubtle

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // BrushIcon (32dp canvas with line preview)
        BrushIcon(
            brushType = brushType,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Brush display name
        Text(
            text = brushType.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = AppPrimary
        )
    }
}

/**
 * Canvas drawing a simple line to represent the brush style.
 *
 * @param brushType The brush type to preview
 * @param modifier Modifier for the canvas
 */
@Composable
private fun BrushIcon(
    brushType: BrushType,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = brushType.previewStrokeWidth
        val alpha = brushType.previewAlpha
        val strokeCap = brushType.previewStrokeCap

        // Draw a diagonal line from bottom-left to top-right
        drawLine(
            color = AppPrimary.copy(alpha = alpha),
            start = Offset(4f, size.height - 4f),
            end = Offset(size.width - 4f, 4f),
            strokeWidth = strokeWidth,
            cap = strokeCap
        )
    }
}

/**
 * Display name for each brush type.
 */
val BrushType.displayName: String
    get() = when (this) {
        BrushType.PEN -> "Pen"
        BrushType.FINE_PEN -> "Fine"
        BrushType.BALLPOINT -> "Ball"
        BrushType.PENCIL -> "Pencil"
        BrushType.MARKER -> "Marker"
        BrushType.WATERCOLOR -> "Water"
        BrushType.FOUNTAIN_INK -> "Ink"
        BrushType.BRUSH -> "Brush"
    }

/**
 * Preview stroke width for each brush type.
 */
val BrushType.previewStrokeWidth: Float
    get() = when (this) {
        BrushType.PEN -> 3f
        BrushType.FINE_PEN -> 1.5f
        BrushType.BALLPOINT -> 2.5f
        BrushType.PENCIL -> 4f
        BrushType.MARKER -> 8f
        BrushType.WATERCOLOR -> 6f
        BrushType.FOUNTAIN_INK -> 3.5f
        BrushType.BRUSH -> 5f
    }

/**
 * Preview alpha for each brush type.
 */
val BrushType.previewAlpha: Float
    get() = when (this) {
        BrushType.PEN -> 1f
        BrushType.FINE_PEN -> 1f
        BrushType.BALLPOINT -> 1f
        BrushType.PENCIL -> 0.9f
        BrushType.MARKER -> 0.85f
        BrushType.WATERCOLOR -> 0.6f
        BrushType.FOUNTAIN_INK -> 1f
        BrushType.BRUSH -> 0.95f
    }

/**
 * Preview stroke cap for each brush type.
 */
val BrushType.previewStrokeCap: StrokeCap
    get() = when (this) {
        BrushType.MARKER -> StrokeCap.Square
        else -> StrokeCap.Round
    }
