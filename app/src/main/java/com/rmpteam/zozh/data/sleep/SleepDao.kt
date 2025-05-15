package com.rmpteam.zozh.data.sleep

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep WHERE startTime BETWEEN :startDateTime AND :endDateTime ORDER BY startTime DESC")
    fun getSleepBetweenDateTimes(startDateTime: ZonedDateTime, endDateTime: ZonedDateTime): Flow<List<Sleep>>

    @Query("SELECT * FROM sleep WHERE id = :id")
    fun getSleepById(id: Long): Flow<Sleep>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(sleep: Sleep): Long

    @Update
    fun update(sleep: Sleep)

    @Delete
    fun delete(sleep: Sleep)
}