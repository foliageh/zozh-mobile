package com.rmpteam.zozh.data.user

class FakeUserProfileDatasource {
    private val userList = mutableListOf<UserProfile>()

    fun addUser(userProfile: UserProfile): Result<UserProfile> {
        if (userList.any { it.username == userProfile.username })
            return Result.failure(Exception("User with username '${userProfile.username}' already exists."))
        userList.add(userProfile)
        return Result.success(userProfile)
    }

    fun findUserByUsername(username: String): Result<UserProfile> {
        return userList.find { it.username == username }?.let { Result.success(it) }
            ?: Result.failure(Exception("User not found."))
    }

    fun updateUser(userProfile: UserProfile): Result<UserProfile> {
        return userList.find { it.username == userProfile.username }?.let {
            userList[userList.indexOf(it)] = userProfile
            Result.success(userProfile)
        } ?: Result.failure(Exception("User not found."))
    }
}