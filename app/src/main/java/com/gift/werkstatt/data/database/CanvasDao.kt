package com.gift.werkstatt.data.database

import androidx.room.*
import com.gift.werkstatt.data.models.CanvasEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface CanvasDao {
    @Query("SELECT * FROM canvas_entries ORDER BY updatedAt DESC")
    fun getAllEntries(): Flow<List<CanvasEntry>>
    
    @Query("SELECT * FROM canvas_entries WHERE id = :id")
    suspend fun getEntry(id: String): CanvasEntry?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: CanvasEntry)
    
    @Update
    suspend fun updateEntry(entry: CanvasEntry)
    
    @Delete
    suspend fun deleteEntry(entry: CanvasEntry)
    
    @Query("DELETE FROM canvas_entries WHERE id = :id")
    suspend fun deleteById(id: String)
}
