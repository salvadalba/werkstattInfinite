package com.gift.werkstatt.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Represents a single point in a stroke
 */
data class StrokePoint(
    val x: Float,
    val y: Float,
    val pressure: Float = 1f
)

/**
 * Represents a drawing stroke on the canvas
 */
data class Stroke(
    val id: String = java.util.UUID.randomUUID().toString(),
    val points: List<StrokePoint> = emptyList(),
    val color: Long = 0xFF1A1A1A,
    val width: Float = 4f,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Represents a canvas/journal entry
 */
@Entity(tableName = "canvas_entries")
@TypeConverters(CanvasConverters::class)
data class CanvasEntry(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "Untitled",
    val strokes: List<Stroke> = emptyList(),
    val viewportX: Float = 0f,
    val viewportY: Float = 0f,
    val zoom: Float = 1f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Type converters for Room database
 */
class CanvasConverters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromStrokeList(strokes: List<Stroke>): String {
        return gson.toJson(strokes)
    }
    
    @TypeConverter
    fun toStrokeList(json: String): List<Stroke> {
        val type = object : TypeToken<List<Stroke>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
