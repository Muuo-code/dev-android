package com.example.swiftride

import android.app.Application
import androidx.lifecycle.*
import com.example.swiftride.com.example.swiftride.data.AppDatabase
import com.example.swiftride.com.example.swiftride.data.CarEntity
import com.example.swiftride.com.example.swiftride.data.UserEntity // Added
import com.example.swiftride.com.example.swiftride.data.BookingEntity // Added
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit // For price calculation

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    // --- Database setup ---
    private val appDatabase = AppDatabase.getDatabase(application)
    private val carDao = appDatabase.carDao()
    private val userDao = appDatabase.userDao()       // Added
    private val bookingDao = appDatabase.bookingDao() // Added

    // --- Car Data ---
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

    // --- User Management ---
    private val _currentUser = MutableLiveData<UserEntity?>()
    val currentUser: LiveData<UserEntity?> get() = _currentUser

    // Call this after successful Firebase login
    fun loginOrRegisterUser(userId: String, email: String, displayName: String?, isAdminFlag: Boolean = false) {
        viewModelScope.launch {
            var user = userDao.getUserById(userId)
            if (user == null) {
                user = UserEntity(userId = userId, email = email, displayName = displayName, isAdmin = isAdminFlag)
                userDao.insertUser(user)
            }
            // If you want to ensure the isAdminFlag from call overrides db, uncomment below
            // else if (isAdminFlag && !user.isAdmin) {
            //    user = user.copy(isAdmin = true)
            //    userDao.updateUser(user) // Assuming you'll add updateUser in UserDao
            // }
            _currentUser.postValue(user)
            // Consider loading user-specific data here if not handled by observers elsewhere
        }
    }

    suspend fun isAdminUser(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(userId)?.isAdmin ?: false
        }
    }

    fun logoutUser() {
        _currentUser.value = null
        // Clear any other user-specific LiveData if necessary
    }

    // --- Booking State & Management ---
    private val _selectedCar = MutableLiveData<CarEntity?>()
    val selectedCar: LiveData<CarEntity?> get() = _selectedCar

    private val _pickupDate = MutableLiveData<String?>() // Dates as String for now
    val pickupDate: LiveData<String?> get() = _pickupDate

    private val _returnDate = MutableLiveData<String?>()
    val returnDate: LiveData<String?> get() = _returnDate

    val allBookings: LiveData<List<BookingEntity>> = bookingDao.getAllBookings() // For Admin

    // To get bookings for a specific user, observe this from the UI after user logs in:
    // fun getBookingsForUser(userId: String): LiveData<List<BookingEntity>> {
    //     return bookingDao.getBookingsForUser(userId)
    // }

    fun selectCar(car: CarEntity) {
        _selectedCar.value = car
    }

    fun setPickupDate(date: String) {
        _pickupDate.value = date
    }

    fun setReturnDate(date: String) {
        _returnDate.value = date
    }

    fun createBooking() {
        val car = _selectedCar.value
        val user = _currentUser.value
        val pickup = _pickupDate.value
        val returns = _returnDate.value

        if (car != null && user != null && pickup != null && returns != null) {
            // Basic price calculation. TODO: Implement robust date parsing and difference calculation.
            val days = 7L // Placeholder for actual day calculation
            val totalPrice = car.pricePerDay * days.coerceAtLeast(1)

            val newBooking = BookingEntity(
                bookingUserId = user.userId,
                bookingCarId = car.id,
                pickupDate = pickup,
                returnDate = returns,
                totalPrice = totalPrice,
                bookingStatus = "Confirmed"
            )
            viewModelScope.launch {
                bookingDao.insertBooking(newBooking)
                _selectedCar.postValue(null)
                _pickupDate.postValue(null)
                _returnDate.postValue(null)
            }
        } else {
            // TODO: Handle error: missing data for booking (e.g., show a Toast)
        }
    }

    fun updateBookingStatus(booking: BookingEntity, newStatus: String) {
        viewModelScope.launch {
            val updatedBooking = booking.copy(bookingStatus = newStatus)
            // bookingDao.updateBooking(updatedBooking) // Assuming updateBooking exists in BookingDao
        }
    }
}
