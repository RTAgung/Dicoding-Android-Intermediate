package com.example.submission1.data

import com.example.submission1.data.source.preferences.AppPreferences
import com.example.submission1.data.source.remote.service.ApiService

class AppRepository private constructor(
    private val apiService: ApiService,
    private val appPreferences: AppPreferences
) {


    companion object {
        @Volatile
        private var instance: AppRepository? = null

        fun getInstance(
            apiService: ApiService, appPreferences: AppPreferences
        ): AppRepository = instance ?: synchronized(this) {
            instance ?: AppRepository(apiService, appPreferences)
        }.also { instance = it }
    }
}