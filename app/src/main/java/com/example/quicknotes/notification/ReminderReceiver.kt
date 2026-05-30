package com.example.quicknotes.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.quicknotes.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!intent.hasExtra("task_id")) {
            Log.d("REMINDER_DEBUG", "Ignoring non-reminder broadcast")
            return
        }

        val title = intent.getStringExtra("title") ?: "Task Reminder"
        val taskId = intent.getIntExtra("task_id", 0)
        val channelId = "todo_reminder_channel"

        val channel = NotificationChannel(
            channelId,
            "Todo Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminder notifications for tasks"
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_checklist_24)
            .setContentTitle("QuickTasks Reminder")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(taskId, notification)
            Log.d("REMINDER_DEBUG", "Notification shown for task ID: $taskId")
        } else {
            Log.e("REMINDER_DEBUG", "Notification permission not granted at trigger time")
        }
    }
}
