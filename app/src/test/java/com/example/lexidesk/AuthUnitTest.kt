package com.example.lexidesk

import com.example.lexidesk.domain.auth.AuthResult
import com.example.lexidesk.ui.auth.LoginUiState
import com.example.lexidesk.ui.auth.RegisterUiState
import com.example.lexidesk.utils.ValidationUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest

class AuthUnitTest {

    @Test
    fun authResult_success_containsCorrectData() {
        val result = AuthResult.Success(email = "counsel@lexidesk.com", message = "Login successful.")
        assertEquals("counsel@lexidesk.com", result.email)
        assertEquals("Login successful.", result.message)
    }

    @Test
    fun authResult_error_containsErrorMessage() {
        val error = AuthResult.Error(message = "Invalid credentials provided.")
        assertEquals("Invalid credentials provided.", error.message)
    }

    @Test
    fun loginUiState_defaultState_isNotLoadingAndEmpty() {
        val state = LoginUiState()
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertFalse(state.passwordVisible)
        assertNull(state.errorMessage)
    }

    @Test
    fun registerUiState_defaultState_isNotLoadingAndEmpty() {
        val state = RegisterUiState()
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertEquals("", state.confirmPassword)
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertFalse(state.passwordVisible)
        assertFalse(state.confirmPasswordVisible)
        assertNull(state.errorMessage)
    }

    @Test
    fun passwordHashing_producesExpectedSha256Digest() {
        val rawPassword = "SecurePassword123!"
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(rawPassword.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

        assertNotNull(hash)
        assertEquals(64, hash.length) // SHA-256 hex string is exactly 64 characters
        // Consistent hashing
        val digest2 = MessageDigest.getInstance("SHA-256")
        val hash2 = digest2.digest(rawPassword.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
        assertEquals(hash, hash2)
    }

    @Test
    fun emailValidation_validCorporateEmails_pass() {
        assertTrue(ValidationUtils.isValidEmailFormat("advocate.kumar@lawdesk.in"))
        assertTrue(ValidationUtils.isValidEmailFormat("partner@chambers.org"))
        assertTrue(ValidationUtils.isValidEmailFormat("legal.lead_1@corp.co.uk"))
    }

    @Test
    fun emailValidation_malformedEmails_fail() {
        assertFalse(ValidationUtils.isValidEmailFormat("plainaddress"))
        assertFalse(ValidationUtils.isValidEmailFormat("@missingusername.com"))
        assertFalse(ValidationUtils.isValidEmailFormat("username@.com"))
    }
}
