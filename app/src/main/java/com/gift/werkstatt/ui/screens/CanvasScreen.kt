package com.gift.werkstatt.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.gift.werkstatt.ui.canvas.CanvasState
import com.gift.werkstatt.ui.canvas.GridMode
import com.gift.werkstatt.ui.canvas.InfiniteCanvas
import com.gift.werkstatt.ui.components.BrutalistToolbar
import com.gift.werkstatt.ui.components.CanvasTopBar
import com.gift.werkstatt.ui.components.PenPicker
import com.gift.werkstatt.data.models.CanvasImage
import com.gift.werkstatt.data.models.StrokePoint
import com.gift.werkstatt.data.models.Stroke
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

@Composable
fun CanvasScreen(
    state: CanvasState,
    onStrokeStart: (StrokePoint) -> Unit,
    onStrokeMove: (StrokePoint) -> Unit,
    onStrokeEnd: () -> Unit,
    onViewportChange: (androidx.compose.ui.geometry.Offset) -> Unit,
    onZoomChange: (Float) -> Unit,
    onOpenList: () -> Unit,
    onUndo: () -> Unit,
    onCycleGrid: () -> Unit,
    onToggleEraser: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onTitleClick: () -> Unit,
    onStrokeWidthChange: (Float) -> Unit,
    onStrokeColorChange: (Long) -> Unit,
    onAddImage: (String, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPenPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val savedPath = saveImageToInternal(context, selectedUri)
            if (savedPath != null) {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(savedPath, options)
                // Scale down to max 300 pixels width
                val maxWidth = 300f
                val scale = if (options.outWidth > maxWidth) maxWidth / options.outWidth else 1f
                val width = options.outWidth * scale
                val height = options.outHeight * scale
                onAddImage(savedPath, width, height)
            }
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            CanvasTopBar(
                title = state.currentEntry?.title ?: "Untitled",
                isSaving = state.isSaving,
                onTitleClick = onTitleClick
            )
            
            // Canvas area - images are now rendered INSIDE the canvas
            Box(modifier = Modifier.weight(1f)) {
                InfiniteCanvas(
                    strokes = state.strokes,
                    images = state.images,
                    currentStroke = state.currentStroke,
                    onStrokeStart = onStrokeStart,
                    onStrokeMove = onStrokeMove,
                    onStrokeEnd = onStrokeEnd,
                    viewportOffset = state.viewportOffset,
                    zoom = state.zoom,
                    onViewportChange = onViewportChange,
                    onZoomChange = onZoomChange,
                    gridMode = state.gridMode,
                    snapToGrid = state.snapToGrid,
                    strokeColor = Color(state.strokeColor),
                    strokeWidth = state.strokeWidth,
                    eraserMode = state.eraserMode
                )
            }
            
            // Bottom toolbar
            BrutalistToolbar(
                onPenPicker = { showPenPicker = true },
                onToggleEraser = onToggleEraser,
                onUndo = onUndo,
                onCycleGrid = onCycleGrid,
                onZoomIn = onZoomIn,
                onZoomOut = onZoomOut,
                onOpenList = onOpenList,
                onExport = { 
                    exportCanvas(
                        context = context,
                        strokes = state.strokes,
                        images = state.images,
                        title = state.currentEntry?.title ?: "canvas"
                    )
                },
                onImport = {
                    imagePickerLauncher.launch("image/*")
                },
                eraserMode = state.eraserMode,
                canUndo = state.strokes.isNotEmpty()
            )
        }
        
        // Pen picker dialog
        if (showPenPicker) {
            Dialog(onDismissRequest = { showPenPicker = false }) {
                PenPicker(
                    currentWidth = state.strokeWidth,
                    currentColor = state.strokeColor,
                    onWidthSelected = { 
                        onStrokeWidthChange(it)
                    },
                    onColorSelected = { 
                        onStrokeColorChange(it)
                    },
                    onDismiss = { showPenPicker = false }
                )
            }
        }
    }
}

/**
 * Save image from URI to internal storage
 */
private fun saveImageToInternal(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val filename = "img_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }
        inputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}

/**
 * Calculate bounding box of all content (strokes + images)
 */
