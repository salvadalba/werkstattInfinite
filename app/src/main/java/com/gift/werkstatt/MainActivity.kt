package com.gift.werkstatt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gift.werkstatt.data.database.CanvasDatabase
import com.gift.werkstatt.data.repository.CanvasRepository
import com.gift.werkstatt.ui.canvas.CanvasViewModel
import com.gift.werkstatt.ui.screens.CanvasScreen
import com.gift.werkstatt.ui.screens.EntryListScreen
import com.gift.werkstatt.ui.screens.TitleEditDialog
import com.gift.werkstatt.ui.theme.WerkstattInfiniteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = CanvasDatabase.getDatabase(applicationContext)
        val repository = CanvasRepository(database.canvasDao())
        
        setContent {
            WerkstattInfiniteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WerkstattApp(repository = repository)
                }
            }
        }
    }
}

@Composable
fun WerkstattApp(repository: CanvasRepository) {
    val viewModel: CanvasViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CanvasViewModel(repository) as T
            }
        }
    )
    
    val state by viewModel.state.collectAsState()
    var showList by remember { mutableStateOf(true) }
    var showTitleDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf<String?>(null) }
    
    // Handle back button - save and go to list instead of closing app
    BackHandler(enabled = !showList) {
        viewModel.saveNow()
        showList = true
    }
    
    // Show title edit dialog
    if (showTitleDialog && state.currentEntry != null) {
        TitleEditDialog(
            currentTitle = state.currentEntry?.title ?: "Untitled",
            onDismiss = { showTitleDialog = false },
            onConfirm = { newTitle ->
                viewModel.updateTitle(newTitle)
                showTitleDialog = false
            }
        )
    }
    
    // Show delete confirmation
    showDeleteConfirm?.let { entryId ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { androidx.compose.material3.Text("Delete Canvas?") },
            text = { androidx.compose.material3.Text("This action cannot be undone.") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        viewModel.deleteEntry(entryId)
                        showDeleteConfirm = null
                    }
                ) {
                    androidx.compose.material3.Text("Delete")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDeleteConfirm = null }) {
                    androidx.compose.material3.Text("Cancel")
                }
            }
        )
    }
    
    if (showList) {
        EntryListScreen(
            entries = state.allEntries,
            onEntryClick = { entry ->
                viewModel.loadEntry(entry.id)
                showList = false
            },
            onNewEntry = {
                viewModel.createNewEntry()
                showList = false
            },
            onDeleteEntry = { entry ->
                showDeleteConfirm = entry.id
            }
        )
    } else {
        CanvasScreen(
            state = state,
            onStrokeStart = viewModel::onStrokeStart,
            onStrokeMove = viewModel::onStrokeMove,
            onStrokeEnd = viewModel::onStrokeEnd,
            onOpenList = { 
                viewModel.saveNow()
                showList = true 
            },
            onUndo = viewModel::undoLastStroke,
            onCycleGrid = viewModel::cycleGridMode,
            onToggleEraser = viewModel::toggleEraserMode,
            onTitleClick = { showTitleDialog = true },
            onStrokeWidthChange = viewModel::setStrokeWidth,
            onStrokeColorChange = viewModel::setStrokeColor,
            onAddImage = viewModel::addImage,
            onUpdateImagePosition = viewModel::updateImagePosition,
            onResizeImage = viewModel::resizeImage,
            onDeleteImage = viewModel::deleteImage
        )
    }
}
