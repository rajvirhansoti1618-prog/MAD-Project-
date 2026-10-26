package com.example.lexidesk

import com.example.lexidesk.domain.auth.AuthRepository
import com.example.lexidesk.domain.auth.AuthResult
import com.example.lexidesk.ui.auth.RegisterViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RegisterViewModelTest {

    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: RegisterViewModel

    private class FakeAuthRepository : AuthRepository {
        var registeredEmail: String? = null
        override val isLoggedIn: Flow<Boolean> = flowOf(false)
        override val currentUserEmail: Flow<String?> = flowOf(null)

        override suspend fun login(email: String, password: String): AuthResult = AuthResult.Success(email)

        override suspend fun register(email: String, password: String): AuthResult {
            registeredEmail = email
            return AuthResult.Success(email, "Registered")
        }

        override suspend fun logout() {}
        override suspend fun sendPasswordReset(email: String): AuthResult = AuthResult.Success(email)
    }

    @Before
    fun setUp() {
        fakeRepository = FakeAuthRepository()
        viewModel = RegisterViewModel(fakeRepository)
    }

    @Test
    fun onEmailChanged_updatesStateAndClearsError() {
        viewModel.onEmailChanged("counsel@lexidesk.com")
        assertEquals("counsel@lexidesk.com", viewModel.uiState.value.email)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun onPasswordChanged_updatesState() {
        viewModel.onPasswordChanged("Secure123")
        assertEquals("Secure123", viewModel.uiState.value.password)
    }

    @Test
    fun onConfirmPasswordChanged_updatesState() {
        viewModel.onConfirmPasswordChanged("Secure123")
        assertEquals("Secure123", viewModel.uiState.value.confirmPassword)
    }

    @Test
    fun toggleVisibility_flipsFlagsCorrectly() {
        assertFalse(viewModel.uiState.value.passwordVisible)
        viewModel.togglePasswordVisibility()
        assertTrue(viewModel.uiState.value.passwordVisible)

        assertFalse(viewModel.uiState.value.confirmPasswordVisible)
        viewModel.toggleConfirmPasswordVisibility()
        assertTrue(viewModel.uiState.value.confirmPasswordVisible)
    }

    @Test
    fun register_withEmptyFields_setsErrorMessage() {
        var success = false
        viewModel.register { success = true }

        assertFalse(success)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals("All fields are required.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun register_withMismatchedPasswords_setsErrorMessage() {
        viewModel.onEmailChanged("user@firm.com")
        viewModel.onPasswordChanged("Pass123")
        viewModel.onConfirmPasswordChanged("DifferentPass123")

        var success = false
        viewModel.register { success = true }

        assertFalse(success)
        assertEquals("Passwords do not match.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun register_withShortPassword_setsErrorMessage() {
        viewModel.onEmailChanged("user@firm.com")
        viewModel.onPasswordChanged("12345")
        viewModel.onConfirmPasswordChanged("12345")

        var success = false
        viewModel.register { success = true }

        assertFalse(success)
        assertEquals("Password must be at least 6 characters.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun dismissError_resetsErrorMessage() {
        viewModel.register {}
        assertNotNull(viewModel.uiState.value.errorMessage)
        viewModel.dismissError()
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
