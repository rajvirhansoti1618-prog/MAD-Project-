package com.example.lexidesk.data.firebase.model

import com.example.lexidesk.data.entity.CaseEntity
import com.example.lexidesk.data.entity.ClientEntity
import com.example.lexidesk.data.entity.DraftTemplateEntity
import com.example.lexidesk.data.entity.NoteEntity
import com.example.lexidesk.data.entity.ReminderEntity
import com.example.lexidesk.data.entity.TaskEntity

data class CloudClientDto(
    val id: Long = 0L,
    val fullName: String = "",
    val clientType: String = "Individual",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val companyName: String = "",
    val notes: String = "",
    val tags: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "fullName" to fullName,
        "clientType" to clientType,
        "phone" to phone,
        "email" to email,
        "address" to address,
        "companyName" to companyName,
        "notes" to notes,
        "tags" to tags,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "isDeleted" to isDeleted
    )

    fun toEntity(): ClientEntity = ClientEntity(
        id = id,
        fullName = fullName,
        clientType = clientType,
        phone = phone,
        email = email,
        address = address,
        companyName = companyName,
        notes = notes,
        tags = tags,
        createdAt = createdAt
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): CloudClientDto {
            return CloudClientDto(
                id = (map["id"] as? Number)?.toLong() ?: 0L,
                fullName = map["fullName"] as? String ?: "",
                clientType = map["clientType"] as? String ?: "Individual",
                phone = map["phone"] as? String ?: "",
                email = map["email"] as? String ?: "",
                address = map["address"] as? String ?: "",
                companyName = map["companyName"] as? String ?: "",
                notes = map["notes"] as? String ?: "",
                tags = map["tags"] as? String ?: "",
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0L,
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                isDeleted = map["isDeleted"] as? Boolean ?: false
            )
        }
    }
}

fun ClientEntity.toCloudDto(
    updatedAt: Long = System.currentTimeMillis(),
    isDeleted: Boolean = false
): CloudClientDto = CloudClientDto(
    id = id,
    fullName = fullName,
    clientType = clientType,
    phone = phone,
    email = email,
    address = address,
    companyName = companyName,
    notes = notes,
    tags = tags,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)

data class CloudCaseDto(
    val id: Long = 0L,
    val clientId: Long? = null,
    val title: String = "",
    val category: String = "General Advisory",
    val description: String = "",
    val referenceNumber: String = "",
    val priority: String = "Medium",
    val status: String = "In Progress",
    val startDate: Long? = null,
    val dueDate: Long? = null,
    val courtOrLocation: String = "",
    val notes: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "clientId" to clientId,
        "title" to title,
        "category" to category,
        "description" to description,
        "referenceNumber" to referenceNumber,
        "priority" to priority,
        "status" to status,
        "startDate" to startDate,
        "dueDate" to dueDate,
        "courtOrLocation" to courtOrLocation,
        "notes" to notes,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "isDeleted" to isDeleted
    )

    fun toEntity(): CaseEntity = CaseEntity(
        id = id,
        clientId = clientId,
        title = title,
        category = category,
        description = description,
        referenceNumber = referenceNumber,
        priority = priority,
        status = status,
        startDate = startDate,
        dueDate = dueDate,
        courtOrLocation = courtOrLocation,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): CloudCaseDto {
            return CloudCaseDto(
                id = (map["id"] as? Number)?.toLong() ?: 0L,
                clientId = (map["clientId"] as? Number)?.toLong(),
                title = map["title"] as? String ?: "",
                category = map["category"] as? String ?: "General Advisory",
                description = map["description"] as? String ?: "",
                referenceNumber = map["referenceNumber"] as? String ?: "",
                priority = map["priority"] as? String ?: "Medium",
                status = map["status"] as? String ?: "In Progress",
                startDate = (map["startDate"] as? Number)?.toLong(),
                dueDate = (map["dueDate"] as? Number)?.toLong(),
                courtOrLocation = map["courtOrLocation"] as? String ?: "",
                notes = map["notes"] as? String ?: "",
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0L,
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                isDeleted = map["isDeleted"] as? Boolean ?: false
            )
        }
    }
}

