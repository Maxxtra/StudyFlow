package com.example.studyflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.database.entities.StudyTask
import com.example.studyflow.data.database.entities.TaskType
import com.example.studyflow.data.repository.StudyTaskRepository
import com.example.studyflow.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class TaskFormState(
    val title: String = "",
    val description: String = "",
    val type: TaskType = TaskType.HOMEWORK,
    val subjectId: Long? = null,
    val deadline: Long = System.currentTimeMillis(),
    val estimatedTimeMinutes: Int = 60,
    val difficulty: Int = 3
)

class TaskViewModel(
    private val taskRepository: StudyTaskRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _formState = MutableStateFlow(TaskFormState())
    val formState: StateFlow<TaskFormState> = _formState.asStateFlow()

    init {
        loadDefaultSettings()
    }

    private fun loadDefaultSettings() {
        viewModelScope.launch {
            val difficulty = settingsDataStore.defaultDifficulty.first()
            val estimatedTime = settingsDataStore.defaultEstimatedTime.first()
            _formState.value = _formState.value.copy(
                difficulty = difficulty,
                estimatedTimeMinutes = estimatedTime
            )
        }
    }

    fun updateTitle(title: String) {
        _formState.value = _formState.value.copy(title = title)
    }

    fun updateDescription(description: String) {
        _formState.value = _formState.value.copy(description = description)
    }

    fun updateType(type: TaskType) {
        _formState.value = _formState.value.copy(type = type)
    }

    fun updateSubjectId(subjectId: Long) {
        _formState.value = _formState.value.copy(subjectId = subjectId)
    }

    fun updateDeadline(deadline: Long) {
        _formState.value = _formState.value.copy(deadline = deadline)
    }

    fun updateEstimatedTime(minutes: Int) {
        _formState.value = _formState.value.copy(estimatedTimeMinutes = minutes)
    }

    fun updateDifficulty(difficulty: Int) {
        _formState.value = _formState.value.copy(difficulty = difficulty)
    }

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId)
            task?.let {
                _formState.value = TaskFormState(
                    title = it.title,
                    description = it.description,
                    type = it.type,
                    subjectId = it.subjectId,
                    deadline = it.deadline,
                    estimatedTimeMinutes = it.estimatedTimeMinutes,
                    difficulty = it.difficulty
                )
            }
        }
    }

    fun saveTask(onSuccess: () -> Unit = {}) {
        val state = _formState.value
        if (state.title.isBlank() || state.subjectId == null) return

        viewModelScope.launch {
            val task = StudyTask(
                title = state.title,
                description = state.description,
                type = state.type,
                subjectId = state.subjectId,
                deadline = state.deadline,
                estimatedTimeMinutes = state.estimatedTimeMinutes,
                difficulty = state.difficulty
            )
            taskRepository.insertTask(task)
            resetForm()
            onSuccess()
        }
    }

    fun updateTask(taskId: Long, onSuccess: () -> Unit = {}) {
        val state = _formState.value
        if (state.title.isBlank() || state.subjectId == null) return

        viewModelScope.launch {
            val existingTask = taskRepository.getTaskById(taskId)
            existingTask?.let { task ->
                val updatedTask = task.copy(
                    title = state.title,
                    description = state.description,
                    type = state.type,
                    subjectId = state.subjectId,
                    deadline = state.deadline,
                    estimatedTimeMinutes = state.estimatedTimeMinutes,
                    difficulty = state.difficulty
                )
                taskRepository.updateTask(updatedTask)
                resetForm()
                onSuccess()
            }
        }
    }

    fun resetForm() {
        viewModelScope.launch {
            val difficulty = settingsDataStore.defaultDifficulty.first()
            val estimatedTime = settingsDataStore.defaultEstimatedTime.first()
            _formState.value = TaskFormState(
                difficulty = difficulty,
                estimatedTimeMinutes = estimatedTime
            )
        }
    }
}
