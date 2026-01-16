package com.example.studyflow.data.repository

import com.example.studyflow.data.database.dao.SubjectDao
import com.example.studyflow.data.database.entities.Subject
import kotlinx.coroutines.flow.Flow

class SubjectRepository(private val subjectDao: SubjectDao) {
    val allSubjects: Flow<List<Subject>> = subjectDao.getAllSubjects()

    suspend fun getSubjectById(id: Long): Subject? {
        return subjectDao.getSubjectById(id)
    }

    suspend fun insertSubject(subject: Subject): Long {
        return subjectDao.insertSubject(subject)
    }

    suspend fun updateSubject(subject: Subject) {
        subjectDao.updateSubject(subject)
    }

    suspend fun deleteSubject(subject: Subject) {
        subjectDao.deleteSubject(subject)
    }

    suspend fun deleteSubjectById(id: Long) {
        subjectDao.deleteSubjectById(id)
    }
}
