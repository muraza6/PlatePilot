package com.bodycalc.platepilot.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: MealType,
    val imageUrl: String? = null, // Card/HomeScreen image (e.g., without background)
    val detailImageUrl: String? = null, // DetailScreen image (e.g., with background)
    val description: String = "",
    val ingredients: String = "", // JSON string of ingredients list
    val recipe: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0,
    val isCloudImage: Boolean = false // true = Firebase URL, false = local drawable name
)

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}

data class MealWithNutrition(
    val meal: Meal,
    val totalCalories: Int,
    val totalProtein: Int,
    val totalCarbs: Int,
    val totalFats: Int
)
