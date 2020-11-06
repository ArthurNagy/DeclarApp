package com.arthurnagy.staysafe.core

/**
 * Wrapper class exposing a given data type of [T] to the UI layer from the Data layer(use-case, repository, source).
 * Can represent multiple states:
 * * [Success] -
 * * [Error] -
 */
sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Error<out T>(val exception: Throwable) : ResultWrapper<T>()

    companion object {
        /**
         * Wrap a given [function] into a [ResultWrapper] of either [ResultWrapper.Success] in case the function succeeds or [ResultWrapper.Error] in case of a known [Exception]
         */
        suspend operator fun <T> invoke(function: suspend () -> T): ResultWrapper<T> = try {
            Success(function())
        } catch (exception: Exception) {
            Error(exception)
        }
    }
}