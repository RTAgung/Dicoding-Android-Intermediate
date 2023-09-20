package com.example.submission1.ui.auth

import androidx.lifecycle.ViewModel
import com.example.submission1.data.AppRepository

class AuthViewModel(private val appRepository: AppRepository) : ViewModel() {
    fun makeRegister(name: String, email: String, password: String) =
        appRepository.makeRegister(name, email, password)
}