package com.bodycalc.platepilot.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

object WaterNotificationScheduler {
    
    fun scheduleHourlyReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WaterReminderReceiver::class.java)
        
        // Schedule for every 2 hours from 8 AM to 10 PM
        val hours = listOf(8, 10, 12, 14, 16, 18, 20, 22)
        
        hours.forEachIndexed { index, hour ->
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
            
            // Schedule repeating alarm
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            
            Log.d("WaterNotificationScheduler", "Scheduled water reminder for ${hour}:00")
        }
    }
    
    fun cancelReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WaterReminderReceiver::class.java)
        
        for (i in 0..7) {
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                2000 + i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
        
        Log.d("WaterNotificationScheduler", "Water reminders cancelled")
    }
}
