package com.example.lexidesk.domain.auth

sealed class AuthResult {
    data class Success(val email: String, val message: String = "Success") : AuthResult()
    data class Error(val message: String) : AuthResult()
}
