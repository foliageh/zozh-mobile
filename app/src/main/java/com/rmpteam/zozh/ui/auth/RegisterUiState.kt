package com.rmpteam.zozh.ui.auth

data class RegisterUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val email: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val registrationSuccess: Boolean = false
) 