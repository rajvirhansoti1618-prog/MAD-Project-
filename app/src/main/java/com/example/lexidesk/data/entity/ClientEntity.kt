package com.example.lexidesk.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val clientType: String = "Individual", // "Individual" or "Business"
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val companyName: String = "",
    val notes: String = "",
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
