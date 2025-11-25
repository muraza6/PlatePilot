package com.bodycalc.platepilot.data.repository

import com.bodycalc.platepilot.data.local.MealDao
import com.bodycalc.platepilot.data.local.MealPlanDao
import com.bodycalc.platepilot.data.local.UserProfileDao
import com.bodycalc.platepilot.data.model.DailyMealPlan
import com.bodycalc.platepilot.data.model.Meal
import com.bodycalc.platepilot.data.model.MealPlan
import com.bodycalc.platepilot.data.model.MealType
import com.bodycalc.platepilot.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class MealRepository(
    private val mealDao: MealDao,
    private val mealPlanDao: MealPlanDao,
    private val userProfileDao: UserProfileDao
) {
    // Meal operations
    fun getAllMeals(): Flow<List<Meal>> = mealDao.getAllMeals()
    
    fun getMealsByType(type: MealType): Flow<List<Meal>> = mealDao.getMealsByType(type)
    
    suspend fun getMealById(id: Long): Meal? = mealDao.getMealById(id)
    
    suspend fun insertMeal(meal: Meal): Long = mealDao.insertMeal(meal)
    
    suspend fun insertMeals(meals: List<Meal>) = mealDao.insertMeals(meals)
    
    suspend fun updateMeal(meal: Meal) = mealDao.updateMeal(meal)
    
    suspend fun deleteMeal(meal: Meal) = mealDao.deleteMeal(meal)
    
    // Meal plan operations
    fun getAllMealPlans(): Flow<List<MealPlan>> = mealPlanDao.getAllMealPlans()
    
    suspend fun getMealPlanByDate(date: String): MealPlan? = mealPlanDao.getMealPlanByDate(date)
    
    fun getMealPlanByDateFlow(date: String): Flow<MealPlan?> = mealPlanDao.getMealPlanByDateFlow(date)
    
    suspend fun insertMealPlan(mealPlan: MealPlan): Long = mealPlanDao.insertMealPlan(mealPlan)
    
    suspend fun updateMealPlan(mealPlan: MealPlan) = mealPlanDao.updateMealPlan(mealPlan)
    
    suspend fun deleteMealPlan(mealPlan: MealPlan) = mealPlanDao.deleteMealPlan(mealPlan)
    
    // User profile operations
    fun getUserProfile(): Flow<UserProfile?> = userProfileDao.getUserProfile()
    
    suspend fun getUserProfileOnce(): UserProfile? = userProfileDao.getUserProfileOnce()
    
    suspend fun insertUserProfile(userProfile: UserProfile) = userProfileDao.insertUserProfile(userProfile)
    
    suspend fun updateUserProfile(userProfile: UserProfile) = userProfileDao.updateUserProfile(userProfile)
    
    // Initialize sample data
    suspend fun initializeSampleData() {
        // Check if data already exists using first() to get immediate result
        val existingMeals = mealDao.getAllMeals().first()
        
        // Only insert if database is empty
        if (existingMeals.isNotEmpty()) {
            return
        }
        
        val sampleMeals = listOf(
            Meal(
                name = "Oatmeal with Berries",
                type = MealType.BREAKFAST,
                description = "Warm oatmeal topped with fresh berries",
                ingredients = "[\"Oats\", \"Milk\", \"Blueberries\", \"Raspberries\", \"Honey\"]",
                recipe = "1. Cook oats with milk\n2. Top with berries\n3. Drizzle honey",
                calories = 350,
                protein = 12,
                carbs = 58,
                fats = 8,
                imageUrl = "oats"
            ),
            Meal(
                name = "Grilled Chicken Salad",
                type = MealType.LUNCH,
                description = "Fresh salad with grilled chicken breast",
                ingredients = "[\"Chicken breast\", \"Mixed greens\", \"Tomatoes\", \"Cucumber\", \"Avocado\", \"Lime\"]",
                recipe = "1. Grill chicken\n2. Chop vegetables\n3. Mix and dress",
                calories = 420,
                protein = 45,
                carbs = 22,
                fats = 18,
                imageUrl = "grilled_chicken_salad"
            ),
            Meal(
                name = "Quinoa Stir-Fry",
                type = MealType.DINNER,
                description = "Protein-rich quinoa with vegetables",
                ingredients = "[\"Quinoa\", \"Shrimp\", \"Mixed vegetables\", \"Eggs\", \"Soy sauce\"]",
                recipe = "1. Cook quinoa\n2. Stir-fry vegetables and shrimp\n3. Add eggs\n4. Mix all",
                calories = 485,
                protein = 32,
                carbs = 52,
                fats = 15,
                imageUrl = "quinoa_stir_fry_bowl_image"
            ),
            Meal(
                name = "Fruit Salad Bowl",
                type = MealType.SNACK,
                description = "Fresh mixed fruit salad",
                ingredients = "[\"Strawberries\", \"Blueberries\", \"Mango\", \"Kiwi\", \"Honey\"]",
                recipe = "1. Wash and cut fruits\n2. Mix together\n3. Drizzle with honey",
                calories = 280,
                protein = 3,
                carbs = 68,
                fats = 2,
                imageUrl = "fruit_salad_bowl"
            )
        )
        
        insertMeals(sampleMeals)
        
        // Create today's meal plan
        val today = LocalDate.now().toString()
        val mealPlan = MealPlan(
            date = today,
            breakfastId = 1,
            lunchId = 2,
            lunch2Id = 4,
            dinnerId = 3
        )
        insertMealPlan(mealPlan)
    }
}
