package com.bodycalc.platepilot.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

object WaterNotificationScheduler {
    private const val ACTION_WATER_REMINDER = "com.bodycalc.platepilot.WATER_REMINDER"
    private const val ACTION_WATER_RESET = "com.bodycalc.platepilot.WATER_RESET"
    
    fun scheduleHourlyReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Schedule for every 2 hours from 8 AM to 10 PM
        val hours = listOf(8, 10, 12, 14, 16, 18, 20, 22)
        
        hours.forEachIndexed { index, hour ->
            val intent = Intent(context, WaterReminderReceiver::class.java).apply {
                action = ACTION_WATER_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                2000 + index, // Unique request code for water reminders
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                
                // If time has passed today, schedule for tomorrow
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }
            
            // Schedule repeating alarm with setAndAllowWhileIdle for Doze mode compatibility
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    // Also schedule daily repetition
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis + AlarmManager.INTERVAL_DAY,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                } else {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                }
                Log.d("WaterNotificationScheduler", "Scheduled water reminder for ${hour}:00")
            } catch (e: SecurityException) {
                Log.e("WaterNotificationScheduler", "Failed to schedule water reminder", e)
            }
        }
        
        // Schedule midnight reset
        scheduleMidnightReset(context)
    }
    
    private fun scheduleMidnightReset(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WaterResetReceiver::class.java).apply {
            action = ACTION_WATER_RESET
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            2100, // Unique request code for midnight reset
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            
            // Schedule for next midnight
            add(Calendar.DAY_OF_MONTH, 1)
        }
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                // Also schedule daily repetition
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis + AlarmManager.INTERVAL_DAY,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } else {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
            Log.d("WaterNotificationScheduler", "Scheduled water reset at midnight")
        } catch (e: SecurityException) {
            Log.e("WaterNotificationScheduler", "Failed to schedule water reset", e)
        }
    }
    
    fun cancelReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Cancel water reminders (8 hourly reminders)
        for (i in 0..7) {
            val intent = Intent(context, WaterReminderReceiver::class.java).apply {
                action = ACTION_WATER_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                2000 + i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            try {
                alarmManager.cancel(pendingIntent)
            } catch (e: Exception) {
                Log.e("WaterNotificationScheduler", "Failed to cancel water reminder", e)
            }
        }
        
        // Cancel midnight reset
        val resetIntent = Intent(context, WaterResetReceiver::class.java).apply {
            action = ACTION_WATER_RESET
        }
        val resetPendingIntent = PendingIntent.getBroadcast(
            context,
            2100,
            resetIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager.cancel(resetPendingIntent)
        } catch (e: Exception) {
            Log.e("WaterNotificationScheduler", "Failed to cancel water reset", e)
        }
        
        Log.d("WaterNotificationScheduler", "Water reminders cancelled")
    }
}
