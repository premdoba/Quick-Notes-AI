package com.example.quicknotes.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class SettingsManager(
    private val context: Context
) {

    companion object {

        val DEFAULT_EDUCATION =
            stringPreferencesKey("default_education")

        val DEFAULT_MCQ =
            stringPreferencesKey("default_mcq")

        val APP_THEME =
            stringPreferencesKey("app_theme")
    }

    val educationFlow: Flow<String> =
        context.dataStore.data.map {

            it[DEFAULT_EDUCATION] ?: "Graduation"
        }

    val mcqFlow: Flow<String> =
        context.dataStore.data.map {

            it[DEFAULT_MCQ] ?: "Medium"
        }

    val themeFlow: Flow<String> =
        context.dataStore.data.map {

            it[APP_THEME] ?: "Auto"
        }

    suspend fun saveEducation(value: String) {

        context.dataStore.edit {

            it[DEFAULT_EDUCATION] = value
        }
    }

    suspend fun saveMcq(value: String) {

        context.dataStore.edit {

            it[DEFAULT_MCQ] = value
        }
    }

    suspend fun saveTheme(value: String) {

        context.dataStore.edit {

            it[APP_THEME] = value
        }
    }
}