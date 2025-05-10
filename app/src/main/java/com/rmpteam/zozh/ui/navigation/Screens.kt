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
    val floatingButtonDescription: String? = "Добавить",
    val showAppBar: Boolean = true
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
    
    // Auth and Profile screens
    @Serializable
    data object Auth : Screen()
    @Serializable
    data object Login : Screen()
    @Serializable
    data object Register : Screen()
    @Serializable
    data object ProfileSetup : Screen()
    @Serializable
    data object Settings : Screen()
    
    // Splash screen
    @Serializable
    data object Splash : Screen()

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
            
            // Auth and Profile screens info
            Login::class to ScreenInfo(
                title = "Вход",
                showAppBar = false
            ),
            Register::class to ScreenInfo(
                title = "Регистрация",
                withBackButton = true,
                showAppBar = false
            ),
            ProfileSetup::class to ScreenInfo(
                title = "Настройка профиля",
                showAppBar = false
            ),
            Settings::class to ScreenInfo(
                title = "Настройки",
                withBackButton = true
            ),
            
            // Splash screen has no info display
            Splash::class to ScreenInfo(
                title = "",
                showAppBar = false
            )
        )
    }
}



