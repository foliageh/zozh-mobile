package com.rmpteam.zozh.data.nutrition

import java.time.LocalDateTime

object FakeMealDatasource {
    val mealList = mutableListOf(
        Meal(
            1,
            "Обед",
            LocalDateTime.of(2025, 5, 9, 14, 25),
            30, 50, 60
        ),
        Meal(
            2,
            "Поздний ужин",
            LocalDateTime.of(2025, 5, 9, 23, 50),
            20, 19, 44
        ),
    )
}
