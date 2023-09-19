package com.example.submission1.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submission1.data.AppRepository
import com.example.submission1.di.AppInjection
import com.example.submission1.ui.auth.AuthViewModel
import com.example.submission1.ui.home.HomeViewModel
import com.example.submission1.ui.home.create.AddStoryViewModel
import com.example.submission1.ui.welcome.WelcomeViewModel

class ViewModelFactory private constructor(private val appRepository: AppRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(appRepository) as T
        } else if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return WelcomeViewModel(appRepository) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(appRepository) as T
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(appRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(AppInjection.provideRepository(context))
        }.also { instance = it }
    }
}