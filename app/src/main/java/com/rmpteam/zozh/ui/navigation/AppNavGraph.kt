package com.rmpteam.zozh.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.rmpteam.zozh.ui.nutrition.NutritionMainScreen
import com.rmpteam.zozh.ui.nutrition.NutritionRecordScreen
import com.rmpteam.zozh.ui.physicalActivity.HealthDashboardScreen

@Composable
fun AppNavHost(
    screenInfo: ScreenInfo,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Nutrition,
        modifier = modifier
    ) {
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
                NutritionRecordScreen()
            }
        }

        composable<Screen.HealthDashboard> {
            HealthDashboardScreen()
        }

        composable<Screen.Other> {
            Text(text = "просто для примера")
        }
    }
}