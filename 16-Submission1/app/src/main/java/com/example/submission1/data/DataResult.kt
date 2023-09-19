package com.example.submission1.data

sealed class DataResult<out R> private constructor() {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val error: String) : DataResult<Nothing>()
    object Loading : DataResult<Nothing>()
}
