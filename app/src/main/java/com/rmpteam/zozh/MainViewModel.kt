package com.rmpteam.zozh

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.user.UserProfile
import com.rmpteam.zozh.data.user.UserRepository
import com.rmpteam.zozh.ui.navigation.Screen
import com.rmpteam.zozh.ui.navigation.userRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val isLoading: Boolean = true,
    val startScreen: Screen? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository = application.userRepository // Access repository via extension

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser().firstOrNull() // Check initial user state
            val screen = determineStartScreen(user)
            _uiState.update { it.copy(isLoading = false, startScreen = screen) }
        }
    }

    private fun determineStartScreen(user: UserProfile?): Screen {
        return if (user == null) {
            Screen.Auth
        } else if (isProfileComplete(user)) {
            Screen.Nutrition
        } else {
            Screen.ProfileSetup
        }
    }

    // Helper to check if profile is complete (adjust fields as needed)
    private fun isProfileComplete(user: UserProfile): Boolean {
        val currentWeight = user.weight
        val currentHeight = user.height
        val currentAge = user.age

        return currentWeight != null && currentWeight > 0 &&
               currentHeight != null && currentHeight > 0 &&
               currentAge != null && currentAge > 0 &&
               user.gender != null &&
               user.goal != null
    }
} 