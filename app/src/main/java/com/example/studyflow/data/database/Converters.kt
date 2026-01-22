package com.example.studyflow.data.database

import androidx.room.TypeConverter
import com.example.studyflow.data.database.entities.TaskType

class Converters {
    @TypeConverter
    fun fromTaskType(taskType: TaskType): String {
        return taskType.name
    }

    @TypeConverter
    fun toTaskType(value: String): TaskType {
        return TaskType.valueOf(value)
    }
}
