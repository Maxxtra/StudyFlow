package com.example.studyflow

import android.app.Application
import com.example.studyflow.data.AppContainer
import com.example.studyflow.data.DefaultAppContainer

class StudyFlowApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)
    }
}
