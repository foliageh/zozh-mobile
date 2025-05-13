package com.rmpteam.zozh.ui.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.nutrition.MealRepository
import com.rmpteam.zozh.data.user.UserPreferencesRepository
import com.rmpteam.zozh.data.user.UserProfile
import com.rmpteam.zozh.util.DateTimeUtil
import com.rmpteam.zozh.util.DateTimeUtil.startOfDay
import com.rmpteam.zozh.util.FLOW_TIMEOUT_MS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.time.ZonedDateTime

data class NutritionMainUiState(
    val date: ZonedDateTime,
    val mealList: List<MealRecord> = emptyList()
)

data class UserProfileUiState(
    val userProfile: UserProfile
)

class NutritionMainViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    mealRepository: MealRepository
) : ViewModel() {
    private val initialDate: ZonedDateTime = DateTimeUtil.now().startOfDay()

    val uiState = mealRepository.getMealsByDate(initialDate)
        .map { it.map { meal -> meal.toMealRecord() } }
        .map { NutritionMainUiState(mealList = it, date = initialDate) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
            initialValue = NutritionMainUiState(date = initialDate)
        )

    val userProfileUiState =
        userPreferencesRepository.userProfile.map { userProfile ->
            UserProfileUiState(userProfile = userProfile!!)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
            initialValue = runBlocking {
                UserProfileUiState(userProfile = userPreferencesRepository.userProfile.first()!!)
            }
        )

    fun setCaloriesPreference(calories: Int) {
        //viewModelScope.launch {
        //    userPreferencesRepository.saveCaloriesPreference(calories)
        //}
    }
}
