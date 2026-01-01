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
 * Brutalist-style blocky button
 */
@Composable
fun BrutalistButton(
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = BauhausBlue,
    contentColor: Color = BauhausWhite
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .background(if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor
            )
        }
    }
}

/**
 * Bottom toolbar with brutalist style
 */
@Composable
fun BrutalistToolbar(
    onNewCanvas: () -> Unit,
    onOpenList: () -> Unit,
    onUndo: () -> Unit,
    onClear: () -> Unit,
    onToggleGrid: () -> Unit,
    onToggleSnap: () -> Unit,
    gridEnabled: Boolean,
    snapEnabled: Boolean,
    canUndo: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BauhausBlue)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BrutalistButton(
            onClick = onNewCanvas,
            icon = Icons.Default.Add,
            label = "NEW"
        )
        
        BrutalistButton(
            onClick = onOpenList,
            icon = Icons.Default.List,
            label = "LIST"
        )
        
        BrutalistButton(
            onClick = onUndo,
            icon = Icons.Default.Undo,
            label = "UNDO",
            enabled = canUndo
        )
        
        BrutalistButton(
            onClick = onToggleGrid,
            icon = if (gridEnabled) Icons.Default.GridOn else Icons.Default.GridOff,
            label = "GRID"
        )
        
        BrutalistButton(
            onClick = onToggleSnap,
            icon = if (snapEnabled) Icons.Default.FilterCenterFocus else Icons.Default.CenterFocusWeak,
            label = "SNAP"
        )
        
        BrutalistButton(
            onClick = onClear,
            icon = Icons.Default.Delete,
            label = "CLEAR"
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
