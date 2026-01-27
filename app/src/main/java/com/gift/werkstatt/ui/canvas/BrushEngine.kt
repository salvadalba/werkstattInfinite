package com.gift.werkstatt.ui.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.gift.werkstatt.data.models.BrushType
import com.gift.werkstatt.data.models.StrokePoint
import kotlin.math.sqrt
import kotlin.random.Random

object BrushEngine {

    fun DrawScope.drawBrushStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        brushType: BrushType,
        opacity: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        if (points.size < 2) return

        val scaledWidth = width * scaleX
        val strokeColor = color.copy(alpha = opacity)

        when (brushType) {
            BrushType.PEN -> drawPenStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            BrushType.FINE_PEN -> drawPenStroke(points, strokeColor, scaledWidth * 0.5f, scaleX, scaleY)
            BrushType.BALLPOINT -> drawBallpointStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            BrushType.PENCIL -> drawPencilStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            BrushType.MARKER -> drawMarkerStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            BrushType.WATERCOLOR -> drawWatercolorStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            BrushType.FOUNTAIN_INK -> drawFountainInkStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            BrushType.BRUSH -> drawTaperedBrushStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
        }
    }

    private fun DrawScope.drawPenStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        val path = createSmoothPath(points, scaleX, scaleY)
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

    private fun DrawScope.drawBallpointStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        // Ballpoint has slight opacity variation
        points.zipWithNext().forEach { (p1, p2) ->
            val opacityVariation = 0.95f + Random.nextFloat() * 0.05f
            drawLine(
                color = color.copy(alpha = color.alpha * opacityVariation),
                start = Offset(p1.x * scaleX, p1.y * scaleY),
                end = Offset(p2.x * scaleX, p2.y * scaleY),
                strokeWidth = width,
                cap = StrokeCap.Round
            )
        }
    }

    private fun DrawScope.drawPencilStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        // Pencil has grainy texture - draw multiple thin lines
        val path = createSmoothPath(points, scaleX, scaleY)

        // Base stroke
        drawPath(
            path = path,
            color = color.copy(alpha = color.alpha * 0.7f),
            style = Stroke(
                width = width,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Add texture dots along the path
        points.forEach { point ->
            repeat(3) {
                val offsetX = (Random.nextFloat() - 0.5f) * width * 0.8f
                val offsetY = (Random.nextFloat() - 0.5f) * width * 0.8f
                if (Random.nextFloat() > 0.5f) {
                    drawCircle(
                        color = color.copy(alpha = color.alpha * Random.nextFloat() * 0.5f),
                        radius = width * 0.1f,
                        center = Offset(
                            point.x * scaleX + offsetX,
                            point.y * scaleY + offsetY
                        )
                    )
                }
            }
        }
    }

    private fun DrawScope.drawMarkerStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        val path = createSmoothPath(points, scaleX, scaleY)
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = width * 1.5f,
                cap = StrokeCap.Square,
                join = StrokeJoin.Bevel
            )
        )
    }

    private fun DrawScope.drawWatercolorStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        // Watercolor has soft edges - draw multiple passes with decreasing opacity
        val path = createSmoothPath(points, scaleX, scaleY)

        // Outer soft glow
        drawPath(
            path = path,
            color = color.copy(alpha = color.alpha * 0.2f),
            style = Stroke(
                width = width * 2f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Middle layer
        drawPath(
            path = path,
            color = color.copy(alpha = color.alpha * 0.4f),
            style = Stroke(
                width = width * 1.4f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Core stroke
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

    private fun DrawScope.drawFountainInkStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        // Fountain ink varies thickness based on speed
        if (points.size < 2) return

        for (i in 1 until points.size) {
            val p1 = points[i - 1]
            val p2 = points[i]

            // Calculate speed (distance between points)
            val dx = p2.x - p1.x
            val dy = p2.y - p1.y
            val distance = sqrt(dx * dx + dy * dy)

            // Slower = thicker, faster = thinner
            val speedFactor = (1f - (distance / 50f).coerceIn(0f, 0.7f))
            val strokeWidth = width * (0.5f + speedFactor * 0.8f)

            drawLine(
                color = color,
                start = Offset(p1.x * scaleX, p1.y * scaleY),
                end = Offset(p2.x * scaleX, p2.y * scaleY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }

    private fun DrawScope.drawTaperedBrushStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        // Brush has tapered ends
        if (points.size < 2) return

        val totalPoints = points.size

        for (i in 1 until points.size) {
            val p1 = points[i - 1]
            val p2 = points[i]

            // Taper at start and end
            val progress = i.toFloat() / totalPoints
            val taperFactor = when {
                progress < 0.1f -> progress * 10f // Taper in
                progress > 0.9f -> (1f - progress) * 10f // Taper out
                else -> 1f
            }

            val strokeWidth = width * taperFactor.coerceIn(0.3f, 1f)

            drawLine(
                color = color,
                start = Offset(p1.x * scaleX, p1.y * scaleY),
                end = Offset(p2.x * scaleX, p2.y * scaleY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }

    private fun createSmoothPath(
        points: List<StrokePoint>,
        scaleX: Float,
        scaleY: Float
    ): Path {
        return Path().apply {
            moveTo(points[0].x * scaleX, points[0].y * scaleY)

            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val curr = points[i]
                val midX = (prev.x + curr.x) / 2 * scaleX
                val midY = (prev.y + curr.y) / 2 * scaleY
                quadraticBezierTo(prev.x * scaleX, prev.y * scaleY, midX, midY)
            }

            val last = points.last()
            lineTo(last.x * scaleX, last.y * scaleY)
        }
    }
}
