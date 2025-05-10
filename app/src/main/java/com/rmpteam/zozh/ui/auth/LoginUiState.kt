package com.rmpteam.zozh.ui.auth

import com.rmpteam.zozh.data.user.UserProfile

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val loginSucceeded: Boolean = false, // Renamed for clarity
    val loggedInUser: UserProfile? = null, // To hold the user profile upon successful login
    val requiresProfileSetup: Boolean? = null // To signal navigation path
) 