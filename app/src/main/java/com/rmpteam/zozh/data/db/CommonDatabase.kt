package com.rmpteam.zozh.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rmpteam.zozh.data.nutrition.Meal
import com.rmpteam.zozh.data.nutrition.MealDao
import com.rmpteam.zozh.data.sleep.Sleep
import com.rmpteam.zozh.data.sleep.SleepDao
import kotlinx.coroutines.Dispatchers

@Database(entities = [Meal::class, Sleep::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CommonDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao
    abstract fun sleepDao(): SleepDao

    companion object {
        @Volatile
        private var Instance: CommonDatabase? = null

        fun getDatabase(context: Context): CommonDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CommonDatabase::class.java, "common_database")
                    .setQueryCoroutineContext(Dispatchers.IO)
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}