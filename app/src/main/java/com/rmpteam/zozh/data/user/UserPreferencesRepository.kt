package com.rmpteam.zozh.data.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val CALORIES = intPreferencesKey("calories")
    }

    val calories: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else throw it
        }
        .map { preferences ->
            preferences[CALORIES] ?: 2100
        }

    suspend fun saveCaloriesPreference(calories: Int) {
        dataStore.edit { preferences ->
            preferences[CALORIES] = calories
        }
    }
}