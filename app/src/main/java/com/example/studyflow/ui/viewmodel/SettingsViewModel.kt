package com.example.studyflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val dailyStudyHours: Int = 4,
    val defaultDifficulty: Int = 3,
    val defaultEstimatedTime: Int = 60
)

class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsDataStore.dailyStudyHours,
                settingsDataStore.defaultDifficulty,
                settingsDataStore.defaultEstimatedTime
            ) { hours, difficulty, estimatedTime ->
                SettingsUiState(
                    dailyStudyHours = hours,
                    defaultDifficulty = difficulty,
                    defaultEstimatedTime = estimatedTime
                )
            }.collect { settings ->
                _uiState.value = settings
            }
        }
    }

    fun setDailyStudyHours(hours: Int) {
        viewModelScope.launch {
            settingsDataStore.setDailyStudyHours(hours)
        }
    }

    fun setDefaultDifficulty(difficulty: Int) {
        viewModelScope.launch {
            settingsDataStore.setDefaultDifficulty(difficulty)
        }
    }

    fun setDefaultEstimatedTime(minutes: Int) {
        viewModelScope.launch {
            settingsDataStore.setDefaultEstimatedTime(minutes)
        }
    }
}
