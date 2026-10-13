package com.example.lexidesk.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "draft_templates")
data class DraftTemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val templateType: String,
    val title: String,
    val content: String,
    val clientId: Long? = null,
    val lastModified: Long = System.currentTimeMillis()
)
