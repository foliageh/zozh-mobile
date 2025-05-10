package com.rmpteam.zozh.data.nutrition

import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface MealRepository {
    fun getMealsByDate(dateTime: ZonedDateTime): Flow<List<Meal>>
    fun getMealById(id: Long): Flow<Meal?>
    suspend fun insertMeal(meal: Meal): Long
    suspend fun deleteMeal(meal: Meal)
    suspend fun updateMeal(meal: Meal)
}
