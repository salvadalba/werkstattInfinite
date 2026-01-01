package com.gift.werkstatt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gift.werkstatt.ui.canvas.CanvasState
import com.gift.werkstatt.ui.canvas.InfiniteCanvas
import com.gift.werkstatt.ui.components.BrutalistToolbar
import com.gift.werkstatt.ui.components.CanvasTopBar
import com.gift.werkstatt.data.models.StrokePoint

@Composable
fun CanvasScreen(
    state: CanvasState,
    onStrokeStart: (StrokePoint) -> Unit,
    onStrokeMove: (StrokePoint) -> Unit,
    onStrokeEnd: () -> Unit,
    onViewportChange: (androidx.compose.ui.geometry.Offset) -> Unit,
    onZoomChange: (Float) -> Unit,
    onNewCanvas: () -> Unit,
    onOpenList: () -> Unit,
    onUndo: () -> Unit,
    onClear: () -> Unit,
    onToggleGrid: () -> Unit,
    onToggleSnap: () -> Unit,
    onTitleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top bar
        CanvasTopBar(
            title = state.currentEntry?.title ?: "Untitled",
            isSaving = state.isSaving,
            onTitleClick = onTitleClick
        )
        
        // Canvas area
        Box(
            modifier = Modifier.weight(1f)
        ) {
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
                gridEnabled = state.gridEnabled,
                snapToGrid = state.snapToGrid,
                strokeColor = Color(state.strokeColor),
                strokeWidth = state.strokeWidth
            )
        }
        
        // Bottom toolbar
        BrutalistToolbar(
            onNewCanvas = onNewCanvas,
            onOpenList = onOpenList,
            onUndo = onUndo,
            onClear = onClear,
            onToggleGrid = onToggleGrid,
            onToggleSnap = onToggleSnap,
            gridEnabled = state.gridEnabled,
            snapEnabled = state.snapToGrid,
            canUndo = state.strokes.isNotEmpty()
        )
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
