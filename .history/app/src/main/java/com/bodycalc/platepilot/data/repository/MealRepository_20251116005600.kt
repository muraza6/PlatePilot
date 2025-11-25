package com.bodycalc.platepilot.data.repository

import com.bodycalc.platepilot.data.local.MealDao
import com.bodycalc.platepilot.data.local.MealPlanDao
import com.bodycalc.platepilot.data.local.UserProfileDao
import com.bodycalc.platepilot.data.model.DailyMealPlan
import com.bodycalc.platepilot.data.model.Meal
import com.bodycalc.platepilot.data.model.MealPlan
import com.bodycalc.platepilot.data.model.MealType
import com.bodycalc.platepilot.data.model.UserProfile
import com.bodycalc.platepilot.utils.GitHubImageManager
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
            // Local meals (offline support)
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
                imageUrl = "oatmeal_berries_new",
                detailImageUrl = "recipe_oats",
                isCloudImage = false
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
                imageUrl = "grilled_chicken_salad_new",
                detailImageUrl = "recipe_grilled_chicken_salad",
                isCloudImage = false
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
                imageUrl = "quinoa_stir_fry_new",
                detailImageUrl = "recipe_quinoa_stir_fry_bowl_image",
                isCloudImage = false
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
                imageUrl = "fruit_salad_bowl_new",
                detailImageUrl = "recipe_fruit_salad_bowl",
                isCloudImage = false
            ),
            
            // Cloud meals (GitHub-hosted images - require internet)
            
            // Breakfast meals from GitHub
            Meal(
                name = "Pancake Breakfast",
                type = MealType.BREAKFAST,
                description = "Fluffy pancakes with syrup and butter",
                ingredients = "[\"Flour\", \"Eggs\", \"Milk\", \"Butter\", \"Maple syrup\", \"Berries\"]",
                recipe = "1. Mix dry ingredients\n2. Add wet ingredients\n3. Cook on griddle\n4. Serve with syrup",
                calories = 520,
                protein = 14,
                carbs = 78,
                fats = 18,
                imageUrl = GitHubImageManager.getBreakfastImageUrl("Pancake Breakfast_card.png"),
                detailImageUrl = GitHubImageManager.getBreakfastImageUrl("Pancake Breakfast_detail.png"),
                isCloudImage = true
            ),
            Meal(
                name = "Avocado Toast Breakfast",
                type = MealType.BREAKFAST,
                description = "Healthy avocado toast with eggs",
                ingredients = "[\"Whole grain bread\", \"Avocado\", \"Eggs\", \"Cherry tomatoes\", \"Olive oil\", \"Salt\", \"Pepper\"]",
                recipe = "1. Toast bread\n2. Mash avocado\n3. Fry or poach eggs\n4. Assemble and season",
                calories = 380,
                protein = 16,
                carbs = 32,
                fats = 22,
                imageUrl = GitHubImageManager.getBreakfastImageUrl("Avocado Toast Breakfast_card.png"),
                detailImageUrl = GitHubImageManager.getBreakfastImageUrl("Avocado Toast Breakfast_detail.png"),
                isCloudImage = true
            ),
            Meal(
                name = "Acai Smoothie Bowl",
                type = MealType.BREAKFAST,
                description = "Refreshing acai bowl with toppings",
                ingredients = "[\"Acai puree\", \"Banana\", \"Granola\", \"Berries\", \"Coconut flakes\", \"Honey\"]",
                recipe = "1. Blend acai with banana\n2. Pour into bowl\n3. Add toppings\n4. Serve immediately",
                calories = 420,
                protein = 8,
                carbs = 68,
                fats = 14,
                imageUrl = GitHubImageManager.getBreakfastImageUrl("Acai:Smoothie Bowl Breakfast_card.png"),
                detailImageUrl = GitHubImageManager.getBreakfastImageUrl("Acai:Smoothie Bowl Breakfast_detail.png"),
                isCloudImage = true
            ),
            Meal(
                name = "Keto Breakfast",
                type = MealType.BREAKFAST,
                description = "Low-carb keto breakfast plate",
                ingredients = "[\"Eggs\", \"Bacon\", \"Avocado\", \"Cheese\", \"Spinach\", \"Butter\"]",
                recipe = "1. Cook bacon until crispy\n2. Scramble eggs with butter\n3. Serve with avocado and greens",
                calories = 580,
                protein = 32,
                carbs = 8,
                fats = 48,
                imageUrl = GitHubImageManager.getBreakfastImageUrl("Keto Breakfast_card.png"),
                detailImageUrl = GitHubImageManager.getBreakfastImageUrl("Keto Breakfast_detail.png"),
                isCloudImage = true
            ),
            
            // Non-veg meals from GitHub
            Meal(
                name = "Chicken Tikka",
                type = MealType.LUNCH,
                description = "Spicy Indian chicken tikka with aromatic spices",
                ingredients = "[\"Chicken\", \"Yogurt\", \"Garam masala\", \"Cumin\", \"Coriander\", \"Lemon\", \"Ginger\", \"Garlic\"]",
                recipe = "1. Marinate chicken in yogurt and spices for 2 hours\n2. Skewer chicken pieces\n3. Grill until charred and cooked through\n4. Serve with lemon wedges",
                calories = 450,
                protein = 42,
                carbs = 12,
                fats = 25,
                imageUrl = GitHubImageManager.getNonVegImageUrl("chicken_tikka_card.png"),
                detailImageUrl = GitHubImageManager.getNonVegImageUrl("chicken_tikka_detail.jpeg"),
                isCloudImage = true
            ),
            Meal(
                name = "Grilled Chicken Breast",
                type = MealType.LUNCH,
                description = "Perfectly grilled chicken breast with vegetables",
                ingredients = "[\"Chicken breast\", \"Olive oil\", \"Garlic\", \"Herbs\", \"Vegetables\", \"Lemon\"]",
                recipe = "1. Season chicken with herbs\n2. Grill until cooked through\n3. Serve with roasted vegetables",
                calories = 420,
                protein = 48,
                carbs = 18,
                fats = 16,
                imageUrl = GitHubImageManager.getNonVegImageUrl("Grilled Chicken Breast Meal_card.png"),
                detailImageUrl = GitHubImageManager.getNonVegImageUrl("Grilled Chicken Breast Meal_detail.png"),
                isCloudImage = true
            ),
            Meal(
                name = "Salmon & Quinoa",
                type = MealType.DINNER,
                description = "Grilled salmon with quinoa and vegetables",
                ingredients = "[\"Salmon fillet\", \"Quinoa\", \"Broccoli\", \"Lemon\", \"Olive oil\", \"Garlic\"]",
                recipe = "1. Cook quinoa\n2. Grill salmon\n3. Steam broccoli\n4. Combine and season",
                calories = 520,
                protein = 42,
                carbs = 38,
                fats = 22,
                imageUrl = GitHubImageManager.getNonVegImageUrl("Salmon & Quinoa Meal_card.png"),
                detailImageUrl = GitHubImageManager.getNonVegImageUrl("Salmon & Quinoa Meal_detail.png"),
                isCloudImage = true
            ),
            Meal(
                name = "Shrimp Tacos",
                type = MealType.DINNER,
                description = "Fresh shrimp tacos with cabbage slaw and lime crema",
                ingredients = "[\"Shrimp\", \"Corn tortillas\", \"Cabbage\", \"Lime\", \"Cilantro\", \"Avocado\", \"Sour cream\", \"Chili powder\"]",
                recipe = "1. Season and cook shrimp with chili powder\n2. Prepare cabbage slaw with lime\n3. Make lime crema\n4. Warm tortillas and assemble tacos",
                calories = 520,
                protein = 38,
                carbs = 45,
                fats = 22,
                imageUrl = GitHubImageManager.getNonVegImageUrl("Shrimp Tacos_card.png"),
                detailImageUrl = GitHubImageManager.getNonVegImageUrl("Shrimp Tacos_detail.png"),
                isCloudImage = true
            ),
            
            // Vegan meals from GitHub
            Meal(
                name = "Vegan Tofu Bowl",
                type = MealType.LUNCH,
                description = "Nutritious tofu bowl with vegetables and grains",
                ingredients = "[\"Tofu\", \"Brown rice\", \"Kale\", \"Sweet potato\", \"Sesame oil\", \"Tahini sauce\"]",
                recipe = "1. Bake tofu until crispy\n2. Cook rice\n3. Roast vegetables\n4. Assemble bowl with tahini",
                calories = 460,
                protein = 22,
                carbs = 52,
                fats = 18,
                imageUrl = GitHubImageManager.getVeganImageUrl("Vegan Tofu Meal_card.png"),
                detailImageUrl = GitHubImageManager.getVeganImageUrl("Vegan Tofu Meal_detail.png"),
                isCloudImage = true
            )
        )
        
        insertMeals(sampleMeals)
        
        // Create 7 days of meal plans (today + next 6 days)
        val mealPlans = listOf(
            // Day 1 - Today
            MealPlan(
                date = LocalDate.now().toString(),
                breakfastId = 1,  // Oatmeal with Berries
                lunchId = 2,      // Grilled Chicken Salad
                lunch2Id = 4,     // Fruit Salad Bowl
                dinnerId = 3      // Quinoa Stir-Fry
            ),
            // Day 2 - Tomorrow
            MealPlan(
                date = LocalDate.now().plusDays(1).toString(),
                breakfastId = 5,  // Pancake Breakfast
                lunchId = 9,      // Chicken Tikka
                lunch2Id = 4,     // Fruit Salad Bowl
                dinnerId = 12     // Shrimp Tacos
            ),
            // Day 3
            MealPlan(
                date = LocalDate.now().plusDays(2).toString(),
                breakfastId = 6,  // Avocado Toast Breakfast
                lunchId = 10,     // Grilled Chicken Breast
                lunch2Id = 4,     // Fruit Salad Bowl
                dinnerId = 11     // Salmon & Quinoa
            ),
            // Day 4
            MealPlan(
                date = LocalDate.now().plusDays(3).toString(),
                breakfastId = 7,  // Acai Smoothie Bowl
                lunchId = 13,     // Vegan Tofu Bowl
                lunch2Id = 4,     // Fruit Salad Bowl
                dinnerId = 3      // Quinoa Stir-Fry
            ),
            // Day 5
            MealPlan(
                date = LocalDate.now().plusDays(4).toString(),
                breakfastId = 8,  // Keto Breakfast
                lunchId = 2,      // Grilled Chicken Salad
                lunch2Id = 4,     // Fruit Salad Bowl
                dinnerId = 12     // Shrimp Tacos
            ),
            // Day 6
            MealPlan(
                date = LocalDate.now().plusDays(5).toString(),
                breakfastId = 1,  // Oatmeal with Berries
                lunchId = 9,      // Chicken Tikka
                lunch2Id = 4,     // Fruit Salad Bowl
                dinnerId = 11     // Salmon & Quinoa
            ),
            // Day 7
            MealPlan(
                date = LocalDate.now().plusDays(6).toString(),
                breakfastId = 5,  // Pancake Breakfast
                lunchId = 10,     // Grilled Chicken Breast
                lunch2Id = 4,     // Fruit Salad Bowl
                dinnerId = 3      // Quinoa Stir-Fry
            )
        )
        
        // Insert meal plans only if they don't exist
        mealPlans.forEach { plan ->
            val existingPlan = getMealPlanByDate(plan.date)
            if (existingPlan == null) {
                insertMealPlan(plan)
            }
        }
    }
}
