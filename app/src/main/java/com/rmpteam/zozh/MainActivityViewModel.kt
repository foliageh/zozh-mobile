package com.rmpteam.zozh

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.user.UserProfile
import com.rmpteam.zozh.ui.navigation.Screen
import com.rmpteam.zozh.util.FLOW_TIMEOUT_MS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class MainActivityUiState(
    val isLoading: Boolean = true,
    val startScreen: Screen? = null
)

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val userPreferencesRepository =
        (application.applicationContext as ZOZHApplication).container.userPreferencesRepository

    val uiState = userPreferencesRepository.userProfile.map { userProfile ->
        MainActivityUiState(isLoading = false, startScreen = determineStartScreen(userProfile))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
        initialValue = MainActivityUiState()
    )

    private fun determineStartScreen(userProfile: UserProfile?): Screen {
        return if (userProfile == null) Screen.Auth
        else if (!userProfile.isProfileComplete()) Screen.ProfileSetup
        else Screen.Nutrition
    }
}