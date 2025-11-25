package com.bodycalc.platepilot.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
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
    
    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        sharedPreferences.edit()
            .putBoolean("notifications_enabled", enabled)
            .apply()
    }
    
    fun setShowTomorrowCards(show: Boolean) {
        _showTomorrowCards.value = show
        sharedPreferences.edit()
            .putBoolean("show_tomorrow_cards", show)
            .apply()
    }
    
    companion object {
        fun isShowTomorrowCards(context: Context): Boolean {
            return context.getSharedPreferences("platepilot_settings", Context.MODE_PRIVATE)
                .getBoolean("show_tomorrow_cards", false)
        }
    }
}
