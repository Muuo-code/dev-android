package com.example.swiftride.com.example.swiftride.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["bookingUserId"],
            onDelete = ForeignKey.CASCADE // If a user is deleted, their bookings are also deleted
        ),
        ForeignKey(
            entity = CarEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookingCarId"],
            onDelete = ForeignKey.CASCADE // If a car is deleted, its bookings are also deleted
        )
    ],
    indices = [Index("bookingUserId"), Index("bookingCarId")]
)
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val bookingId: Int = 0,
    val bookingUserId: String, // Foreign key for UserEntity
    val bookingCarId: Int,    // Foreign key for CarEntity
    val pickupDate: String,   // Consider using a more robust date type like Long (timestamp) or Date converters
    val returnDate: String,   // Same as above
    val totalPrice: Double,
    val bookingStatus: String = "Confirmed" // e.g., Confirmed, Cancelled, Completed
)