package com.rmpteam.zozh.data.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val CALORIES = intPreferencesKey("calories")
        val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
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
        
    val currentUserId: Flow<String?> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else throw it
        }
        .map { preferences ->
            preferences[CURRENT_USER_ID]
        }

    suspend fun saveCaloriesPreference(calories: Int) {
        dataStore.edit { preferences ->
            preferences[CALORIES] = calories
        }
    }
    
    suspend fun saveCurrentUserId(userId: String?) {
        dataStore.edit { preferences ->
            if (userId != null) {
                preferences[CURRENT_USER_ID] = userId
            } else {
                preferences.remove(CURRENT_USER_ID)
            }
        }
    }
}