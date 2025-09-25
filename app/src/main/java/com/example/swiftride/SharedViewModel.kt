package com.example.swiftride

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    // Selected car
    private val _selectedCar = MutableLiveData<Car?>()
    val selectedCar: LiveData<Car?> get() = _selectedCar

    // Pickup date
    private val _pickupDate = MutableLiveData<String?>()
    val pickupDate: LiveData<String?> get() = _pickupDate

    // Return date
    private val _returnDate = MutableLiveData<String?>()
    val returnDate: LiveData<String?> get() = _returnDate

    // Functions to update values
    fun selectCar(car: Car) {
        _selectedCar.value = car
    }

    fun setPickupDate(date: String) {
        _pickupDate.value = date
    }

    fun setReturnDate(date: String) {
        _returnDate.value = date
    }
}
