package com.rmpteam.zozh.data.nutrition

import com.rmpteam.zozh.util.DateTimeUtil

object FakeMealDatasource {
    val mealList = mutableListOf(
        Meal(
            1,
            "Обед",
            DateTimeUtil.now(),
            30, 50, 60
        ),
        Meal(
            2,
            "Поздний ужин мм как вкусно ням-ням-ням",
            DateTimeUtil.now().minusHours(3),
            20, 19, 44
        ),
        Meal(
            2,
            "Завтрак большой",
            DateTimeUtil.now().minusDays(2),
            25, 40, 30
        ),
    )
}