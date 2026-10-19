package com.example.lexidesk.utils

import android.util.Patterns

object ValidationUtils {
    /**
     * Returns true if the given email address is syntactically valid.
     */
    fun isValidEmail(email: String?): Boolean {
        return !email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    /**
     * Returns true if the given phone number contains at least 7 digits.
     */
    fun isValidPhone(phone: String?): Boolean {
        return !phone.isNullOrBlank() && phone.matches(Regex("\\d{7,}"))
    }

}
