package com.example.quicknotes

import TodoViewModel
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.quicknotes.settings.SettingsViewModel
import com.example.quicknotes.ui.AppNavigation
import com.example.quicknotes.ui.theme.QuickNotesTheme
import com.example.quicknotes.viewmodel.StudyViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        createNotificationChannel()

        setContent {

            val navController = rememberNavController()

            val studyViewModel: StudyViewModel = viewModel()

            val todoViewModel: TodoViewModel = viewModel()

            val settingViewModel: SettingsViewModel = viewModel()

            val selectedTheme by settingViewModel.theme.collectAsState()

            val darkTheme = when (selectedTheme) {

                "Light" -> false

                "Dark" -> true

                else -> isSystemInDarkTheme()
            }

            QuickNotesTheme(
                darkTheme = darkTheme
            ) {

                AppNavigation(
                    navController,
                    studyViewModel,
                    todoViewModel,
                    settingViewModel
                )
            }
        }
    }
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                "todo_reminder_channel",
                "Todo Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                description = "Reminder notifications for tasks"
            }

            val notificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }
}