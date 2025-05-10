package com.rmpteam.zozh.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.rmpteam.zozh.data.MockUserRepository
import com.rmpteam.zozh.ui.auth.LoginScreen
import com.rmpteam.zozh.ui.auth.RegisterScreen
import com.rmpteam.zozh.ui.nutrition.NutritionMainScreen
import com.rmpteam.zozh.ui.nutrition.NutritionRecordScreen
import com.rmpteam.zozh.ui.profile.ProfileSetupScreen
import com.rmpteam.zozh.ui.settings.SettingsScreen

// Create a singleton of the repository for now
val userRepository = MockUserRepository()

@Composable
fun AppNavHost(
    screenInfo: ScreenInfo,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    // Get current auth state
    val currentUser = userRepository.getCurrentUser()
    val isAuthenticated = currentUser != null
    val hasCompletedProfile = isAuthenticated && 
                             currentUser!!.weight != null && 
                             currentUser.height != null && 
                             currentUser.gender != null && 
                             currentUser.age != null && 
                             currentUser.goal != null
    
    // Determine starting screen based on auth state
    val startDestination = if (!isAuthenticated) {
        Screen.Auth
    } else if (!hasCompletedProfile) {
        Screen.ProfileSetup
    } else {
        Screen.Nutrition
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth Navigation
        navigation<Screen.Auth>(startDestination = Screen.Login) {
            composable<Screen.Login> {
                LoginScreen(
                    onLoginSuccess = {
                        val user = userRepository.getCurrentUser()
                        if (user?.weight == null || user.height == null || 
                            user.gender == null || user.age == null || user.goal == null) {
                            // User hasn't completed profile setup
                            navController.navigate(Screen.ProfileSetup) {
                                popUpTo(Screen.Auth) { inclusive = true }
                            }
                        } else {
                            // User is fully set up, can access the main app
                            navController.navigate(Screen.Nutrition) {
                                popUpTo(Screen.Auth) { inclusive = true }
                            }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Screen.Register)
                    },
                    userRepository = userRepository
                )
            }
            
            composable<Screen.Register> {
                RegisterScreen(
                    onRegisterSuccess = {
                        // After registration, return to login screen
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    userRepository = userRepository
                )
            }
        }
        
        // Profile Setup - mandatory before accessing the app
        composable<Screen.ProfileSetup> {
            ProfileSetupScreen(
                onSetupComplete = {
                    // Profile setup complete, navigate to main app
                    navController.navigate(Screen.Nutrition) {
                        popUpTo(Screen.ProfileSetup) { inclusive = true }
                    }
                },
                userRepository = userRepository
            )
        }

        // Nutrition Navigation
        navigation<Screen.Nutrition>(startDestination = Screen.NutritionMain) {
            composable<Screen.NutritionMain> {
                screenInfo.floatingButtonAction = { navController.navigate(Screen.NutritionRecord()) }
                NutritionMainScreen(
                    onNavigateToNutritionRecord = { mealId ->
                        navController.navigate(Screen.NutritionRecord(mealId = mealId))
                    }
                )
            }
            
            composable<Screen.NutritionRecord> {
                NutritionRecordScreen()
            }
        }

        // Settings
        composable<Screen.Settings> {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                userRepository = userRepository,
                onLogout = {
                    userRepository.logout()
                    navController.navigate(Screen.Auth) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Other> {
            Text(text = "просто для примера")
        }
    }
}