package com.gift.werkstatt.ui.canvas

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.gift.werkstatt.data.models.BrushType
import com.gift.werkstatt.data.models.CanvasImage
import com.gift.werkstatt.data.models.Stroke as StrokeData
import com.gift.werkstatt.data.models.StrokePoint
import com.gift.werkstatt.ui.canvas.BrushEngine.drawBrushStroke
import com.gift.werkstatt.ui.theme.BauhausBlack
import com.gift.werkstatt.ui.theme.CanvasBackground
import com.gift.werkstatt.ui.theme.GridColor

// Fixed canvas dimensions (2:3 aspect ratio)
const val CANVAS_WIDTH = 1500f
const val CANVAS_HEIGHT = 2250f

enum class GridMode {
    NONE,
    LINES,
    DOTS
}

/**
 * Fixed-size Canvas with grid and drawing capabilities
 * No pan/zoom - simpler coordinate system
 */
@Composable
fun FixedCanvas(
    strokes: List<StrokeData>,
    images: List<CanvasImage>,
    currentStroke: List<StrokePoint>,
    onStrokeStart: (StrokePoint) -> Unit,
    onStrokeMove: (StrokePoint) -> Unit,
    onStrokeEnd: () -> Unit,
    gridMode: GridMode = GridMode.LINES,
    gridSize: Float = 40f,
    strokeColor: Color = BauhausBlack,
    strokeWidth: Float = 4f,
    brushType: BrushType = BrushType.PEN,
    brushOpacity: Float = 1f,
    eraserMode: Boolean = false,
    editingImageId: String? = null,
    modifier: Modifier = Modifier
) {
    // Cache loaded images by file path
    val imageCache = remember { mutableMapOf<String, ImageBitmap?>() }

    // Clean up cache for removed images to prevent memory leaks
    LaunchedEffect(images.map { it.filePath }) {
        val currentPaths = images.map { it.filePath }.toSet()
        imageCache.keys.retainAll(currentPaths)
    }

    val loadedImages = remember(images.map { it.filePath }) {
        images.mapNotNull { canvasImage ->
            val cached = imageCache[canvasImage.filePath]
            if (cached != null) {
                canvasImage to cached
            } else {
                try {
                    // Calculate optimal sample size based on target display size
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeFile(canvasImage.filePath, options)

                    val targetWidth = (canvasImage.width * 2).toInt() // 2x for quality
                    val targetHeight = (canvasImage.height * 2).toInt()
                    val inSampleSize = calculateInSampleSize(
                        options.outWidth,
                        options.outHeight,
                        targetWidth,
                        targetHeight
                    )

                    options.apply {
                        this.inSampleSize = inSampleSize
                        inJustDecodeBounds = false
                    }

                    val bitmap = BitmapFactory.decodeFile(canvasImage.filePath, options)
                    val imageBitmap = bitmap?.asImageBitmap()
                    imageCache[canvasImage.filePath] = imageBitmap
                    imageBitmap?.let { canvasImage to it }
                } catch (e: Exception) {
                    imageCache[canvasImage.filePath] = null
                    null
                }
            }
        }
    }
    
    // Track canvas size for coordinate conversion
    var canvasSize by remember { mutableStateOf(Offset.Zero) }
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(CANVAS_WIDTH / CANVAS_HEIGHT)
            .background(CanvasBackground)
            .pointerInput(editingImageId) {
                // Only handle drawing if NOT in image edit mode
                if (editingImageId != null) return@pointerInput

                canvasSize = Offset(size.width.toFloat(), size.height.toFloat())

                awaitEachGesture {
                    val firstDown = awaitFirstDown()
                    
                    // Convert screen coordinates to canvas coordinates
                    val scaleX = CANVAS_WIDTH / size.width
                    val scaleY = CANVAS_HEIGHT / size.height
                    
                    val canvasX = firstDown.position.x * scaleX
                    val canvasY = firstDown.position.y * scaleY
                    onStrokeStart(StrokePoint(canvasX, canvasY, 1f))
                    
                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.pressed }
                        if (change != null) {
                            val cx = change.position.x * scaleX
                            val cy = change.position.y * scaleY
                            onStrokeMove(StrokePoint(cx, cy, 1f))
                            change.consume()
                        }
                    } while (event.changes.any { it.pressed })
                    
                    onStrokeEnd()
                }
            }
    ) {
        // Scale factor from canvas coords to screen coords
        val scaleX = size.width / CANVAS_WIDTH
        val scaleY = size.height / CANVAS_HEIGHT
        
        // Draw grid
        when (gridMode) {
            GridMode.LINES -> drawLineGrid(gridSize, scaleX, scaleY)
            GridMode.DOTS -> drawDottedGrid(gridSize, scaleX, scaleY)
            GridMode.NONE -> { }
        }
        
        // Draw images FIRST (under strokes)
        loadedImages.forEach { (canvasImage, imageBitmap) ->
            val screenX = (canvasImage.x * scaleX).toInt()
            val screenY = (canvasImage.y * scaleY).toInt()
            val screenW = (canvasImage.width * scaleX).toInt()
            val screenH = (canvasImage.height * scaleY).toInt()
            
            drawImage(
                image = imageBitmap,
                dstOffset = IntOffset(screenX, screenY),
                dstSize = IntSize(screenW, screenH)
            )
        }
        
        // Draw completed strokes using brush engine
        strokes.forEach { stroke ->
            drawBrushStroke(
                points = stroke.points,
                color = Color(stroke.color),
                width = stroke.width,
                brushType = stroke.brushType,
                opacity = stroke.opacity,
                scaleX = scaleX,
                scaleY = scaleY
            )
        }

        // Draw current stroke
        if (currentStroke.isNotEmpty()) {
            drawBrushStroke(
                points = currentStroke,
                color = strokeColor,
                width = strokeWidth,
                brushType = brushType,
                opacity = brushOpacity,
                scaleX = scaleX,
                scaleY = scaleY
            )
        }
    }
}

