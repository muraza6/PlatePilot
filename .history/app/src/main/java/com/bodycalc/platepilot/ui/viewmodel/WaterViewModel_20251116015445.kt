package com.bodycalc.platepilot.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodycalc.platepilot.data.local.PlatePilotDatabase
import com.bodycalc.platepilot.data.model.WaterLog
import com.bodycalc.platepilot.data.repository.WaterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class WaterUiState(
    val totalWater: Int = 0, // ml consumed today
    val dailyGoal: Int = 2000, // ml (8 glasses × 250ml)
    val waterLogs: List<WaterLog> = emptyList(),
    val currentDate: String = LocalDate.now().toString()
)

class WaterViewModel(application: Application) : AndroidViewModel(application) {
    private val database = PlatePilotDatabase.getDatabase(application)
    private val repository = WaterRepository(database.waterLogDao())
    
    private val _uiState = MutableStateFlow(WaterUiState())
    val uiState: StateFlow<WaterUiState> = _uiState.asStateFlow()
    
    init {
        loadTodaysWater()
    }
    
    private fun loadTodaysWater() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            
            // Combine both flows
            combine(
                repository.getTotalWaterForDate(today),
                repository.getWaterLogsForDate(today)
            ) { total, logs ->
                WaterUiState(
                    totalWater = total ?: 0,
                    waterLogs = logs,
                    currentDate = today
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
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
