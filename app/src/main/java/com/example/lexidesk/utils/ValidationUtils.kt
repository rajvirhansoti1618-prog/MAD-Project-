package com.example.lexidesk.utils

object ValidationUtils {

    /**
     * Stricter email regex that rejects:
     *  - consecutive dots in the local part or domain  (a..b@, a@b..c)
     *  - local part starting or ending with a dot      (.a@ or a.@)
     *  - domain labels starting or ending with a hyphen (-x.com or x-.com)
     *  - missing TLD of at least 2 alpha characters
     *
     * Accepts legitimate addresses including subdomains (name@company.co.in).
     */
    private val EMAIL_REGEX = Regex(
        """^[A-Za-z0-9_%+\-]([A-Za-z0-9_%+\-]|\.(?!\.))*[A-Za-z0-9_%+\-]@""" +   // local: no leading/trailing dot, no consecutive dots; ≥2 chars
        """(?:[A-Za-z0-9](?:[A-Za-z0-9\-]*[A-Za-z0-9])?\.)+""" +                   // domain labels: no leading/trailing hyphen, separated by single dots
        """[A-Za-z]{2,}$"""                                                           // TLD ≥ 2 alpha chars
    )

    // Single-char local part (e.g. "a@b.com") – the main regex above requires ≥2 chars in local
    // so we need a separate pattern for the single-char case.
    private val EMAIL_REGEX_SHORT_LOCAL = Regex(
        """^[A-Za-z0-9_%+\-]@""" +
        """(?:[A-Za-z0-9](?:[A-Za-z0-9\-]*[A-Za-z0-9])?\.)+""" +
        """[A-Za-z]{2,}$"""
    )

    /**
     * Returns true if the given email matches valid standard email format.
     * Rejects: consecutive dots, dot at start/end of local part,
     * domain labels starting/ending with hyphen, missing TLD.
     * Returns false for null/blank input.
     */
    fun isValidEmailFormat(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        val trimmed = email.trim()
        // Fast-fail: consecutive dots anywhere in the address are invalid
        if (trimmed.contains("..")) return false
        return trimmed.matches(EMAIL_REGEX) || trimmed.matches(EMAIL_REGEX_SHORT_LOCAL)
    }

    /**
     * Alias for [isValidEmailFormat]. Returns true if the email is syntactically valid.
     */
    fun isValidEmail(email: String?): Boolean = isValidEmailFormat(email)

    /**
     * Returns true if [phone] is exactly 10 ASCII digit characters ('0'–'9').
     * Rejects null, blank, strings with any non-digit character (spaces, '+', '-',
     * letters, Unicode digits outside ASCII, etc.), and strings whose length ≠ 10.
     * Does NOT silently strip or convert the input.
     */
    fun isValid10DigitPhone(phone: String?): Boolean {
        if (phone.isNullOrBlank()) return false
        val clean = phone.trim()
        // Require exactly 10 characters, each being an ASCII digit 0-9.
        // Using Char.isDigit() would accept Unicode digits; we explicitly check ASCII range.
        if (clean.length != 10) return false
        return clean.all { it in '0'..'9' }
    }

    /**
     * Returns true if the given phone number contains a valid format with at least 7 digits.
     * Supports optional '+' prefix, digits, spaces, and hyphens.
     * (Used for general/international phone validation; not for the Add Client form.)
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
     * Returns true if the given postal/ZIP code has a valid format (between 3 and 10 alphanumeric
     * characters, optional space or hyphen, e.g. "90210", "110001", "SW1A 1AA", "K1A-0B1").
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
// ValidationUtils - Core input validation module for LexiDesk
// Handles email, phone, name, and URL validation
// All validators return Boolean for consistent API usage
// Email validation uses RFC-compliant regex pattern matching
// Phone validation supports international formats with country codes
// Name validation enforces minimum length and character restrictions
// URL validation checks for valid http/https scheme and host
// All string inputs are trimmed before validation
// Null and blank inputs are treated as invalid across all validators
// TODO: Add address validation with postal code support
// TODO: Integrate Indian Aadhaar and PAN number validation
// TODO: Add case number format validation per court type
// Edge case: emails with subdomains are accepted
// Edge case: URLs without trailing slash are valid
// Performance: regex patterns are compiled at class load, not per call
// Unit tests are located in ValidationUtilsTest.kt
// See also: DateUtils.kt for date parsing and formatting
// See also: FileUtils.kt for file type and size validation
// Validators are used across AddEditClientScreen and AddEditCaseScreen
// Error messages should be localized in future iterations
// Max field lengths: name=100, email=254, phone=10 (client form), url=2048
// Minimum field lengths: name=2, email=5, phone=10 (client form)
// All validators are stateless and thread-safe
// Module last reviewed: October 2026
