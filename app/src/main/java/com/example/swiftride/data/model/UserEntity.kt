package com.example.swiftride.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String, // To align with Firebase UID
    val email: String,
    val displayName: String?,
    val isAdmin: Boolean = false
)