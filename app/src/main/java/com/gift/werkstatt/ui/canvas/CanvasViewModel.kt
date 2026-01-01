package com.gift.werkstatt.ui.canvas

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gift.werkstatt.data.models.CanvasEntry
import com.gift.werkstatt.data.models.Stroke
import com.gift.werkstatt.data.models.StrokePoint
import com.gift.werkstatt.data.repository.CanvasRepository
import com.gift.werkstatt.ui.theme.BauhausBlack
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt

enum class GridMode {
    NONE,
    LINES,
    DOTS
}

data class CanvasState(
    val currentEntry: CanvasEntry? = null,
    val strokes: List<Stroke> = emptyList(),
    val currentStroke: List<StrokePoint> = emptyList(),
    val viewportOffset: Offset = Offset.Zero,
    val zoom: Float = 1f,
    val gridMode: GridMode = GridMode.LINES,
    val snapToGrid: Boolean = false,
    val strokeColor: Long = BauhausBlack.value.toLong(),
    val strokeWidth: Float = 4f,
    val eraserMode: Boolean = false,
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val allEntries: List<CanvasEntry> = emptyList()
)

class CanvasViewModel(
    private val repository: CanvasRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CanvasState())
    val state: StateFlow<CanvasState> = _state.asStateFlow()
    
    private var autoSaveJob: Job? = null
    
    init {
        // Load all entries
        viewModelScope.launch {
            repository.allEntries.collect { entries ->
                _state.value = _state.value.copy(allEntries = entries)
            }
        }
    }
    
    fun loadEntry(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val entry = repository.getEntry(id)
            entry?.let {
                _state.value = _state.value.copy(
                    currentEntry = it,
                    strokes = it.strokes,
                    viewportOffset = Offset(it.viewportX, it.viewportY),
                    zoom = it.zoom,
                    isLoading = false
                )
            } ?: run {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
    
    fun createNewEntry(title: String = "Untitled") {
        viewModelScope.launch {
            val entry = repository.createEntry(title)
            _state.value = _state.value.copy(
                currentEntry = entry,
                strokes = emptyList(),
                viewportOffset = Offset.Zero,
                zoom = 1f
            )
        }
    }
    
    fun onStrokeStart(point: StrokePoint) {
        if (_state.value.eraserMode) {
            // Eraser mode: try to erase strokes near this point
            eraseStrokesNear(point)
        } else {
            _state.value = _state.value.copy(
                currentStroke = listOf(point)
            )
        }
    }
    
    fun onStrokeMove(point: StrokePoint) {
        if (_state.value.eraserMode) {
            // Continue erasing
            eraseStrokesNear(point)
        } else {
            _state.value = _state.value.copy(
                currentStroke = _state.value.currentStroke + point
            )
        }
    }
    
    fun onStrokeEnd() {
        if (_state.value.eraserMode) {
            // Nothing to do, erasing is done on move
            return
        }
        
        val currentStroke = _state.value.currentStroke
        if (currentStroke.size >= 2) {
            val newStroke = Stroke(
                points = currentStroke,
                color = _state.value.strokeColor,
                width = _state.value.strokeWidth
            )
            _state.value = _state.value.copy(
                strokes = _state.value.strokes + newStroke,
                currentStroke = emptyList()
            )
            triggerAutoSave()
        } else {
            _state.value = _state.value.copy(currentStroke = emptyList())
        }
    }
    
    private fun eraseStrokesNear(point: StrokePoint) {
        val eraseRadius = 30f // Pixels to consider for erasing
        val strokesToKeep = _state.value.strokes.filter { stroke ->
            // Check if any point in stroke is near the erase point
            !stroke.points.any { strokePoint ->
                val dx = strokePoint.x - point.x
                val dy = strokePoint.y - point.y
                sqrt(dx * dx + dy * dy) < eraseRadius
            }
        }
        
        if (strokesToKeep.size != _state.value.strokes.size) {
            _state.value = _state.value.copy(strokes = strokesToKeep)
            triggerAutoSave()
        }
    }
    
    fun onViewportChange(offset: Offset) {
        _state.value = _state.value.copy(viewportOffset = offset)
    }
    
    fun onZoomChange(zoom: Float) {
        _state.value = _state.value.copy(zoom = zoom)
    }
    
    fun cycleGridMode() {
        val nextMode = when (_state.value.gridMode) {
            GridMode.NONE -> GridMode.LINES
            GridMode.LINES -> GridMode.DOTS
            GridMode.DOTS -> GridMode.NONE
        }
        _state.value = _state.value.copy(gridMode = nextMode)
    }
    
    fun toggleSnapToGrid() {
        _state.value = _state.value.copy(snapToGrid = !_state.value.snapToGrid)
    }
    
    fun toggleEraserMode() {
        _state.value = _state.value.copy(eraserMode = !_state.value.eraserMode)
    }
    
    fun setStrokeColor(color: Long) {
        _state.value = _state.value.copy(strokeColor = color)
    }
    
    fun setStrokeWidth(width: Float) {
        _state.value = _state.value.copy(strokeWidth = width)
    }
    
    fun clearCanvas() {
        _state.value = _state.value.copy(strokes = emptyList())
        triggerAutoSave()
    }
    
    fun undoLastStroke() {
        val strokes = _state.value.strokes
        if (strokes.isNotEmpty()) {
            _state.value = _state.value.copy(strokes = strokes.dropLast(1))
            triggerAutoSave()
        }
    }
    
    fun deleteEntry(id: String) {
        viewModelScope.launch {
            repository.deleteEntry(id)
            if (_state.value.currentEntry?.id == id) {
                _state.value = _state.value.copy(
                    currentEntry = null,
                    strokes = emptyList()
                )
            }
        }
    }
    
    fun zoomIn() {
        val newZoom = (_state.value.zoom * 1.25f).coerceAtMost(5f)
        _state.value = _state.value.copy(zoom = newZoom)
    }
    
    fun zoomOut() {
        val newZoom = (_state.value.zoom / 1.25f).coerceAtLeast(0.1f)
        _state.value = _state.value.copy(zoom = newZoom)
    }
    
    private fun triggerAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(300) // Debounce 300ms
            saveCurrentEntry()
        }
    }
    
    private suspend fun saveCurrentEntry() {
        val entry = _state.value.currentEntry ?: return
        _state.value = _state.value.copy(isSaving = true)
        
        repository.saveEntry(
            entry.copy(
                strokes = _state.value.strokes,
                viewportX = _state.value.viewportOffset.x,
                viewportY = _state.value.viewportOffset.y,
                zoom = _state.value.zoom
            )
        )
        
        _state.value = _state.value.copy(isSaving = false)
    }
    
    fun updateTitle(title: String) {
        _state.value.currentEntry?.let { entry ->
            _state.value = _state.value.copy(
                currentEntry = entry.copy(title = title)
            )
            viewModelScope.launch {
                repository.updateTitle(entry.id, title)
            }
        }
    }
}
