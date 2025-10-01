package com.example.swiftride.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.swiftride.data.model.BookingEntity

@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Query("SELECT * FROM bookings ORDER BY pickupDate DESC")
    fun getAllBookings(): LiveData<List<BookingEntity>> // For admin to view all requests

    @Query("SELECT * FROM bookings WHERE bookingUserId = :userId ORDER BY pickupDate DESC")
    fun getBookingsForUser(userId: String): LiveData<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId")
    suspend fun getBookingById(bookingId: Int): BookingEntity?
}
