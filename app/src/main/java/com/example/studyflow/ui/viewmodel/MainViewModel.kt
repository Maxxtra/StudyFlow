package com.example.studyflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.database.entities.StudyTask
import com.example.studyflow.data.datastore.SettingsDataStore
import com.example.studyflow.data.repository.StudyTaskRepository
import com.example.studyflow.domain.model.DailyPlan
import com.example.studyflow.domain.scheduler.TaskScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MainUiState(
    val activeTasks: List<StudyTask> = emptyList(),
    val allTasks: List<StudyTask> = emptyList(),
    val dailyPlan: DailyPlan? = null,
    val isLoading: Boolean = true
)

class MainViewModel(
    private val taskRepository: StudyTaskRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val taskScheduler = TaskScheduler()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                taskRepository.allTasks,
                settingsDataStore.dailyStudyHours
            ) { allTasks, hours ->
                _uiState.update { currentState ->
                    val activeTasks = allTasks.filter { !it.isCompleted }
                    val dailyMinutes = hours * 60
                    val plan = taskScheduler.generateDailyPlan(activeTasks, dailyMinutes)

                    activeTasks.forEach { task ->
                        val priority = taskScheduler.calculatePriority(task)
                        if (task.priority != priority) {
                            taskRepository.updateTaskPriority(task.id, priority)
                        }
                    }

                    currentState.copy(
                        activeTasks = activeTasks,
                        allTasks = allTasks,
                        dailyPlan = plan,
                        isLoading = false
                    )
                }
            }.collect()
        }
    }

    fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTaskCompletion(taskId, isCompleted)
            if (isCompleted) {
                taskRepository.updateTaskInProgress(taskId, false)
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.deleteTaskById(taskId)
        }
    }

    fun toggleTaskInProgress(taskId: Long, inProgress: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTaskInProgress(taskId, inProgress)
        }
    }
}
