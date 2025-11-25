package com.bodycalc.platepilot.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodycalc.platepilot.data.cache.MealCacheManager
import com.bodycalc.platepilot.data.local.PlatePilotDatabase
import com.bodycalc.platepilot.data.model.Meal
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
    
    private val _currentDate = MutableStateFlow<LocalDate?>(null)
    val currentDate: StateFlow<LocalDate?> = _currentDate.asStateFlow()
    
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
            cacheManager.loadSevenDayCache()
            observeMealPlanChanges()
        }
    }
    
    private fun observeMealPlanChanges() {
        viewModelScope.launch {
            _currentDate.collect { date ->
                if (date == null) return@collect
                
                _isLoading.value = true
                val dateString = date.toString()
                
                android.util.Log.d("HomeViewModel", "Loading meals for date: $dateString")
                
                try {
                    val cachedMealPlan = cacheManager.getMealsForDate(dateString)
                    
                    if (cachedMealPlan != null) {
                        android.util.Log.d("HomeViewModel", "Loading from cache")
                        _breakfast.value = cachedMealPlan.breakfast
                        _lunch.value = cachedMealPlan.lunch
                        _lunch2.value = cachedMealPlan.lunch2
                        _dinner.value = cachedMealPlan.dinner
                         // ✅ ADD DETAILED LOGS
                        android.util.Log.d("HomeViewModel", "Loading from cache for $dateString")
    android.util.Log.d("HomeViewModel", "  Breakfast: ${cachedMealPlan.breakfast?.name} (ID: ${cachedMealPlan.breakfast?.id})")
    android.util.Log.d("HomeViewModel", "  Lunch: ${cachedMealPlan.lunch?.name} (ID: ${cachedMealPlan.lunch?.id})")
    android.util.Log.d("HomeViewModel", "  Dinner: ${cachedMealPlan.dinner?.name} (ID: ${cachedMealPlan.dinner?.id})")
    android.util.Log.d("HomeViewModel", "  Breakfast Image: ${cachedMealPlan.breakfast?.imageUrl}")
                    } else {
                        android.util.Log.d("HomeViewModel", "Loading from database")
                        val mealPlan = repository.getMealPlanByDate(dateString)
                        
                        if (mealPlan != null) {
                            _breakfast.value = mealPlan.breakfastId?.let { id -> repository.getMealById(id) }
                            _lunch.value = mealPlan.lunchId?.let { id -> repository.getMealById(id) }
                            _lunch2.value = mealPlan.lunch2Id?.let { id -> repository.getMealById(id) }
                            _dinner.value = mealPlan.dinnerId?.let { id -> repository.getMealById(id) }
                        } else {
                            _breakfast.value = null
                            _lunch.value = null
                            _lunch2.value = null
                            _dinner.value = null
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("HomeViewModel", "Error loading meals", e)
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun navigateToDate(date: LocalDate) {
        android.util.Log.d("HomeViewModel", "navigateToDate called with: $date")
        _currentDate.value = date
    }
    
    fun previousDay() {
        _currentDate.value?.let {
            _currentDate.value = it.minusDays(1)
        }
    }
    
    fun nextDay() {
        _currentDate.value?.let {
            _currentDate.value = it.plusDays(1)
        }
    }
    
    fun refreshMeals() {
        viewModelScope.launch {
            cacheManager.refreshCache()
            _currentDate.value?.let { current ->
                _currentDate.value = current.minusDays(1)
                _currentDate.value = current
            }
        }
    }
}