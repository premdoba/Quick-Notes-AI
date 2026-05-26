package com.example.quicknotes.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

class PreferenceManager(
    private val context: Context
) {

    companion object {

        private val EDUCATION =
            stringPreferencesKey("education")

        private val MCQ =
            stringPreferencesKey("mcq")

        private val THEME =
            stringPreferencesKey("theme")
    }

    val preferencesFlow: Flow<UserPreferences> =
        context.dataStore.data.map { prefs ->

            UserPreferences(

                educationLevel =
                    prefs[EDUCATION] ?: "Graduation",

                mcqDifficulty =
                    prefs[MCQ] ?: "Medium",

                appTheme =
                    prefs[THEME] ?: "Auto"
            )
        }

    suspend fun saveEducation(level: String) {

        context.dataStore.edit {

            it[EDUCATION] = level
        }
    }

    suspend fun saveMcq(level: String) {

        context.dataStore.edit {

            it[MCQ] = level
        }
    }

    suspend fun saveTheme(theme: String) {

        context.dataStore.edit {

            it[THEME] = theme
        }
    }
}