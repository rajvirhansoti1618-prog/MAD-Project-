package com.example.lexidesk.domain.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isLoggedIn: Flow<Boolean>
    val currentUserEmail: Flow<String?>

    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String): AuthResult
    suspend fun logout()
    suspend fun sendPasswordReset(email: String): AuthResult
}
