package com.rmpteam.zozh.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.user.UserProfile
import com.rmpteam.zozh.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun loginUser() {
        if (uiState.value.username.isBlank() || uiState.value.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Логин и пароль не могут быть пустыми") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = userRepository.login(uiState.value.username, uiState.value.password)
            result.fold(
                onSuccess = { userProfile ->
                    val profileComplete = userProfile.weight != null &&
                                          userProfile.height != null &&
                                          userProfile.gender != null &&
                                          userProfile.age != null &&
                                          userProfile.goal != null
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginSucceeded = true,
                            loggedInUser = userProfile,
                            requiresProfileSetup = !profileComplete,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Ошибка входа",
                            loginSucceeded = false,
                            loggedInUser = null,
                            requiresProfileSetup = null
                        )
                    }
                }
            )
        }
    }
    
    fun onLoginNavigationHandled() {
        _uiState.update { it.copy(loginSucceeded = false, loggedInUser = null, requiresProfileSetup = null) }
    }
} 