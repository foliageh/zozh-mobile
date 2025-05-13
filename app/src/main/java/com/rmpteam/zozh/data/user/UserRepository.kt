package com.rmpteam.zozh.data.user

interface UserRepository {
    suspend fun findUserByUsername(username: String): Result<UserProfile>
    suspend fun insertUser(userProfile: UserProfile): Result<UserProfile>
    suspend fun updateUser(userProfile: UserProfile): Result<UserProfile>
}