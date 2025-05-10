package com.rmpteam.zozh.data.nutrition

import com.rmpteam.zozh.util.DateTimeUtil.endOfDay
import com.rmpteam.zozh.util.DateTimeUtil.startOfDay
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

class OfflineMealRepository(private val mealDao: MealDao) : MealRepository {
    override fun getMealsByDate(dateTime: ZonedDateTime): Flow<List<Meal>> =
        mealDao.getMealsBetweenDateTimes(dateTime.startOfDay(), dateTime.endOfDay())
    override fun getMealById(id: Long): Flow<Meal?> = mealDao.getMealById(id)
    override suspend fun insertMeal(meal: Meal): Long = mealDao.insert(meal)
    override suspend fun deleteMeal(meal: Meal) = mealDao.delete(meal)
    override suspend fun updateMeal(meal: Meal) = mealDao.update(meal)
}