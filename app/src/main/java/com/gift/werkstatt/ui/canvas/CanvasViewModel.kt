package com.gift.werkstatt.ui.canvas

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gift.werkstatt.data.models.BrushDefaults
import com.gift.werkstatt.data.models.BrushType
import com.gift.werkstatt.data.models.CanvasEntry
import com.gift.werkstatt.data.models.CanvasImage
import com.gift.werkstatt.data.models.Stroke
import com.gift.werkstatt.data.models.StrokePoint
import com.gift.werkstatt.data.repository.CanvasRepository
import com.gift.werkstatt.ui.theme.BauhausBlack
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sqrt

// CANVAS_WIDTH and CANVAS_HEIGHT are defined in FixedCanvas.kt

data class CanvasState(
    val currentEntry: CanvasEntry? = null,
    val strokes: List<Stroke> = emptyList(),
    val images: List<CanvasImage> = emptyList(),
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
    val allEntries: List<CanvasEntry> = emptyList(),
    val brushType: BrushType = BrushType.PEN,
    val brushOpacity: Float = 1f,
    val recentColors: List<Long> = emptyList(),
    val editingImageId: String? = null
)

class CanvasViewModel(
    private val repository: CanvasRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CanvasState())
    val state: StateFlow<CanvasState> = _state.asStateFlow()
    
    private var autoSaveJob: Job? = null
    private var hasUnsavedChanges = false
    
    init {
        // Load all entries
        viewModelScope.launch {
            repository.allEntries.collect { entries ->
                _state.value = _state.value.copy(allEntries = entries)
            }
        }

        // Start auto-save with proper cancellation support
        startAutoSave()
    }

    private fun startAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            while (isActive) { // Check if coroutine is still active
                delay(120_000) // 120 seconds
                if (hasUnsavedChanges && isActive) {
                    saveCurrentEntry()
                    hasUnsavedChanges = false
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoSaveJob?.cancel()
        autoSaveJob = null
    }
    
    fun loadEntry(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val entry = repository.getEntry(id)
            entry?.let {
                _state.value = _state.value.copy(
                    currentEntry = it,
                    strokes = it.strokes,
                    images = it.images,
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
                images = emptyList(),
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
                width = _state.value.strokeWidth,
                brushType = _state.value.brushType,
                opacity = _state.value.brushOpacity
            )
            _state.value = _state.value.copy(
                strokes = _state.value.strokes + newStroke,
                currentStroke = emptyList()
            )
            markUnsaved()
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
            markUnsaved()
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

    fun setBrushType(type: BrushType) {
        val defaults = BrushDefaults.forType(type)
        _state.value = _state.value.copy(
            brushType = type,
            brushOpacity = defaults.opacity
        )
    }

    fun setBrushSize(size: Float) {
        _state.value = _state.value.copy(strokeWidth = size)
    }

    fun setColor(color: Long) {
        val recentColors = _state.value.recentColors.toMutableList()
        recentColors.remove(color) // Remove if already exists
        recentColors.add(0, color) // Add to front
        if (recentColors.size > 8) {
            recentColors.removeLast()
        }
        _state.value = _state.value.copy(
            strokeColor = color,
            recentColors = recentColors
        )
    }

    fun enterImageEditMode(imageId: String) {
        _state.value = _state.value.copy(editingImageId = imageId)
    }

    fun exitImageEditMode() {
        _state.value = _state.value.copy(editingImageId = null)
    }
    
    fun clearCanvas() {
        _state.value = _state.value.copy(strokes = emptyList())
        markUnsaved()
    }
    
    fun undoLastStroke() {
        val strokes = _state.value.strokes
        if (strokes.isNotEmpty()) {
            _state.value = _state.value.copy(strokes = strokes.dropLast(1))
            markUnsaved()
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
    
    fun addImage(filePath: String, width: Float, height: Float) {
        // Center image on fixed canvas
        val centerX = CANVAS_WIDTH / 2 - width / 2
        val centerY = CANVAS_HEIGHT / 2 - height / 2
        val newImage = CanvasImage(
            filePath = filePath,
            x = centerX,
            y = centerY,
            width = width,
            height = height
        )
        _state.value = _state.value.copy(
            images = _state.value.images + newImage
        )
        markUnsaved()
    }
    
    fun updateImagePosition(imageId: String, deltaX: Float, deltaY: Float) {
        val updatedImages = _state.value.images.map { img ->
            if (img.id == imageId) {
                img.copy(
                    x = img.x + deltaX,
                    y = img.y + deltaY
                )
            } else {
                img
            }
        }
        _state.value = _state.value.copy(images = updatedImages)
        markUnsaved()
    }
    
    fun deleteImage(imageId: String) {
        _state.value = _state.value.copy(
            images = _state.value.images.filter { it.id != imageId }
        )
        markUnsaved()
    }
    
    fun resizeImage(imageId: String, deltaWidth: Float, deltaHeight: Float) {
        val updatedImages = _state.value.images.map { img ->
            if (img.id == imageId) {
                val newWidth = (img.width + deltaWidth).coerceAtLeast(50f)
                val newHeight = (img.height + deltaHeight).coerceAtLeast(50f)
                img.copy(
                    width = newWidth,
                    height = newHeight
                )
            } else {
                img
            }
        }
        _state.value = _state.value.copy(images = updatedImages)
        markUnsaved()
    }
    
    private fun markUnsaved() {
        hasUnsavedChanges = true
    }
    
    // Call when leaving canvas to save immediately
    fun saveNow() {
        if (hasUnsavedChanges) {
            viewModelScope.launch {
                saveCurrentEntry()
                hasUnsavedChanges = false
            }
        }
    }
    
    private suspend fun saveCurrentEntry() {
        val entry = _state.value.currentEntry ?: return
        _state.value = _state.value.copy(isSaving = true)
        
        repository.saveEntry(
            entry.copy(
                strokes = _state.value.strokes,
                images = _state.value.images,
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
