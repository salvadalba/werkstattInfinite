package com.gift.werkstatt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gift.werkstatt.data.models.CanvasEntry
import com.gift.werkstatt.ui.theme.BauhausBlue
import com.gift.werkstatt.ui.theme.BauhausBlack
import com.gift.werkstatt.ui.theme.BauhausLightGray
import com.gift.werkstatt.ui.theme.BauhausWhite
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EntryListScreen(
    entries: List<CanvasEntry>,
    onEntryClick: (CanvasEntry) -> Unit,
    onNewEntry: () -> Unit,
    onDeleteEntry: (CanvasEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BauhausLightGray)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BauhausBlue)
                .padding(16.dp)
        ) {
            Text(
                text = "WERKSTATT INFINITE",
                style = MaterialTheme.typography.headlineMedium,
                color = BauhausWhite
            )
        }
        
        // Empty state or list
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No entries yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = BauhausBlack.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tap + to create your first canvas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BauhausBlack.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    EntryCard(
                        entry = entry,
                        onClick = { onEntryClick(entry) },
                        onDelete = { onDeleteEntry(entry) }
                    )
                }
            }
        }
        
        // New canvas button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BauhausBlue)
                .clickable(onClick = onNewEntry)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Canvas",
                    tint = BauhausWhite,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "NEW CANVAS",
                    style = MaterialTheme.typography.titleLarge,
                    color = BauhausWhite
                )
            }
        }
    }
}

@Composable
private fun EntryCard(
    entry: CanvasEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = BauhausBlue  // Changed from white to Bauhaus blue
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = BauhausWhite,  // White text on blue background
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${entry.strokes.size} strokes • ${dateFormat.format(Date(entry.updatedAt))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = BauhausWhite.copy(alpha = 0.8f)  // Slightly transparent white
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = BauhausWhite.copy(alpha = 0.8f)  // White delete icon
                )
            }
        }
    }
}
