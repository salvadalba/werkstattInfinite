package com.gift.werkstatt.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gift.werkstatt.data.models.CanvasConverters
import com.gift.werkstatt.data.models.CanvasEntry

@Database(
    entities = [CanvasEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(CanvasConverters::class)
abstract class CanvasDatabase : RoomDatabase() {
    abstract fun canvasDao(): CanvasDao
    
    companion object {
        @Volatile
        private var INSTANCE: CanvasDatabase? = null
        
        fun getDatabase(context: Context): CanvasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CanvasDatabase::class.java,
                    "werkstatt_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
