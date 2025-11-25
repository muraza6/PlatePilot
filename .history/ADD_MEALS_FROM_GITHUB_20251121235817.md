# Adding New Meals from GitHub to PlatePilot App

This guide explains how to add new meals from your GitHub repository to your PlatePilot meal planning app.

## Prerequisites

- GitHub repository: `https://github.com/muraza6/meal-images`
- Images organized in branches by meal category (e.g., `breakfast`, `Nonveg_meals`, `Vegan_meals`)
- Android Studio installed
- ADB (Android Debug Bridge) set up

## Step 1: Upload Meal Images to GitHub

### 1.1 Organize Your Images

Create images for each meal:
- **Card Image**: `meal_name_card.png` (shown in meal list/cards)
- **Detail Image**: `meal_name_detail.png` (shown in meal detail screen)

### 1.2 Create Branch for Meal Category

```bash
cd ~/meal-images
git checkout -b breakfast    # or Nonveg_meals, Vegan_meals, Lunch_meals, etc.
```

### 1.3 Upload Images

```bash
# Add your images
git add Pancake_Breakfast_card.png
git add Pancake_Breakfast_detail.png

# Commit and push
git commit -m "Add Pancake Breakfast meal images"
git push origin breakfast
```

### 1.4 Verify Upload

Visit `https://github.com/muraza6/meal-images` and check that your branch contains the images.

## Step 2: Check Your Uploaded Images

### 2.1 Clone and Check Branches

```bash
cd ~
git clone https://github.com/muraza6/meal-images.git
cd meal-images
git branch -a
```

### 2.2 List Files in Each Branch

```bash
# Check breakfast meals
git ls-tree -r origin/breakfast --name-only

# Check non-veg meals
git ls-tree -r origin/Nonveg_meals --name-only

# Check vegan meals
git ls-tree -r origin/Vegan_meals --name-only
```

### 2.3 Note Down Your Meal Details

For each meal, note:
- **Meal name** (e.g., "Crepes")
- **Branch name** (e.g., "breakfast")
- **Card image filename** (e.g., "Crepes_card.png")
- **Detail image filename** (e.g., "Crepes_detail.png")
- **Meal type** (BREAKFAST, LUNCH, DINNER, SNACK)
- **Nutrition info** (calories, protein, carbs, fats)

## Step 3: Add Helper Methods (If Needed)

If you're adding a new meal category, add a helper method to `GitHubImageManager.kt`:

### 3.1 Open GitHubImageManager.kt

```
app/src/main/java/com/bodycalc/platepilot/utils/GitHubImageManager.kt
```

### 3.2 Add Category Method

```kotlin
/**
 * Convenience method for [category] meals
 */
fun get[Category]ImageUrl(imagePath: String): String {
    return getImageUrl("[Branch_Name]", imagePath)
}
```

**Example for Lunch meals:**
```kotlin
fun getLunchImageUrl(imagePath: String): String {
    return getImageUrl("Lunch_meals", imagePath)
}
```

## Step 4: Add Meals to MealRepository.kt

### 4.1 Open MealRepository.kt

```
app/src/main/java/com/bodycalc/platepilot/data/repository/MealRepository.kt
```

### 4.2 Find the initializeSampleData() Function

Look for `val sampleMeals = listOf(` around line 66.

### 4.3 Add Your New Meal

Add your meal before the closing `)` of the list:

```kotlin
Meal(
    name = "Your Meal Name",
    type = MealType.BREAKFAST,  // or LUNCH, DINNER, SNACK
    description = "Description of your meal",
    ingredients = "[\"Ingredient 1\", \"Ingredient 2\", \"Ingredient 3\"]",
    recipe = "1. First step\n2. Second step\n3. Third step\n4. Final step",
    calories = 380,  // Total calories
    protein = 12,    // Protein in grams
    carbs = 48,      // Carbs in grams
    fats = 16,       // Fats in grams
    imageUrl = GitHubImageManager.getBreakfastImageUrl("Your_Meal_card.png"),
    detailImageUrl = GitHubImageManager.getBreakfastImageUrl("Your_Meal_detail.png"),
    isCloudImage = true
),
```

