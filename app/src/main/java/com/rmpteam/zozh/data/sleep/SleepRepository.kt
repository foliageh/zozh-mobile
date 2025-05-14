// app/src/main/java/com/rmpteam/zozh/data/sleep/SleepRepository.kt
package com.rmpteam.zozh.data.sleep

import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface SleepRepository {
    fun getSleepByDate(dateTime: ZonedDateTime): Flow<List<Sleep>>
    fun getSleepById(id: Long): Flow<Sleep?>
    suspend fun insertSleep(sleep: Sleep): Long
    suspend fun deleteSleep(sleep: Sleep)
    suspend fun updateSleep(sleep: Sleep)
}