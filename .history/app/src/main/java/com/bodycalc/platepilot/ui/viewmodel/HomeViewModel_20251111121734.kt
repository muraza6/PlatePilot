package com.bodycalc.platepilot.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodycalc.platepilot.data.local.PlatePilotDatabase
import com.bodycalc.platepilot.data.model.DailyMealPlan
import com.bodycalc.platepilot.data.model.Meal
import com.bodycalc.platepilot.data.model.MealPlan
import com.bodycalc.platepilot.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = PlatePilotDatabase.getDatabase(application)
    private val repository = MealRepository(
        database.mealDao(),
        database.mealPlanDao(),
        database.userProfileDao()
    )
    
    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> = _currentDate.asStateFlow()
    
    private val _breakfast = MutableStateFlow<Meal?>(null)
    val breakfast: StateFlow<Meal?> = _breakfast.asStateFlow()
    
    private val _lunch = MutableStateFlow<Meal?>(null)
    val lunch: StateFlow<Meal?> = _lunch.asStateFlow()
    
    private val _lunch2 = MutableStateFlow<Meal?>(null)
    val lunch2: StateFlow<Meal?> = _lunch2.asStateFlow()
    
    private val _dinner = MutableStateFlow<Meal?>(null)
    val dinner: StateFlow<Meal?> = _dinner.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        initializeData()
        loadTodaysMeals()
    }
    
    private fun initializeData() {
        viewModelScope.launch {
            repository.initializeSampleData()
        }
    }
    
    fun loadTodaysMeals() {
        viewModelScope.launch {
            _isLoading.value = true
            val dateString = _currentDate.value.toString()
            val mealPlan = repository.getMealPlanByDate(dateString)
            
            if (mealPlan != null) {
                mealPlan.breakfastId?.let { id ->
                    _breakfast.value = repository.getMealById(id)
                }
                mealPlan.lunchId?.let { id ->
                    _lunch.value = repository.getMealById(id)
                }
                mealPlan.lunch2Id?.let { id ->
                    _lunch2.value = repository.getMealById(id)
                }
                mealPlan.dinnerId?.let { id ->
                    _dinner.value = repository.getMealById(id)
                }
            }
            _isLoading.value = false
        }
    }
    
    fun navigateToDate(date: LocalDate) {
        _currentDate.value = date
        loadTodaysMeals()
    }
    
    fun previousDay() {
        _currentDate.value = _currentDate.value.minusDays(1)
        loadTodaysMeals()
    }
    
    fun nextDay() {
        _currentDate.value = _currentDate.value.plusDays(1)
        loadTodaysMeals()
    }
}
