package com.example.studyflow.data.database.dao

import androidx.room.*
import com.example.studyflow.data.database.entities.StudyTask
import com.example.studyflow.data.database.entities.TaskType
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyTaskDao {
    @Query("SELECT * FROM study_tasks ORDER BY deadline ASC")
    fun getAllTasks(): Flow<List<StudyTask>>

    @Query("SELECT * FROM study_tasks WHERE isCompleted = 0 ORDER BY priority DESC, deadline ASC")
    fun getActiveTasks(): Flow<List<StudyTask>>

    @Query("SELECT * FROM study_tasks WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTasks(): Flow<List<StudyTask>>

    @Query("SELECT * FROM study_tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): StudyTask?

    @Query("SELECT * FROM study_tasks WHERE subjectId = :subjectId ORDER BY deadline ASC")
    fun getTasksBySubject(subjectId: Long): Flow<List<StudyTask>>

    @Query("SELECT * FROM study_tasks WHERE type = :type ORDER BY deadline ASC")
    fun getTasksByType(type: TaskType): Flow<List<StudyTask>>

    @Query("SELECT * FROM study_tasks WHERE deadline BETWEEN :startDate AND :endDate ORDER BY deadline ASC")
    fun getTasksBetweenDates(startDate: Long, endDate: Long): Flow<List<StudyTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTask): Long

    @Update
    suspend fun updateTask(task: StudyTask)

    @Delete
    suspend fun deleteTask(task: StudyTask)

    @Query("DELETE FROM study_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("UPDATE study_tasks SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :id")
    suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean, completedAt: Long?)

    @Query("UPDATE study_tasks SET priority = :priority WHERE id = :id")
    suspend fun updateTaskPriority(id: Long, priority: Float)

    @Query("UPDATE study_tasks SET inProgress = :inProgress WHERE id = :id")
    suspend fun updateTaskInProgress(id: Long, inProgress: Boolean)
}
