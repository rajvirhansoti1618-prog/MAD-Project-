package com.example.lexidesk.utils

import android.content.Context
import com.example.lexidesk.LexiDeskApp
import com.example.lexidesk.data.entity.CaseEntity
import com.example.lexidesk.data.entity.ClientEntity
import com.example.lexidesk.data.entity.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

object BackupRestoreManager {

    suspend fun exportBackupJson(context: Context, app: LexiDeskApp): File? = withContext(Dispatchers.IO) {
        try {
            val clients = app.clientRepository.allClients.first()
            val cases = app.caseRepository.allCases.first()
            val tasks = app.taskRepository.allTasks.first()

            val rootJson = JSONObject().apply {
                put("app", "LexiDesk")
                put("version", "1.0")
                put("exportTimestamp", System.currentTimeMillis())

                val clientsArray = JSONArray()
                for (c in clients) {
                    clientsArray.put(JSONObject().apply {
                        put("fullName", c.fullName)
                        put("clientType", c.clientType)
                        put("phone", c.phone)
                        put("email", c.email)
                        put("address", c.address)
                        put("companyName", c.companyName)
                        put("notes", c.notes)
                        put("tags", c.tags)
                    })
                }
                put("clients", clientsArray)

                val casesArray = JSONArray()
                for (c in cases) {
                    casesArray.put(JSONObject().apply {
                        put("title", c.title)
                        put("category", c.category)
                        put("description", c.description)
                        put("referenceNumber", c.referenceNumber)
                        put("priority", c.priority)
                        put("status", c.status)
                        put("courtOrLocation", c.courtOrLocation)
                    })
                }
                put("cases", casesArray)

                val tasksArray = JSONArray()
                for (t in tasks) {
                    tasksArray.put(JSONObject().apply {
                        put("title", t.title)
                        put("description", t.description)
                        put("priority", t.priority)
                        put("status", t.status)
                        if (t.dueDate != null) put("dueDate", t.dueDate)
                        if (t.dueTime != null) put("dueTime", t.dueTime)
                    })
                }
                put("tasks", tasksArray)
            }

            val backupFile = File(context.filesDir, "LexiDesk_Backup_${System.currentTimeMillis()}.json")
            FileOutputStream(backupFile).use { output ->
                output.write(rootJson.toString(2).toByteArray())
            }
            backupFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun restoreBackupJson(app: LexiDeskApp, jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (!root.has("app") || root.getString("app") != "LexiDesk") {
                return@withContext Result.failure(Exception("Invalid backup file: missing LexiDesk header"))
            }

            var recordCount = 0

            if (root.has("clients")) {
                val array = root.getJSONArray("clients")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    app.clientRepository.insertClient(
                        ClientEntity(
                            fullName = obj.optString("fullName", "Unnamed Client"),
                            clientType = obj.optString("clientType", "Individual"),
                            phone = obj.optString("phone", ""),
                            email = obj.optString("email", ""),
                            address = obj.optString("address", ""),
                            companyName = obj.optString("companyName", ""),
                            notes = obj.optString("notes", ""),
                            tags = obj.optString("tags", "")
                        )
                    )
                    recordCount++
                }
            }

            if (root.has("cases")) {
                val array = root.getJSONArray("cases")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    app.caseRepository.insertCase(
                        CaseEntity(
                            title = obj.optString("title", "Untitled Case"),
                            category = obj.optString("category", "Advisory"),
                            description = obj.optString("description", ""),
                            referenceNumber = obj.optString("referenceNumber", ""),
                            priority = obj.optString("priority", "Medium"),
                            status = obj.optString("status", "In Progress"),
                            courtOrLocation = obj.optString("courtOrLocation", "")
                        )
                    )
                    recordCount++
                }
            }

            if (root.has("tasks")) {
                val array = root.getJSONArray("tasks")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    app.taskRepository.insertTask(
                        TaskEntity(
                            title = obj.optString("title", "Untitled Task"),
                            description = obj.optString("description", ""),
                            priority = obj.optString("priority", "Medium"),
                            status = obj.optString("status", "Pending"),
                            dueDate = if (obj.has("dueDate")) obj.getLong("dueDate") else null,
                            dueTime = if (obj.has("dueTime")) obj.optString("dueTime", null) else null
                        )
                    )
                    recordCount++
                }
            }

            Result.success(recordCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
