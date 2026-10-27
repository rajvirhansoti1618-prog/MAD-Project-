package com.example.lexidesk.data.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface CloudDataSource {
    fun getDatabaseReference(path: String): DatabaseReference
    suspend fun putRecord(path: String, recordId: String, data: Map<String, Any?>)
    suspend fun updateFields(path: String, recordId: String, updates: Map<String, Any?>)
    suspend fun deleteRecord(path: String, recordId: String)
    suspend fun fetchRecord(path: String, recordId: String): Map<String, Any?>?
    suspend fun fetchAllRecords(path: String): Map<String, Map<String, Any?>>
}

class FirebaseRealtimeDataSource(
    private val database: FirebaseDatabase
) : CloudDataSource {

    override fun getDatabaseReference(path: String): DatabaseReference {
        return database.getReference(path)
    }

    override suspend fun putRecord(
        path: String,
        recordId: String,
        data: Map<String, Any?>
    ): Unit = withContext(Dispatchers.IO) {
        val targetRef = database.getReference(path).child(recordId)
        suspendCancellableCoroutine { continuation ->
            targetRef.setValue(data) { error, _ ->
                if (error != null) {
                    continuation.resumeWithException(error.toException())
                } else {
                    continuation.resume(Unit)
                }
            }
        }
    }

    override suspend fun updateFields(
        path: String,
        recordId: String,
        updates: Map<String, Any?>
    ): Unit = withContext(Dispatchers.IO) {
        val targetRef = database.getReference(path).child(recordId)
        suspendCancellableCoroutine { continuation ->
            targetRef.updateChildren(updates) { error, _ ->
                if (error != null) {
                    continuation.resumeWithException(error.toException())
                } else {
                    continuation.resume(Unit)
                }
            }
        }
    }

    override suspend fun deleteRecord(
        path: String,
        recordId: String
    ): Unit = withContext(Dispatchers.IO) {
        val targetRef = database.getReference(path).child(recordId)
        suspendCancellableCoroutine { continuation ->
            targetRef.removeValue { error, _ ->
                if (error != null) {
                    continuation.resumeWithException(error.toException())
                } else {
                    continuation.resume(Unit)
                }
            }
        }
    }

    override suspend fun fetchRecord(
        path: String,
        recordId: String
    ): Map<String, Any?>? = withContext(Dispatchers.IO) {
        val targetRef = database.getReference(path).child(recordId)
        suspendCancellableCoroutine { continuation ->
            targetRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    @Suppress("UNCHECKED_CAST")
                    val value = snapshot.value as? Map<String, Any?>
                    continuation.resume(value)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }

    override suspend fun fetchAllRecords(
        path: String
    ): Map<String, Map<String, Any?>> = withContext(Dispatchers.IO) {
        val targetRef = database.getReference(path)
        suspendCancellableCoroutine { continuation ->
            targetRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = mutableMapOf<String, Map<String, Any?>>()
                    for (child in snapshot.children) {
                        val key = child.key ?: continue
                        @Suppress("UNCHECKED_CAST")
                        val map = child.value as? Map<String, Any?>
                        if (map != null) {
                            result[key] = map
                        }
                    }
                    continuation.resume(result)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }
}
