package com.rmpteam.zozh.data.nutrition

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

@Dao
interface MealDao {
    @Query("SELECT * FROM meal WHERE dateTime BETWEEN :startDateTime AND :endDateTime ORDER BY dateTime asc")
    fun getMealsBetweenDateTimes(startDateTime: ZonedDateTime, endDateTime: ZonedDateTime): Flow<List<Meal>>

    @Query("SELECT * FROM meal WHERE id = :id")
    fun getMealById(id: Long): Flow<Meal>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(meal: Meal): Long

    @Update
    fun update(meal: Meal)

    @Delete
    fun delete(meal: Meal)
}