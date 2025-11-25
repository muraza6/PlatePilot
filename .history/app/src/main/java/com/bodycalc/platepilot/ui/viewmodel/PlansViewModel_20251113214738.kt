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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
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
    
    private val _currentMealPlan = MutableStateFlow<MealPlan?>(null)
    val currentMealPlan: StateFlow<MealPlan?> = _currentMealPlan.asStateFlow()
    
    init {
        loadWeekDates()
        observeMealPlanForSelectedDate()
    }
    
    private fun loadWeekDates() {
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        _weekDates.value = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    }
    
    private fun observeMealPlanForSelectedDate() {
        viewModelScope.launch {
            _selectedDate
                .flatMapLatest { date ->
                    val dateString = date.toString()
                    repository.getMealPlanByDateFlow(dateString)
                }
                .collect { mealPlan ->
                    _currentMealPlan.value = mealPlan
                    loadMealsForCurrentPlan(mealPlan)
                }
        }
    }
    
    private suspend fun loadMealsForCurrentPlan(mealPlan: MealPlan?) {
        if (mealPlan == null) {
            _availableMeals.value = emptyList()
            _mealsByType.value = emptyMap()
            return
        }
        
        val meals = mutableListOf<Meal>()
        
        // Load only the meals assigned to this date
        mealPlan.breakfastId?.let { id ->
            repository.getMealById(id)?.let { meals.add(it) }
        }
        mealPlan.lunchId?.let { id ->
            repository.getMealById(id)?.let { meals.add(it) }
        }
        mealPlan.lunch2Id?.let { id ->
            repository.getMealById(id)?.let { meals.add(it) }
        }
        mealPlan.dinnerId?.let { id ->
            repository.getMealById(id)?.let { meals.add(it) }
        }
        
        _availableMeals.value = meals
        _mealsByType.value = meals.groupBy { it.type }
    }
    
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
}
