package com.example.quicknotes.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

object ReminderScheduler {

    fun scheduleReminder(
        context: Context,
        taskId: Int,
        title: String,
        timeInMillis: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (timeInMillis <= System.currentTimeMillis()) {
            Log.d("REMINDER_DEBUG", "Time is in the past, skipping: $timeInMillis")
            return
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("task_id", taskId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeInMillis,
                        pendingIntent
                    )
                    Log.d("REMINDER_DEBUG", "Scheduled EXACT alarm for $timeInMillis")
                } else {
                    // Fallback to non-exact alarm if permission not granted
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeInMillis,
                        pendingIntent
                    )
                    Log.d("REMINDER_DEBUG", "Scheduled INEXACT alarm (fallback) for $timeInMillis")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
                Log.d("REMINDER_DEBUG", "Scheduled EXACT alarm for $timeInMillis")
            }
        } catch (e: Exception) {
            Log.e("REMINDER_DEBUG", "Error scheduling alarm", e)
        }
    }

//    fun cancelReminder(context: Context, taskId: Int) {
//        val intent = Intent(context, ReminderReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            taskId,
//            intent,
//            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
//        )
//        if (pendingIntent != null) {
//            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            alarmManager.cancel(pendingIntent)
//            pendingIntent.cancel()
//            Log.d("REMINDER_DEBUG", "Canceled alarm for task $taskId")
//        }
//    }
}
