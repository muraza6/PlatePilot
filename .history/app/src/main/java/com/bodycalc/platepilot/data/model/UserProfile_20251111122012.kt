package com.bodycalc.platepilot.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Long = 1, // Single user profile
    val name: String = "",
    val dietaryPreferences: String = "", // JSON array: vegetarian, vegan, keto, etc.
    val allergies: String = "", // JSON array: nuts, dairy, gluten, etc.
    val dailyCalorieGoal: Int = 2000,
    val proteinGoal: Int = 150,
    val carbsGoal: Int = 200,
    val fatsGoal: Int = 65,
    val fitnessGoal: String = "" // weight loss, maintenance, muscle gain
)

data class DietaryPreference(
    val name: String,
    val isSelected: Boolean
)

data class Allergy(
    val name: String,
    val isSelected: Boolean
)
