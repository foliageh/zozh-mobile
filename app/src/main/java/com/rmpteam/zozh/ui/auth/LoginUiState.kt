package com.rmpteam.zozh.ui.auth

import com.rmpteam.zozh.data.user.UserProfile

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val loginSucceeded: Boolean = false,
    val loggedInUser: UserProfile? = null,
    val requiresProfileSetup: Boolean? = null 
) 