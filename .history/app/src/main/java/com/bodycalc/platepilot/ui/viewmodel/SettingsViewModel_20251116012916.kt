package com.bodycalc.platepilot.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.bodycalc.platepilot.notification.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val context = application.applicationContext
    
    private val sharedPreferences = application.getSharedPreferences(
        "platepilot_settings",
        Context.MODE_PRIVATE
    )
    
    private val _notificationsEnabled = MutableStateFlow(
        sharedPreferences.getBoolean("notifications_enabled", true)
    )
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()
    
    private val _showTomorrowCards = MutableStateFlow(
        sharedPreferences.getBoolean("show_tomorrow_cards", false)
    )
    val showTomorrowCards: StateFlow<Boolean> = _showTomorrowCards.asStateFlow()
    
    // Day offset: 0 = today, 1 = tomorrow, 2-6 = next 5 days
    private val _selectedDayOffset = MutableStateFlow(
        sharedPreferences.getInt("selected_day_offset", 0)
    )
    val selectedDayOffset: StateFlow<Int> = _selectedDayOffset.asStateFlow()
    
    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        sharedPreferences.edit()
            .putBoolean("notifications_enabled", enabled)
            .apply()
        
        // Schedule or cancel notifications based on user preference
        if (enabled) {
            NotificationScheduler.scheduleDailyNotification(context)
        } else {
            NotificationScheduler.cancelDailyNotification(context)
        }
    }
    
    fun setShowTomorrowCards(show: Boolean) {
        _showTomorrowCards.value = show
        sharedPreferences.edit()
            .putBoolean("show_tomorrow_cards", show)
            .apply()
        
        // When toggling tomorrow, update day offset
        if (show) {
            setSelectedDayOffset(1)
        } else {
            setSelectedDayOffset(0)
        }
    }
    
    fun setSelectedDayOffset(offset: Int) {
        _selectedDayOffset.value = offset
        sharedPreferences.edit()
            .putInt("selected_day_offset", offset)
            .apply()
    }
    
    companion object {
        fun isShowTomorrowCards(context: Context): Boolean {
            return context.getSharedPreferences("platepilot_settings", Context.MODE_PRIVATE)
                .getBoolean("show_tomorrow_cards", false)
        }
        
        fun getSelectedDayOffset(context: Context): Int {
            return context.getSharedPreferences("platepilot_settings", Context.MODE_PRIVATE)
                .getInt("selected_day_offset", 0)
        }
    }
}
