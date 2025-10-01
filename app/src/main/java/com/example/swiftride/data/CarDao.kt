package com.example.swiftride.com.example.swiftride.data

import androidx.lifecycle.LiveData // Added import
import androidx.room.*


@Dao
interface CarDao {
    @Query("SELECT * FROM cars ORDER BY id DESC")
    fun getAllCars(): LiveData<List<CarEntity>> // Changed this line

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarEntity)

    @Update
    suspend fun updateCar(car: CarEntity)

    @Delete
    suspend fun deleteCar(car: CarEntity)
}