package com.example.lexidesk.data.auth

import com.example.lexidesk.data.local.UserPreferencesRepository
import com.example.lexidesk.domain.auth.AuthRepository
import com.example.lexidesk.domain.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.security.MessageDigest

class LocalAuthRepository(
    private val userPreferencesRepository: UserPreferencesRepository
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = userPreferencesRepository.authLoggedInFlow

    override val currentUserEmail: Flow<String?> = userPreferencesRepository.authUserEmailFlow

    override suspend fun login(email: String, password: String): AuthResult {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            return AuthResult.Error("Email and password cannot be empty.")
        }

        val storedEmail = userPreferencesRepository.authUserEmailFlow.first()
        val storedHash = userPreferencesRepository.authPasswordHashFlow.first()

        val passwordHash = hashString(trimmedPassword)

        // If no credentials registered yet, allow instant first-user setup or demo login
        if (storedEmail == null || storedHash == null) {
            userPreferencesRepository.setAuthState(
                isLoggedIn = true,
                email = trimmedEmail,
                passwordHash = passwordHash
            )
            return AuthResult.Success(trimmedEmail, "Welcome! Account created successfully.")
        }

        if (storedEmail.equals(trimmedEmail, ignoreCase = true) && storedHash == passwordHash) {
            userPreferencesRepository.setAuthState(isLoggedIn = true, email = storedEmail)
            return AuthResult.Success(storedEmail, "Login successful.")
        }

        return AuthResult.Error("Invalid email or password.")
    }

    override suspend fun register(email: String, password: String): AuthResult {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank()) {
            return AuthResult.Error("Email cannot be empty.")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return AuthResult.Error("Please enter a valid email address.")
        }
        if (trimmedPassword.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters.")
        }

        val passwordHash = hashString(trimmedPassword)
        userPreferencesRepository.setAuthState(
            isLoggedIn = true,
            email = trimmedEmail,
            passwordHash = passwordHash
        )
        return AuthResult.Success(trimmedEmail, "Account registered successfully.")
    }

    override suspend fun logout() {
        userPreferencesRepository.clearAuthState()
    }

    override suspend fun sendPasswordReset(email: String): AuthResult {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return AuthResult.Error("Please enter a valid email address.")
        }
        return AuthResult.Success(trimmedEmail, "Password reset link sent (demo mode).")
    }

    private fun hashString(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}
