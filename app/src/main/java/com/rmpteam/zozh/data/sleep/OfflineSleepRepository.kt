package com.rmpteam.zozh.data.sleep

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.ZonedDateTime

class OfflineSleepRepository : SleepRepository {

    override fun getSleepByDate(dateTime: ZonedDateTime): Flow<List<Sleep>> {
        
        val startDay = dateTime.minusDays(30)

        val filteredList = FakeSleepDatasource.sleepList.filter {
            (it.startTime.isAfter(startDay) || it.startTime.isEqual(startDay)) &&
                    (!it.startTime.isAfter(dateTime))
        }

        println("Получено записей о сне: ${filteredList.size} (период $startDay - $dateTime)")
        println("Общее количество записей: ${FakeSleepDatasource.sleepList.size}")

        return flowOf(filteredList)
    }

    override fun getSleepById(id: Long): Flow<Sleep?> {
        return flowOf(FakeSleepDatasource.sleepList.find { it.id == id })
    }

    override suspend fun insertSleep(sleep: Sleep): Long {
        val id = FakeSleepDatasource.addSleep(sleep)
        println("Добавлена запись через репозиторий, ID: $id")
        return id
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