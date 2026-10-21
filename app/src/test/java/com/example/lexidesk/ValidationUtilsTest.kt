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
    fun isValidEmailFormat_withValidEmail_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmailFormat("test@example.com"))
        assertTrue(ValidationUtils.isValidEmailFormat("advocate.sharma@lawfirm.co.in"))
        assertTrue(ValidationUtils.isValidEmailFormat("audit.team_123@corporation.org"))
    }

    @Test
    fun isValidEmailFormat_withInvalidEmail_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmailFormat("invalid-email"))
        assertFalse(ValidationUtils.isValidEmailFormat("@domain.com"))
        assertFalse(ValidationUtils.isValidEmailFormat("user@.com"))
        assertFalse(ValidationUtils.isValidEmailFormat(null))
        assertFalse(ValidationUtils.isValidEmailFormat(""))
    }

    @Test
    fun isValidTitle_withValidString_returnsTrue() {
        assertTrue(ValidationUtils.isValidTitle("Client Case", 2))
        assertTrue(ValidationUtils.isValidTitle("Tax Audit", 3))
    }

    @Test
    fun isValidTitle_withShortOrBlank_returnsFalse() {
        assertFalse(ValidationUtils.isValidTitle("a", 2))
        assertFalse(ValidationUtils.isValidTitle("", 2))
        assertFalse(ValidationUtils.isValidTitle("   ", 2))
        assertFalse(ValidationUtils.isValidTitle(null, 2))
    }

    @Test
    fun isValidTitle_withWhitespacePadding_trimsCorrectly() {
        assertTrue(ValidationUtils.isValidTitle("   ab   ", 2))
        assertFalse(ValidationUtils.isValidTitle("   a   ", 2))
        assertTrue(ValidationUtils.isValidTitle("M/s ABC Corp v. State (2026)", 5))
    }

    @Test
    fun isValidEmailFormat_withSubdomains_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmailFormat("partner@mail.delhi.lawfirm.in"))
    }
}
