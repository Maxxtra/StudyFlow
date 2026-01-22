package com.example.studyflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.database.entities.Subject
import com.example.studyflow.data.repository.SubjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SubjectUiState(
    val allSubjects: List<Subject> = emptyList(),
    val filteredSubjects: List<Subject> = emptyList(),
    val searchQuery: String = "",
    val sortAscending: Boolean = true,
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
                    it.copy(
                        allSubjects = subjects,
                        filteredSubjects = filterAndSortSubjects(subjects, it.searchQuery, it.sortAscending),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredSubjects = filterAndSortSubjects(it.allSubjects, query, it.sortAscending)
            )
        }
    }

    fun toggleSortOrder() {
        _uiState.update {
            it.copy(
                sortAscending = !it.sortAscending,
                filteredSubjects = filterAndSortSubjects(it.allSubjects, it.searchQuery, !it.sortAscending)
            )
        }
    }

    private fun filterAndSortSubjects(
        subjects: List<Subject>,
        query: String,
        ascending: Boolean
    ): List<Subject> {
        return subjects
            .filter { subject ->
                subject.name.contains(query, ignoreCase = true)
            }
            .let { list ->
                if (ascending) {
                    list.sortedBy { it.name.lowercase() }
                } else {
                    list.sortedByDescending { it.name.lowercase() }
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
