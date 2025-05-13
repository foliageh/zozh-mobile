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
    data object Nutrition : Screen()
    @Serializable
    data object NutritionMain : Screen()
    @Serializable
    data class NutritionRecord(
        val mealId: Long = 0,
    ) : Screen()

    @Serializable
    data object Other : Screen()

    @Serializable
    data object HealthDashboard : Screen()

    companion object {
        val screensInfo = mapOf(
            NutritionMain::class to ScreenInfo(
                title = "Дневник питания",
                showFloatingButton = true
            ),
            NutritionRecord::class to ScreenInfo(
                title = "Приём пищи",
                withBackButton = true
            ),

            Other::class to ScreenInfo(
                title = "Другое"
            ),
            HealthDashboard::class to ScreenInfo(
                title = "Физическая активность",
            )
        )
    }
}



