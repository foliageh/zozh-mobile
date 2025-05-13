package com.rmpteam.zozh.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.user.UserPreferencesRepository
import com.rmpteam.zozh.data.user.UserProfile
import com.rmpteam.zozh.data.user.UserRepository
import com.rmpteam.zozh.util.ValidationResult
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val validationResult: ValidationResult = ValidationResult(false),
    val loginSucceeded: Boolean = false,
    val requiresProfileSetup: Boolean = false,
    val loggedInUser: UserProfile? = null,
    val isLoading: Boolean = false
)

class LoginViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set

    fun updateUsername(username: String) {
        val validationResult = if (username.isNotBlank()) ValidationResult(isValid = true)
        else ValidationResult(isValid = false, errorMessage = "Логин не может быть пустым")
        uiState = uiState.copy(validationResult = validationResult)
    }

    fun updatePassword(password: String) {
        val validationResult = if (password.isNotBlank()) ValidationResult(isValid = true)
        else ValidationResult(isValid = false, errorMessage = "Пароль не может быть пустым")
        uiState = uiState.copy(validationResult = validationResult)
    }

    fun loginUser() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            userRepository.findUserByUsername(uiState.username).fold(
                onSuccess = {
                    if (it.password == uiState.password) {
                        userPreferencesRepository.saveUserProfile(it)
                        uiState = uiState.copy(loginSucceeded = true, loggedInUser = it)
                    } else {
                        val validationResult = ValidationResult(isValid = false, errorMessage = "Неверный пароль")
                        uiState = uiState.copy(loginSucceeded = false, validationResult = validationResult)
                    }
                },
                onFailure = {
                    val validationResult = ValidationResult(isValid = false, errorMessage = "Пользователь не найден")
                    uiState = uiState.copy(loginSucceeded = false, validationResult = validationResult)
                }
            )
            uiState = uiState.copy(isLoading = false)
        }
    }
}