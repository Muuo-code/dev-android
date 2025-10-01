package com.example.swiftride.data

import androidx.lifecycle.LiveData
import com.example.swiftride.data.dao.CarDao
import com.example.swiftride.data.model.CarEntity

class CarRepository(private val carDao: CarDao) {

    fun getAllCars(): LiveData<List<CarEntity>> = carDao.getAllCars()

    suspend fun addCar(car: CarEntity) = carDao.insertCar(car)

    suspend fun updateCar(car: CarEntity) = carDao.updateCar(car)

    suspend fun deleteCar(car: CarEntity) = carDao.deleteCar(car)

}
