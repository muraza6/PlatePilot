package com.bodycalc.platepilot.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MealReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("MealReminderReceiver", "Received meal reminder broadcast")
        
        // Check if notifications are enabled in settings
        val sharedPreferences = context.getSharedPreferences("platepilot_settings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        
        if (notificationsEnabled) {
            NotificationHelper.showMealReminderNotification(context)
        }
    }
}
