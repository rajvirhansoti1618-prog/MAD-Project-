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
     * Returns true if the given phone number contains at least 7 digits.
     */
    fun isValidPhone(phone: String?): Boolean {
        return !phone.isNullOrBlank() && phone.matches(Regex("\\d{7,}"))
    }

}
