package com.rmpteam.zozh.data.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID
import android.util.Log

class OfflineUserRepository(
    private val fakeUserProfileDatasource: FakeUserProfileDatasource,
    private val userPreferencesRepository: UserPreferencesRepository
) : UserRepository {

    override fun getCurrentUser(): Flow<UserProfile?> = userPreferencesRepository.currentUserProfile

    override suspend fun setCurrentUser(userProfile: UserProfile?) {
        userPreferencesRepository.saveCurrentUserProfile(userProfile)
    }
    
    override fun getUserById(id: String): Flow<UserProfile?> {
        return kotlinx.coroutines.flow.flowOf(fakeUserProfileDatasource.findUserById(id))
    }

    override fun getAllUsers(): Flow<List<UserProfile>> {
        return kotlinx.coroutines.flow.flowOf(emptyList())
    }

    override suspend fun insertUser(userProfile: UserProfile): String {
        val result = fakeUserProfileDatasource.addUser(userProfile)
        return result.fold(
            onSuccess = { it.id },
            onFailure = { throw it }
        )
    }

    override suspend fun updateUser(userProfile: UserProfile) {
        val currentUser = getCurrentUser().firstOrNull()
        if (currentUser?.id == userProfile.id) {
            setCurrentUser(userProfile)
        } else {
             //  Handle case where the update is for a user NOT currently logged in
             Log.w("OfflineUserRepository", "updateUser called for a user ID (${userProfile.id}) that does not match the current user ID (${currentUser?.id}) in DataStore.")
        }
    }

    override suspend fun deleteUser(userProfile: UserProfile) {
        val currentUser = getCurrentUser().firstOrNull()
        if (currentUser?.id == userProfile.id) {
            logout()
        }
    }

    override suspend fun login(username: String, password: String): Result<UserProfile> {
        val user = fakeUserProfileDatasource.findUserByUsername(username)
        return if (user != null && user.password == password) {
            setCurrentUser(user)
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid username or password"))
        }
    }

    override suspend fun register(userToRegister: UserProfile): Result<UserProfile> {
         val result = fakeUserProfileDatasource.addUser(userToRegister)
         return result.fold(
             onSuccess = { newUser ->
                 setCurrentUser(newUser)
                 Result.success(newUser)
             },
             onFailure = {
                 Result.failure(it)
             }
         )
    }
    
    override suspend fun logout() {
        userPreferencesRepository.clearCurrentUserProfile()
    }
} 