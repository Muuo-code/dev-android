package com.example.swiftride.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.swiftride.data.AppDatabase
import com.example.swiftride.data.model.BookingEntity
import com.example.swiftride.data.model.CarEntity
import com.example.swiftride.data.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val appDatabase = AppDatabase.getDatabase(application)
    private val carDao = appDatabase.carDao()
    private val userDao = appDatabase.userDao()
    private val bookingDao = appDatabase.bookingDao()

    val allCars: LiveData<List<CarEntity>> = carDao.getAllCars()

    fun insertCar(car: CarEntity) {
        viewModelScope.launch {
            carDao.insertCar(car)
        }
    }

    fun updateCar(car: CarEntity) {
        viewModelScope.launch {
            carDao.updateCar(car)
        }
    }

    fun deleteCar(car: CarEntity) {
        viewModelScope.launch {
            carDao.deleteCar(car)
        }
    }

    private val _currentUser = MutableLiveData<UserEntity?>()
    val currentUser: LiveData<UserEntity?> get() = _currentUser

    fun loginOrRegisterUser(userId: String, email: String, displayName: String?, isAdminFlag: Boolean = false) {
        viewModelScope.launch {
            var user = userDao.getUserById(userId)
            if (user == null) {
                user = UserEntity(userId = userId, email = email, displayName = displayName, isAdmin = isAdminFlag)
                userDao.insertUser(user)
            } else if (isAdminFlag && !user.isAdmin) {
                user = user.copy(isAdmin = true)
                userDao.updateUser(user)
            }
            _currentUser.postValue(user)
        }
    }

    suspend fun isAdminUser(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(userId)?.isAdmin ?: false
        }
    }

    fun logoutUser() {
        _currentUser.value = null
        _selectedCar.value = null
        _pickupDate.value = null
        _returnDate.value = null
    }

    private val _selectedCar = MutableLiveData<CarEntity?>()
    val selectedCar: LiveData<CarEntity?> get() = _selectedCar

    private val _pickupDate = MutableLiveData<String?>()
    val pickupDate: LiveData<String?> get() = _pickupDate

    private val _returnDate = MutableLiveData<String?>()
    val returnDate: LiveData<String?> get() = _returnDate

    val allBookings: LiveData<List<BookingEntity>> = bookingDao.getAllBookings()

    fun getBookingsForUser(userId: String): LiveData<List<BookingEntity>> {
        return bookingDao.getBookingsForUser(userId)
    }

    fun selectCar(car: CarEntity) {
        _selectedCar.value = car
    }

    fun setPickupDate(date: String?) {
        _pickupDate.value = date
    }

    fun setReturnDate(date: String?) {
        _returnDate.value = date
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun createBooking() {
        val car = _selectedCar.value
        val user = _currentUser.value
        val pickupStr = _pickupDate.value
        val returnStr = _returnDate.value

        if (car != null && user != null && !pickupStr.isNullOrEmpty() && !returnStr.isNullOrEmpty()) {
            try {
                val pickupDate = dateFormat.parse(pickupStr)
                val returnDate = dateFormat.parse(returnStr)

                if (pickupDate != null && returnDate != null && returnDate.after(pickupDate)) {
                    val diffInMillis = returnDate.time - pickupDate.time
                    var days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
                    if (days == 0L) days = 1L

                    val totalPrice = car.pricePerDay * days

                    val newBooking = BookingEntity(
                        bookingUserId = user.userId,
                        bookingCarId = car.id,
                        pickupDate = pickupStr,
                        returnDate = returnStr,
                        totalPrice = totalPrice,
                        bookingStatus = "Pending"
                    )
                    viewModelScope.launch {
                        bookingDao.insertBooking(newBooking)
                        _selectedCar.postValue(null)
                        _pickupDate.postValue(null)
                        _returnDate.postValue(null)
                    }
                } else {
                    println("Error: Invalid date range for booking.")
                }
            } catch (e: Exception) {
                println("Error: Could not parse dates for booking: ${e.message}")
            }
        } else {
            println("Error: Missing data for booking creation.")
        }
    }

    fun updateBookingStatus(bookingId: Int, newStatus: String) {
        viewModelScope.launch {
            val booking = bookingDao.getBookingById(bookingId)
            if (booking != null) {
                val updatedBooking = booking.copy(bookingStatus = newStatus)
                bookingDao.updateBooking(updatedBooking)
            }
        }
    }
}
