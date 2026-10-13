package com.example.lexidesk.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["caseId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["clientId"]),
        Index(value = ["caseId"])
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val clientId: Long? = null,
    val caseId: Long? = null,
    val priority: String = "Medium", // Low, Medium, High, Urgent
    val status: String = "Pending", // Pending, In Progress, Completed
    val dueDate: Long? = null,
    val dueTime: String? = null,
    val reminderEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
