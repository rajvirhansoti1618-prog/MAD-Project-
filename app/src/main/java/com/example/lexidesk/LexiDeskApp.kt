package com.example.lexidesk

import android.app.Application
import com.example.lexidesk.data.local.LexiDeskDatabase
import com.example.lexidesk.data.local.UserPreferencesRepository
import com.example.lexidesk.data.repository.CaseRepository
import com.example.lexidesk.data.repository.ClientRepository
import com.example.lexidesk.data.repository.DraftTemplateRepository
import com.example.lexidesk.data.repository.DocumentRepository
import com.example.lexidesk.data.repository.NoteRepository
import com.example.lexidesk.data.repository.ReminderRepository
import com.example.lexidesk.data.repository.TaskRepository
import com.example.lexidesk.notifications.NotificationHelper

class LexiDeskApp : Application() {

    lateinit var database: LexiDeskDatabase
        private set

    lateinit var clientRepository: ClientRepository
        private set

    lateinit var caseRepository: CaseRepository
        private set

    lateinit var taskRepository: TaskRepository
        private set

    lateinit var documentRepository: DocumentRepository
        private set

    lateinit var reminderRepository: ReminderRepository
        private set

    lateinit var noteRepository: NoteRepository
        private set

    lateinit var draftTemplateRepository: DraftTemplateRepository
        private set

    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set

    override fun onCreate() {
        super.onCreate()

        database = LexiDeskDatabase.getDatabase(this)
        clientRepository = ClientRepository(database.clientDao())
        caseRepository = CaseRepository(database.caseDao())
        taskRepository = TaskRepository(database.taskDao())
        documentRepository = DocumentRepository(database.documentDao())
        reminderRepository = ReminderRepository(database.reminderDao())
        noteRepository = NoteRepository(database.noteDao())
        draftTemplateRepository = DraftTemplateRepository(database.draftTemplateDao())
        userPreferencesRepository = UserPreferencesRepository(this)

        NotificationHelper.createNotificationChannels(this)
        com.example.lexidesk.notifications.DeadlineCheckWorker.schedulePeriodicCheck(this)
    }
}
