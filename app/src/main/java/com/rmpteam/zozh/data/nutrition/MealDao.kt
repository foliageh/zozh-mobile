package com.rmpteam.zozh.data.nutrition

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meal WHERE date(dateTime, 'unixepoch') = :dateString ORDER BY dateTime asc")
    fun getMealsByDateString(dateString: String): Flow<List<Meal>>  // ISO 8601 date: YYYY-MM-DD

    @Query("SELECT * FROM meal WHERE id = :id")
    fun getMealById(id: Long): Flow<Meal>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(meal: Meal): Long

    @Update
    fun update(meal: Meal)

    @Delete
    fun delete(meal: Meal)
}