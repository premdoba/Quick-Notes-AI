package com.example.quicknotes.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val educationFlow: Flow<String>
    val mcqFlow: Flow<String>
    val themeFlow: Flow<String>
    suspend fun saveEducation(value: String)
    suspend fun saveMcq(value: String)
    suspend fun saveTheme(value: String)
}