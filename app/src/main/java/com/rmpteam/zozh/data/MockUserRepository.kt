package com.rmpteam.zozh.data

import java.util.UUID

class MockUserRepository {
    private val users = mutableMapOf<String, UserProfile>()
    private var currentUser: UserProfile? = null

    fun register(username: String, password: String): Result<UserProfile> {
        if (users.values.any { it.username == username }) {
            return Result.failure(Exception("Username already exists"))
        }

        val newUser = UserProfile(
            id = UUID.randomUUID().toString(),
            username = username,
            password = password
        )
        users[newUser.id] = newUser
        return Result.success(newUser)
    }

    fun login(username: String, password: String): Result<UserProfile> {
        val user = users.values.find { it.username == username && it.password == password }
        return if (user != null) {
            currentUser = user
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid username or password"))
        }
    }

    fun updateProfile(profile: UserProfile): Result<UserProfile> {
        users[profile.id] = profile
        if (currentUser?.id == profile.id) {
            currentUser = profile
        }
        return Result.success(profile)
    }

    fun getCurrentUser(): UserProfile? = currentUser

    fun logout() {
        currentUser = null
    }
} 