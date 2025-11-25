package com.bodycalc.platepilot.data.remote

import android.util.Log
import com.bodycalc.platepilot.data.model.Meal
import com.bodycalc.platepilot.data.model.MealType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Service for fetching meals from GitHub
 */
class GitHubMealService {
    
    companion object {
        private const val TAG = "GitHubMealService"
        // Base URL doesn't matter since we use @Url in the API interface
        private const val BASE_URL = "https://raw.githubusercontent.com/"
    }
    
    private val api: GitHubMealApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubMealApi::class.java)
    }
    
    /**
     * Fetch meals from GitHub repository
     * @param githubUrl The raw GitHub content URL for meals.json
     * Example: https://raw.githubusercontent.com/username/repo/main/meals.json
     */
    fun getMealsFromGitHub(githubUrl: String): Flow<Result<List<Meal>>> = flow {
        try {
            Log.d(TAG, "Fetching meals from GitHub: $githubUrl")
            
            val response = api.getMeals(githubUrl)
            
            if (response.isSuccessful) {
                val githubMeals = response.body()
                if (githubMeals != null) {
                    // Convert GitHub meals to local Meal entities
                    val meals = githubMeals.mapNotNull { githubMeal ->
                        try {
                            Meal(
                                id = githubMeal.id.toLong(),
                                name = githubMeal.name,
                                type = when (githubMeal.type.uppercase()) {
                                    "BREAKFAST" -> MealType.BREAKFAST
                                    "LUNCH" -> MealType.LUNCH
                                    "DINNER" -> MealType.DINNER
                                    "SNACK" -> MealType.SNACK
                                    else -> MealType.LUNCH // Default fallback
                                },
                                calories = githubMeal.calories,
                                protein = githubMeal.protein,
                                carbs = githubMeal.carbs,
                                fats = githubMeal.fats,
                                imageUrl = githubMeal.imageUrl,
                                isCloudImage = true, // GitHub images are cloud images
                                dietaryCategory = githubMeal.dietaryCategory,
                                description = githubMeal.description
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error converting meal: ${githubMeal.name}", e)
                            null // Skip invalid meals
                        }
                    }
                    Log.d(TAG, "Successfully fetched ${meals.size} meals from GitHub")
                    emit(Result.success(meals))
                } else {
                    Log.e(TAG, "Response body is null")
                    emit(Result.failure(Exception("Empty response from GitHub")))
                }
            } else {
                val errorMsg = "GitHub API error: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                emit(Result.failure(Exception(errorMsg)))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching meals from GitHub", e)
            emit(Result.failure(e))
        }
    }
}
