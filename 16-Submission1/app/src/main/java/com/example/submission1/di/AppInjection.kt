package com.example.submission1.di

import android.content.Context
import com.example.submission1.data.AppRepository
import com.example.submission1.data.source.preferences.AppPreferences
import com.example.submission1.data.source.remote.service.ApiConfig

object AppInjection {
    fun provideRepository(context: Context): AppRepository {
        val apiService = ApiConfig.getApiService()
        val appPreferences = AppPreferences.getInstance(context)
        return AppRepository.getInstance(apiService, appPreferences)
    }
}