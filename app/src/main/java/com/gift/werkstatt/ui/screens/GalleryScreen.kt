package com.gift.werkstatt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gift.werkstatt.data.models.CanvasEntry
import com.gift.werkstatt.ui.theme.AppAccent
import com.gift.werkstatt.ui.theme.AppBackground
import com.gift.werkstatt.ui.theme.AppPrimary
import com.gift.werkstatt.ui.theme.AppSurface
import com.gift.werkstatt.ui.theme.AppSubtle
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GalleryScreen(
    entries: List<CanvasEntry>,
    onEntryClick: (String) -> Unit,
    onCreateNew: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Text(
                text = "Werkstatt",
                style = MaterialTheme.typography.titleLarge,
                color = AppPrimary,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            )

            if (entries.isEmpty()) {
                // Empty state
                EmptyState(
                    onCreateClick = onCreateNew,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Grid of entries
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(entries, key = { it.id }) { entry ->
                        CanvasCard(
                            entry = entry,
                            onClick = { onEntryClick(entry.id) }
                        )
                    }
                }
            }
        }

        // Floating action button
        if (entries.isNotEmpty()) {
            FloatingActionButton(
                onClick = onCreateNew,
                containerColor = AppAccent,
                contentColor = AppBackground,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create new canvas")
            }
        }
    }
}

@Composable
private fun CanvasCard(
    entry: CanvasEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Thumbnail area (takes most space)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(AppSubtle),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder - actual thumbnail loading will be in Task 20
                Text(
                    text = entry.title.take(2).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppPrimary.copy(alpha = 0.3f)
                )
            }

            // Info section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDate(entry.updatedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = AppPrimary.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Simple placeholder icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(AppSubtle, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = AppPrimary.copy(alpha = 0.3f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Start your first canvas",
            style = MaterialTheme.typography.titleMedium,
            color = AppPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCreateClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppAccent,
                contentColor = AppBackground
            )
        ) {
            Text("Create")
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
