package com.rmpteam.zozh.ui.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.UserPreferencesRepository
import com.rmpteam.zozh.data.nutrition.MealRepository
import com.rmpteam.zozh.util.FLOW_TIMEOUT_MS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

data class NutritionMainUiState(
    val date: LocalDate,
    val mealList: List<MealRecord> = emptyList()
)

data class UserPreferencesUiState(
    val calories: Int
)

class NutritionMainViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    mealRepository: MealRepository
) : ViewModel() {
    private val initialDate: LocalDate = LocalDate.now()

    val uiState = mealRepository.getMealsByDate(initialDate)
        .map { it.map { meal -> meal.toMealRecord() } }
        .map { NutritionMainUiState(mealList = it, date = initialDate) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
            initialValue = NutritionMainUiState(date = initialDate)
        )

    // какие-то страшные вещи, надо что-то получше придумать с userPreferences
    val userPreferencesUiState =
        userPreferencesRepository.calories.map { calories ->
            UserPreferencesUiState(calories = calories)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
            initialValue = runBlocking {
                UserPreferencesUiState(calories = userPreferencesRepository.calories.first())
            }
        )

//    fun setCaloriesPreference(calories: Int) {
//        viewModelScope.launch {
//            userPreferencesRepository.saveCaloriesPreference(calories)
//        }
//    }
}
