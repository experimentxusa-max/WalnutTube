package com.walnuttube.core.common

/**
 * Represents the result of an operation that can succeed with a value or fail with an [AppException].
 *
 * @param T The success value type.
 */
sealed interface AppResult<out T> {

    /**
     * Operation succeeded with a value.
     */
    data class Success<out T>(val value: T) : AppResult<T>

    /**
     * Operation failed with a structured application exception.
     */
    data class Error(val exception: AppException) : AppResult<Nothing>
}