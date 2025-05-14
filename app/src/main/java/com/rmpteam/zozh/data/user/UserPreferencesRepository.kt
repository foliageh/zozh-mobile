package com.rmpteam.zozh.data.user

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val USER_PROFILE_JSON = stringPreferencesKey("user_profile_json")
    }

    val userProfile: Flow<UserProfile?> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[USER_PROFILE_JSON]?.let {
                try {
                    Json.decodeFromString<UserProfile>(it)
                } catch (e: Exception) {
                    Log.e("UserPreferencesRepository", "userProfile: failed to decode userProfile from json", e)
                    dataStore.edit { prefs -> prefs.remove(USER_PROFILE_JSON) }
                    null
                }
            }
        }

    suspend fun saveUserProfile(userProfile: UserProfile) {
        dataStore.edit { preferences ->
            try {
                preferences[USER_PROFILE_JSON] = Json.encodeToString(userProfile)
            } catch (e: Exception) {
                Log.e("UserPreferencesRepository", "saveUserProfile: failed to encode userProfile to json", e)
            }
        }
    }

    suspend fun clearUserProfile() {
        dataStore.edit { preferences ->
            preferences.remove(USER_PROFILE_JSON)
        }
    }
}