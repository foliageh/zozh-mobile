package com.rmpteam.zozh.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.rmpteam.zozh.ui.nutrition.NutritionMainScreen
import com.rmpteam.zozh.ui.nutrition.MealDetailScreen
import com.rmpteam.zozh.ui.sleep.SleepDetailScreen
import com.rmpteam.zozh.ui.sleep.SleepMainScreen

@Composable
fun AppNavHost(
    screenInfo: ScreenInfo,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.NutritionSection,
        modifier = modifier
    ) {
        navigation<Screen.NutritionSection>(startDestination = Screen.NutritionMain) {
            composable<Screen.NutritionMain> {
                screenInfo.floatingButtonAction = { navController.navigate(Screen.MealDetail()) }
                NutritionMainScreen(
                    onNavigateToMealDetail = { mealId ->
                        navController.navigate(Screen.MealDetail(mealId = mealId))
                    }
                )
            }
            composable<Screen.MealDetail> {
                MealDetailScreen(onNavigateBack = { navController.navigateUp() })
            }
        }

        navigation<Screen.SleepSection>(startDestination = Screen.SleepMain) {
            composable<Screen.SleepMain> {
                SleepMainScreen(
                    onSleepItemClick = { sleep ->
                        navController.navigate(Screen.SleepDetail(sleepId = sleep.id))
                    }
                )
            }
            composable<Screen.SleepDetail> {
                val args = it.toRoute<Screen.SleepDetail>()
                SleepDetailScreen(
                    sleepId = args.sleepId,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}