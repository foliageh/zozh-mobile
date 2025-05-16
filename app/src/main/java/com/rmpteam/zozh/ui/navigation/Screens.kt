package com.rmpteam.zozh.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

data class ScreenInfo(
    val title: String = "",
    val withBackButton: Boolean = false,
    val showFloatingButton: Boolean = false,
    var floatingButtonAction: (() -> Unit)? = null,
    val floatingButtonIcon: ImageVector? = Icons.Filled.Add,
    val floatingButtonDescription: String? = "Добавить"
)

sealed class Screen {
    @Serializable
    data object NutritionSection : Screen()
    @Serializable
    data object NutritionMain : Screen()
    @Serializable
    data class MealDetail(val mealId: Long = 0) : Screen()

    @Serializable
    data object SleepSection : Screen()
    @Serializable
    data object SleepMain : Screen()
    @Serializable
    data class SleepDetail(val sleepId: Long) : Screen()

    companion object {
        val screensInfo = mapOf(
            NutritionMain::class to ScreenInfo(
                title = "Дневник питания",
                showFloatingButton = true
            ),
            MealDetail::class to ScreenInfo(
                title = "Приём пищи",
                withBackButton = true
            ),

            SleepMain::class to ScreenInfo(
                title = "Статистика сна"
            ),
            SleepDetail::class to ScreenInfo(
                title = "Детали сна",
                withBackButton = true
            )
        )
    }
}