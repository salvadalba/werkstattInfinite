package com.gift.werkstatt.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gift.werkstatt.data.models.CanvasConverters
import com.gift.werkstatt.data.models.CanvasEntry

@Database(
    entities = [CanvasEntry::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(CanvasConverters::class)
abstract class CanvasDatabase : RoomDatabase() {
    abstract fun canvasDao(): CanvasDao

    companion object {
        @Volatile
        private var INSTANCE: CanvasDatabase? = null

        // Migration placeholder for future schema changes
        // Example: Adding a new column in version 2
        // private val MIGRATION_1_2 = object : Migration(1, 2) {
        //     override fun migrate(database: SupportSQLiteDatabase) {
        //         database.execSQL("ALTER TABLE canvas_entries ADD COLUMN new_field TEXT DEFAULT ''")
        //     }
        // }

        fun getDatabase(context: Context): CanvasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CanvasDatabase::class.java,
                    "werkstatt_database"
                )
                    // Add migrations here when schema changes:
                    // .addMigrations(MIGRATION_1_2)
                    // Fallback for development only - remove before production release
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
