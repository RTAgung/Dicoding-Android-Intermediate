package com.example.submission1.data.source.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

class AppPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
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