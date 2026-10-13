package com.example.lexidesk.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lexidesk.data.entity.DraftTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftTemplateDao {
    @Query("SELECT * FROM draft_templates ORDER BY lastModified DESC")
    fun getAllDrafts(): Flow<List<DraftTemplateEntity>>

    @Query("SELECT * FROM draft_templates WHERE id = :id")
    fun getDraftById(id: Long): Flow<DraftTemplateEntity?>

    @Query("SELECT * FROM draft_templates WHERE id = :id")
    suspend fun getDraftByIdOnce(id: Long): DraftTemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: DraftTemplateEntity): Long

    @Update
    suspend fun updateDraft(draft: DraftTemplateEntity)

    @Delete
    suspend fun deleteDraft(draft: DraftTemplateEntity)
}
