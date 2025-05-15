package com.rmpteam.zozh.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.user.UserProfile
import com.rmpteam.zozh.data.user.UserRepository
import com.rmpteam.zozh.util.ValidationUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay

class SettingsViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { user ->
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            currentUser = user,
                            weight = user.weight?.toString() ?: "",
                            height = user.height?.toString() ?: "",
                            age = user.age?.toString() ?: "",
                            selectedGender = user.gender,
                            selectedGoal = user.goal,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "User not found") }
                }
            }
        }
    }

    fun updateWeight(weight: String) {
        _uiState.update { it.copy(weight = weight, errorMessage = null) }
    }

    fun updateHeight(height: String) {
        _uiState.update { it.copy(height = height, errorMessage = null) }
    }

    fun updateAge(age: String) {
        _uiState.update { it.copy(age = age, errorMessage = null) }
    }

    fun updateSelectedGender(gender: com.rmpteam.zozh.data.user.Gender) {
        _uiState.update { it.copy(selectedGender = gender, errorMessage = null) }
    }

    fun updateSelectedGoal(goal: com.rmpteam.zozh.data.user.WeightGoal) {
        _uiState.update { it.copy(selectedGoal = goal, errorMessage = null) }
    }

    fun saveProfile(onSuccess: () -> Unit) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val currentState = _uiState.value
        val user = currentState.currentUser

        if (user == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Cannot save: User data not loaded.") }
            return
        }

        val validationResult = ValidationUtil.validateProfileData(
            weightStr = currentState.weight,
            heightStr = currentState.height,
            ageStr = currentState.age,
            gender = currentState.selectedGender,
            goal = currentState.selectedGoal
        )

        when (validationResult) {
            is ValidationUtil.ValidationResult.Error -> {
                Log.e("SettingsViewModel", "Validation Error: ${validationResult.message}")
                _uiState.update { it.copy(errorMessage = validationResult.message, isLoading = false) }
                return
            }
            ValidationUtil.ValidationResult.Success -> {
                // Proceed if validation is successful
            }
        }

        val weightFloat = currentState.weight.toFloatOrNull()
        val heightInt = currentState.height.toIntOrNull()
        val ageInt = currentState.age.toIntOrNull()
        val gender = currentState.selectedGender
        val goal = currentState.selectedGoal

        if (weightFloat == null || heightInt == null || ageInt == null || gender == null || goal == null) {
            Log.e("SettingsViewModel", "Post-validation parsing/nullability check failed. Weight: ${currentState.weight}, Height: ${currentState.height}, Age: ${currentState.age}, Gender: $gender, Goal: $goal")
            _uiState.update { it.copy(errorMessage = "Invalid data format. Please check all fields.", isLoading = false) }
            return
        }
        
        val updatedProfile = user.copy(
            weight = weightFloat,
            height = heightInt,
            age = ageInt,
            gender = gender, 
            goal = goal
        )

        Log.d("SettingsViewModel", "saveProfile: Starting coroutine to update user.")
        viewModelScope.launch {
            var operationSuccessful = false
            Log.d("SettingsViewModel", "saveProfile coroutine: Entered try block.")
            try {
                Log.d("SettingsViewModel", "saveProfile coroutine: Calling userRepository.updateUser...")
                userRepository.updateUser(updatedProfile)
                Log.d("SettingsViewModel", "saveProfile coroutine: userRepository.updateUser completed.")
                operationSuccessful = true
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "saveProfile coroutine: Entered catch block.", e)
                if (e !is kotlinx.coroutines.CancellationException) {
                    Log.e("SettingsViewModel", "Error updating user profile", e)
                    _uiState.update { it.copy(errorMessage = "Ошибка сохранения: ${e.localizedMessage ?: "Неизвестная ошибка"}") }
                }
            } finally {
                Log.d("SettingsViewModel", "saveProfile coroutine: Entered finally block. Coroutine active: ${currentCoroutineContext().isActive}")
                if (currentCoroutineContext().isActive) { 
                    if (operationSuccessful) {
                        Log.d("SettingsViewModel", "saveProfile coroutine (finally): Operation successful. Setting isLoading=false and preparing to call onSuccess.")
                        _uiState.update { it.copy(isLoading = false) }
                        // Give the UI a moment to process the isLoading=false update before navigating
                        delay(50) // 50ms delay, can be adjusted
                        Log.d("SettingsViewModel", "saveProfile coroutine (finally): Calling onSuccess after delay.")
                        onSuccess()
                    } else {
                        Log.d("SettingsViewModel", "saveProfile coroutine (finally): Operation NOT successful. Setting isLoading=false.")
                        _uiState.update { it.copy(isLoading = false) }
                    }
                } else {
                    Log.d("SettingsViewModel", "saveProfile coroutine (finally): Coroutine was cancelled. Setting isLoading=false.")
                    _uiState.update { it.copy(isLoading = false) }
                }
                Log.d("SettingsViewModel", "saveProfile coroutine (finally): Exiting finally block. isLoading should be false.")
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout()
            onSuccess()
        }
    }
} 