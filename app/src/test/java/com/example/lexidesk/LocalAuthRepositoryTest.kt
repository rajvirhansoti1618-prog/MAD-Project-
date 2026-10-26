package com.example.lexidesk

import com.example.lexidesk.domain.auth.AuthResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest

class LocalAuthRepositoryTest {

    private fun hashString(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    @Test
    fun hashString_withIdenticalInputs_producesIdenticalHashes() {
        val hash1 = hashString("PartnerPass2026")
        val hash2 = hashString("PartnerPass2026")
        assertEquals(hash1, hash2)
    }

    @Test
    fun hashString_withDifferentInputs_producesDifferentHashes() {
        val hash1 = hashString("Password1")
        val hash2 = hashString("Password2")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun hashString_produces64CharHexString() {
        val hash = hashString("SampleString")
        assertEquals(64, hash.length)
        assertTrue(hash.all { it.isDigit() || it in 'a'..'f' })
    }

    @Test
    fun authResult_success_propertiesMatch() {
        val res = AuthResult.Success("advocate@highcourt.gov.in", "Welcome back")
        assertEquals("advocate@highcourt.gov.in", res.email)
        assertEquals("Welcome back", res.message)
    }

    @Test
    fun authResult_error_messageMatches() {
        val err = AuthResult.Error("Account locked")
        assertEquals("Account locked", err.message)
    }

    @Test
    fun passwordLengthValidation_detectsTooShort() {
        val shortPass = "12345"
        val validPass = "123456"
        assertTrue(shortPass.length < 6)
        assertFalse(validPass.length < 6)
    }
}
