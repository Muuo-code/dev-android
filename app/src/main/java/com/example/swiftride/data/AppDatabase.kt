package com.example.swiftride.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.swiftride.data.dao.BookingDao
import com.example.swiftride.data.dao.CarDao
import com.example.swiftride.data.dao.UserDao
import com.example.swiftride.data.model.BookingEntity
import com.example.swiftride.data.model.CarEntity
import com.example.swiftride.data.model.UserEntity

@Database(
    entities = [CarEntity::class, UserEntity::class, BookingEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carDao(): CarDao
    abstract fun userDao(): UserDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "swift_ride_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