private fun DrawScope.drawLineGrid(gridSize: Float, scaleX: Float, scaleY: Float) {
    val gridColor = GridColor
    
    // Vertical lines
    var x = 0f
    while (x <= CANVAS_WIDTH) {
        val screenX = x * scaleX
        drawLine(
            color = gridColor,
            start = Offset(screenX, 0f),
            end = Offset(screenX, size.height),
            strokeWidth = if (x.toInt() % (gridSize.toInt() * 5) == 0) 1.5f else 0.5f
        )
        x += gridSize
    }
    
    // Horizontal lines
    var y = 0f
    while (y <= CANVAS_HEIGHT) {
        val screenY = y * scaleY
        drawLine(
            color = gridColor,
            start = Offset(0f, screenY),
            end = Offset(size.width, screenY),
            strokeWidth = if (y.toInt() % (gridSize.toInt() * 5) == 0) 1.5f else 0.5f
        )
        y += gridSize
    }
}

private fun DrawScope.drawDottedGrid(gridSize: Float, scaleX: Float, scaleY: Float) {
    val gridColor = GridColor
    val dotRadius = 2f
    
    var x = 0f
    while (x <= CANVAS_WIDTH) {
        var y = 0f
        while (y <= CANVAS_HEIGHT) {
            val isMajor = x.toInt() % (gridSize.toInt() * 5) == 0 && 
                          y.toInt() % (gridSize.toInt() * 5) == 0
            drawCircle(
                color = gridColor,
                radius = if (isMajor) dotRadius * 2 else dotRadius,
                center = Offset(x * scaleX, y * scaleY)
            )
            y += gridSize
        }
        x += gridSize
    }
}

/**
 * Calculate optimal inSampleSize for BitmapFactory to efficiently load images
 */
private fun calculateInSampleSize(
    width: Int,
    height: Int,
    reqWidth: Int,
    reqHeight: Int
): Int {
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while (halfHeight / inSampleSize >= reqHeight &&
            halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
