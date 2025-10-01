package com.example.swiftride.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class CarEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val brand: String,
    val model: String,
    val pricePerDay: Double,
    val seats: Int,
    val transmission: String, //Either Automatic, Hybrid or Manual
    val imageResId: Int
)