package com.bodycalc.platepilot.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootCompletedReceiver", "Device boot completed - rescheduling notifications")
            
            // Check if notifications are enabled
            val sharedPreferences = context.getSharedPreferences("platepilot_settings", Context.MODE_PRIVATE)
            val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
            val waterRemindersEnabled = sharedPreferences.getBoolean("water_reminders_enabled", false)
            
            if (notificationsEnabled) {
                NotificationScheduler.scheduleDailyNotification(context)
            }
            
            if (waterRemindersEnabled) {
                WaterNotificationScheduler.scheduleWaterReminders(context)
            }
        }
    }
}