fun CaseEntity.toCloudDto(
    updatedAt: Long = this.updatedAt,
    isDeleted: Boolean = false
): CloudCaseDto = CloudCaseDto(
    id = id,
    clientId = clientId,
    title = title,
    category = category,
    description = description,
    referenceNumber = referenceNumber,
    priority = priority,
    status = status,
    startDate = startDate,
    dueDate = dueDate,
    courtOrLocation = courtOrLocation,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)

data class CloudTaskDto(
    val id: Long = 0L,
    val title: String = "",
    val description: String = "",
    val clientId: Long? = null,
    val caseId: Long? = null,
    val priority: String = "Medium",
    val status: String = "Pending",
    val dueDate: Long? = null,
    val dueTime: String? = null,
    val reminderEnabled: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "clientId" to clientId,
        "caseId" to caseId,
        "priority" to priority,
        "status" to status,
        "dueDate" to dueDate,
        "dueTime" to dueTime,
        "reminderEnabled" to reminderEnabled,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "isDeleted" to isDeleted
    )

    fun toEntity(): TaskEntity = TaskEntity(
        id = id,
        title = title,
        description = description,
        clientId = clientId,
        caseId = caseId,
        priority = priority,
        status = status,
        dueDate = dueDate,
        dueTime = dueTime,
        reminderEnabled = reminderEnabled,
        createdAt = createdAt
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): CloudTaskDto {
            return CloudTaskDto(
                id = (map["id"] as? Number)?.toLong() ?: 0L,
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: "",
                clientId = (map["clientId"] as? Number)?.toLong(),
                caseId = (map["caseId"] as? Number)?.toLong(),
                priority = map["priority"] as? String ?: "Medium",
                status = map["status"] as? String ?: "Pending",
                dueDate = (map["dueDate"] as? Number)?.toLong(),
                dueTime = map["dueTime"] as? String,
                reminderEnabled = map["reminderEnabled"] as? Boolean ?: false,
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0L,
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                isDeleted = map["isDeleted"] as? Boolean ?: false
            )
        }
    }
}

fun TaskEntity.toCloudDto(
    updatedAt: Long = System.currentTimeMillis(),
    isDeleted: Boolean = false
): CloudTaskDto = CloudTaskDto(
    id = id,
    title = title,
    description = description,
    clientId = clientId,
    caseId = caseId,
    priority = priority,
    status = status,
    dueDate = dueDate,
    dueTime = dueTime,
    reminderEnabled = reminderEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)

data class CloudNoteDto(
    val id: Long = 0L,
    val clientId: Long? = null,
    val caseId: Long? = null,
    val title: String = "",
    val content: String = "",
    val followUpDate: Long? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "clientId" to clientId,
        "caseId" to caseId,
        "title" to title,
        "content" to content,
        "followUpDate" to followUpDate,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "isDeleted" to isDeleted
    )

    fun toEntity(): NoteEntity = NoteEntity(
        id = id,
        clientId = clientId,
        caseId = caseId,
        title = title,
        content = content,
        followUpDate = followUpDate,
        createdAt = createdAt
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): CloudNoteDto {
            return CloudNoteDto(
                id = (map["id"] as? Number)?.toLong() ?: 0L,
                clientId = (map["clientId"] as? Number)?.toLong(),
                caseId = (map["caseId"] as? Number)?.toLong(),
                title = map["title"] as? String ?: "",
                content = map["content"] as? String ?: "",
                followUpDate = (map["followUpDate"] as? Number)?.toLong(),
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0L,
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                isDeleted = map["isDeleted"] as? Boolean ?: false
            )
        }
    }
}

fun NoteEntity.toCloudDto(
    updatedAt: Long = System.currentTimeMillis(),
    isDeleted: Boolean = false
): CloudNoteDto = CloudNoteDto(
    id = id,
    clientId = clientId,
    caseId = caseId,
    title = title,
    content = content,
    followUpDate = followUpDate,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)

