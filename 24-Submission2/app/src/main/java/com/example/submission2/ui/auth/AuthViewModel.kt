package com.example.submission2.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission2.data.AppRepository
import com.example.submission2.data.model.User
import kotlinx.coroutines.launch

class AuthViewModel(private val appRepository: AppRepository) : ViewModel() {
    fun makeRegister(name: String, email: String, password: String) =
        appRepository.makeRegister(name, email, password)

    fun makeLogin(email: String, password: String) = appRepository.makeLogin(email, password)

    fun saveUserSession(user: User) {
        viewModelScope.launch {
            appRepository.saveUserSession(user)
        }
    }
}