package com.example.swiftride.com.example.swiftride.data

class CarRepository(private val carDao: CarDao) {

    suspend fun getAllCars(): List<CarEntity> = carDao.getAllCars()

    suspend fun addCar(car: CarEntity) = carDao.insertCar(car)

    suspend fun updateCar(car: CarEntity) = carDao.updateCar(car)

    suspend fun deleteCar(car: CarEntity) = carDao.deleteCar(car)

}