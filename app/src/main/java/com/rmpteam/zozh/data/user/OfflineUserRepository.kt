package com.rmpteam.zozh.data.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OfflineUserRepository(
    private val userDao: UserDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : UserRepository {
    // In-memory cache of the current logged-in user
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    private val coroutineScope = MainScope()
    
    init {
        // For debugging: Check if we need to create a test user
        createTestUserIfNeeded()
        
        // Restore user session if available
        coroutineScope.launch {
            restoreUserSession()
        }
    }
    
    // Debug function to create a test user for easier testing
    private fun createTestUserIfNeeded() {
        coroutineScope.launch {
            val testUser = userDao.getUserByCredentials("test", "test")
            if (testUser == null) {
                // Create a test user with a complete profile
                val newTestUser = UserProfile(
                    id = "test-user-id",
                    username = "test",
                    password = "test",
                    weight = 70f,
                    height = 175,
                    gender = Gender.MALE,
                    age = 30,
                    goal = WeightGoal.MAINTAIN_WEIGHT
                )
                userDao.insert(newTestUser)
            }
        }
    }
    
    private suspend fun restoreUserSession() {
        val savedUserId = userPreferencesRepository.currentUserId.first()
        if (savedUserId != null) {
            val user = userDao.getUserById(savedUserId).first()
            if (user != null) {
                _currentUser.value = user
            }
        }
    }
    
    override fun getUserById(id: String): Flow<UserProfile?> = userDao.getUserById(id)
    
    override fun getAllUsers(): Flow<List<UserProfile>> = userDao.getAllUsers()
    
    override suspend fun insertUser(userProfile: UserProfile): String {
        userDao.insert(userProfile)
        // If this is the currently logged-in user, update the flow
        if (_currentUser.value?.id == userProfile.id) {
            _currentUser.value = userProfile
        }
        return userProfile.id
    }
    
    override suspend fun updateUser(userProfile: UserProfile) {
        userDao.update(userProfile)
        // If this is the currently logged-in user, update the flow
        if (_currentUser.value?.id == userProfile.id) {
            _currentUser.value = userProfile
        }
    }
    
    override suspend fun deleteUser(userProfile: UserProfile) {
        userDao.delete(userProfile)
        // If this is the currently logged-in user, clear the flow
        if (_currentUser.value?.id == userProfile.id) {
            _currentUser.value = null
            userPreferencesRepository.saveCurrentUserId(null)
        }
    }
    
    override suspend fun login(username: String, password: String): Result<UserProfile> {
        val user = userDao.getUserByCredentials(username, password)
        return if (user != null) {
            setCurrentUser(user)
            userPreferencesRepository.saveCurrentUserId(user.id)
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid username or password"))
        }
    }
    
    override suspend fun register(username: String, password: String): Result<UserProfile> {
        if (userDao.isUsernameExists(username)) {
            return Result.failure(Exception("Username already exists"))
        }
        
        val newUser = UserProfile(
            id = UUID.randomUUID().toString(),
            username = username,
            password = password
        )
        insertUser(newUser)
        // After registering a new user, set them as the current user
        setCurrentUser(newUser)
        userPreferencesRepository.saveCurrentUserId(newUser.id)
        return Result.success(newUser)
    }
    
    override fun getCurrentUser(): Flow<UserProfile?> = _currentUser.asStateFlow()
    
    override suspend fun setCurrentUser(userProfile: UserProfile?) {
        _currentUser.value = userProfile
        userPreferencesRepository.saveCurrentUserId(userProfile?.id)
    }
    
    override suspend fun logout() {
        setCurrentUser(null)
        userPreferencesRepository.saveCurrentUserId(null)
    }
} 