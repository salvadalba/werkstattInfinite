package com.gift.werkstatt.data.repository

import com.gift.werkstatt.data.database.CanvasDao
import com.gift.werkstatt.data.models.CanvasEntry
import com.gift.werkstatt.data.models.Stroke
import kotlinx.coroutines.flow.Flow

class CanvasRepository(private val canvasDao: CanvasDao) {
    
    val allEntries: Flow<List<CanvasEntry>> = canvasDao.getAllEntries()
    
    suspend fun getEntry(id: String): CanvasEntry? {
        return canvasDao.getEntry(id)
    }
    
    suspend fun createEntry(title: String = "Untitled"): CanvasEntry {
        val entry = CanvasEntry(title = title)
        canvasDao.insertEntry(entry)
        return entry
    }
    
    suspend fun saveEntry(entry: CanvasEntry) {
        canvasDao.insertEntry(entry.copy(updatedAt = System.currentTimeMillis()))
    }
    
    suspend fun updateStrokes(entryId: String, strokes: List<Stroke>) {
        val entry = canvasDao.getEntry(entryId)
        entry?.let {
            canvasDao.updateEntry(it.copy(
                strokes = strokes,
                updatedAt = System.currentTimeMillis()
            ))
        }
    }
    
    suspend fun deleteEntry(id: String) {
        canvasDao.deleteById(id)
    }
    
    suspend fun updateTitle(id: String, title: String) {
        val entry = canvasDao.getEntry(id)
        entry?.let {
            canvasDao.updateEntry(it.copy(
                title = title,
                updatedAt = System.currentTimeMillis()
            ))
        }
    }
}
