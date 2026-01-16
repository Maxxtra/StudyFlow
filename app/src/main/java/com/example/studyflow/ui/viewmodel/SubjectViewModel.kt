package com.example.studyflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.database.entities.Subject
import com.example.studyflow.data.repository.SubjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SubjectUiState(
    val subjects: List<Subject> = emptyList(),
    val isLoading: Boolean = true
)

class SubjectViewModel(
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectUiState())
    val uiState: StateFlow<SubjectUiState> = _uiState.asStateFlow()

    init {
        loadSubjects()
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            subjectRepository.allSubjects.collect { subjects ->
                _uiState.update {
                    it.copy(subjects = subjects, isLoading = false)
                }
            }
        }
    }

    fun addSubject(name: String, color: Long) {
        viewModelScope.launch {
            val subject = Subject(name = name, color = color)
            subjectRepository.insertSubject(subject)
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            subjectRepository.updateSubject(subject)
        }
    }

    fun deleteSubject(subjectId: Long) {
        viewModelScope.launch {
            subjectRepository.deleteSubjectById(subjectId)
        }
    }
}
