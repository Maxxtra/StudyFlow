package com.example.studyflow.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    companion object {
        private val DAILY_STUDY_HOURS = intPreferencesKey("daily_study_hours")
        private val DEFAULT_DIFFICULTY = intPreferencesKey("default_difficulty")
        private val DEFAULT_ESTIMATED_TIME = intPreferencesKey("default_estimated_time")
    }

    val dailyStudyHours: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[DAILY_STUDY_HOURS] ?: 4
        }

    val defaultDifficulty: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[DEFAULT_DIFFICULTY] ?: 3
        }

    val defaultEstimatedTime: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[DEFAULT_ESTIMATED_TIME] ?: 60
        }

    suspend fun setDailyStudyHours(hours: Int) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_STUDY_HOURS] = hours
        }
    }

    suspend fun setDefaultDifficulty(difficulty: Int) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_DIFFICULTY] = difficulty
        }
    }

    suspend fun setDefaultEstimatedTime(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_ESTIMATED_TIME] = minutes
        }
    }
}
