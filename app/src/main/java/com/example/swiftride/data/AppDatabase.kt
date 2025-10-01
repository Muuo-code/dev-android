// AppDatabase.kt
package com.example.swiftride.com.example.swiftride.data // Assuming this is your data package

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
// Remove 'com.example.swiftride.Car' if CarEntity is used everywhere
// import com.example.swiftride.Car // This seems to be an old or incorrect entity reference

@Database(
    entities = [CarEntity::class, UserEntity::class, BookingEntity::class], // Added UserEntity and BookingEntity
    version = 2, // Incremented version
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carDao(): CarDao
    abstract fun userDao(): UserDao       // Added UserDao
    abstract fun bookingDao(): BookingDao // Added BookingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "car_database" // You might want to rename this if it's no longer just for cars
                )
                .fallbackToDestructiveMigration() // Added for schema changes during development
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