### 4.4 Example: Adding Crepes

```kotlin
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
    isCloudImage = true
),
```

## Step 5: Update Meal Plan Distribution

### 5.1 Find ensureSevenDayMealPlans() Function

Look for the function around line 357 in `MealRepository.kt`.

### 5.2 Count Your New Meal IDs

Meals are inserted in order, so count from the beginning:
- Meal 1 = First meal in the list
- Meal 2 = Second meal in the list
- Your new meal = Position in the list

### 5.3 Update the Meal Lists

Add your new meal IDs to the appropriate lists:

```kotlin
// All available breakfast meals
val breakfasts = listOf(1L, 5L, 6L, 7L, 8L, 14L, 15L)  // Add your breakfast IDs

// All available lunch meals
val lunches = listOf(2L, 9L, 10L, 16L, 13L, 17L, 2L)   // Add your lunch IDs

// All available dinner meals
val dinners = listOf(3L, 12L, 11L, 19L, 18L, 11L, 19L) // Add your dinner IDs
```

### 5.4 Important Notes

- Each list should have exactly **7 entries** (one for each day)
- Use meal IDs that match the meal type (breakfast for breakfasts, etc.)
- Avoid duplicating the same meal on consecutive days

## Step 6: Handle Special Characters in Filenames

If your filename has special characters (like ö, é, ñ), you need to use Unicode encoding:

### 6.1 Check the Actual Filename

```bash
cd ~/meal-images
git ls-tree -r origin/[branch_name] --name-only
```

### 6.2 Use Unicode Encoding

If you see something like `"Do\314\210ner_Kebab_card.png"`, use:

```kotlin
imageUrl = GitHubImageManager.getNonVegImageUrl("Do\u0308ner_Kebab_card.png"),
```

Common Unicode replacements:
- `ö` = `o\u0308`
- `é` = `e\u0301`
- `ñ` = `n\u0303`

## Step 7: Build and Test

### 7.1 Clean and Build

```bash
cd /Users/musharrafraza/AndroidStudioProjects/PlatePilot
./gradlew clean
./gradlew assembleDebug -x lint
```

### 7.2 Uninstall Old App

```bash
adb uninstall com.bodycalc.platepilot
```

### 7.3 Install New App

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 7.4 Verify in App

1. Open the PlatePilot app
2. Go to **Home** screen - check if meals show correctly
3. Go to **Plans** screen - verify all 7 days have meals
4. Click on your new meal - verify the image loads
5. Check **Day 6** and **Day 7** specifically

## Step 8: Troubleshooting

### Images Not Loading

**Problem**: Meal images show placeholder/broken image

**Solutions**:
1. Check filename exactly matches GitHub (case-sensitive)
2. Verify branch name is correct
3. Check for special characters and use Unicode encoding
4. Ensure images are publicly accessible on GitHub

**Test URL**:
```
https://raw.githubusercontent.com/muraza6/meal-images/[branch]/[filename]
```

### Wrong Meal Type Showing

**Problem**: Breakfast showing in lunch slot, etc.

**Solutions**:
1. Verify `type = MealType.[CORRECT_TYPE]` in meal definition
2. Check meal ID is in the correct list (breakfasts, lunches, dinners)
3. Ensure the meal ID corresponds to the correct position in `sampleMeals`

### Duplicate Meals on Same Day

**Problem**: Same meal appears twice in one day

**Solutions**:
1. Check meal ID lists (breakfasts, lunches, dinners)
2. Ensure you're not using a breakfast ID in the lunch list
3. Verify all IDs in each list match the meal type

### Day 6 or Day 7 Issues

**Problem**: Missing meals or wrong meals on Day 6/7

**Solutions**:
1. Verify meal lists have exactly 7 entries each
2. Check indices 5 and 6 in each list (Day 6 and Day 7)
3. Make sure meal IDs exist and match the type

