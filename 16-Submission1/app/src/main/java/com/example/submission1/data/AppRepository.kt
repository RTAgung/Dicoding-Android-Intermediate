package com.example.submission1.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.submission1.data.model.Story
import com.example.submission1.data.model.User
import com.example.submission1.data.source.preferences.AppPreferences
import com.example.submission1.data.source.remote.service.ApiService
import com.example.submission1.utils.Helper
import com.example.submission1.utils.Mapping

class AppRepository private constructor(
    private val apiService: ApiService,
    private val appPreferences: AppPreferences
) {
    fun makeRegister(
        name: String,
        email: String,
        password: String
    ): LiveData<ResultState<Boolean>> = liveData {
        emit(ResultState.Loading)
        try {
            val requestBody = Mapping.createRegisterRequestBody(name, email, password)
            val dataResponse = apiService.register(requestBody)
            val isSuccess = !(dataResponse.error ?: true)
            emit(ResultState.Success(isSuccess))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun makeLogin(
        email: String,
        password: String
    ): LiveData<ResultState<User?>> = liveData {
        emit(ResultState.Loading)
        try {
            val requestBody = Mapping.createLoginRequestBody(email, password)
            val dataResponse = apiService.login(requestBody)
            if (dataResponse.loginResult != null) {
                val user = Mapping.loginResultResponseToUser(dataResponse.loginResult)
                emit(ResultState.Success(user))
            } else {
                emit(ResultState.Success(null))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    suspend fun saveUserSession(user: User) {
        appPreferences.saveUserSession(user)
    }

    fun getUserSession(): LiveData<User?> = appPreferences.getUserSession().asLiveData()

    suspend fun clearUserSession() {
        appPreferences.clearUserSession()
    }

    fun getStory(token: String): LiveData<ResultState<List<Story>>> = liveData {
        emit(ResultState.Loading)
        try {
            val queryMap = Mapping.createStoryQueryMap()
            val dataResponse = apiService.story(Helper.generateToken(token), queryMap)
            if (dataResponse.error == false) {
                val listStory = Mapping.storyResponseToStory(dataResponse.listStory)
                emit(ResultState.Success(listStory))
            } else {
                emit(ResultState.Error(dataResponse.message ?: ""))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

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