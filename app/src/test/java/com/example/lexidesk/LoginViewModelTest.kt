package com.example.lexidesk

import com.example.lexidesk.domain.auth.AuthRepository
import com.example.lexidesk.domain.auth.AuthResult
import com.example.lexidesk.ui.auth.LoginViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: LoginViewModel

    private class FakeAuthRepository : AuthRepository {
        var shouldSucceed: Boolean = true
        override val isLoggedIn: Flow<Boolean> = flowOf(false)
        override val currentUserEmail: Flow<String?> = flowOf(null)

        override suspend fun login(email: String, password: String): AuthResult {
            return if (shouldSucceed) {
                AuthResult.Success(email, "Success")
            } else {
                AuthResult.Error("Invalid credentials")
            }
        }

        override suspend fun register(email: String, password: String): AuthResult = AuthResult.Success(email)
        override suspend fun logout() {}
        override suspend fun sendPasswordReset(email: String): AuthResult = AuthResult.Success(email)
    }

    @Before
    fun setUp() {
        fakeRepository = FakeAuthRepository()
        viewModel = LoginViewModel(fakeRepository)
    }

    @Test
    fun onEmailChanged_updatesStateAndClearsError() {
        viewModel.onEmailChanged("advocate@lexidesk.in")
        assertEquals("advocate@lexidesk.in", viewModel.uiState.value.email)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun onPasswordChanged_updatesStateAndClearsError() {
        viewModel.onPasswordChanged("SecretPass123!")
        assertEquals("SecretPass123!", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun togglePasswordVisibility_invertsVisibilityFlag() {
        assertFalse(viewModel.uiState.value.passwordVisible)
        viewModel.togglePasswordVisibility()
        assertTrue(viewModel.uiState.value.passwordVisible)
        viewModel.togglePasswordVisibility()
        assertFalse(viewModel.uiState.value.passwordVisible)
    }

    @Test
    fun login_withEmptyFields_setsErrorMessageWithoutCallingRepository() {
        var successCalled = false
        viewModel.login { successCalled = true }

        assertFalse(successCalled)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals("Email and password cannot be empty.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun dismissError_clearsErrorMessage() {
        viewModel.login {}
        assertNotNull(viewModel.uiState.value.errorMessage)
        viewModel.dismissError()
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
