package com.rmpteam.zozh.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.rmpteam.zozh.data.user.UserPreferencesRepository
import com.rmpteam.zozh.data.db.CommonDatabase
import com.rmpteam.zozh.data.nutrition.MealRepository
import com.rmpteam.zozh.data.nutrition.OfflineMealRepository
import com.rmpteam.zozh.data.sleep.FakeSleepRepository
import com.rmpteam.zozh.data.sleep.SleepRepository

interface AppContainer {
    val userPreferencesRepository: UserPreferencesRepository
    val mealRepository: MealRepository
    val sleepRepository: SleepRepository
}

class OfflineAppContainer(private val context: Context) : AppContainer {
    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }

    override val mealRepository: MealRepository by lazy {
        OfflineMealRepository(CommonDatabase.getDatabase(context).mealDao())
    }

    override val sleepRepository: SleepRepository by lazy {
        FakeSleepRepository()
    }
}

private const val PREFERENCES_NAME = "preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME
)