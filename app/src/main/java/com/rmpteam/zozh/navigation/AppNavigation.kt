package com.rmpteam.zozh.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rmpteam.zozh.data.MockUserRepository
import com.rmpteam.zozh.ui.auth.LoginScreen
import com.rmpteam.zozh.ui.auth.RegisterScreen
import com.rmpteam.zozh.ui.profile.ProfileSetupScreen
import com.rmpteam.zozh.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ProfileSetup : Screen("profile_setup")
    object Settings : Screen("settings")
    object Main : Screen("main")
}

@Composable
fun AppNavigation(userRepository: MockUserRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    val user = userRepository.getCurrentUser()
                    if (user?.weight == null) {
                        navController.navigate(Screen.ProfileSetup.route)
                    } else {
                        navController.navigate(Screen.Main.route)
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                userRepository = userRepository
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.ProfileSetup.route)
                },
                onBackClick = {
                    navController.popBackStack()
                },
                userRepository = userRepository
            )
        }

        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                userRepository = userRepository
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                userRepository = userRepository
            )
        }

        composable(Screen.Main.route) {
            // TODO: Implement main screen
            // For now, just show a button to go to settings
            androidx.compose.material3.Button(
                onClick = {
                    navController.navigate(Screen.Settings.route)
                }
            ) {
                androidx.compose.material3.Text("Настройки")
            }
        }
    }
} 