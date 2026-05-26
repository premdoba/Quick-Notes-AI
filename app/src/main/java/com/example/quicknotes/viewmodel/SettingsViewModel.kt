package com.example.quicknotes.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val manager =
        SettingsManager(application)

    val education =
        manager.educationFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "Graduation"
        )

    val mcq =
        manager.mcqFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "Medium"
        )

    val theme =
        manager.themeFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "Auto"
        )

    fun saveEducation(value: String) {

        viewModelScope.launch {

            manager.saveEducation(value)
        }
    }

    fun saveMcq(value: String) {

        viewModelScope.launch {

            manager.saveMcq(value)
        }
    }

    fun saveTheme(value: String) {

        viewModelScope.launch {

            manager.saveTheme(value)
        }
    }
}