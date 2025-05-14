package com.rmpteam.zozh.di

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rmpteam.zozh.ZOZHApplication
import com.rmpteam.zozh.ui.nutrition.NutritionMainViewModel
import com.rmpteam.zozh.ui.nutrition.NutritionRecordViewModel
import com.rmpteam.zozh.ui.settings.SettingsViewModel
import com.rmpteam.zozh.ui.profile.ProfileSetupViewModel
import com.rmpteam.zozh.ui.auth.LoginViewModel
import com.rmpteam.zozh.ui.auth.RegisterViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { NutritionMainViewModel(
            mealRepository = appContainer().mealRepository,
            userPreferencesRepository = appContainer().userPreferencesRepository
        ) }

        initializer { NutritionRecordViewModel(
            this.createSavedStateHandle(),
            mealRepository = appContainer().mealRepository
        ) }

        initializer { SettingsViewModel(
            userRepository = appContainer().userRepository
        ) }

        initializer { ProfileSetupViewModel(
            userRepository = appContainer().userRepository
        ) }

        initializer { LoginViewModel(userRepository = appContainer().userRepository) }

        initializer { RegisterViewModel(userRepository = appContainer().userRepository) }
    }
}

fun CreationExtras.appContainer(): AppContainer =
    (this[APPLICATION_KEY] as ZOZHApplication).container
