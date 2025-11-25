package com.bodycalc.platepilot.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Retrofit API interface for fetching meals from GitHub
 */
interface GitHubMealApi {
    /**
     * Fetch meals.json from a GitHub repository
     * @param url The raw GitHub content URL (e.g., https://raw.githubusercontent.com/username/repo/main/meals.json)
     * @return List of meals from GitHub
     */
    @GET
    suspend fun getMeals(@Url url: String): Response<List<GitHubMeal>>
}

/**
 * Data class representing a meal from GitHub
 */
data class GitHubMeal(
    val id: Int,
    val name: String,
    val type: String,  // "Breakfast", "Lunch", "Dinner", "Snack"
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fats: Int,
    val imageUrl: String,
    val dietaryCategory: String,  // "Vegetarian", "Vegan", "Keto", "Non-Veg"
    val description: String = "",
    val ingredients: List<String> = emptyList(),  // List of ingredients
    val recipe: String = "",  // Cooking instructions
    val detailImageUrl: String = ""  // Optional detail image URL
)
