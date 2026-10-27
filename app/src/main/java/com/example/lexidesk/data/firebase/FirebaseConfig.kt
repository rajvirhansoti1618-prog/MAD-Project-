package com.example.lexidesk.data.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase

object FirebaseConfig {
    const val DATABASE_URL = "https://lexidesk-b3d7b-default-rtdb.asia-southeast1.firebasedatabase.app"
    const val PROJECT_ID = "lexidesk-b3d7b"
    const val APPLICATION_ID = "com.example.lexidesk"

    @Synchronized
    fun initialize(context: Context): FirebaseApp {
        val existing = FirebaseApp.getApps(context).firstOrNull()
        if (existing != null) {
            return existing
        }

        val options = FirebaseOptions.Builder()
            .setApplicationId(APPLICATION_ID)
            .setProjectId(PROJECT_ID)
            .setDatabaseUrl(DATABASE_URL)
            .build()

        return FirebaseApp.initializeApp(context.applicationContext, options)
    }

    @Synchronized
    fun getDatabase(context: Context): FirebaseDatabase {
        initialize(context)
        val db = FirebaseDatabase.getInstance(DATABASE_URL)
        try {
            db.setPersistenceEnabled(true)
        } catch (_: Exception) {
            // Ignored if persistence was already configured
        }
        return db
    }
}
