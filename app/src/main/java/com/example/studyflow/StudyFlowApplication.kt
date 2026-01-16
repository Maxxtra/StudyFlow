package com.example.studyflow

import android.app.Application
import com.example.studyflow.data.database.StudyFlowDatabase
import com.example.studyflow.data.datastore.SettingsDataStore
import com.example.studyflow.data.repository.StudyTaskRepository
import com.example.studyflow.data.repository.SubjectRepository

class StudyFlowApplication : Application() {
    private val database by lazy { StudyFlowDatabase.getDatabase(this) }

    val subjectRepository by lazy { SubjectRepository(database.subjectDao()) }
    val taskRepository by lazy { StudyTaskRepository(database.studyTaskDao()) }
    val settingsDataStore by lazy { SettingsDataStore(this) }
}
