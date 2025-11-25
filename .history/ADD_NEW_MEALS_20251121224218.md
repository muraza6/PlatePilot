# Adding New Meals to PlatePilot

This guide explains how to add new meals to your PlatePilot meal planning app.

## Overview

PlatePilot stores meals in a local database with nutrition information. Each meal includes:
- Name
- Calories
- Protein (grams)
- Carbohydrates (grams)
- Fats (grams)
- Serving size

## Step 1: Locate the Meal Repository

Open the file: `app/src/main/java/com/bodycalc/platepilot/data/repository/MealRepository.kt`

## Step 2: Find the initializeSampleData() Function

Look for the `initializeSampleData()` function in the MealRepository class.

## Step 3: Add New Meals to the List

Inside the `initializeSampleData()` function, find the `newMeals` list and add your new meals:

```kotlin
val newMeals = listOf(
    // Existing meals (don't modify these)
    Meal(name = "Oatmeal with Berries", calories = 320, protein = 12f, carbs = 54f, fats = 8f, servingSize = "1 bowl"),
    Meal(name = "Grilled Chicken Salad", calories = 350, protein = 35f, carbs = 15f, fats = 18f, servingSize = "1 bowl"),
    
    // Add your new meals here
    Meal(name = "Your New Meal Name", calories = 250, protein = 20f, carbs = 30f, fats = 8f, servingSize = "1 serving"),
    
    // More examples below...
)
```

## Step 4: Meal Format

Each meal must follow this exact format:

```kotlin
Meal(
    name = "Meal Name",           // String - descriptive name
    calories = 250,               // Int - total calories
    protein = 20f,                // Float - protein in grams
    carbs = 30f,                  // Float - carbohydrates in grams
    fats = 8f,                    // Float - fats in grams
    servingSize = "1 serving"     // String - serving description
)
```

## Step 5: Nutrition Data Sources

Get accurate nutrition data from:
- USDA FoodData Central
- MyFitnessPal
- Nutritionix API
- Reliable recipe websites

## Step 6: Examples of New Meals

### High Protein Meals
```kotlin
Meal(name = "Greek Yogurt Parfait", calories = 280, protein = 20f, carbs = 35f, fats = 8f, servingSize = "1 cup"),
Meal(name = "Tuna Salad", calories = 280, protein = 32f, carbs = 12f, fats = 14f, servingSize = "1 can tuna"),
Meal(name = "Turkey Lettuce Wraps", calories = 220, protein = 28f, carbs = 8f, fats = 10f, servingSize = "2 wraps"),
```

### Vegetarian Meals
```kotlin
Meal(name = "Lentil Soup", calories = 240, protein = 18f, carbs = 35f, fats = 4f, servingSize = "1 bowl"),
Meal(name = "Eggplant Parmesan", calories = 320, protein = 18f, carbs = 25f, fats = 16f, servingSize = "1 serving"),
Meal(name = "Black Bean Burrito", calories = 350, protein = 15f, carbs = 50f, fats = 10f, servingSize = "1 burrito"),
```

### Breakfast Meals
```kotlin
Meal(name = "Smoothie Bowl", calories = 280, protein = 12f, carbs = 45f, fats = 8f, servingSize = "1 bowl"),
Meal(name = "Egg White Omelette", calories = 180, protein = 22f, carbs = 5f, fats = 8f, servingSize = "3 egg whites"),
Meal(name = "Chia Seed Pudding", calories = 240, protein = 8f, carbs = 30f, fats = 12f, servingSize = "1 cup"),
```

### Snacks
```kotlin
Meal(name = "Protein Bar", calories = 200, protein = 20f, carbs = 20f, fats = 6f, servingSize = "1 bar"),
Meal(name = "Trail Mix", calories = 280, protein = 10f, carbs = 25f, fats = 16f, servingSize = "1/4 cup"),
Meal(name = "Greek Yogurt", calories = 150, protein = 15f, carbs = 12f, fats = 5f, servingSize = "6oz"),
```

### International Meals
```kotlin
Meal(name = "Chicken Shawarma", calories = 320, protein = 28f, carbs = 25f, fats = 15f, servingSize = "1 wrap"),
Meal(name = "Vegetable Sushi", calories = 280, protein = 8f, carbs = 50f, fats = 6f, servingSize = "6 pieces"),
Meal(name = "Falafel Bowl", calories = 380, protein = 15f, carbs = 45f, fats = 16f, servingSize = "1 bowl"),
```

## Step 7: Test the Changes

1. **Clean and rebuild the app:**
   ```bash
   ./gradlew clean
   ./gradlew assembleDebug
   ```

2. **Uninstall the app from your device** (to clear existing data)

3. **Install the new APK:**
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

4. **Run the app** - it will create the database with your new meals

5. **Verify the meals appear** in the meal planning screens

## Step 8: Troubleshooting

### Meals Not Appearing
- Check that the app data was cleared (uninstall/reinstall)
- Verify the meal format is correct
- Check Android Studio logs for database errors

### Nutrition Data Issues
- Ensure all values are positive numbers
- Protein, carbs, fats should be floats (use `20f` not `20`)
- Calories should be integers

### Database Errors
- Check that the Meal entity hasn't changed
- Verify Room database version is correct

## Step 9: Best Practices

### Nutrition Data
- Use reliable sources for nutrition information
- Include serving sizes that users can easily measure
- Consider different dietary needs (keto, vegetarian, vegan, etc.)

### Meal Variety
- Include breakfast, lunch, dinner, and snack options
- Add meals from different cuisines
- Balance protein, carb, and fat ratios

### Naming Conventions
- Use descriptive, appetizing names
- Include main ingredients in the name
- Keep names under 50 characters

## Step 10: Advanced Features (Optional)

### Adding Images
If you want to add images for meals, update the Meal entity to include image URLs:

```kotlin
Meal(
    name = "Chicken Shawarma",
    calories = 320,
    protein = 28f,
    carbs = 25f,
    fats = 15f,
    servingSize = "1 wrap",
    imageUrl = "chicken_shawarma.jpg"  // Add this field
)
```

### Meal Categories
Add categories to organize meals:

```kotlin
enum class MealCategory {
    BREAKFAST, LUNCH, DINNER, SNACK, DESSERT
}

Meal(
    name = "Greek Yogurt Parfait",
    category = MealCategory.BREAKFAST,  // Add category
    calories = 280,
    // ... other fields
)
```

## Support

If you encounter issues:
1. Check the Android Studio Logcat for error messages
2. Verify all syntax is correct
3. Ensure the app builds successfully
4. Test on a device or emulator

## Example Complete Addition

Here's how to add a complete new meal:

```kotlin
// In MealRepository.kt, inside initializeSampleData()
val newMeals = listOf(
    // ... existing meals ...
    
    // NEW MEAL ADDITION
    Meal(
        name = "Mediterranean Quinoa Bowl",
        calories = 380,
        protein = 15f,
        carbs = 45f,
        fats = 16f,
        servingSize = "1 large bowl"
    ),
    
    // ... more meals ...
)
```

After adding, rebuild and test the app!</content>
<parameter name="filePath">/Users/musharrafraza/AndroidStudioProjects/PlatePilot/ADD_NEW_MEALS.md