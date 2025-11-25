package com.bodycalc.platepilot.data.cache

import android.content.Context
import android.content.SharedPreferences
import com.bodycalc.platepilot.data.model.DailyMealPlan
import com.bodycalc.platepilot.data.model.Meal
import com.bodycalc.platepilot.data.model.MealPlan
import com.bodycalc.platepilot.data.repository.MealRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate

/**
 * Caches meal data for 7 days to enable instant updates on HomeScreen
 * when day selection changes in Settings.
 */
class MealCacheManager(
    private val context: Context,
    private val repository: MealRepository
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "meal_cache",
        Context.MODE_PRIVATE
    )
    
    // In-memory cache: maps date string to DailyMealPlan
    private val mealCache = mutableMapOf<String, DailyMealPlan>()
    
    /**
     * Pre-load all 7 days of meal data into memory cache
     */
    suspend fun loadSevenDayCache() {
        val today = LocalDate.now()
        
        // Check if cache needs refresh (daily)
        val lastCacheUpdate = sharedPreferences.getLong("last_cache_update", 0)
        val currentDayMillis = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        val lastUpdateDay = lastCacheUpdate / (1000 * 60 * 60 * 24)
        
        // If cache was updated today, skip reload
        if (currentDayMillis == lastUpdateDay) {
            loadCacheFromMemory()
            return
        }
        
        // Clear old cache and load new 7 days
        mealCache.clear()
        
        for (dayOffset in 0..6) {
            val date = today.plusDays(dayOffset.toLong())
            val dateString = date.toString()
            
            try {
                val mealPlan = repository.getMealPlanByDate(dateString)
                
                if (mealPlan != null) {
                    val breakfast = mealPlan.breakfastId?.let { id -> repository.getMealById(id) }
                    val lunch = mealPlan.lunchId?.let { id -> repository.getMealById(id) }
                    val lunch2 = mealPlan.lunch2Id?.let { id -> repository.getMealById(id) }
                    val dinner = mealPlan.dinnerId?.let { id -> repository.getMealById(id) }
                    
                    val dailyMealPlan = DailyMealPlan(
                        date = date,
                        breakfast = breakfast,
                        lunch = lunch,
                        lunch2 = lunch2,
                        dinner = dinner
                    )
                    
                    mealCache[dateString] = dailyMealPlan
                } else {
                    // Create empty meal plan for the day
                    mealCache[dateString] = DailyMealPlan(
                        date = date,
                        breakfast = null,
                        lunch = null,
                        lunch2 = null,
                        dinner = null
                    )
                }
            } catch (e: Exception) {
                // Handle error gracefully
                mealCache[dateString] = DailyMealPlan(
                    date = date,
                    breakfast = null,
                    lunch = null,
                    lunch2 = null,
                    dinner = null
                )
            }
        }
        
        // Update cache timestamp
        sharedPreferences.edit()
            .putLong("last_cache_update", System.currentTimeMillis())
            .apply()
    }
    
    /**
     * Get meals for a specific date from cache
     */
    fun getMealsForDate(date: LocalDate): DailyMealPlan? {
        return mealCache[date.toString()]
    }
    
    /**
     * Get meals for a specific date string from cache
     */
    fun getMealsForDate(dateString: String): DailyMealPlan? {
        return mealCache[dateString]
    }
    
    /**
     * Refresh cache (useful if user updates meal plans manually)
     */
    suspend fun refreshCache() {
        mealCache.clear()
        sharedPreferences.edit()
            .putLong("last_cache_update", 0)
            .apply()
        loadSevenDayCache()
    }
    
    /**
     * Get all cached dates (for debugging)
     */
    fun getCachedDates(): List<String> {
        return mealCache.keys.toList()
    }
    
    /**
     * Load cache from memory (if already loaded)
     */
    private suspend fun loadCacheFromMemory() {
        if (mealCache.isNotEmpty()) return
        
        val today = LocalDate.now()
        for (dayOffset in 0..6) {
            val date = today.plusDays(dayOffset.toLong())
            val dateString = date.toString()
            
            try {
                val mealPlan = repository.getMealPlanByDate(dateString)
                
                if (mealPlan != null) {
                    val breakfast = mealPlan.breakfastId?.let { id -> repository.getMealById(id) }
                    val lunch = mealPlan.lunchId?.let { id -> repository.getMealById(id) }
                    val lunch2 = mealPlan.lunch2Id?.let { id -> repository.getMealById(id) }
                    val dinner = mealPlan.dinnerId?.let { id -> repository.getMealById(id) }
                    
                    val dailyMealPlan = DailyMealPlan(
                        date = dateString,
                        breakfast = breakfast,
                        lunch = lunch,
                        lunch2 = lunch2,
                        dinner = dinner
                    )
                    
                    mealCache[dateString] = dailyMealPlan
                }
            } catch (e: Exception) {
                // Handle error gracefully
            }
        }
    }
}
