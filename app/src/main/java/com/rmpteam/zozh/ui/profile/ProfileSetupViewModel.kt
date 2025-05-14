package com.rmpteam.zozh.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.user.Gender
import com.rmpteam.zozh.data.user.UserPreferencesRepository
import com.rmpteam.zozh.data.user.UserProfile
import com.rmpteam.zozh.data.user.UserRepository
import com.rmpteam.zozh.data.user.WeightGoal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileSetupUiState(
    val currentUser: UserProfile? = null,
    val weight: String = "",
    val height: String = "",
    val age: String = "",
    val selectedGender: Gender? = null,
    val selectedGoal: WeightGoal? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = true
)

class ProfileSetupViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { user ->
                _uiState.update {
                    it.copy(
                        currentUser = user,
                        weight = user?.weight?.toString() ?: "",
                        height = user?.height?.toString() ?: "",
                        age = user?.age?.toString() ?: "",
                        selectedGender = user?.gender,
                        selectedGoal = user?.goal,
                        isLoading = false
                    )
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
        val currentState = _uiState.value
        
        // Ensure the user is loaded before attempting to save - Keep this initial check
        val initialUser = currentState.currentUser
        if (initialUser == null) {
             _uiState.update { it.copy(errorMessage = "Ошибка: Данные пользователя еще не загружены. Попробуйте еще раз.") }
             return // Don't proceed if user is null initially
        }
        
        // Validate based on current UI input
        when (val validationResult = ValidationUtil.validateProfileData(
            weightStr = currentState.weight,
            heightStr = currentState.height,
            ageStr = currentState.age,
            gender = currentState.selectedGender,
            goal = currentState.selectedGoal
        )) {
            is ValidationUtil.ValidationResult.Error -> {
                _uiState.update { it.copy(errorMessage = validationResult.message) }
                return
            }
            ValidationUtil.ValidationResult.Success -> {
                // Proceed if validation is successful
            }
        }

        viewModelScope.launch {
            // Re-fetch the user from the repository right before updating
            // This ensures we have the ID consistent with the persistent source
            val userToUpdate = userRepository.getCurrentUser().firstOrNull()
            
            if (userToUpdate == null) {
                 _uiState.update { it.copy(errorMessage = "Ошибка: Не удалось получить данные пользователя для обновления.") }
                 return@launch
            }

            val weightFloat = currentState.weight.toFloat()
            val heightInt = currentState.height.toInt()
            val ageInt = currentState.age.toInt()

            val updatedProfile = userToUpdate.copy( // Use the freshly fetched user as the base for copy
                weight = weightFloat,
                height = heightInt,
                age = ageInt,
                gender = currentState.selectedGender,
                goal = currentState.selectedGoal
            )
            
            // <<< Logging Point 1 >>>
            Log.d("ProfileSetupViewModel", "Attempting to update user. ID: ${updatedProfile.id}")

            try {
                userRepository.updateUser(updatedProfile)
                // No need to call setCurrentUser here, updateUser in OfflineUserRepository handles it
                // userRepository.setCurrentUser(updatedProfile) 
                onSuccess()
            } catch (e: Exception) {
                 Log.e("ProfileSetupViewModel", "Error updating profile: ${e.message}", e) // Log exception too
                _uiState.update { it.copy(errorMessage = "Пожалуйста, проверьте правильность введенных данных: ${e.message}") }
            }
        }
    }
} 