## Step 9: Complete Example

Here's a complete example of adding "Butter Chicken":

### 9.1 Upload to GitHub

```bash
cd ~/meal-images
git checkout -b Nonveg_meals
# Add butter_chicken_card.png and butter_chicken_detail.png
git add butter_chicken_card.png butter_chicken_detail.png
git commit -m "Add Butter Chicken"
git push origin Nonveg_meals
```

### 9.2 Add to MealRepository.kt

```kotlin
// In sampleMeals list (position 20 = ID 20)
Meal(
    name = "Butter Chicken",
    type = MealType.DINNER,
    description = "Creamy and rich Indian butter chicken curry",
    ingredients = "[\"Chicken\", \"Butter\", \"Cream\", \"Tomatoes\", \"Garam masala\", \"Ginger\", \"Garlic\"]",
    recipe = "1. Marinate chicken in yogurt and spices\n2. Cook tomatoes into paste\n3. Add butter and cream\n4. Simmer chicken in sauce",
    calories = 520,
    protein = 35,
    carbs = 15,
    fats = 38,
    imageUrl = GitHubImageManager.getNonVegImageUrl("butter_chicken_card.png"),
    detailImageUrl = GitHubImageManager.getNonVegImageUrl("butter_chicken_detail.png"),
    isCloudImage = true
),
```

### 9.3 Update Dinner List

```kotlin
// In ensureSevenDayMealPlans()
val dinners = listOf(3L, 12L, 11L, 20L, 18L, 11L, 19L)  // Added 20 for Day 4
```

### 9.4 Build and Install

```bash
./gradlew assembleDebug -x lint
adb uninstall com.bodycalc.platepilot
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Step 10: Quick Reference

### Meal Types
- `MealType.BREAKFAST` - Breakfast meals
- `MealType.LUNCH` - Lunch meals
- `MealType.DINNER` - Dinner meals
- `MealType.SNACK` - Snack meals

### GitHub Image Helper Methods
- `GitHubImageManager.getBreakfastImageUrl("filename.png")`
- `GitHubImageManager.getNonVegImageUrl("filename.png")`
- `GitHubImageManager.getVeganImageUrl("filename.png")`

### Build Commands
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug -x lint

# Uninstall and install
adb uninstall com.bodycalc.platepilot
adb install app/build/outputs/apk/debug/app-debug.apk

# One-liner
./gradlew assembleDebug -x lint && adb uninstall com.bodycalc.platepilot && sleep 1 && adb install app/build/outputs/apk/debug/app-debug.apk
```

## Summary Checklist

- [ ] Upload card and detail images to GitHub
- [ ] Note meal name, branch, filenames, and nutrition info
- [ ] Add helper method to GitHubImageManager.kt (if new category)
- [ ] Add meal to sampleMeals list in MealRepository.kt
- [ ] Note the meal ID (position in list)
- [ ] Update meal plan distribution lists (breakfasts, lunches, dinners)
- [ ] Handle special characters with Unicode encoding if needed
- [ ] Clean and build the app
- [ ] Uninstall old app from device
- [ ] Install new APK
- [ ] Test all 7 days in the app
- [ ] Verify images load correctly
- [ ] Check meal details screen

## Tips

1. **Add meals in batches** - Add 3-5 meals at once for efficiency
2. **Test incrementally** - Test after adding each batch
3. **Use consistent naming** - Keep filenames simple (no spaces, use underscores)
4. **Get accurate nutrition data** - Use USDA or MyFitnessPal
5. **Vary meal types** - Balance breakfast, lunch, dinner across 7 days
6. **Check GitHub first** - Always verify images uploaded successfully
7. **Document your meals** - Keep notes on which ID is which meal

---

**Need Help?**
- Check Android Studio Logcat for errors
- Verify GitHub URLs are accessible
- Test with simple meals first
- Make sure device has internet connection for cloud images
