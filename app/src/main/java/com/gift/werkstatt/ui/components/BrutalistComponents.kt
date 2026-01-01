package com.gift.werkstatt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.gift.werkstatt.ui.theme.BauhausBlue
import com.gift.werkstatt.ui.theme.BauhausWhite

/**
 * Brutalist-style icon-only button
 */
@Composable
fun BrutalistIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = BauhausBlue,
    contentColor: Color = BauhausWhite
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .background(if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Bottom toolbar with brutalist style - icons only
 */
@Composable
fun BrutalistToolbar(
    onPenPicker: () -> Unit,
    onToggleEraser: () -> Unit,
    onUndo: () -> Unit,
    onCycleGrid: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onOpenList: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    eraserMode: Boolean,
    canUndo: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BauhausBlue)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BrutalistIconButton(
            onClick = onPenPicker,
            icon = Icons.Default.Edit,
            contentDescription = "Pen settings"
        )
        
        BrutalistIconButton(
            onClick = onToggleEraser,
            icon = if (eraserMode) Icons.Default.Draw else Icons.Default.Delete,
            contentDescription = if (eraserMode) "Draw mode" else "Eraser mode",
            backgroundColor = if (eraserMode) Color.Red else BauhausBlue
        )
        
        BrutalistIconButton(
            onClick = onUndo,
            icon = Icons.Default.Undo,
            contentDescription = "Undo",
            enabled = canUndo
        )
        
        BrutalistIconButton(
            onClick = onCycleGrid,
            icon = Icons.Default.GridOn,
            contentDescription = "Cycle grid"
        )
        
        BrutalistIconButton(
            onClick = onZoomIn,
            icon = Icons.Default.ZoomIn,
            contentDescription = "Zoom in"
        )
        
        BrutalistIconButton(
            onClick = onZoomOut,
            icon = Icons.Default.ZoomOut,
            contentDescription = "Zoom out"
        )
        
        BrutalistIconButton(
            onClick = onImport,
            icon = Icons.Default.Image,
            contentDescription = "Import image"
        )
        
        BrutalistIconButton(
            onClick = onExport,
            icon = Icons.Default.Share,
            contentDescription = "Export"
        )
        
        BrutalistIconButton(
            onClick = onOpenList,
            icon = Icons.Default.List,
            contentDescription = "Canvas list"
        )
    }
}

/**
 * Top bar with title and save status
 */
@Composable
fun CanvasTopBar(
    title: String,
    isSaving: Boolean,
    onTitleClick: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BauhausBlue)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = BauhausWhite
                    )
                }
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = BauhausWhite,
                modifier = Modifier.clickable(onClick = onTitleClick)
            )
        }
        
        if (isSaving) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = BauhausWhite,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Saving...",
                    style = MaterialTheme.typography.bodySmall,
                    color = BauhausWhite
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Saved",
                tint = BauhausWhite.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Pen picker with stroke width and color options
 */
@Composable
fun PenPicker(
    currentWidth: Float,
    currentColor: Long,
    onWidthSelected: (Float) -> Unit,
    onColorSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strokeWidths = listOf(2f, 4f, 8f, 12f, 20f)
    val colors = listOf(
        0xFF1A1A1A, // Black
        0xFF006392, // Bauhaus Blue
        0xFFE53935, // Red
        0xFF43A047, // Green
        0xFFFB8C00, // Orange
        0xFF8E24AA  // Purple
    )
    
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PEN SIZE",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Stroke width options
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                strokeWidths.forEach { width ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (currentWidth == width) 
                                    BauhausBlue 
                                else 
                                    Color.LightGray,
                                shape = MaterialTheme.shapes.small
                            )
                            .clickable { onWidthSelected(width) },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width.dp.coerceAtMost(32.dp))
                                .background(Color.White, shape = MaterialTheme.shapes.extraSmall)
                        )
                    }
                }
            }
            
            Text(
                text = "COLOR",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Color options
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(color),
                                shape = MaterialTheme.shapes.small
                            )
                            .clickable { onColorSelected(color) }
                            .then(
                                if (currentColor == color) {
                                    Modifier.padding(2.dp)
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentColor == color) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            
            // Done button
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BauhausBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("DONE")
            }
        }
    }
}
