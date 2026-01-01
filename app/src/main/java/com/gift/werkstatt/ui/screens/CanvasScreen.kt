package com.gift.werkstatt.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.gift.werkstatt.ui.canvas.CanvasState
import com.gift.werkstatt.ui.canvas.GridMode
import com.gift.werkstatt.ui.canvas.InfiniteCanvas
import com.gift.werkstatt.ui.components.BrutalistToolbar
import com.gift.werkstatt.ui.components.CanvasTopBar
import com.gift.werkstatt.ui.components.PenPicker
import com.gift.werkstatt.ui.theme.CanvasBackground
import com.gift.werkstatt.data.models.StrokePoint
import com.gift.werkstatt.data.models.Stroke
import java.io.File
import java.io.FileOutputStream

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
    modifier: Modifier = Modifier
) {
    var showPenPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            CanvasTopBar(
                title = state.currentEntry?.title ?: "Untitled",
                isSaving = state.isSaving,
                onTitleClick = onTitleClick
            )
            
            // Canvas area
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
                        title = state.currentEntry?.title ?: "canvas"
                    )
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
 * Export canvas strokes to PNG and share
 */
private fun exportCanvas(context: Context, strokes: List<Stroke>, title: String) {
    // Create bitmap
    val width = 2048
    val height = 2048
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    // Fill with white background
    canvas.drawColor(android.graphics.Color.WHITE)
    
    // Center offset
    val offsetX = width / 2f
    val offsetY = height / 2f
    
    // Draw all strokes
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
    
    // Save to cache directory
    val filename = "${title.replace(" ", "_")}_${System.currentTimeMillis()}.png"
    val file = File(context.cacheDir, filename)
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    
    // Share the file
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
    
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
