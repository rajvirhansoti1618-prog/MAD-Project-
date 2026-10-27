package com.example.lexidesk.data.firebase

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthService(
    context: Context
) {
    private val auth: FirebaseAuth by lazy {
        FirebaseConfig.initialize(context)
        FirebaseAuth.getInstance()
    }

    private val _currentUserState = MutableStateFlow<FirebaseUser?>(null)
    val currentUserState: StateFlow<FirebaseUser?> = _currentUserState.asStateFlow()

    init {
        _currentUserState.value = auth.currentUser
        auth.addAuthStateListener { firebaseAuth ->
            _currentUserState.value = firebaseAuth.currentUser
        }
    }

    val currentUid: String?
        get() = auth.currentUser?.uid

    val isAuthenticated: Boolean
        get() = auth.currentUser != null

    suspend fun ensureAuthenticated(): FirebaseUser = withContext(Dispatchers.IO) {
        val existing = auth.currentUser
        if (existing != null) {
            return@withContext existing
        }

        suspendCancellableCoroutine { continuation ->
            auth.signInAnonymously()
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null) {
                        continuation.resume(user)
                    } else {
                        continuation.resumeWithException(IllegalStateException("Anonymous sign-in returned null user"))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    suspend fun signInWithEmail(email: String, password: String): FirebaseUser = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null) {
                        continuation.resume(user)
                    } else {
                        continuation.resumeWithException(IllegalStateException("Sign in returned null user"))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    suspend fun registerWithEmail(email: String, password: String): FirebaseUser = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null) {
                        continuation.resume(user)
                    } else {
                        continuation.resumeWithException(IllegalStateException("Registration returned null user"))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUserState.value = null
    }
}
