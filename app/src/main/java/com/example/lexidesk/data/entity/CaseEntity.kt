package com.example.lexidesk.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cases",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["clientId"])]
)
data class CaseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Long? = null,
    val title: String,
    val category: String = "General Advisory",
    val description: String = "",
    val referenceNumber: String = "",
    val priority: String = "Medium", // Low, Medium, High, Urgent
    val status: String = "In Progress", // Not Started, In Progress, Waiting, Completed, On Hold
    val startDate: Long? = null,
    val dueDate: Long? = null,
    val courtOrLocation: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    @get:Ignore
    val priorityWeight: Int
        get() = when (priority.trim().lowercase()) {
            "urgent" -> 4
            "high" -> 3
            "medium" -> 2
            "low" -> 1
            else -> 0
        }
}
