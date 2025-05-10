package com.rmpteam.zozh.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rmpteam.zozh.data.user.UserDao
import com.rmpteam.zozh.data.user.UserProfile
import com.rmpteam.zozh.data.nutrition.Meal
import com.rmpteam.zozh.data.nutrition.MealDao
import kotlinx.coroutines.Dispatchers

@Database(entities = [Meal::class, UserProfile::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CommonDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var Instance: CommonDatabase? = null

        fun getDatabase(context: Context): CommonDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CommonDatabase::class.java, "common_database")
                    //.createFromAsset("database/app.db")
                    .setQueryCoroutineContext(Dispatchers.IO)
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}