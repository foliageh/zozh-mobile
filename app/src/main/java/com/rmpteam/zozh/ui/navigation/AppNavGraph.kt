package com.rmpteam.zozh.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rmpteam.zozh.ZOZHApplication
import com.rmpteam.zozh.data.user.UserRepository
import com.rmpteam.zozh.ui.auth.LoginScreen
import com.rmpteam.zozh.ui.auth.RegisterScreen
import com.rmpteam.zozh.ui.nutrition.NutritionMainScreen
import com.rmpteam.zozh.ui.nutrition.NutritionRecordScreen
import com.rmpteam.zozh.ui.profile.ProfileSetupScreen
import com.rmpteam.zozh.ui.settings.SettingsScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Get the user repository from the container
val Context.userRepository: UserRepository
    get() = (applicationContext as ZOZHApplication).container.userRepository

@Composable
fun AppNavHost(
    screenInfo: ScreenInfo,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    // Get the repository from the context
    val context = LocalContext.current
    val userRepository = context.userRepository
    
    // Start with a splash screen while checking authentication
    NavHost(
        navController = navController,
        startDestination = Screen.Splash,
        modifier = modifier
    ) {
        // Splash screen - checks authentication and redirects
        composable<Screen.Splash> {
            SplashScreenContent(
                navController = navController,
                userRepository = userRepository
            )
        }
        
        // Auth Navigation
        navigation<Screen.Auth>(startDestination = Screen.Login) {
            composable<Screen.Login> {
                LoginScreen(
                    onLoginSuccessNavigation = { requiresProfileSetup ->
                        if (requiresProfileSetup) {
                            navController.navigate(Screen.ProfileSetup) {
                                popUpTo(Screen.Auth) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Nutrition) {
                                popUpTo(Screen.Auth) { inclusive = true }
                            }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Screen.Register)
                    }
                )
            }
            
            composable<Screen.Register> {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.ProfileSetup) {
                            popUpTo(Screen.Auth) { inclusive = true }
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        // Profile Setup - mandatory before accessing the app
        composable<Screen.ProfileSetup> {
            ProfileSetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Nutrition) {
                        popUpTo(Screen.ProfileSetup) { inclusive = true }
                        popUpTo(Screen.Auth) { inclusive = true }
                    }
                }
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
                onLogout = {
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

@Composable
fun SplashScreenContent(
    navController: NavHostController,
    userRepository: UserRepository
) {
    // Show a loading indicator with logo
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App name as logo
            Text(
                text = "ZOZH",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // App slogan or description
            Text(
                text = "Здоровый Образ Жизни",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Loading indicator
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp)
            )
        }
    }
    
    // Check authentication status and navigate accordingly
    LaunchedEffect(Unit) {
        delay(1500) // Simulate loading/splash time
        val currentUser = userRepository.getCurrentUser().first()
        
        val destination = if (currentUser != null) {
            // User is authenticated
            if (currentUser.weight != null && currentUser.height != null && 
                currentUser.gender != null && currentUser.age != null && currentUser.goal != null) {
                // User profile is complete, go to main screen
                Screen.Nutrition
            } else {
                // User needs to complete profile
                Screen.ProfileSetup
            }
        } else {
            // User is not authenticated, go to login
            Screen.Auth
        }
        
        // Navigate to the appropriate destination
        navController.navigate(destination)
    }
}