package com.gift.werkstatt.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.gift.werkstatt.data.models.CanvasImage
import com.gift.werkstatt.data.models.Stroke
import com.gift.werkstatt.ui.canvas.CANVAS_WIDTH
import com.gift.werkstatt.ui.canvas.CANVAS_HEIGHT
import java.io.File
import java.io.FileOutputStream

/**
 * Manages thumbnail generation and storage for canvas entries.
 */
class ThumbnailManager(private val context: Context) {

    private val thumbnailDir: File by lazy {
        File(context.filesDir, "thumbnails").apply { mkdirs() }
    }

    /**
     * Generate a thumbnail for a canvas and save it to internal storage.
     * @param entryId The canvas entry ID
     * @param strokes List of strokes on the canvas
     * @param images List of images on the canvas
     * @return The file path of the saved thumbnail, or null if failed
     */
    fun generateThumbnail(
        entryId: String,
        strokes: List<Stroke>,
        images: List<CanvasImage>
    ): String? {
        var bitmap: Bitmap? = null
        var scaledBitmap: Bitmap? = null

        return try {
            // Create full-size canvas bitmap
            bitmap = Bitmap.createBitmap(
                CANVAS_WIDTH.toInt(),
                CANVAS_HEIGHT.toInt(),
                Bitmap.Config.ARGB_8888
            ) ?: return null

            val canvas = Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE)

            // Draw images
            images.forEach { img ->
                var imgBitmap: Bitmap? = null
                try {
                    imgBitmap = BitmapFactory.decodeFile(img.filePath)
                    imgBitmap?.let {
                        canvas.drawBitmap(
                            it,
                            null,
                            android.graphics.RectF(
                                img.x, img.y,
                                img.x + img.width, img.y + img.height
                            ),
                            null
                        )
                    }
                } finally {
                    imgBitmap?.recycle()
                }
            }

            // Draw strokes
            strokes.forEach { stroke ->
                val paint = Paint().apply {
                    color = stroke.color.toInt()
                    strokeWidth = stroke.width
                    style = Paint.Style.STROKE
                    strokeCap = Paint.Cap.ROUND
                    strokeJoin = Paint.Join.ROUND
                    isAntiAlias = true
                    alpha = (stroke.opacity * 255).toInt()
                }

                if (stroke.points.size >= 2) {
                    val path = Path()
                    path.moveTo(stroke.points[0].x, stroke.points[0].y)

                    for (i in 1 until stroke.points.size) {
                        val prev = stroke.points[i - 1]
                        val curr = stroke.points[i]
                        val midX = (prev.x + curr.x) / 2
                        val midY = (prev.y + curr.y) / 2
                        path.quadTo(prev.x, prev.y, midX, midY)
                    }

                    val last = stroke.points.last()
                    path.lineTo(last.x, last.y)
                    canvas.drawPath(path, paint)
                }
            }

            // Scale down for thumbnail (300x450 for 3:4 aspect ratio)
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, 300, 450, true)

            // Save to file
            val file = File(thumbnailDir, "thumb_$entryId.jpg")
            FileOutputStream(file).use { out ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }

            file.absolutePath
        } catch (e: Exception) {
            null
        } finally {
            bitmap?.recycle()
            scaledBitmap?.recycle()
        }
    }

    /**
     * Get the thumbnail file path for an entry if it exists.
     */
    fun getThumbnailPath(entryId: String): String? {
        val file = File(thumbnailDir, "thumb_$entryId.jpg")
        return if (file.exists()) file.absolutePath else null
    }

    /**
     * Delete thumbnail for an entry.
     */
    fun deleteThumbnail(entryId: String) {
        File(thumbnailDir, "thumb_$entryId.jpg").delete()
    }
}
