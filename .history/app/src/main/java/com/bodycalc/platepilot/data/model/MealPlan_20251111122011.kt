package com.bodycalc.platepilot.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "meal_plans")
data class MealPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // Store as ISO string (YYYY-MM-DD)
    val breakfastId: Long? = null,
    val lunchId: Long? = null,
    val lunch2Id: Long? = null, // Second lunch option
    val dinnerId: Long? = null,
    val snackIds: String = "" // JSON array of snack IDs
)

data class DailyMealPlan(
    val date: LocalDate,
    val breakfast: Meal? = null,
    val lunch: Meal? = null,
    val lunch2: Meal? = null,
    val dinner: Meal? = null,
    val snacks: List<Meal> = emptyList()
)
