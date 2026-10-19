package com.example.lexidesk

import com.example.lexidesk.utils.ValidationUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun isValidPhone_withValidDigits_returnsTrue() {
        assertTrue(ValidationUtils.isValidPhone("1234567"))
        assertTrue(ValidationUtils.isValidPhone("9876543210"))
        assertTrue(ValidationUtils.isValidPhone("1234567890123"))
    }

    @Test
    fun isValidPhone_withShortLength_returnsFalse() {
        assertFalse(ValidationUtils.isValidPhone("123456"))
        assertFalse(ValidationUtils.isValidPhone("1"))
    }

    @Test
    fun isValidPhone_withNonDigits_returnsFalse() {
        assertFalse(ValidationUtils.isValidPhone("12345abc"))
        assertFalse(ValidationUtils.isValidPhone("phone-number"))
    }

    @Test
    fun isValidPhone_withNullOrBlank_returnsFalse() {
        assertFalse(ValidationUtils.isValidPhone(null))
        assertFalse(ValidationUtils.isValidPhone(""))
        assertFalse(ValidationUtils.isValidPhone("   "))
    }
}
