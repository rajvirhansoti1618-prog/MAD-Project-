package com.example.lexidesk.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lexidesk.data.entity.CaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {
    @Query("SELECT * FROM cases ORDER BY updatedAt DESC")
    fun getAllCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE id = :id")
    fun getCaseById(id: Long): Flow<CaseEntity?>

    @Query("SELECT * FROM cases WHERE id = :id")
    suspend fun getCaseByIdOnce(id: Long): CaseEntity?

    @Query("SELECT * FROM cases WHERE clientId = :clientId ORDER BY updatedAt DESC")
    fun getCasesByClient(clientId: Long): Flow<List<CaseEntity>>

    @Query("SELECT COUNT(*) FROM cases WHERE status != 'Completed'")
    fun getActiveCasesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cases WHERE status = 'Completed'")
    fun getCompletedCasesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cases")
    fun getTotalCasesCount(): Flow<Int>

    @Query("""
        SELECT * FROM cases 
        WHERE title LIKE '%' || :query || '%' 
           OR referenceNumber LIKE '%' || :query || '%' 
           OR category LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
    """)
    fun searchCases(query: String): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE dueDate IS NOT NULL AND status != 'Completed' ORDER BY dueDate ASC LIMIT :limit")
    fun getUpcomingCases(limit: Int = 5): Flow<List<CaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(caseItem: CaseEntity): Long

    @Update
    suspend fun updateCase(caseItem: CaseEntity)

    @Delete
    suspend fun deleteCase(caseItem: CaseEntity)

    @Query("DELETE FROM cases WHERE id = :id")
    suspend fun deleteCaseById(id: Long)
}