private fun calculateBoundingBox(strokes: List<Stroke>, images: List<CanvasImage>): android.graphics.RectF {
    var minX = Float.MAX_VALUE
    var minY = Float.MAX_VALUE
    var maxX = Float.MIN_VALUE
    var maxY = Float.MIN_VALUE
    
    // Include stroke points
    strokes.forEach { stroke ->
        stroke.points.forEach { point ->
            minX = min(minX, point.x)
            minY = min(minY, point.y)
            maxX = max(maxX, point.x)
            maxY = max(maxY, point.y)
        }
    }
    
    // Include images
    images.forEach { img ->
        minX = min(minX, img.x)
        minY = min(minY, img.y)
        maxX = max(maxX, img.x + img.width)
        maxY = max(maxY, img.y + img.height)
    }
    
    // If empty, return small default area
    if (minX == Float.MAX_VALUE) {
        return android.graphics.RectF(-100f, -100f, 100f, 100f)
    }
    
    // Add padding
    val padding = 50f
    return android.graphics.RectF(minX - padding, minY - padding, maxX + padding, maxY + padding)
}

/**
 * Export canvas strokes and images to PNG centered on content
 */
private fun exportCanvas(context: Context, strokes: List<Stroke>, images: List<CanvasImage>, title: String) {
    // Calculate bounding box of all content
    val bounds = calculateBoundingBox(strokes, images)
    
    // Calculate export size (with some padding, max 4096 for performance)
    val contentWidth = bounds.width()
    val contentHeight = bounds.height()
    val maxSize = 4096f
    val scale = if (contentWidth > maxSize || contentHeight > maxSize) {
        min(maxSize / contentWidth, maxSize / contentHeight)
    } else {
        1f
    }
    
    val exportWidth = (contentWidth * scale).toInt().coerceAtLeast(200)
    val exportHeight = (contentHeight * scale).toInt().coerceAtLeast(200)
    
    val bitmap = Bitmap.createBitmap(exportWidth, exportHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    canvas.drawColor(android.graphics.Color.WHITE)
    
    // Offset to center content at origin of export
    val offsetX = -bounds.left * scale
    val offsetY = -bounds.top * scale
    
    // Draw images first
    images.forEach { img ->
        try {
            val imgBitmap = BitmapFactory.decodeFile(img.filePath)
            if (imgBitmap != null) {
                val destLeft = (img.x * scale + offsetX)
                val destTop = (img.y * scale + offsetY)
                val destRight = destLeft + img.width * scale
                val destBottom = destTop + img.height * scale
                
                canvas.drawBitmap(
                    imgBitmap,
                    null,
                    android.graphics.RectF(destLeft, destTop, destRight, destBottom),
                    null
                )
            }
        } catch (e: Exception) {
            // Skip failed images
        }
    }
    
    // Draw strokes on top
    strokes.forEach { stroke ->
        val paint = Paint().apply {
            color = stroke.color.toInt()
            strokeWidth = stroke.width * scale
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }
        
        if (stroke.points.size >= 2) {
            val path = Path()
            path.moveTo(
                stroke.points[0].x * scale + offsetX, 
                stroke.points[0].y * scale + offsetY
            )
            
            for (i in 1 until stroke.points.size) {
                val prev = stroke.points[i - 1]
                val curr = stroke.points[i]
                val midX = (prev.x + curr.x) / 2 * scale + offsetX
                val midY = (prev.y + curr.y) / 2 * scale + offsetY
                path.quadTo(
                    prev.x * scale + offsetX, 
                    prev.y * scale + offsetY, 
                    midX, 
                    midY
                )
            }
            
            val last = stroke.points.last()
            path.lineTo(last.x * scale + offsetX, last.y * scale + offsetY)
            
            canvas.drawPath(path, paint)
        }
    }
    
    val filename = "${title.replace(" ", "_")}_${System.currentTimeMillis()}.png"
    val file = File(context.cacheDir, filename)
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    
    context.startActivity(Intent.createChooser(shareIntent, "Export Canvas"))
}

@Composable
fun TitleEditDialog(
    currentTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var title by remember { mutableStateOf(currentTitle) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Title") },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Canvas Title") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(title) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
