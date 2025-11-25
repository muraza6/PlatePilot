package com.bodycalc.platepilot.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class WaterReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WaterReminderReceiver", "Received water reminder broadcast")
        
        // Check if water reminders are enabled in settings
        val sharedPreferences = context.getSharedPreferences("platepilot_settings", Context.MODE_PRIVATE)
        val waterRemindersEnabled = sharedPreferences.getBoolean("water_reminders_enabled", false)
        
        if (waterRemindersEnabled) {
            NotificationHelper.showWaterReminder(context)
        }
    }
}
