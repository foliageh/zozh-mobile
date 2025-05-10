package com.rmpteam.zozh.data.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val CURRENT_USER_PROFILE_JSON = stringPreferencesKey("current_user_profile_json")
    }

    val currentUserProfile: Flow<UserProfile?> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[CURRENT_USER_PROFILE_JSON]?.let {
                try {
                    Json.decodeFromString<UserProfile>(it)
                } catch (e: Exception) {
                    dataStore.edit { prefs -> prefs.remove(CURRENT_USER_PROFILE_JSON) }
                    null 
                }
            }
        }

    suspend fun saveCurrentUserProfile(userProfile: UserProfile?) {
        dataStore.edit { preferences ->
            if (userProfile != null) {
                try {
                    preferences[CURRENT_USER_PROFILE_JSON] = Json.encodeToString(userProfile)
                } catch (e: Exception) {
                    // Log error, don't save corrupted data
                }
            } else {
                preferences.remove(CURRENT_USER_PROFILE_JSON)
            }
        }
    }

    suspend fun clearCurrentUserProfile() {
        dataStore.edit { preferences ->
            preferences.remove(CURRENT_USER_PROFILE_JSON)
        }
    }

    private val CALORIES = stringPreferencesKey("calories_pref_independent")

    val caloriesPreference: Flow<Int?> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else throw it
        }
        .map { preferences ->
            preferences[CALORIES]?.toIntOrNull() ?: 2100
        }

    suspend fun saveCaloriesPreference(calories: Int?) {
        dataStore.edit { preferences ->
            if (calories != null) {
                preferences[CALORIES] = calories.toString()
            } else {
                preferences.remove(CALORIES)
            }
        }
    }
}