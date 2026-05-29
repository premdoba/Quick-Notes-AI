package com.example.quicknotes.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.quicknotes.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        android.util.Log.d(
            "REMINDER_DEBUG",
            "Receiver triggered"
        )

        val title =
            intent.getStringExtra("title")
                ?: "Task Reminder"

        val channelId = "todo_reminder_channel"

        val channel = NotificationChannel(
            channelId,
            "Todo Reminders",
            NotificationManager.IMPORTANCE_HIGH
        )

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.createNotificationChannel(channel)

        val notification =
            NotificationCompat.Builder(
                context,
                channelId
            )
                .setSmallIcon(R.drawable.baseline_checklist_24)
                .setContentTitle("QuickTasks Reminder")
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat
            .from(context)
            .notify(
                System.currentTimeMillis().toInt(),
                notification
            )
    }
}