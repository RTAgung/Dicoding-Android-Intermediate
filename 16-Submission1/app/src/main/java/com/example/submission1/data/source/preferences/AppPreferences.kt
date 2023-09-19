package com.example.submission1.data.source.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.submission1.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun saveUserSession(user: User) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.userId
            preferences[USER_NAME_KEY] = user.name
            preferences[USER_TOKEN_KEY] = user.token
        }
    }

    fun getUserSession(): Flow<User> {
        return dataStore.data.map { preferences ->
            val userId: String = preferences[USER_ID_KEY] ?: ""
            val name: String = preferences[USER_NAME_KEY] ?: ""
            val token: String = preferences[USER_TOKEN_KEY] ?: ""
            User(userId, name, token)
        }
    }

    suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id_key")
        private val USER_NAME_KEY = stringPreferencesKey("user_name_key")
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token_key")

        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "setting_datastore")

        @Volatile
        private var INSTANCE: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AppPreferences(context.dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}