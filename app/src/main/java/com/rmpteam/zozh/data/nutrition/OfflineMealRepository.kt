package com.rmpteam.zozh.data.nutrition

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class OfflineMealRepository(private val mealDao: MealDao) : MealRepository {
    override fun getMealsByDate(date: LocalDate): Flow<List<Meal>> = mealDao.getMealsByDateString(date.toString())
    override fun getMealById(id: Long): Flow<Meal?> = mealDao.getMealById(id)
    override suspend fun insertMeal(meal: Meal): Long = mealDao.insert(meal)
    override suspend fun deleteMeal(meal: Meal) = mealDao.delete(meal)
    override suspend fun updateMeal(meal: Meal) = mealDao.update(meal)
}