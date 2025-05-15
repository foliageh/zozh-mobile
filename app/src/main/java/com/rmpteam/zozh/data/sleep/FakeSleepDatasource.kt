package com.rmpteam.zozh.data.sleep

import com.rmpteam.zozh.util.DateTimeUtil

object FakeSleepDatasource {
    
    private val currentDate = DateTimeUtil.now()

    val sleepList = mutableListOf(
        Sleep(
            id = 1,
            startTime = currentDate.minusDays(1).withHour(23).withMinute(30),
            endTime = currentDate.withHour(7).withMinute(0),
            quality = SleepQuality.GOOD,
            notes = "Спал хорошо"
        ),
        Sleep(
            id = 2,
            startTime = currentDate.minusDays(3).withHour(22).withMinute(45),
            endTime = currentDate.minusDays(2).withHour(7).withMinute(15),
            quality = SleepQuality.EXCELLENT,
            notes = "Отличный сон"
        ),
        Sleep(
            id = 3,
            startTime = currentDate.minusDays(4).withHour(0).withMinute(15),
            endTime = currentDate.minusDays(3).withHour(6).withMinute(30),
            quality = SleepQuality.FAIR,
            notes = "Спал нормально, но недостаточно"
        ),
        Sleep(
            id = 4,
            startTime = currentDate.minusDays(5).withHour(23).withMinute(0),
            endTime = currentDate.minusDays(4).withHour(5).withMinute(0),
            quality = SleepQuality.POOR,
            notes = "Плохой сон, много просыпался"
        ),
        Sleep(
            id = 5,
            startTime = currentDate.minusDays(8).withHour(22).withMinute(30),
            endTime = currentDate.minusDays(7).withHour(7).withMinute(30),
            quality = SleepQuality.GOOD,
            notes = "Хороший длительный сон"
        ),
        Sleep(
            id = 6,
            startTime = currentDate.minusDays(9).withHour(23).withMinute(45),
            endTime = currentDate.minusDays(8).withHour(7).withMinute(0),
            quality = SleepQuality.GOOD,
            notes = "Стандартный сон"
        ),
        Sleep(
            id = 7,
            startTime = currentDate.minusDays(10).withHour(21).withMinute(0),
            endTime = currentDate.minusDays(9).withHour(6).withMinute(30),
            quality = SleepQuality.EXCELLENT,
            notes = "Рано лег и отлично выспался"
        )
    )

    fun addSleep(sleep: Sleep): Long {
        val newId = (sleepList.maxOfOrNull { it.id } ?: 0) + 1
        val newSleep = sleep.copy(id = newId)
        sleepList.add(newSleep)
        println("Добавлена запись о сне с ID $newId: ${newSleep.startTime} - ${newSleep.endTime}")
        return newId
    }
}