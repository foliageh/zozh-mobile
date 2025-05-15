package com.rmpteam.zozh.data.user

import kotlinx.serialization.json.Json
import java.util.UUID
import android.util.Log

object FakeUserProfileDatasource {
    private val users = mutableListOf<UserProfile>()

    fun addUser(user: UserProfile): Result<UserProfile> {
        if (users.any { it.username == user.username }) {
            return Result.failure(Exception("User with username '${user.username}' already exists."))
        }
        val newUser = user.copy(id = UUID.randomUUID().toString())
        users.add(newUser)
        return Result.success(newUser)
    }

    fun findUserByUsername(username: String): UserProfile? {
        return users.find { it.username == username }
    }

    fun findUserById(id: String): UserProfile? {
        return users.find { it.id == id }
    }

    fun updateUser(updatedProfile: UserProfile): Result<UserProfile> {
        Log.d("FakeUserProfileDatasource", "updateUser called. Target ID: ${updatedProfile.id}")
        Log.d("FakeUserProfileDatasource", "Current users list: ${users.joinToString { it.id }}")
        
        val userIndex = users.indexOfFirst { it.id == updatedProfile.id }
        return if (userIndex != -1) {
            Log.d("FakeUserProfileDatasource", "User found at index $userIndex. Updating.")
            users[userIndex] = updatedProfile
            Result.success(updatedProfile)
        } else {
            Log.e("FakeUserProfileDatasource", "User with ID ${updatedProfile.id} not found in list!")
            Result.failure(Exception("User not found for update."))
        }
    }
} 