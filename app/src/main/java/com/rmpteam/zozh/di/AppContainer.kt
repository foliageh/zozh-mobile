package com.rmpteam.zozh.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.rmpteam.zozh.data.user.OfflineUserRepository
import com.rmpteam.zozh.data.user.UserPreferencesRepository
import com.rmpteam.zozh.data.user.UserRepository
import com.rmpteam.zozh.data.db.CommonDatabase
import com.rmpteam.zozh.data.nutrition.MealRepository
import com.rmpteam.zozh.data.nutrition.OfflineMealRepository

interface AppContainer {
    val userPreferencesRepository: UserPreferencesRepository
    val mealRepository: MealRepository
    val userRepository: UserRepository
}

class OfflineAppContainer(private val context: Context) : AppContainer {
    override val mealRepository: MealRepository by lazy {
        OfflineMealRepository(CommonDatabase.getDatabase(context).mealDao())
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
    
    override val userRepository: UserRepository by lazy {
        OfflineUserRepository(
            CommonDatabase.getDatabase(context).userDao(),
            userPreferencesRepository
        )
    }
}

private const val PREFERENCES_NAME = "preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME
)
