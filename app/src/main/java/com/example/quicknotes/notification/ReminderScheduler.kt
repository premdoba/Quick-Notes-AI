package com.example.quicknotes.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log

object ReminderScheduler {

    fun scheduleReminder(
        context: Context,
        taskId: Int,
        title: String,
        timeInMillis: Long
    ) {

        val intent = Intent(
            context,
            ReminderReceiver::class.java
        ).apply {

            putExtra("title", title)
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                taskId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )

        Log.d(
            "REMINDER_DEBUG",
            "Reminder scheduled for $timeInMillis"
        )
    }

    fun cancelReminder(
        context: Context,
        taskId: Int
    ) {

        val intent = Intent(
            context,
            ReminderReceiver::class.java
        )

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                taskId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }
}