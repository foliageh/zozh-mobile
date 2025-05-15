package com.rmpteam.zozh.ui.settings

import com.rmpteam.zozh.data.user.Gender
import com.rmpteam.zozh.data.user.UserProfile
import com.rmpteam.zozh.data.user.WeightGoal

data class SettingsUiState(
    val currentUser: UserProfile? = null,
    val weight: String = "",
    val height: String = "",
    val age: String = "",
    val selectedGender: Gender? = null,
    val selectedGoal: WeightGoal? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = true
) 