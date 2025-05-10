package com.rmpteam.zozh.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileSetupViewModel(private val userRepository: UserRepository) : ViewModel() {

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
        val userToUpdate = currentState.currentUser ?: com.rmpteam.zozh.data.user.UserProfile(
            id = "",
            username = "",
            password = ""
        )

        val weightFloat = currentState.weight.toFloatOrNull()
        val heightInt = currentState.height.toIntOrNull()
        val ageInt = currentState.age.toIntOrNull()

        if (weightFloat == null || heightInt == null || ageInt == null || currentState.selectedGender == null || currentState.selectedGoal == null) {
            _uiState.update { it.copy(errorMessage = "Пожалуйста, заполните все поля") }
            return
        }

        val updatedProfile = userToUpdate.copy(
            weight = weightFloat,
            height = heightInt,
            age = ageInt,
            gender = currentState.selectedGender,
            goal = currentState.selectedGoal
        )

        viewModelScope.launch {
            try {
                userRepository.updateUser(updatedProfile)
                userRepository.setCurrentUser(updatedProfile)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Пожалуйста, проверьте правильность введенных данных: ${e.message}") }
            }
        }
    }
} 