package com.example.lexidesk.data.repository

import com.example.lexidesk.data.dao.TaskDao
import com.example.lexidesk.data.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val syncPublisher: com.example.lexidesk.data.firebase.sync.CloudSyncPublisher? = null
) {

    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val pendingTasksCount: Flow<Int> = taskDao.getPendingTasksCount()
    val completedTasksCount: Flow<Int> = taskDao.getCompletedTasksCount()

    fun getOverdueTasksCount(currentTime: Long): Flow<Int> = taskDao.getOverdueTasksCount(currentTime)

    fun getTaskById(id: Long): Flow<TaskEntity?> = taskDao.getTaskById(id)

    suspend fun getTaskByIdOnce(id: Long): TaskEntity? = taskDao.getTaskByIdOnce(id)

    fun getTasksByClient(clientId: Long): Flow<List<TaskEntity>> = taskDao.getTasksByClient(clientId)

    fun getTasksByCase(caseId: Long): Flow<List<TaskEntity>> = taskDao.getTasksByCase(caseId)

    fun searchTasks(query: String): Flow<List<TaskEntity>> = taskDao.searchTasks(query)

    fun getTasksForDate(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksForDate(startOfDay, endOfDay)

    suspend fun insertTask(task: TaskEntity): Long {
        val id = taskDao.insertTask(task)
        val inserted = if (task.id == 0L) task.copy(id = id) else task
        syncPublisher?.publishTaskUpsert(inserted)
        return id
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
        syncPublisher?.publishTaskUpsert(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
        syncPublisher?.publishTaskDelete(task.id)
    }

    suspend fun deleteTaskById(id: Long) {
        taskDao.deleteTaskById(id)
        syncPublisher?.publishTaskDelete(id)
    }

    suspend fun toggleTaskCompleted(id: Long, currentStatus: String) {
        val newStatus = if (currentStatus == "Completed") "Pending" else "Completed"
        taskDao.updateTaskStatus(id, newStatus)
        val updated = taskDao.getTaskByIdOnce(id)
        if (updated != null) {
            syncPublisher?.publishTaskUpsert(updated)
        }
    }
}
