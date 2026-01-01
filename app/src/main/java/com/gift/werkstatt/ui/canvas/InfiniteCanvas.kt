package com.gift.werkstatt.ui.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import com.gift.werkstatt.data.models.Stroke as StrokeData
import com.gift.werkstatt.data.models.StrokePoint
import com.gift.werkstatt.ui.theme.BauhausBlack
import com.gift.werkstatt.ui.theme.CanvasBackground
import com.gift.werkstatt.ui.theme.GridColor

/**
 * Infinite Canvas with pan, zoom, grid, and drawing capabilities
 */
@Composable
fun InfiniteCanvas(
    strokes: List<StrokeData>,
    currentStroke: List<StrokePoint>,
    onStrokeStart: (StrokePoint) -> Unit,
    onStrokeMove: (StrokePoint) -> Unit,
    onStrokeEnd: () -> Unit,
    viewportOffset: Offset,
    zoom: Float,
    onViewportChange: (Offset) -> Unit,
    onZoomChange: (Float) -> Unit,
    gridEnabled: Boolean = true,
    snapToGrid: Boolean = false,
    gridSize: Float = 40f,
    strokeColor: Color = BauhausBlack,
    strokeWidth: Float = 4f,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .pointerInput(viewportOffset, zoom, snapToGrid, gridSize) {
                awaitEachGesture {
                    val firstDown = awaitFirstDown()
                    var isDrawing = true
                    var lastPosition = firstDown.position
                    
                    // Start drawing with first touch
                    val canvasPoint = screenToCanvas(firstDown.position, viewportOffset, zoom, snapToGrid, gridSize)
                    onStrokeStart(StrokePoint(canvasPoint.x, canvasPoint.y, 1f))
                    
                    do {
                        val event = awaitPointerEvent()
                        val pointerCount = event.changes.count { it.pressed }
                        
                        if (pointerCount >= 2) {
                            // Two or more fingers: pan and zoom
                            if (isDrawing) {
                                // Was drawing, now stop and switch to pan/zoom
                                onStrokeEnd()
                                isDrawing = false
                            }
                            
                            val zoomChange = event.calculateZoom()
                            val panChange = event.calculatePan()
                            
                            if (zoomChange != 1f) {
                                val newZoom = (zoom * zoomChange).coerceIn(0.1f, 5f)
                                onZoomChange(newZoom)
                            }
                            
                            if (panChange != Offset.Zero) {
                                onViewportChange(viewportOffset + panChange)
                            }
                            
                            event.changes.forEach { it.consume() }
                        } else if (pointerCount == 1 && isDrawing) {
                            // Single finger: draw
                            val change = event.changes.firstOrNull { it.pressed }
                            if (change != null) {
                                val canvasPt = screenToCanvas(change.position, viewportOffset, zoom, snapToGrid, gridSize)
                                onStrokeMove(StrokePoint(canvasPt.x, canvasPt.y, 1f))
                                lastPosition = change.position
                                change.consume()
                            }
                        }
                    } while (event.changes.any { it.pressed })
                    
                    // Finish stroke when all fingers lifted
                    if (isDrawing) {
                        onStrokeEnd()
                    }
                }
            }
    ) {
        // Apply viewport transform
        withTransform({
            scale(zoom, zoom, Offset.Zero)
            translate(viewportOffset.x, viewportOffset.y)
        }) {
            // Draw grid
            if (gridEnabled) {
                drawGrid(gridSize)
            }
            
            // Draw completed strokes
            strokes.forEach { stroke ->
                drawStroke(stroke.points, Color(stroke.color), stroke.width)
            }
            
            // Draw current stroke being drawn
            if (currentStroke.isNotEmpty()) {
                drawStroke(currentStroke, strokeColor, strokeWidth)
            }
        }
    }
}

/**
 * Convert screen coordinates to canvas coordinates
 */
private fun screenToCanvas(
    screenPoint: Offset,
    viewportOffset: Offset,
    zoom: Float,
    snapToGrid: Boolean,
    gridSize: Float
): Offset {
    val canvasX = (screenPoint.x / zoom) - viewportOffset.x
    val canvasY = (screenPoint.y / zoom) - viewportOffset.y
    
    return if (snapToGrid) {
        Offset(
            (canvasX / gridSize).toInt() * gridSize,
            (canvasY / gridSize).toInt() * gridSize
        )
    } else {
        Offset(canvasX, canvasY)
    }
}

/**
 * Draw Bauhaus-style grid
 */
private fun DrawScope.drawGrid(gridSize: Float) {
    val gridColor = GridColor
    
    // Calculate visible area with some padding
    val startX = -5000f
    val endX = 5000f
    val startY = -5000f
    val endY = 5000f
    
    // Draw vertical lines
    var x = startX
    while (x <= endX) {
        drawLine(
            color = gridColor,
            start = Offset(x, startY),
            end = Offset(x, endY),
            strokeWidth = if (x.toInt() % (gridSize.toInt() * 5) == 0) 1.5f else 0.5f
        )
        x += gridSize
    }
    
    // Draw horizontal lines
    var y = startY
    while (y <= endY) {
        drawLine(
            color = gridColor,
            start = Offset(startX, y),
            end = Offset(endX, y),
            strokeWidth = if (y.toInt() % (gridSize.toInt() * 5) == 0) 1.5f else 0.5f
        )
        y += gridSize
    }
}

/**
 * Draw a stroke as a smooth path
 */
private fun DrawScope.drawStroke(
    points: List<StrokePoint>,
    color: Color,
    width: Float
) {
    if (points.size < 2) return
    
    val path = Path().apply {
        moveTo(points[0].x, points[0].y)
        
        for (i in 1 until points.size) {
            val prev = points[i - 1]
            val curr = points[i]
            
            // Smooth curve using quadratic bezier
            val midX = (prev.x + curr.x) / 2
            val midY = (prev.y + curr.y) / 2
            
            quadraticBezierTo(prev.x, prev.y, midX, midY)
        }
        
        // Connect to the last point
        val last = points.last()
        lineTo(last.x, last.y)
    }
    
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = width,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}
