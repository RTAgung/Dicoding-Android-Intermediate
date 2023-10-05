package com.example.submission2.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission2.data.AppRepository
import com.example.submission2.data.model.User
import kotlinx.coroutines.launch

class MapsViewModel(private val appRepository: AppRepository) : ViewModel() {
    var user: User? = null

    private val _isReadyToLoadData = MutableLiveData<Boolean>()
    val isReadyToLoadData: LiveData<Boolean> = _isReadyToLoadData

    fun getUserSession() = appRepository.getUserSession()
    fun setReadyToLoadData(isReady: Boolean) {
        _isReadyToLoadData.value = isReady
    }

    fun clearUserSession() {
        viewModelScope.launch {
            appRepository.clearUserSession()
        }
    }

    fun getStoryWithLocation() = appRepository.getStoryWithLocation(user!!.token)
}