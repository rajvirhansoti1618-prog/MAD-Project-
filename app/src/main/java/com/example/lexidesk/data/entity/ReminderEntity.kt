package com.example.lexidesk.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
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
        ),
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["clientId"]),
        Index(value = ["caseId"]),
        Index(value = ["taskId"])
    ]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val reminderType: String = "Custom", // Court Hearing, Client Meeting, Tax Filing, Document Submission, Payment Due, Assignment Deadline, Follow-up Call, Custom
    val eventTimestamp: Long,
    val minutesBefore: Int = 0, // 0 (at time), 10, 30, 60, 1440 (1 day before)
    val clientId: Long? = null,
    val caseId: Long? = null,
    val taskId: Long? = null,
    val isCompleted: Boolean = false,
    val requestCode: Int = 0
)
