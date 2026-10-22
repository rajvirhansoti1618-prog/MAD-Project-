package com.example.lexidesk.utils

import android.util.Patterns

object ValidationUtils {
    /**
     * Returns true if the given email address is syntactically valid.
     */
    fun isValidEmail(email: String?): Boolean {
        return !email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    /**
     * Returns true if the given email matches valid standard email format.
     */
    fun isValidEmailFormat(email: String?): Boolean {
        return !email.isNullOrBlank() && email.trim().matches(EMAIL_REGEX)
    }

    /**
     * Returns true if the given phone number contains a valid format with at least 7 digits.
     * Supports optional '+' prefix, digits, spaces, and hyphens.
     */
    fun isValidPhone(phone: String?): Boolean {
        if (phone.isNullOrBlank()) return false
        val clean = phone.trim()
        val digitsOnly = clean.filter { it.isDigit() }
        if (digitsOnly.length < 7 || digitsOnly.length > 15) return false
        return clean.matches(Regex("^\\+?[0-9\\-\\s]+$"))
    }

    /**
     * Returns true if the given title is not blank and has at least the specified minimum length.
     */
    fun isValidTitle(title: String?, minLength: Int = 2): Boolean {
        return !title.isNullOrBlank() && title.trim().length >= minLength
    }

    /**
     * Returns true if the given postal/ZIP code has a valid format (between 3 and 10 alphanumeric characters,
     * optional space or hyphen, e.g. "90210", "110001", "SW1A 1AA", "K1A-0B1").
     */
    fun isValidPostalCode(code: String?): Boolean {
        if (code.isNullOrBlank()) return false
        val clean = code.trim()
        val alphanumericOnly = clean.filter { it.isLetterOrDigit() }
        if (alphanumericOnly.length < 3 || alphanumericOnly.length > 10) return false
        return clean.matches(Regex("^[A-Za-z0-9]+([\\s\\-][A-Za-z0-9]+)?$"))
    }

    /**
     * Returns true if the given URL has a recognizable scheme (http or https) and a non-blank host.
     */
    fun isValidUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val trimmed = url.trim()
        return trimmed.matches(Regex("^https?://[a-zA-Z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=%]+$"))
    }
}
// micro change 1
// micro change 2
// micro change 3
// micro change 4
// micro change 5
// micro change 6
// micro change 7
// micro change 8
// micro change 9
// micro change 10
// micro change 11
// micro change 12
// micro change 13
// micro change 14
// micro change 15
// micro change 16
// micro change 17
// micro change 18
// micro change 19
// micro change 20
// micro change 21
// micro change 22
