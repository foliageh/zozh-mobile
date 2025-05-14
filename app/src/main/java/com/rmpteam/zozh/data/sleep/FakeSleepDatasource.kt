// app/src/main/java/com/rmpteam/zozh/data/sleep/FakeSleepDatasource.kt
package com.rmpteam.zozh.data.sleep

import com.rmpteam.zozh.util.DateTimeUtil

object FakeSleepDatasource {
    val sleepList = mutableListOf(
        Sleep(
            id = 1,
            startTime = DateTimeUtil.now().minusDays(1).withHour(23).withMinute(30),
            endTime = DateTimeUtil.now().withHour(7).withMinute(0),
            quality = SleepQuality.GOOD,
            notes = "Спал хорошо"
        ),
        Sleep(
            id = 2,
            startTime = DateTimeUtil.now().minusDays(3).withHour(22).withMinute(45),
            endTime = DateTimeUtil.now().minusDays(2).withHour(7).withMinute(15),
            quality = SleepQuality.EXCELLENT,
            notes = "Отличный сон"
        ),
        Sleep(
            id = 3,
            startTime = DateTimeUtil.now().minusDays(4).withHour(0).withMinute(15),
            endTime = DateTimeUtil.now().minusDays(3).withHour(6).withMinute(30),
            quality = SleepQuality.FAIR,
            notes = "Спал нормально, но недостаточно"
        ),
        Sleep(
            id = 4,
            startTime = DateTimeUtil.now().minusDays(5).withHour(23).withMinute(0),
            endTime = DateTimeUtil.now().minusDays(4).withHour(5).withMinute(0),
            quality = SleepQuality.POOR,
            notes = "Плохой сон, много просыпался"
        ),
        Sleep(
            id = 5,
            startTime = DateTimeUtil.now().minusDays(6).withHour(22).withMinute(30),
            endTime = DateTimeUtil.now().minusDays(5).withHour(7).withMinute(30),
            quality = SleepQuality.GOOD,
            notes = "Хороший длительный сон"
        ),
        Sleep(
            id = 6,
            startTime = DateTimeUtil.now().minusDays(7).withHour(23).withMinute(45),
            endTime = DateTimeUtil.now().minusDays(6).withHour(7).withMinute(0),
            quality = SleepQuality.GOOD,
            notes = "Стандартный сон"
        ),
        Sleep(
            id = 7,
            startTime = DateTimeUtil.now().minusDays(8).withHour(21).withMinute(0),
            endTime = DateTimeUtil.now().minusDays(7).withHour(6).withMinute(30),
            quality = SleepQuality.EXCELLENT,
            notes = "Рано лег и отлично выспался"
        )
    )
}