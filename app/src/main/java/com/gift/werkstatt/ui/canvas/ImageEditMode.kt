package com.gift.werkstatt.ui.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.gift.werkstatt.data.models.CanvasImage
import com.gift.werkstatt.ui.theme.AppAccent
import com.gift.werkstatt.ui.theme.AppError
import kotlin.math.roundToInt

/**
 * Overlay for editing an image (move, resize, delete)
 * Appears when user double-taps an image to enter edit mode
 */
@Composable
fun ImageEditOverlay(
    image: CanvasImage,
    scaleX: Float,
    scaleY: Float,
    onMove: (Float, Float) -> Unit,  // delta in canvas coordinates
    onResize: (Float, Float) -> Unit, // delta in canvas coordinates (width, height)
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val handleSize = 24.dp
    val borderWidth = 2.dp
    val density = LocalDensity.current

    // Convert canvas coordinates to screen coordinates (pixels)
    val screenX = (image.x * scaleX).roundToInt()
    val screenY = (image.y * scaleY).roundToInt()
    val screenWidth = image.width * scaleX
    val screenHeight = image.height * scaleY

    // Convert pixels to dp for sizing
    val widthDp = with(density) { screenWidth.toDp() }
    val heightDp = with(density) { screenHeight.toDp() }

    Box(
        modifier = modifier
            .offset { IntOffset(screenX, screenY) }
            .requiredWidth(widthDp)
            .requiredHeight(heightDp)
    ) {
        // Main draggable area with border
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(borderWidth, AppAccent, RoundedCornerShape(4.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        // Convert screen delta to canvas delta
                        val canvasDeltaX = dragAmount.x / scaleX
                        val canvasDeltaY = dragAmount.y / scaleY
                        onMove(canvasDeltaX, canvasDeltaY)
                    }
                }
        )

        // Corner handles
        // Top-Start handle (visual only)
        CornerHandle(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = -handleSize / 2, y = -handleSize / 2)
        )

        // Top-End handle (visual only)
        CornerHandle(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = handleSize / 2, y = -handleSize / 2)
        )

        // Bottom-Start handle (visual only)
        CornerHandle(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = -handleSize / 2, y = handleSize / 2)
        )

        // Bottom-End handle (active resize)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = handleSize / 2, y = handleSize / 2)
                .size(handleSize)
                .background(AppAccent, CircleShape)
                .border(borderWidth, Color.White, CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        // Keep aspect ratio: average the deltas
                        val avgDelta = (dragAmount.x + dragAmount.y) / 2
                        // Convert to canvas coordinates
                        val canvasDeltaWidth = avgDelta / scaleX
                        // Calculate height delta maintaining aspect ratio
                        val canvasDeltaHeight = avgDelta / scaleY * (image.height / image.width)
                        onResize(canvasDeltaWidth, canvasDeltaHeight)
                    }
                }
        )

        // Delete button below the image
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 48.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = AppError,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete image"
            )
        }
    }
}

/**
 * Visual corner handle component
 */
@Composable
private fun CornerHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(24.dp)
            .background(AppAccent, CircleShape)
            .border(2.dp, Color.White, CircleShape)
    )
}

/**
 * Hint displayed when in image edit mode
 */
@Composable
fun EditModeHint(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tap outside to exit edit mode",
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .background(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
