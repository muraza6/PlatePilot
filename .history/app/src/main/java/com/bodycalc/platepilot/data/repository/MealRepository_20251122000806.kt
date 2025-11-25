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
    
    // Regenerate meal plans based on dietary preference
    suspend fun regenerateMealPlans() {
        // Delete all existing meal plans
        val allPlans = mealPlanDao.getAllMealPlans().first()
        allPlans.forEach { plan ->
            mealPlanDao.deleteMealPlan(plan)
        }
        
        // Generate new plans based on current dietary preference
        ensureSevenDayMealPlans()
    }
    
    // Initialize sample data
    suspend fun initializeSampleData() {
        // Check if data already exists using first() to get immediate result
        val existingMeals = mealDao.getAllMeals().first()
        
        // Only insert meals if database is empty
        if (existingMeals.isEmpty()) {
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
                isCloudImage = false,
                dietaryCategory = "Vegetarian"
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
                isCloudImage = false,
                dietaryCategory = "Non-Veg"
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
                isCloudImage = false,
                dietaryCategory = "Non-Veg"
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
                isCloudImage = false,
                dietaryCategory = "Vegan"
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
                isCloudImage = true,
                dietaryCategory = "Vegetarian"
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
                isCloudImage = true,
                dietaryCategory = "Vegetarian"
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
                isCloudImage = true,
                dietaryCategory = "Vegan"
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
                isCloudImage = true,
                dietaryCategory = "Keto"
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
                isCloudImage = true,
                dietaryCategory = "Non-Veg"
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
                isCloudImage = true,
                dietaryCategory = "Non-Veg"
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
                isCloudImage = true,
                dietaryCategory = "Non-Veg"
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
                isCloudImage = true,
                dietaryCategory = "Non-Veg"
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
                isCloudImage = true,
                dietaryCategory = "Vegan"
            ),
            
            // New Breakfast Meals from GitHub
            Meal(
                name = "Crepes",
                type = MealType.BREAKFAST,
                description = "Delicate French-style crepes with your choice of fillings",
                ingredients = "[\"Flour\", \"Eggs\", \"Milk\", \"Butter\", \"Sugar\", \"Vanilla extract\"]",
                recipe = "1. Mix batter ingredients\n2. Let rest for 30 minutes\n3. Cook on non-stick pan\n4. Fill with your preferred toppings",
                calories = 350,
                protein = 8,
                carbs = 52,
                fats = 12,
                imageUrl = GitHubImageManager.getBreakfastImageUrl("Crepes_card.png"),
                detailImageUrl = GitHubImageManager.getBreakfastImageUrl("Crepes_detail.png"),
                isCloudImage = true,
                dietaryCategory = "Vegetarian"
            ),
            Meal(
                name = "French Toast",
                type = MealType.BREAKFAST,
                description = "Classic French toast with cinnamon and maple syrup",
                ingredients = "[\"Bread\", \"Eggs\", \"Milk\", \"Cinnamon\", \"Vanilla\", \"Butter\", \"Maple syrup\"]",
                recipe = "1. Whisk eggs with milk and cinnamon\n2. Dip bread slices\n3. Cook on buttered griddle\n4. Serve with maple syrup",
                calories = 380,
                protein = 12,
                carbs = 48,
                fats = 16,
                imageUrl = GitHubImageManager.getBreakfastImageUrl("French_Toast_card.png"),
                detailImageUrl = GitHubImageManager.getBreakfastImageUrl("French_Toast_detail.png"),
                isCloudImage = true,
                dietaryCategory = "Vegetarian"
            ),
            
            // New Non-Veg Meals from GitHub
            Meal(
                name = "Döner Kebab",
                type = MealType.LUNCH,
                description = "Tender marinated meat served in pita bread with fresh vegetables",
                ingredients = "[\"Lamb or chicken\", \"Pita bread\", \"Tomatoes\", \"Lettuce\", \"Onions\", \"Yogurt sauce\", \"Garlic\"]",
                recipe = "1. Marinate meat overnight\n2. Cook on vertical rotisserie or grill\n3. Warm pita bread\n4. Slice meat and serve with vegetables and sauce",
                calories = 520,
                protein = 32,
                carbs = 45,
                fats = 24,
                imageUrl = GitHubImageManager.getNonVegImageUrl("Do\u0308ner_Kebab_card.png"),
                detailImageUrl = GitHubImageManager.getNonVegImageUrl("Do\u0308ner_Kebab_detail.png"),
                isCloudImage = true,
                dietaryCategory = "Non-Veg"
            ),
            Meal(
                name = "Fried Rice",
                type = MealType.LUNCH,
                description = "Aromatic fried rice with vegetables, egg, and your choice of protein",
                ingredients = "[\"Cooked rice\", \"Eggs\", \"Carrots\", \"Peas\", \"Green beans\", \"Soy sauce\", \"Garlic\"]",
                recipe = "1. Cook rice a day ahead\n2. Scramble eggs and remove\n3. Stir-fry vegetables\n4. Add rice and soy sauce, mix in eggs",
                calories = 420,
                protein = 15,
                carbs = 52,
                fats = 16,
                imageUrl = GitHubImageManager.getNonVegImageUrl("Fried_rice_card.png"),
                detailImageUrl = GitHubImageManager.getNonVegImageUrl("Fried_rice_detail.png"),
                isCloudImage = true,
                dietaryCategory = "Vegetarian"
            ),
            
            // New Vegan Meals from GitHub
            Meal(
                name = "Pasta",
                type = MealType.DINNER,
                description = "Delicious vegan pasta with fresh herbs and olive oil",
                ingredients = "[\"Pasta\", \"Olive oil\", \"Garlic\", \"Fresh parsley\", \"Basil\", \"Tomatoes\", \"Mushrooms\"]",
                recipe = "1. Cook pasta until al dente\n2. Sauté garlic and vegetables\n3. Toss pasta with oil and vegetables\n4. Garnish with fresh herbs",
                calories = 420,
                protein = 16,
                carbs = 65,
                fats = 12,
                imageUrl = GitHubImageManager.getVeganImageUrl("Pasta_card.png"),
                detailImageUrl = GitHubImageManager.getVeganImageUrl("Pasta_detail.png"),
                isCloudImage = true,
                dietaryCategory = "Vegan"
            ),
            Meal(
                name = "Spaghetti",
                type = MealType.DINNER,
                description = "Classic spaghetti with rich tomato sauce and fresh vegetables",
                ingredients = "[\"Spaghetti\", \"Tomatoes\", \"Garlic\", \"Onions\", \"Olive oil\", \"Herbs\", \"Spinach\"]",
                recipe = "1. Cook spaghetti\n2. Simmer tomato sauce with vegetables\n3. Combine pasta and sauce\n4. Top with fresh basil",
                calories = 380,
                protein = 14,
                carbs = 62,
                fats = 10,
                imageUrl = GitHubImageManager.getVeganImageUrl("Sphageti_card.png"),
                detailImageUrl = GitHubImageManager.getVeganImageUrl("Sphageti_detail.png"),
                isCloudImage = true,
                dietaryCategory = "Vegan"
            )
        )
        
            insertMeals(sampleMeals)
        }
        
        // Always ensure 7 days of meal plans exist for the current week
        ensureSevenDayMealPlans()
    }
    
    /**
     * Ensures that meal plans exist for the next 7 days from today.
     * This is called every time the app starts to keep meal plans current.
     * Uses all available meals distributed across 7 days for variety.
     * Filters meals based on user's dietary preference.
     */
    private suspend fun ensureSevenDayMealPlans() {
        val today = LocalDate.now()
        
        // Get user's dietary preference
        val userProfile = getUserProfileOnce()
        val dietaryPreference = userProfile?.dietaryPreferences ?: "None"
        
        // Get all meals from database
        val allMeals = mealDao.getAllMeals().first()
        
        // Filter meals based on dietary preference
        val filteredMeals = if (dietaryPreference == "None" || dietaryPreference.isEmpty()) {
            allMeals // Show all meals if no preference selected
        } else {
            allMeals.filter { meal ->
                meal.dietaryCategory == dietaryPreference ||
                // Vegan meals are also suitable for vegetarians
                (dietaryPreference == "Vegetarian" && meal.dietaryCategory == "Vegan")
            }
        }
        
        // Separate filtered meals by type
        val breakfastMeals = filteredMeals.filter { it.type == MealType.BREAKFAST }
        val lunchMeals = filteredMeals.filter { it.type == MealType.LUNCH }
        val dinnerMeals = filteredMeals.filter { it.type == MealType.DINNER }
        val snackMeals = filteredMeals.filter { it.type == MealType.SNACK }
        
        // Ensure we have enough meals for 7 days (cycle if needed)
        val breakfasts = if (breakfastMeals.isNotEmpty()) {
            (0..6).map { breakfastMeals[it % breakfastMeals.size].id }
        } else {
            listOf(1L, 1L, 1L, 1L, 1L, 1L, 1L) // Fallback to first meal
        }
        
        val lunches = if (lunchMeals.isNotEmpty()) {
            (0..6).map { lunchMeals[it % lunchMeals.size].id }
        } else {
            listOf(2L, 2L, 2L, 2L, 2L, 2L, 2L) // Fallback
        }
        
        val dinners = if (dinnerMeals.isNotEmpty()) {
            (0..6).map { dinnerMeals[it % dinnerMeals.size].id }
        } else {
            listOf(3L, 3L, 3L, 3L, 3L, 3L, 3L) // Fallback
        }
        
        val snackId = snackMeals.firstOrNull()?.id ?: 4L  // Default to Fruit Salad Bowl
        
        // Create meal plans for the next 7 days with variety
        for (dayOffset in 0..6) {
            val date = today.plusDays(dayOffset.toLong()).toString()
            val existingPlan = getMealPlanByDate(date)
            
            if (existingPlan == null) {
                val mealPlan = MealPlan(
                    date = date,
                    breakfastId = breakfasts[dayOffset],
                    lunchId = lunches[dayOffset],
                    lunch2Id = snackId,
                    dinnerId = dinners[dayOffset]
                )
                insertMealPlan(mealPlan)
            }
        }
    }
}
