package com.saitejajanjirala.noteswithreminder.domain.models


sealed class Result<T>{
    data class Loading<T>(val isLoading: Boolean = false): Result<T>()
    data class Error<T>(val msg : String?=null):Result<T>()
    data class Success<T>(val data : T?=null): Result<T>()
}