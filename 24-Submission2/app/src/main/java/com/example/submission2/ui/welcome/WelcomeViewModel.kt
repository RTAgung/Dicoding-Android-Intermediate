package com.example.submission2.ui.welcome

import androidx.lifecycle.ViewModel
import com.example.submission2.data.AppRepository

class WelcomeViewModel(private val appRepository: AppRepository) : ViewModel() {
    fun getUserSession() = appRepository.getUserSession()
}