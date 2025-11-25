package com.bodycalc.platepilot.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class WaterResetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WaterResetReceiver", "Water reset triggered at midnight")
        
        // Save the reset timestamp
        val prefs = context.getSharedPreferences("water_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putLong("last_reset_time", System.currentTimeMillis())
            .apply()
        
        // The ViewModel's date monitoring will automatically detect the date change
        // and reload data for the new day (which will be 0)
    }
}
