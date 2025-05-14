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

val Context.userRepository: UserRepository
    get() = (applicationContext as ZOZHApplication).container.userRepository

@Composable
fun AppNavHost(
    screenInfo: ScreenInfo,
    navController: NavHostController,
    startScreen: Screen,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val userRepository = context.userRepository
    
    NavHost(
        navController = navController,
        startDestination = startScreen,
        modifier = modifier
    ) {
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
                //val args = it.toRoute<Screen.NutritionRecord>()
                //NutritionRecordScreen(mealId = args.mealId)
                NutritionRecordScreen(onNavigateBack = { navController.navigateUp() })
            }
        }

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