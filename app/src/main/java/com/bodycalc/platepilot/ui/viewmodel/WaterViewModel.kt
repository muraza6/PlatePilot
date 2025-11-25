package com.bodycalc.platepilot.ui.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodycalc.platepilot.data.local.PlatePilotDatabase
import com.bodycalc.platepilot.data.model.WaterLog
import com.bodycalc.platepilot.data.repository.WaterRepository
import com.bodycalc.platepilot.notification.WaterResetReceiver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar

data class WaterUiState(
    val totalWater: Int = 0, // ml consumed today
    val dailyGoal: Int = 3000, // ml (3L daily goal)
    val waterLogs: List<WaterLog> = emptyList(),
    val currentDate: String = LocalDate.now().toString()
)

class WaterViewModel(application: Application) : AndroidViewModel(application) {
    private val database = PlatePilotDatabase.getDatabase(application)
    private val repository = WaterRepository(database.waterLogDao())
    private val sharedPrefs = application.getSharedPreferences("water_prefs", Context.MODE_PRIVATE)
    
    private val _uiState = MutableStateFlow(WaterUiState())
    val uiState: StateFlow<WaterUiState> = _uiState.asStateFlow()
    
    init {
        loadTodaysWater()
        startDateChangeMonitoring()
        scheduleNightlyReset()
    }
    
    private fun loadTodaysWater() {
        viewModelScope.launch {
            // Always get fresh date
            val today = LocalDate.now().toString()
            
            // Check if date has changed since last time
            val lastDate = sharedPrefs.getString("last_water_date", "")
            if (lastDate != today) {
                // Date changed - save new date
                sharedPrefs.edit().putString("last_water_date", today).apply()
            }
            
            // Combine both flows
            combine(
                repository.getTotalWaterForDate(today),
                repository.getWaterLogsForDate(today)
            ) { total, logs ->
                WaterUiState(
                    totalWater = total ?: 0,
                    waterLogs = logs,
                    currentDate = today,
                    dailyGoal = 3000
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    // Monitor for date changes and refresh data
    private fun startDateChangeMonitoring() {
        viewModelScope.launch {
            while (isActive) {
                delay(60000) // Check every minute
                val currentDate = LocalDate.now().toString()
                if (_uiState.value.currentDate != currentDate) {
                    // Date has changed, reload data
                    loadTodaysWater()
                }
            }
        }
    }
    
    // Schedule automatic reset at midnight
    private fun scheduleNightlyReset() {
        val alarmManager = getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Calculate time until midnight
        val now = Calendar.getInstance()
        val midnight = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val intent = Intent(getApplication(), WaterResetReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            9999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Set repeating alarm for midnight every day
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            midnight.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
    
    fun addWater(amount: Int) {
        viewModelScope.launch {
            repository.addWater(amount)
        }
    }
    
    fun addGlass() {
        addWater(250) // 250ml per glass
    }
    
    fun deleteWaterLog(waterLog: WaterLog) {
        viewModelScope.launch {
            repository.deleteWaterLog(waterLog)
        }
    }
    
    fun resetToday() {
        viewModelScope.launch {
            repository.resetDailyWater(LocalDate.now().toString())
        }
    }
    
    fun setDailyGoal(goalInMl: Int) {
        _uiState.update { it.copy(dailyGoal = goalInMl) }
        // Save to SharedPreferences
        getApplication<Application>().getSharedPreferences("water_settings", 0)
            .edit()
            .putInt("daily_goal", goalInMl)
            .apply()
    }
}
