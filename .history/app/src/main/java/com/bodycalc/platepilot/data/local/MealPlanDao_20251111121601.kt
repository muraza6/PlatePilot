package com.bodycalc.platepilot.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bodycalc.platepilot.data.model.MealPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {
    @Query("SELECT * FROM meal_plans ORDER BY date ASC")
    fun getAllMealPlans(): Flow<List<MealPlan>>
    
    @Query("SELECT * FROM meal_plans WHERE date = :date LIMIT 1")
    suspend fun getMealPlanByDate(date: String): MealPlan?
    
    @Query("SELECT * FROM meal_plans WHERE date = :date LIMIT 1")
    fun getMealPlanByDateFlow(date: String): Flow<MealPlan?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(mealPlan: MealPlan): Long
    
    @Update
    suspend fun updateMealPlan(mealPlan: MealPlan)
    
    @Delete
    suspend fun deleteMealPlan(mealPlan: MealPlan)
    
    @Query("DELETE FROM meal_plans")
    suspend fun deleteAllMealPlans()
}
