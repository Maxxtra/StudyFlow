package com.example.studyflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studyflow.data.datastore.SettingsDataStore
import com.example.studyflow.data.repository.HolidayRepository
import com.example.studyflow.data.repository.StudyTaskRepository
import com.example.studyflow.data.repository.SubjectRepository

class MainViewModelFactory(
    private val taskRepository: StudyTaskRepository,
    private val settingsDataStore: SettingsDataStore,
    private val holidayRepository: HolidayRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(taskRepository, settingsDataStore, holidayRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SubjectViewModelFactory(
    private val subjectRepository: SubjectRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SubjectViewModel(subjectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TaskViewModelFactory(
    private val taskRepository: StudyTaskRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(taskRepository, settingsDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SettingsViewModelFactory(
    private val settingsDataStore: SettingsDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
