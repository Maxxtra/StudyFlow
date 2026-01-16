package com.example.studyflow.data.repository

import com.example.studyflow.data.database.dao.StudyTaskDao
import com.example.studyflow.data.database.entities.StudyTask
import com.example.studyflow.data.database.entities.TaskType
import kotlinx.coroutines.flow.Flow

class StudyTaskRepository(private val studyTaskDao: StudyTaskDao) {
    val allTasks: Flow<List<StudyTask>> = studyTaskDao.getAllTasks()
    val activeTasks: Flow<List<StudyTask>> = studyTaskDao.getActiveTasks()
    val completedTasks: Flow<List<StudyTask>> = studyTaskDao.getCompletedTasks()

    suspend fun getTaskById(id: Long): StudyTask? {
        return studyTaskDao.getTaskById(id)
    }

    fun getTasksBySubject(subjectId: Long): Flow<List<StudyTask>> {
        return studyTaskDao.getTasksBySubject(subjectId)
    }

    fun getTasksByType(type: TaskType): Flow<List<StudyTask>> {
        return studyTaskDao.getTasksByType(type)
    }

    fun getTasksBetweenDates(startDate: Long, endDate: Long): Flow<List<StudyTask>> {
        return studyTaskDao.getTasksBetweenDates(startDate, endDate)
    }

    suspend fun insertTask(task: StudyTask): Long {
        return studyTaskDao.insertTask(task)
    }

    suspend fun updateTask(task: StudyTask) {
        studyTaskDao.updateTask(task)
    }

    suspend fun deleteTask(task: StudyTask) {
        studyTaskDao.deleteTask(task)
    }

    suspend fun deleteTaskById(id: Long) {
        studyTaskDao.deleteTaskById(id)
    }

    suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean) {
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        studyTaskDao.updateTaskCompletion(id, isCompleted, completedAt)
    }

    suspend fun updateTaskPriority(id: Long, priority: Float) {
        studyTaskDao.updateTaskPriority(id, priority)
    }

    suspend fun updateTaskInProgress(id: Long, inProgress: Boolean) {
        studyTaskDao.updateTaskInProgress(id, inProgress)
    }
}
