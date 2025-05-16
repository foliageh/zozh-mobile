package com.rmpteam.zozh.data.sleep

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.ZonedDateTime

class FakeSleepRepository : SleepRepository {
    override fun getSleepByDate(dateTime: ZonedDateTime): Flow<List<Sleep>> {
        val startDay = dateTime.minusDays(30)

        val filteredList = FakeSleepDatasource.sleepList.filter {
            (it.startTime.isAfter(startDay) || it.startTime.isEqual(startDay))
                    && !it.startTime.isAfter(dateTime)
        }

        Log.d("SLEEP", "getSleepByDate: Получено записей о сне: ${filteredList.size} (период $startDay - $dateTime)")
        Log.d("SLEEP", "getSleepByDate: Общее количество записей: ${FakeSleepDatasource.sleepList.size}")

        return flowOf(filteredList)
    }

    override fun getSleepById(id: Long): Flow<Sleep?> {
        return flowOf(FakeSleepDatasource.sleepList.find { it.id == id })
    }

    override suspend fun insertSleep(sleep: Sleep): Long {
        val newId = FakeSleepDatasource.sleepList.maxOfOrNull { it.id }?.plus(1) ?: 1
        return FakeSleepDatasource.addSleep(sleep.copy(id = newId))
    }

    override suspend fun deleteSleep(sleep: Sleep) {
        FakeSleepDatasource.sleepList.removeIf { it.id == sleep.id }
    }

    override suspend fun updateSleep(sleep: Sleep) {
        val index = FakeSleepDatasource.sleepList.indexOfFirst { it.id == sleep.id }
        if (index != -1) {
            FakeSleepDatasource.sleepList[index] = sleep
        }
    }
}