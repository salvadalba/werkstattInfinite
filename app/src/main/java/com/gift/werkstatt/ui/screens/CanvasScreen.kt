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
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.gift.werkstatt.ui.canvas.CanvasState
import com.gift.werkstatt.ui.canvas.FixedCanvas
import com.gift.werkstatt.ui.canvas.CANVAS_WIDTH
import com.gift.werkstatt.ui.canvas.CANVAS_HEIGHT
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
import kotlin.math.roundToInt

@Composable
fun CanvasScreen(
    state: CanvasState,
    onStrokeStart: (StrokePoint) -> Unit,
    onStrokeMove: (StrokePoint) -> Unit,
    onStrokeEnd: () -> Unit,
    onOpenList: () -> Unit,
    onUndo: () -> Unit,
    onCycleGrid: () -> Unit,
    onToggleEraser: () -> Unit,
    onTitleClick: () -> Unit,
    onStrokeWidthChange: (Float) -> Unit,
    onStrokeColorChange: (Long) -> Unit,
    onAddImage: (String, Float, Float) -> Unit,
    onUpdateImagePosition: (String, Float, Float) -> Unit,
    onResizeImage: (String, Float, Float) -> Unit,
    onDeleteImage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPenPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val density = LocalDensity.current
    
    // Track canvas container size for coordinate conversion
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val savedPath = saveImageToInternal(context, selectedUri)
            if (savedPath != null) {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(savedPath, options)
                // Scale to fit canvas width (max 60% of canvas width)
                val maxWidth = CANVAS_WIDTH * 0.6f
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
            
            // Canvas area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .onSizeChanged { containerSize = it }
                ) {
                    // Fixed canvas (renders images + strokes)
                    FixedCanvas(
                        strokes = state.strokes,
                        images = state.images,
                        currentStroke = state.currentStroke,
                        onStrokeStart = onStrokeStart,
                        onStrokeMove = onStrokeMove,
                        onStrokeEnd = onStrokeEnd,
                        gridMode = state.gridMode,
                        strokeColor = Color(state.strokeColor),
                        strokeWidth = state.strokeWidth,
                        eraserMode = state.eraserMode
                    )
                    
                    // Image touch overlays (drag to move, corners to resize, long-press to delete)
                    if (containerSize.width > 0) {
                        val scaleX = containerSize.width / CANVAS_WIDTH
                        val scaleY = containerSize.height / CANVAS_HEIGHT
                        
                        state.images.forEach { canvasImage ->
                            ImageOverlay(
                                canvasImage = canvasImage,
                                scaleX = scaleX,
                                scaleY = scaleY,
                                onMove = { dx, dy -> 
                                    onUpdateImagePosition(canvasImage.id, dx / scaleX, dy / scaleY)
                                },
                                onResize = { dw, dh ->
                                    onResizeImage(canvasImage.id, dw / scaleX, dh / scaleY)
                                },
                                onDelete = { onDeleteImage(canvasImage.id) }
                            )
                        }
                    }
                }
            }
            
            // Bottom toolbar (simplified - no zoom)
            BrutalistToolbar(
                onPenPicker = { showPenPicker = true },
                onToggleEraser = onToggleEraser,
                onUndo = onUndo,
                onCycleGrid = onCycleGrid,
                onZoomIn = { }, // No longer needed
                onZoomOut = { }, // No longer needed
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
 * Image overlay with drag-to-move and corner-drag-to-resize
 */
@Composable
private fun ImageOverlay(
    canvasImage: CanvasImage,
    scaleX: Float,
    scaleY: Float,
    onMove: (Float, Float) -> Unit,
    onResize: (Float, Float) -> Unit,
    onDelete: () -> Unit
) {
    val screenX = (canvasImage.x * scaleX).roundToInt()
    val screenY = (canvasImage.y * scaleY).roundToInt()
    val screenW = (canvasImage.width * scaleX).roundToInt()
    val screenH = (canvasImage.height * scaleY).roundToInt()
    
    val handleSize = 32.dp
    val density = LocalDensity.current
    val handlePx = with(density) { handleSize.toPx() }
    
    Box(
        modifier = Modifier
            .offset { IntOffset(screenX, screenY) }
            .size(
                width = with(density) { screenW.toDp() },
                height = with(density) { screenH.toDp() }
            )
    ) {
        // Main draggable area (center - for moving)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(handleSize / 2)
                .pointerInput(canvasImage.id) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onMove(dragAmount.x, dragAmount.y)
                    }
                }
                .pointerInput(canvasImage.id) {
                    detectTapGestures(
                        onLongPress = { onDelete() }
                    )
                }
        )
        
        // Bottom-right corner handle (for resizing)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(handleSize)
                .background(Color.Blue.copy(alpha = 0.5f), CircleShape)
                .pointerInput(canvasImage.id) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        // Keep aspect ratio
                        val avgDelta = (dragAmount.x + dragAmount.y) / 2
                        onResize(avgDelta, avgDelta * (canvasImage.height / canvasImage.width))
                    }
                }
        )
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
 * Export fixed canvas to PNG
 */
private fun exportCanvas(context: Context, strokes: List<Stroke>, images: List<CanvasImage>, title: String) {
    val bitmap = Bitmap.createBitmap(
        CANVAS_WIDTH.toInt(), 
        CANVAS_HEIGHT.toInt(), 
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    
    canvas.drawColor(android.graphics.Color.WHITE)
    
    // Draw images first
    images.forEach { img ->
        try {
            val imgBitmap = BitmapFactory.decodeFile(img.filePath)
            if (imgBitmap != null) {
                canvas.drawBitmap(
                    imgBitmap,
                    null,
                    android.graphics.RectF(
                        img.x,
                        img.y,
                        img.x + img.width,
                        img.y + img.height
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
