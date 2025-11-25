package com.bodycalc.platepilot.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodycalc.platepilot.data.local.PlatePilotDatabase
import com.bodycalc.platepilot.data.model.Meal
import com.bodycalc.platepilot.data.model.MealPlan
import com.bodycalc.platepilot.data.model.MealType
import com.bodycalc.platepilot.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class PlansViewModel(application: Application) : AndroidViewModel(application) {
    private val database = PlatePilotDatabase.getDatabase(application)
    private val repository = MealRepository(
        database.mealDao(),
        database.mealPlanDao(),
        database.userProfileDao()
    )
    
    private val _weekDates = MutableStateFlow<List<LocalDate>>(emptyList())
    val weekDates: StateFlow<List<LocalDate>> = _weekDates.asStateFlow()
    
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()
    
    private val _availableMeals = MutableStateFlow<List<Meal>>(emptyList())
    val availableMeals: StateFlow<List<Meal>> = _availableMeals.asStateFlow()
    
    private val _mealsByType = MutableStateFlow<Map<MealType, List<Meal>>>(emptyMap())
    val mealsByType: StateFlow<Map<MealType, List<Meal>>> = _mealsByType.asStateFlow()
    
    init {
        loadWeekDates()
        loadAvailableMeals()
    }
    
    private fun loadWeekDates() {
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        _weekDates.value = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    }
    
    private fun loadAvailableMeals() {
        viewModelScope.launch {
            repository.getAllMeals().collect { meals ->
                _availableMeals.value = meals
                
                // Group by type
                val grouped = meals.groupBy { it.type }
                _mealsByType.value = grouped
            }
        }
    }
    
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
    
    fun assignMealToPlan(date: LocalDate, mealId: Long, mealType: MealType) {
        viewModelScope.launch {
            val dateString = date.toString()
            var mealPlan = repository.getMealPlanByDate(dateString)
            
            if (mealPlan == null) {
                mealPlan = MealPlan(date = dateString)
            }
            
            val updatedPlan = when (mealType) {
                MealType.BREAKFAST -> mealPlan.copy(breakfastId = mealId)
                MealType.LUNCH -> mealPlan.copy(lunchId = mealId)
                MealType.SNACK -> mealPlan.copy(lunch2Id = mealId)
                MealType.DINNER -> mealPlan.copy(dinnerId = mealId)
            }
            
            if (mealPlan.id == 0L) {
                repository.insertMealPlan(updatedPlan)
            } else {
                repository.updateMealPlan(updatedPlan)
            }
        }
    }
}
