package com.rmpteam.zozh.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.rmpteam.zozh.data.db.CommonDatabase
import com.rmpteam.zozh.data.nutrition.MealRepository
import com.rmpteam.zozh.data.nutrition.OfflineMealRepository
import com.rmpteam.zozh.data.user.FakeUserProfileDatasource
import com.rmpteam.zozh.data.user.FakeUserRepository
import com.rmpteam.zozh.data.user.UserPreferencesRepository
import com.rmpteam.zozh.data.user.UserRepository

interface AppContainer {
    val userPreferencesRepository: UserPreferencesRepository
    val fakeUserProfileDatasource: FakeUserProfileDatasource
    val userRepository: UserRepository
    val mealRepository: MealRepository
}

class OfflineAppContainer(private val context: Context) : AppContainer {
    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
    
    override val fakeUserProfileDatasource: FakeUserProfileDatasource by lazy {
        FakeUserProfileDatasource()
    }

    override val userRepository: UserRepository by lazy {
        FakeUserRepository(fakeUserProfileDatasource = fakeUserProfileDatasource)
    }

    override val mealRepository: MealRepository by lazy {
        OfflineMealRepository(CommonDatabase.getDatabase(context).mealDao())
    }
}

private const val PREFERENCES_NAME = "preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME
)
