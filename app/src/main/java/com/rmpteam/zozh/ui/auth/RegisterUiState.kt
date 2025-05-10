package com.rmpteam.zozh.ui.auth

data class RegisterUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val email: String = "", // Optional: if you want to collect email during registration
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val registrationSuccess: Boolean = false // To signal navigation
) 