package com.bodycalc.platepilot.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodycalc.platepilot.data.local.PlatePilotDatabase
import com.bodycalc.platepilot.data.model.UserProfile
import com.bodycalc.platepilot.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val database = PlatePilotDatabase.getDatabase(application)
    private val repository = MealRepository(
        database.mealDao(),
        database.mealPlanDao(),
        database.userProfileDao()
    )
    
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()
    
    private val _calorieGoal = MutableStateFlow(2000)
    val calorieGoal: StateFlow<Int> = _calorieGoal.asStateFlow()
    
    private val _proteinGoal = MutableStateFlow(150)
    val proteinGoal: StateFlow<Int> = _proteinGoal.asStateFlow()
    
    private val _fitnessGoal = MutableStateFlow("")
    val fitnessGoal: StateFlow<String> = _fitnessGoal.asStateFlow()
    
    private val _dietaryPreference = MutableStateFlow("")
    val dietaryPreference: StateFlow<String> = _dietaryPreference.asStateFlow()
    
    init {
        loadUserProfile()
    }
    
    private fun loadUserProfile() {
        viewModelScope.launch {
            repository.getUserProfile().collect { profile ->
                _userProfile.value = profile
                profile?.let {
                    _name.value = it.name
                    _calorieGoal.value = it.dailyCalorieGoal
                    _proteinGoal.value = it.proteinGoal
                    _fitnessGoal.value = it.fitnessGoal
                    _dietaryPreference.value = it.dietaryPreferences
                }
            }
        }
    }
    
    fun updateName(newName: String) {
        _name.value = newName
    }
    
    fun updateCalorieGoal(calories: Int) {
        _calorieGoal.value = calories
    }
    
    fun updateProteinGoal(protein: Int) {
        _proteinGoal.value = protein
    }
    
    fun updateFitnessGoal(goal: String) {
        _fitnessGoal.value = goal
    }
    
    fun updateDietaryPreference(preference: String) {
        _dietaryPreference.value = preference
    }
    
    fun saveProfile() {
        viewModelScope.launch {
            val existingProfile = _userProfile.value
            val profile = if (existingProfile != null) {
                // Update existing profile, preserving all fields
                existingProfile.copy(
                    name = _name.value,
                    dailyCalorieGoal = _calorieGoal.value,
                    proteinGoal = _proteinGoal.value,
                    fitnessGoal = _fitnessGoal.value,
                    dietaryPreferences = _dietaryPreference.value
                )
            } else {
                // Create new profile
                UserProfile(
                    name = _name.value,
                    dailyCalorieGoal = _calorieGoal.value,
                    proteinGoal = _proteinGoal.value,
                    fitnessGoal = _fitnessGoal.value,
                    dietaryPreferences = _dietaryPreference.value
                )
            }
            
            if (existingProfile == null) {
                repository.insertUserProfile(profile)
            } else {
                repository.updateUserProfile(profile)
            }
        }
    }
}
