package com.walnuttube.core.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppResultTest {

    @Test
    fun success_holdsValue() {
        val result = AppResult.Success("test value")
        assertEquals("test value", result.value)
    }

    @Test
    fun error_holdsException() {
        val exception = AppException.NetworkException("network error")
        val result = AppResult.Error(exception)
        assertEquals(exception, result.exception)
    }

    @Test
    fun success_patternMatch() {
        val result: AppResult<String> = AppResult.Success("value")
        val matched = when (result) {
            is AppResult.Success -> result.value
            is AppResult.Error -> "error"
        }
        assertEquals("value", matched)
    }

    @Test
    fun error_patternMatch() {
        val result: AppResult<String> = AppResult.Error(AppException.NetworkException("err"))
        val matched = when (result) {
            is AppResult.Success -> "success"
            is AppResult.Error -> result.exception.message
        }
        assertEquals("err", matched)
    }

    @Test
    fun success_equality_sameValue() {
        val result1 = AppResult.Success("same")
        val result2 = AppResult.Success("same")
        assertEquals(result1, result2)
    }

    @Test
    fun error_equality_sameExceptionMessage() {
        val exc1 = AppException.NetworkException("same message")
        val exc2 = AppException.NetworkException("same message")
        val result1 = AppResult.Error(exc1)
        val result2 = AppResult.Error(exc2)
        // Data class equality compares exception instances; check message equivalence
        assertEquals(result1.exception.message, result2.exception.message)
        assertEquals(result1.exception.javaClass, result2.exception.javaClass)
    }

    @Test
    fun success_toString_nonEmpty() {
        val result = AppResult.Success("test")
        val str = result.toString()
        assertTrue(str.isNotEmpty())
        assertTrue(str.contains("test"))
    }

    @Test
    fun error_toString_nonEmpty() {
        val result = AppResult.Error(AppException.NetworkException("network error"))
        val str = result.toString()
        assertTrue(str.isNotEmpty())
        assertTrue(str.contains("network error"))
    }
}