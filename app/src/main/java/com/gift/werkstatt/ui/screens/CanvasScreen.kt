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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.gift.werkstatt.data.models.BrushType
import com.gift.werkstatt.data.models.CanvasImage
import com.gift.werkstatt.data.models.Stroke
import com.gift.werkstatt.data.models.StrokePoint
import com.gift.werkstatt.ui.canvas.CanvasState
import com.gift.werkstatt.ui.canvas.FixedCanvas
import com.gift.werkstatt.ui.canvas.CANVAS_WIDTH
import com.gift.werkstatt.ui.canvas.CANVAS_HEIGHT
import com.gift.werkstatt.ui.canvas.ImageEditOverlay
import com.gift.werkstatt.ui.canvas.EditModeHint
import com.gift.werkstatt.ui.components.SlideUpPanel
import com.gift.werkstatt.ui.theme.AppBackground
import com.gift.werkstatt.ui.theme.AppPrimary
import com.gift.werkstatt.ui.theme.AppAccent
import com.gift.werkstatt.ui.theme.AppSurface
import com.gift.werkstatt.ui.theme.AppSubtle
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

@Composable
fun CanvasScreen(
    state: CanvasState,
    onStrokeStart: (StrokePoint) -> Unit,
    onStrokeMove: (StrokePoint) -> Unit,
    onStrokeEnd: () -> Unit,
    onNavigateBack: () -> Unit,
    onUndo: () -> Unit,
    onCycleGrid: () -> Unit,
    onToggleEraser: () -> Unit,
    onTitleClick: () -> Unit,
    onBrushTypeChange: (BrushType) -> Unit,
    onBrushSizeChange: (Float) -> Unit,
    onColorChange: (Long) -> Unit,
    onAddImage: (String, Float, Float) -> Unit,
    onUpdateImagePosition: (String, Float, Float) -> Unit,
    onResizeImage: (String, Float, Float) -> Unit,
    onDeleteImage: (String) -> Unit,
    onEnterImageEditMode: (String) -> Unit,
    onExitImageEditMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPanel by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Snackbar for user feedback
    val snackbarHostState = remember { SnackbarHostState() }

    // Loading state for image import
    var isImportingImage by remember { mutableStateOf(false) }

    // Track canvas container size for coordinate conversion
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    // Image picker launcher with error handling
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            scope.launch {
                isImportingImage = true
                try {
                    val savedPath = saveImageToInternal(
                        context = context,
                        uri = selectedUri,
                        onError = { error ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = error,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                    if (savedPath != null) {
                        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                        BitmapFactory.decodeFile(savedPath, options)
                        // Scale to fit canvas width (max 60% of canvas width)
                        val maxWidth = CANVAS_WIDTH * 0.6f
                        val scale = if (options.outWidth > maxWidth) maxWidth / options.outWidth else 1f
                        val width = options.outWidth * scale
                        val height = options.outHeight * scale
                        onAddImage(savedPath, width, height)
                        snackbarHostState.showSnackbar(
                            message = "Image imported",
                            duration = SnackbarDuration.Short
                        )
                    }
                } finally {
                    isImportingImage = false
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // New Slim Top Bar (48.dp height)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(AppBackground)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppPrimary
                        )
                    }

                    // Title (clickable)
                    Text(
                        text = state.currentEntry?.title ?: "Untitled",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTitleClick() }
                            .padding(horizontal = 8.dp)
                    )

                    // Save indicator
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = AppAccent,
                            strokeWidth = 2.dp
                        )
                    }
                }

                // Canvas area with image edit overlay
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .onSizeChanged { containerSize = it },
                    contentAlignment = Alignment.Center
                ) {
                    // Main canvas with double-tap detection for images
                    Box(
                        modifier = Modifier
                            .pointerInput(state.images, state.editingImageId) {
                                if (state.editingImageId == null) {
                                    detectTapGestures(
                                        onDoubleTap = { offset ->
                                            // Convert screen tap to canvas coordinates
                                            val scaleX = CANVAS_WIDTH / size.width
                                            val scaleY = CANVAS_HEIGHT / size.height
                                            val canvasX = offset.x * scaleX
                                            val canvasY = offset.y * scaleY

                                            // Find image at this location (last one = topmost)
                                            state.images.findLast { img ->
                                                canvasX >= img.x && canvasX <= img.x + img.width &&
                                                canvasY >= img.y && canvasY <= img.y + img.height
                                            }?.let { img ->
                                                onEnterImageEditMode(img.id)
                                            }
                                        }
                                    )
                                }
                            }
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
                            brushType = state.brushType,
                            brushOpacity = state.brushOpacity,
                            editingImageId = state.editingImageId
                        )
                    }

                    // Image edit mode overlay
                    if (state.editingImageId != null) {
                        val editingImage = state.images.find { it.id == state.editingImageId }
                        if (editingImage != null && containerSize.width > 0) {
                            // Calculate scale factors
                            val scaleX = containerSize.width / CANVAS_WIDTH
                            val scaleY = containerSize.height / CANVAS_HEIGHT

                            // Tap outside to exit edit mode
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        detectTapGestures { onExitImageEditMode() }
                                    }
                            )

                            ImageEditOverlay(
                                image = editingImage,
                                scaleX = scaleX,
                                scaleY = scaleY,
                                onMove = { dx, dy -> onUpdateImagePosition(editingImage.id, dx, dy) },
                                onResize = { dw, dh -> onResizeImage(editingImage.id, dw, dh) },
                                onDelete = { onDeleteImage(editingImage.id); onExitImageEditMode() }
                            )

                            // Hint toast
                            EditModeHint(modifier = Modifier.align(Alignment.BottomCenter))
                        }
                    }

                    // Loading overlay for image import
                    if (isImportingImage) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                }

                // New Slim Bottom Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(AppBackground)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Brush preview button (opens panel)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppSurface)
                            .clickable { showPanel = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(state.strokeColor), CircleShape)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = state.brushType.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelMedium,
                            color = AppPrimary
                        )
                    }

                    // Action buttons
                    Row {
                        IconButton(onClick = onUndo, enabled = state.strokes.isNotEmpty()) {
                            Icon(
                                Icons.Default.Undo,
                                contentDescription = "Undo",
                                tint = if (state.strokes.isNotEmpty()) AppPrimary else AppSubtle
                            )
                        }
                        IconButton(onClick = onCycleGrid) {
                            Icon(
                                Icons.Default.GridOn,
                                contentDescription = "Grid",
                                tint = AppPrimary
                            )
                        }
                        IconButton(onClick = onToggleEraser) {
                            Icon(
                                if (state.eraserMode) Icons.Default.Edit else Icons.Default.Delete,
                                contentDescription = "Eraser",
                                tint = if (state.eraserMode) AppAccent else AppPrimary
                            )
                        }
                        IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = "Import Image",
                                tint = AppPrimary
                            )
                        }
                    }
                }
            }

            // Slide-up panel
            SlideUpPanel(
                isVisible = showPanel,
                onDismiss = { showPanel = false },
                selectedBrush = state.brushType,
                brushSize = state.strokeWidth,
                currentColor = state.strokeColor,
                recentColors = state.recentColors,
                onBrushSelected = onBrushTypeChange,
                onSizeChanged = onBrushSizeChange,
                onColorSelected = onColorChange,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

/**
 * Save image from URI to internal storage with error handling
 */
private fun saveImageToInternal(
    context: Context,
    uri: Uri,
    onError: (String) -> Unit = {}
): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: run {
                onError("Unable to open image file")
                return null
            }

        val filename = "img_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, filename)

        inputStream.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        file.absolutePath
    } catch (e: SecurityException) {
        onError("Permission denied to access image")
        null
    } catch (e: IOException) {
        onError("Failed to save image: ${e.message}")
        null
    } catch (e: Exception) {
        onError("Unexpected error: ${e.message}")
        null
    }
}

/**
 * Export fixed canvas to PNG with error handling and proper memory management
 */
private fun exportCanvas(
    context: Context,
    strokes: List<Stroke>,
    images: List<CanvasImage>,
    title: String,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    var bitmap: Bitmap? = null
    try {
        bitmap = Bitmap.createBitmap(
            CANVAS_WIDTH.toInt(),
            CANVAS_HEIGHT.toInt(),
            Bitmap.Config.ARGB_8888
        ) ?: run {
            onError("Failed to create bitmap")
            return
        }

        val canvas = Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)

        // Draw images first
        images.forEach { img ->
            var imgBitmap: Bitmap? = null
            try {
                imgBitmap = BitmapFactory.decodeFile(img.filePath)
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
                // Skip failed images but continue
            } finally {
                imgBitmap?.recycle()
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
        onSuccess()

    } catch (e: OutOfMemoryError) {
        onError("Not enough memory to export canvas")
    } catch (e: IOException) {
        onError("Failed to save exported image: ${e.message}")
    } catch (e: Exception) {
        onError("Export failed: ${e.message}")
    } finally {
        bitmap?.recycle()
    }
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
