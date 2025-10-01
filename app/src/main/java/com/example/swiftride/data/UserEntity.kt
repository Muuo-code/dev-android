package com.example.swiftride.com.example.swiftride.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String, // To align with Firebase UID
    val email: String,
    val displayName: String?,
    val isAdmin: Boolean = false
)