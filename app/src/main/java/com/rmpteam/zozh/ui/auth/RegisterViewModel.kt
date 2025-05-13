package com.rmpteam.zozh.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.user.UserPreferencesRepository
import com.rmpteam.zozh.data.user.UserProfile
import com.rmpteam.zozh.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val email: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val registrationSuccess: Boolean = false
)

class RegisterViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, errorMessage = null) }
    }
    
    fun registerUser() {
        val currentState = uiState.value

        when(val validationResult = ValidationUtil.validateRegistrationCredentials(
            username = currentState.username,
            password = currentState.password,
            confirmPassword = currentState.confirmPassword
        )) {
            is ValidationUtil.ValidationResult.Error -> {
                 _uiState.update { it.copy(errorMessage = validationResult.message) }
                 return
            }
            ValidationUtil.ValidationResult.Success -> {
                 // Proceed
            }
        }

        _uiState.update { it.copy(isLoading = true) }

        val userToRegister = UserProfile(
            username = currentState.username,
            password = currentState.password
        )

        viewModelScope.launch {
            val result = userRepository.register(userToRegister)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            registrationSuccess = true,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Ошибка регистрации",
                            registrationSuccess = false
                        )
                    }
                }
            )
        }
    }
} 