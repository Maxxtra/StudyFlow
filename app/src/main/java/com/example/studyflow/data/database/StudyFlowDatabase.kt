package com.example.studyflow.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studyflow.data.database.dao.StudyTaskDao
import com.example.studyflow.data.database.dao.SubjectDao
import com.example.studyflow.data.database.entities.StudyTask
import com.example.studyflow.data.database.entities.Subject

@Database(
    entities = [Subject::class, StudyTask::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class StudyFlowDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun studyTaskDao(): StudyTaskDao

    companion object {
        @Volatile
        private var INSTANCE: StudyFlowDatabase? = null

        fun getDatabase(context: Context): StudyFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudyFlowDatabase::class.java,
                    "studyflow_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
