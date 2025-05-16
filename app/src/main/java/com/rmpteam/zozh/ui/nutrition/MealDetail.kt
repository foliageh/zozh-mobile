package com.rmpteam.zozh.ui.nutrition

import com.rmpteam.zozh.data.nutrition.Meal
import com.rmpteam.zozh.util.DateTimeUtil
import java.time.ZonedDateTime

data class MealDetail(
    val id: Long = 0,
    val name: String = "",
    val dateTime: ZonedDateTime = DateTimeUtil.now(),
    val protein: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
) {
    val calories: Int = protein * 4 + fat * 9 + carbs * 4
}

fun MealDetail.toEntity(): Meal = Meal(
    id = id,
    name = name,
    dateTime = dateTime,
    protein = protein,
    fat = fat,
    carbs = carbs,
)

fun Meal.toMealDetail(): MealDetail = MealDetail(
    id = id,
    name = name,
    dateTime = dateTime,
    protein = protein,
    fat = fat,
    carbs = carbs,
)