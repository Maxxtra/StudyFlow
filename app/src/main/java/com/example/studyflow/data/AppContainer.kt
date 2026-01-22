package com.example.studyflow.data

import android.content.Context
import com.example.studyflow.data.api.RetrofitClient
import com.example.studyflow.data.database.StudyFlowDatabase
import com.example.studyflow.data.datastore.SettingsDataStore
import com.example.studyflow.data.repository.HolidayRepository
import com.example.studyflow.data.repository.StudyTaskRepository
import com.example.studyflow.data.repository.SubjectRepository

/**
 * AppContainer provides dependencies for the application.
 * This follows the manual dependency injection pattern recommended by Android.
 */
interface AppContainer {
    val subjectRepository: SubjectRepository
    val taskRepository: StudyTaskRepository
    val settingsDataStore: SettingsDataStore
    val holidayRepository: HolidayRepository
}

/**
 * Default implementation of AppContainer that creates dependencies lazily.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database by lazy { StudyFlowDatabase.getDatabase(context) }

    override val subjectRepository: SubjectRepository by lazy {
        SubjectRepository(database.subjectDao())
    }

    override val taskRepository: StudyTaskRepository by lazy {
        StudyTaskRepository(database.studyTaskDao())
    }

    override val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(context)
    }

    override val holidayRepository: HolidayRepository by lazy {
        HolidayRepository(RetrofitClient.holidayApiService)
    }
}
