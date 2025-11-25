package com.bodycalc.platepilot.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class WaterReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WaterReminderReceiver", "Received water reminder broadcast: ${intent.action}")
        
        // Check if water reminders are enabled in settings
        val sharedPreferences = context.getSharedPreferences("platepilot_settings", Context.MODE_PRIVATE)
        val waterRemindersEnabled = sharedPreferences.getBoolean("water_reminders_enabled", false)
        
        Log.d("WaterReminderReceiver", "Water reminders enabled: $waterRemindersEnabled")
        
        if (waterRemindersEnabled) {
            Log.d("WaterReminderReceiver", "Showing water reminder notification")
            NotificationHelper.showWaterReminder(context)
        }
    }
}
