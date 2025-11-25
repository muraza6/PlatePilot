package com.bodycalc.platepilot.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodycalc.platepilot.data.cache.MealCacheManager
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
    
    private val cacheManager = MealCacheManager(application, repository)
    
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
        viewModelScope.launch {
            repository.initializeSampleData()
            // Load 7-day cache immediately
            cacheManager.loadSevenDayCache()
            observeMealPlanChanges()
        }
    }
    
    private fun observeMealPlanChanges() {
        viewModelScope.launch {
            _currentDate.collect { date ->
                _isLoading.value = true
                val dateString = date.toString()
                
                try {
                    repository.getMealPlanByDateFlow(dateString).collect { mealPlan ->
                        if (mealPlan != null) {
                            mealPlan.breakfastId?.let { id ->
                                _breakfast.value = repository.getMealById(id)
                            } ?: run { _breakfast.value = null }
                            
                            mealPlan.lunchId?.let { id ->
                                _lunch.value = repository.getMealById(id)
                            } ?: run { _lunch.value = null }
                            
                            mealPlan.lunch2Id?.let { id ->
                                _lunch2.value = repository.getMealById(id)
                            } ?: run { _lunch2.value = null }
                            
                            mealPlan.dinnerId?.let { id ->
                                _dinner.value = repository.getMealById(id)
                            } ?: run { _dinner.value = null }
                        } else {
                            _breakfast.value = null
                            _lunch.value = null
                            _lunch2.value = null
                            _dinner.value = null
                        }
                        
                        // Set loading to false AFTER all meals are loaded
                        _isLoading.value = false
                    }
                } catch (e: Exception) {
                    // Handle error and still set loading to false
                    _isLoading.value = false
                }
            }
        }
    }
    
    private fun loadTodaysMeals() {
        // This function is now redundant since observeMealPlanChanges handles everything
        // Keeping it for backward compatibility but it won't do anything
    }
    
    fun navigateToDate(date: LocalDate) {
        _currentDate.value = date
    }
    
    fun previousDay() {
        _currentDate.value = _currentDate.value.minusDays(1)
    }
    
    fun nextDay() {
        _currentDate.value = _currentDate.value.plusDays(1)
    }
}
