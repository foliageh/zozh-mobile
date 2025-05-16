package com.rmpteam.zozh.ui.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmpteam.zozh.data.nutrition.MealRepository
import com.rmpteam.zozh.data.user.UserPreferencesRepository
import com.rmpteam.zozh.data.user.UserProfile
import com.rmpteam.zozh.util.DateTimeUtil
import com.rmpteam.zozh.util.DateTimeUtil.startOfDay
import com.rmpteam.zozh.util.FLOW_TIMEOUT_MS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.time.ZonedDateTime

data class NutritionMainUiState(
    val date: ZonedDateTime,
    val isLoading: Boolean = true,
    val mealList: List<MealDetail> = emptyList(),
)

data class UserProfileUiState(
    val userProfile: UserProfile
)

class NutritionMainViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val mealRepository: MealRepository
) : ViewModel() {
    private val _dateState = MutableStateFlow(DateTimeUtil.now().startOfDay())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<NutritionMainUiState> = _dateState
        .flatMapLatest { date ->
            mealRepository.getMealsByDate(date)
                .map { it.map { meal -> meal.toMealDetail() } }
                .map { NutritionMainUiState(date = date, isLoading = false, mealList = it) }
                .onStart { emit(NutritionMainUiState(date = date, isLoading = true)) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
            initialValue = NutritionMainUiState(date = _dateState.value)
        )

    val userProfileState =
        userPreferencesRepository.userProfile.map { userProfile ->
            UserProfileUiState(userProfile = userProfile ?: UserProfile())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
            initialValue = runBlocking {
                UserProfileUiState(userProfile = userPreferencesRepository.userProfile.first() ?: UserProfile())
            }
        )

    fun updateDate(date: ZonedDateTime) {
        _dateState.value = date
    }
}