package com.example.quicknotes.data.repository

import com.example.quicknotes.domain.repository.SettingsRepository
import com.example.quicknotes.settings.SettingsManager
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val manager: SettingsManager
) : SettingsRepository {

    override val educationFlow: Flow<String> = manager.educationFlow
    override val mcqFlow: Flow<String> = manager.mcqFlow
    override val themeFlow: Flow<String> = manager.themeFlow

    override suspend fun saveEducation(value: String) {
        manager.saveEducation(value)
    }

    override suspend fun saveMcq(value: String) {
        manager.saveMcq(value)
    }

    override suspend fun saveTheme(value: String) {
        manager.saveTheme(value)
    }
}