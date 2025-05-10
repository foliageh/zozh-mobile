package com.rmpteam.zozh.data.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = :id")
    fun getUserById(id: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile")
    fun getAllUsers(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profile WHERE username = :username AND password = :password LIMIT 1")
    suspend fun getUserByCredentials(username: String, password: String): UserProfile?

    @Query("SELECT EXISTS(SELECT * FROM user_profile WHERE username = :username)")
    suspend fun isUsernameExists(username: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProfile: UserProfile): Long

    @Update
    suspend fun update(userProfile: UserProfile)

    @Delete
    suspend fun delete(userProfile: UserProfile)
} 