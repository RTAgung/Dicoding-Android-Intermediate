package com.example.submission2.di

import android.content.Context
import com.example.submission2.data.AppRepository
import com.example.submission2.data.source.preferences.AppPreferences
import com.example.submission2.data.source.remote.service.ApiConfig

object AppInjection {
    fun provideRepository(context: Context): AppRepository {
        val apiService = ApiConfig.getApiService()
        val appPreferences = AppPreferences.getInstance(context)
        return AppRepository.getInstance(apiService, appPreferences)
    }
}