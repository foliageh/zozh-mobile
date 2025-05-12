package com.rmpteam.zozh.ui.settings

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
        val currentState = _uiState.value
        val user = currentState.currentUser ?: return

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

        val weightFloat = currentState.weight.toFloat()
        val heightInt = currentState.height.toInt()
        val ageInt = currentState.age.toInt()

        val updatedProfile = user.copy(
            weight = weightFloat,
            height = heightInt,
            age = ageInt,
            gender = currentState.selectedGender,
            goal = currentState.selectedGoal
        )

        viewModelScope.launch {
            try {
                userRepository.updateUser(updatedProfile)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Пожалуйста, проверьте правильность введенных данных") }
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