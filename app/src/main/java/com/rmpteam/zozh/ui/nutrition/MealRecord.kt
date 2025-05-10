package com.rmpteam.zozh.ui.nutrition

import com.rmpteam.zozh.data.nutrition.Meal
import com.rmpteam.zozh.util.DateTimeUtil
import java.time.ZonedDateTime

data class MealRecord(
    var id: Long = 0,
    val name: String = "",
    val dateTime: ZonedDateTime = DateTimeUtil.now(),
    val protein: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
) {
    val calories: Int = protein * 4 + fat * 9 + carbs * 4
}

fun MealRecord.toEntity(): Meal = Meal(
    id = id,
    name = name,
    dateTime = dateTime,
    protein = protein,
    fat = fat,
    carbs = carbs,
)

fun Meal.toMealRecord(): MealRecord = MealRecord(
    id = id,
    name = name,
    dateTime = dateTime,
    protein = protein,
    fat = fat,
    carbs = carbs,
)
