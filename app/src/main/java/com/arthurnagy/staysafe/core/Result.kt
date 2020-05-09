package com.arthurnagy.staysafe.core

/**
 * Wrapper class exposing a given data type of [T] to the UI layer from the Data layer(use-case, repository, source).
 * Can represent multiple states:
 * * [Success] -
 * * [Error] -
 */
sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class Error<out T>(val exception: Throwable) : Result<T>()

    companion object {
        /**
         * Wrap a given [function] into a [Result] of either [Result.Success] in case the function succeeds or [Result.Error] in case of a known [Exception]
         */
        suspend operator fun <T> invoke(function: suspend () -> T): Result<T> = try {
            Success(function())
        } catch (exception: Exception) {
            Error(exception)
        }
    }
}