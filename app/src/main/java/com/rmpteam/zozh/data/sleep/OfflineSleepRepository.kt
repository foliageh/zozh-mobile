// app/src/main/java/com/rmpteam/zozh/data/sleep/OfflineSleepRepository.kt
package com.rmpteam.zozh.data.sleep

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.ZonedDateTime

class OfflineSleepRepository : SleepRepository {
    // Используем моковые данные напрямую здесь
    private val sleepList = FakeSleepDatasource.sleepList

    override fun getSleepByDate(dateTime: ZonedDateTime): Flow<List<Sleep>> {
        // Получаем все данные за последние 30 дней до указанной даты
        val startDay = dateTime.minusDays(30)

        return flowOf(sleepList.filter {
            it.startTime.isAfter(startDay) && !it.startTime.isAfter(dateTime)
        })
    }

    override fun getSleepById(id: Long): Flow<Sleep?> {
        return flowOf(sleepList.find { it.id == id })
    }

    override suspend fun insertSleep(sleep: Sleep): Long {
        val newId = sleepList.maxOfOrNull { it.id }?.plus(1) ?: 1
        val newSleep = sleep.copy(id = newId)
        sleepList.add(newSleep)
        return newId
    }

    override suspend fun deleteSleep(sleep: Sleep) {
        sleepList.removeIf { it.id == sleep.id }
    }

    override suspend fun updateSleep(sleep: Sleep) {
        val index = sleepList.indexOfFirst { it.id == sleep.id }
        if (index != -1) {
            sleepList[index] = sleep
        }
    }
}