package com.example.lexidesk.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lexidesk.data.dao.CaseDao
import com.example.lexidesk.data.dao.ClientDao
import com.example.lexidesk.data.dao.DraftTemplateDao
import com.example.lexidesk.data.dao.DocumentDao
import com.example.lexidesk.data.dao.NoteDao
import com.example.lexidesk.data.dao.ReminderDao
import com.example.lexidesk.data.dao.TaskDao
import com.example.lexidesk.data.entity.CaseEntity
import com.example.lexidesk.data.entity.ClientEntity
import com.example.lexidesk.data.entity.DraftTemplateEntity
import com.example.lexidesk.data.entity.DocumentEntity
import com.example.lexidesk.data.entity.NoteEntity
import com.example.lexidesk.data.entity.ReminderEntity
import com.example.lexidesk.data.entity.TaskEntity

@Database(
    entities = [
        ClientEntity::class,
        CaseEntity::class,
        TaskEntity::class,
        DocumentEntity::class,
        ReminderEntity::class,
        NoteEntity::class,
        DraftTemplateEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LexiDeskDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao
    abstract fun caseDao(): CaseDao
    abstract fun taskDao(): TaskDao
    abstract fun documentDao(): DocumentDao
    abstract fun reminderDao(): ReminderDao
    abstract fun noteDao(): NoteDao
    abstract fun draftTemplateDao(): DraftTemplateDao

    companion object {
        @Volatile
        private var INSTANCE: LexiDeskDatabase? = null

        fun getDatabase(context: Context): LexiDeskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LexiDeskDatabase::class.java,
                    "lexidesk_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
