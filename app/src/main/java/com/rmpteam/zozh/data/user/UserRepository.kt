package com.rmpteam.zozh.data.user

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserById(id: String): Flow<UserProfile?>
    fun getAllUsers(): Flow<List<UserProfile>>
    suspend fun insertUser(userProfile: UserProfile): String
    suspend fun updateUser(userProfile: UserProfile)
    suspend fun deleteUser(userProfile: UserProfile)
    suspend fun login(username: String, password: String): Result<UserProfile>
    suspend fun register(userProfile: UserProfile): Result<UserProfile>
    fun getCurrentUser(): Flow<UserProfile?>
    suspend fun setCurrentUser(userProfile: UserProfile?)
    suspend fun logout()
} 