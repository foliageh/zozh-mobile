package com.rmpteam.zozh.ui.nutrition

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rmpteam.zozh.data.nutrition.MealRepository
import com.rmpteam.zozh.ui.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

data class MealUiState(
    val isNewMeal: Boolean = true,
    val meal: MealRecord = MealRecord(),
    val isMealValid: Boolean = false,
    val isMealFound: Boolean = true
)

class NutritionRecordViewModel(
    savedStateHandle: SavedStateHandle,
    private val mealRepository: MealRepository
) : ViewModel() {
    private val viewModelArgs = savedStateHandle.toRoute<Screen.NutritionRecord>()
    private val mealId = viewModelArgs.mealId

    var uiState by mutableStateOf(MealUiState())
        private set

    init {
        if (mealId > 0) {
            viewModelScope.launch {
                val meal = mealRepository.getMealById(mealId)
                    .filterNotNull().mapNotNull { it.toMealRecord() }.firstOrNull()
                uiState = if (meal == null) uiState.copy(isMealFound = false)
                else uiState.copy(isNewMeal = false, meal = meal, isMealValid = validateMeal(meal))
            }
        }
    }

    fun updateUiState(meal: MealRecord) {
        uiState = uiState.copy(meal = meal, isMealValid = validateMeal(meal))
    }

    private fun validateMeal(meal: MealRecord = uiState.meal): Boolean {
        return with(meal) {
            name.isNotBlank() && carbs >= 0 && protein >= 0 && fat >= 0
        }
    }

    fun createMeal() {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.isNewMeal && validateMeal()) {
                val mealId = mealRepository.insertMeal(uiState.meal.toEntity())
                uiState = uiState.copy(isNewMeal = false, meal = uiState.meal.copy(id = mealId))
            }
        }
    }

    fun updateMeal() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!uiState.isNewMeal && validateMeal()) {
                mealRepository.updateMeal(uiState.meal.toEntity())
            }
        }
    }

    suspend fun deleteItem() {
        mealRepository.deleteMeal(uiState.meal.toEntity())
    }
}