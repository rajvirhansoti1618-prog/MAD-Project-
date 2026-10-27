package com.example.lexidesk.data.firebase

object CloudPaths {
    const val DEFAULT_WORKSPACE_ID = "default_firm_workspace"

    fun workspacePath(workspaceId: String): String = "workspaces/$workspaceId"
    fun clientsPath(workspaceId: String): String = "workspaces/$workspaceId/clients"
    fun casesPath(workspaceId: String): String = "workspaces/$workspaceId/cases"
    fun tasksPath(workspaceId: String): String = "workspaces/$workspaceId/tasks"
    fun notesPath(workspaceId: String): String = "workspaces/$workspaceId/notes"
    fun remindersPath(workspaceId: String): String = "workspaces/$workspaceId/reminders"
    fun draftTemplatesPath(workspaceId: String): String = "workspaces/$workspaceId/draft_templates"

    fun clientRecordId(localId: Long): String = "client_$localId"
    fun caseRecordId(localId: Long): String = "case_$localId"
    fun taskRecordId(localId: Long): String = "task_$localId"
    fun noteRecordId(localId: Long): String = "note_$localId"
    fun reminderRecordId(localId: Long): String = "reminder_$localId"
    fun draftTemplateRecordId(localId: Long): String = "draft_$localId"
}
