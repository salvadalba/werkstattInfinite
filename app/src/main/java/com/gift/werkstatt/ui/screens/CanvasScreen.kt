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
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
import kotlin.math.roundToInt

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
    onUpdateImagePosition: (String, Float, Float) -> Unit,
    onDeleteImage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPenPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val density = LocalDensity.current
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val savedPath = saveImageToInternal(context, selectedUri)
            if (savedPath != null) {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(savedPath, options)
                // Scale down to max 300dp width
                val maxWidth = 300f
                val scale = if (options.outWidth > maxWidth) maxWidth / options.outWidth else 1f
                val width = options.outWidth * scale
                val height = options.outHeight * scale
                onAddImage(savedPath, width, height)
            }
        }
    }
    
    // Load images for display
    val loadedImages = remember(state.images) {
        state.images.mapNotNull { canvasImage ->
            try {
                val bitmap = BitmapFactory.decodeFile(canvasImage.filePath)
                bitmap?.let { canvasImage to it.asImageBitmap() }
            } catch (e: Exception) {
                null
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
            
            // Canvas area with images
            Box(modifier = Modifier.weight(1f)) {
                InfiniteCanvas(
                    strokes = state.strokes,
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
                
                // Render draggable images on top of canvas
                loadedImages.forEach { (canvasImage, imageBitmap) ->
                    val screenX = with(density) { 
                        ((canvasImage.x + state.viewportOffset.x) * state.zoom).dp.toPx() 
                    }
                    val screenY = with(density) { 
                        ((canvasImage.y + state.viewportOffset.y) * state.zoom).dp.toPx() 
                    }
                    val imgWidth = with(density) { (canvasImage.width * state.zoom).dp.toPx() }
                    val imgHeight = with(density) { (canvasImage.height * state.zoom).dp.toPx() }
                    
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Imported image - drag to move, long press to delete",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .offset { IntOffset(screenX.roundToInt(), screenY.roundToInt()) }
                            .size(
                                width = with(density) { imgWidth.toDp() },
                                height = with(density) { imgHeight.toDp() }
                            )
                            .pointerInput(canvasImage.id, state.zoom) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    // Convert screen delta to canvas delta
                                    val canvasDeltaX = dragAmount.x / state.zoom / density.density
                                    val canvasDeltaY = dragAmount.y / state.zoom / density.density
                                    onUpdateImagePosition(canvasImage.id, canvasDeltaX, canvasDeltaY)
                                }
                            }
                            .pointerInput(canvasImage.id) {
                                detectTapGestures(
                                    onLongPress = {
                                        onDeleteImage(canvasImage.id)
                                    }
                                )
                            }
                    )
                }
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
 * Export canvas strokes and images to PNG and share
 */
private fun exportCanvas(context: Context, strokes: List<Stroke>, images: List<CanvasImage>, title: String) {
    val width = 2048
    val height = 2048
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    canvas.drawColor(android.graphics.Color.WHITE)
    
    val offsetX = width / 2f
    val offsetY = height / 2f
    
    // Draw images first
    images.forEach { img ->
        try {
            val imgBitmap = BitmapFactory.decodeFile(img.filePath)
            if (imgBitmap != null) {
                canvas.drawBitmap(
                    imgBitmap,
                    null,
                    android.graphics.RectF(
                        img.x + offsetX,
                        img.y + offsetY,
                        img.x + offsetX + img.width,
                        img.y + offsetY + img.height
                    ),
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
            strokeWidth = stroke.width
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }
        
        if (stroke.points.size >= 2) {
            val path = Path()
            path.moveTo(stroke.points[0].x + offsetX, stroke.points[0].y + offsetY)
            
            for (i in 1 until stroke.points.size) {
                val prev = stroke.points[i - 1]
                val curr = stroke.points[i]
                val midX = (prev.x + curr.x) / 2 + offsetX
                val midY = (prev.y + curr.y) / 2 + offsetY
                path.quadTo(prev.x + offsetX, prev.y + offsetY, midX, midY)
            }
            
            val last = stroke.points.last()
            path.lineTo(last.x + offsetX, last.y + offsetY)
            
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
