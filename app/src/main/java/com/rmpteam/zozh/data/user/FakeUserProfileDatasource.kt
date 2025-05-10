package com.rmpteam.zozh.data.user

import kotlinx.serialization.json.Json
import java.util.UUID

object FakeUserProfileDatasource {
    private val users = mutableListOf<UserProfile>()

    // For initial testing, add a predefined user
    init {
        // users.add(UserProfile(id = UUID.randomUUID().toString(), username = "test", password = "test"))
    }

    fun addUser(user: UserProfile): Result<UserProfile> {
        if (users.any { it.username == user.username }) {
            return Result.failure(Exception("User with username '${user.username}' already exists."))
        }
        val newUser = user.copy(id = UUID.randomUUID().toString()) // Ensure unique ID
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
        val userIndex = users.indexOfFirst { it.id == updatedProfile.id }
        return if (userIndex != -1) {
            users[userIndex] = updatedProfile
            Result.success(updatedProfile)
        } else {
            Result.failure(Exception("User not found for update."))
        }
    }
} 