data class CloudReminderDto(
    val id: Long = 0L,
    val title: String = "",
    val description: String = "",
    val reminderType: String = "Custom",
    val eventTimestamp: Long = 0L,
    val minutesBefore: Int = 0,
    val clientId: Long? = null,
    val caseId: Long? = null,
    val taskId: Long? = null,
    val isCompleted: Boolean = false,
    val requestCode: Int = 0,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "reminderType" to reminderType,
        "eventTimestamp" to eventTimestamp,
        "minutesBefore" to minutesBefore,
        "clientId" to clientId,
        "caseId" to caseId,
        "taskId" to taskId,
        "isCompleted" to isCompleted,
        "requestCode" to requestCode,
        "updatedAt" to updatedAt,
        "isDeleted" to isDeleted
    )

    fun toEntity(): ReminderEntity = ReminderEntity(
        id = id,
        title = title,
        description = description,
        reminderType = reminderType,
        eventTimestamp = eventTimestamp,
        minutesBefore = minutesBefore,
        clientId = clientId,
        caseId = caseId,
        taskId = taskId,
        isCompleted = isCompleted,
        requestCode = requestCode
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): CloudReminderDto {
            return CloudReminderDto(
                id = (map["id"] as? Number)?.toLong() ?: 0L,
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: "",
                reminderType = map["reminderType"] as? String ?: "Custom",
                eventTimestamp = (map["eventTimestamp"] as? Number)?.toLong() ?: 0L,
                minutesBefore = (map["minutesBefore"] as? Number)?.toInt() ?: 0,
                clientId = (map["clientId"] as? Number)?.toLong(),
                caseId = (map["caseId"] as? Number)?.toLong(),
                taskId = (map["taskId"] as? Number)?.toLong(),
                isCompleted = map["isCompleted"] as? Boolean ?: false,
                requestCode = (map["requestCode"] as? Number)?.toInt() ?: 0,
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                isDeleted = map["isDeleted"] as? Boolean ?: false
            )
        }
    }
}

fun ReminderEntity.toCloudDto(
    updatedAt: Long = System.currentTimeMillis(),
    isDeleted: Boolean = false
): CloudReminderDto = CloudReminderDto(
    id = id,
    title = title,
    description = description,
    reminderType = reminderType,
    eventTimestamp = eventTimestamp,
    minutesBefore = minutesBefore,
    clientId = clientId,
    caseId = caseId,
    taskId = taskId,
    isCompleted = isCompleted,
    requestCode = requestCode,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)

data class CloudDraftTemplateDto(
    val id: Long = 0L,
    val templateType: String = "",
    val title: String = "",
    val content: String = "",
    val clientId: Long? = null,
    val lastModified: Long = 0L,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "templateType" to templateType,
        "title" to title,
        "content" to content,
        "clientId" to clientId,
        "lastModified" to lastModified,
        "updatedAt" to updatedAt,
        "isDeleted" to isDeleted
    )

    fun toEntity(): DraftTemplateEntity = DraftTemplateEntity(
        id = id,
        templateType = templateType,
        title = title,
        content = content,
        clientId = clientId,
        lastModified = lastModified
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): CloudDraftTemplateDto {
            return CloudDraftTemplateDto(
                id = (map["id"] as? Number)?.toLong() ?: 0L,
                templateType = map["templateType"] as? String ?: "",
                title = map["title"] as? String ?: "",
                content = map["content"] as? String ?: "",
                clientId = (map["clientId"] as? Number)?.toLong(),
                lastModified = (map["lastModified"] as? Number)?.toLong() ?: 0L,
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                isDeleted = map["isDeleted"] as? Boolean ?: false
            )
        }
    }
}

fun DraftTemplateEntity.toCloudDto(
    updatedAt: Long = this.lastModified,
    isDeleted: Boolean = false
): CloudDraftTemplateDto = CloudDraftTemplateDto(
    id = id,
    templateType = templateType,
    title = title,
    content = content,
    clientId = clientId,
    lastModified = lastModified,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)
