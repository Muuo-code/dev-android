package com.example.swiftride.com.example.swiftride.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    // You might want a LiveData version for observing a user, if needed elsewhere
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserByIdLiveData(userId: String): LiveData<UserEntity?>
}