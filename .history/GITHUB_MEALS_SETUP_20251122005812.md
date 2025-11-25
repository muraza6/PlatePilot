# GitHub Meals Setup Guide

This guide explains how to set up automatic meal syncing from your GitHub repository, so you can add new meals without rebuilding the app.

## Overview

The app now fetches meals from a `meals.json` file in your GitHub repository on every app startup. This means you can add, update, or remove meals by simply editing the JSON file and committing to GitHub.

## Setup Steps

### 1. Create `meals.json` in Your GitHub Repository

In your GitHub repository (e.g., `https://github.com/musharrafraza/PlatePilot`), create a file named `meals.json` in the root directory or in a specific folder.

### 2. Update the GitHub URL in MainActivity.kt

Open `app/src/main/java/com/bodycalc/platepilot/MainActivity.kt` and update the `GITHUB_MEALS_URL` constant with your actual GitHub raw content URL:

```kotlin
private const val GITHUB_MEALS_URL = "https://raw.githubusercontent.com/musharrafraza/PlatePilot/main/meals.json"
```

**Important:** Use the raw content URL format:
- Format: `https://raw.githubusercontent.com/<username>/<repo>/<branch>/<path-to-file>`
- Example: `https://raw.githubusercontent.com/musharrafraza/PlatePilot/main/meals.json`

### 3. meals.json File Structure

The `meals.json` file must be a JSON array of meal objects. Each meal object must have these fields:

```json
[
  {
    "id": 1,
    "name": "Oatmeal with Berries",
    "type": "Breakfast",
    "calories": 350,
    "protein": 10,
    "carbs": 60,
    "fats": 8,
    "imageUrl": "https://raw.githubusercontent.com/musharrafraza/PlatePilot/main/Recipe_image/oatmeal_berries.png",
    "dietaryCategory": "Vegetarian",
    "description": "Healthy oatmeal topped with fresh berries and honey"
  },
  {
    "id": 2,
    "name": "Greek Yogurt Parfait",
    "type": "Breakfast",
    "calories": 300,
    "protein": 20,
    "carbs": 35,
    "fats": 8,
    "imageUrl": "https://raw.githubusercontent.com/musharrafraza/PlatePilot/main/Recipe_image/greek_yogurt_parfait.png",
    "dietaryCategory": "Vegetarian",
    "description": "Creamy Greek yogurt layered with granola and fruits"
  }
]
```

### 4. Field Descriptions

| Field | Type | Required | Valid Values | Description |
|-------|------|----------|--------------|-------------|
| `id` | Integer | Yes | Any unique number | Unique identifier for the meal |
| `name` | String | Yes | Any text | Display name of the meal |
| `type` | String | Yes | "Breakfast", "Lunch", "Dinner", "Snack" | Meal category |
| `calories` | Integer | Yes | Any positive number | Total calories |
| `protein` | Integer | Yes | Any positive number | Protein in grams |
| `carbs` | Integer | Yes | Any positive number | Carbohydrates in grams |
| `fats` | Integer | Yes | Any positive number | Fats in grams |
| `imageUrl` | String | Yes | Any valid URL | URL to meal image (use GitHub raw content URL) |
| `dietaryCategory` | String | Yes | "Vegetarian", "Vegan", "Keto", "Non-Veg" | Dietary classification |
| `description` | String | No | Any text | Description or preparation notes (defaults to empty string) |

### 5. Image URLs

For images, use the GitHub raw content URL format:
```
https://raw.githubusercontent.com/<username>/<repo>/<branch>/<path-to-image>
```

Example:
```
https://raw.githubusercontent.com/musharrafraza/PlatePilot/main/Recipe_image/oatmeal_berries.png
```

### 6. Dietary Categories Explained

- **"Vegetarian"**: No meat, but may include dairy and eggs
- **"Vegan"**: No animal products (no meat, dairy, eggs)
- **"Keto"**: Low-carb, high-fat meals
- **"Non-Veg"**: Contains meat, poultry, or seafood

**Filtering Logic:**
- When user selects "None" → Shows all meals
- When user selects "Vegetarian" → Shows Vegetarian + Vegan meals
- When user selects "Vegan" → Shows only Vegan meals
- When user selects "Keto" → Shows only Keto meals
- When user selects "Non-Veg" → Shows all Non-Veg meals

### 7. Example meals.json with Multiple Meals

```json
[
  {
    "id": 1,
    "name": "Oatmeal with Berries",
    "type": "Breakfast",
    "calories": 350,
    "protein": 10,
    "carbs": 60,
    "fats": 8,
    "imageUrl": "https://raw.githubusercontent.com/musharrafraza/PlatePilot/main/Recipe_image/oatmeal_berries.png",
    "dietaryCategory": "Vegetarian",
    "description": "Healthy oatmeal topped with fresh berries and honey"
  },
  {
    "id": 2,
    "name": "Grilled Chicken Salad",
    "type": "Lunch",
    "calories": 450,
    "protein": 35,
    "carbs": 20,
    "fats": 22,
    "imageUrl": "https://raw.githubusercontent.com/musharrafraza/PlatePilot/main/Recipe_image/chicken_salad.png",
    "dietaryCategory": "Non-Veg",
    "description": "Fresh salad with grilled chicken breast and vinaigrette"
  },
  {
    "id": 3,
    "name": "Quinoa Buddha Bowl",
    "type": "Lunch",
    "calories": 400,
    "protein": 15,
    "carbs": 55,
    "fats": 12,
    "imageUrl": "https://raw.githubusercontent.com/musharrafraza/PlatePilot/main/Recipe_image/quinoa_bowl.png",
    "dietaryCategory": "Vegan",
    "description": "Nutritious quinoa bowl with roasted vegetables and tahini"
  },
  {
    "id": 4,
    "name": "Salmon with Asparagus",
    "type": "Dinner",
    "calories": 520,
    "protein": 40,
    "carbs": 15,
    "fats": 32,
    "imageUrl": "https://raw.githubusercontent.com/musharrafraza/PlatePilot/main/Recipe_image/salmon_asparagus.png",
    "dietaryCategory": "Non-Veg",
    "description": "Baked salmon fillet with roasted asparagus"
  },
  {
    "id": 5,
    "name": "Keto Avocado Eggs",
    "type": "Breakfast",
    "calories": 380,
    "protein": 18,
    "carbs": 8,
    "fats": 30,
    "imageUrl": "https://raw.githubusercontent.com/musharrafraza/PlatePilot/main/Recipe_image/avocado_eggs.png",
    "dietaryCategory": "Keto",
    "description": "Baked avocado halves with eggs and cheese"
  },
  {
    "id": 6,
    "name": "Mixed Nuts",
    "type": "Snack",
    "calories": 200,
    "protein": 6,
    "carbs": 10,
    "fats": 16,
    "imageUrl": "https://raw.githubusercontent.com/musharrafraza/PlatePilot/main/Recipe_image/mixed_nuts.png",
    "dietaryCategory": "Vegan",
    "description": "Assorted raw nuts for a healthy snack"
  }
]
```

## How to Add a New Meal

1. Open `meals.json` in your GitHub repository
2. Add a new meal object to the array with a unique `id`
3. Ensure all required fields are filled correctly
4. Upload the meal image to your GitHub repository
5. Use the raw GitHub URL for the `imageUrl` field
6. Commit and push the changes to GitHub
7. **Restart the app** - meals will sync automatically on app startup

## How to Update a Meal

1. Find the meal in `meals.json` by its `id`
2. Update the fields you want to change
3. Commit and push the changes
4. Restart the app to see the updates

## How to Delete a Meal

1. Remove the meal object from the `meals.json` array
2. Commit and push the changes
3. Restart the app - the meal will be removed

## Troubleshooting

### Meals not updating?

1. **Check the GitHub URL**: Make sure `GITHUB_MEALS_URL` in `MainActivity.kt` points to the raw content URL
2. **Verify JSON syntax**: Use a JSON validator (https://jsonlint.com/) to check for syntax errors
3. **Check logs**: Open Android Studio Logcat and filter by "MainActivity" or "MealRepository" to see sync errors
4. **Restart the app**: Meals sync only on app startup
5. **Internet connection**: Ensure the device has internet access

### App shows old meals?

The app falls back to local database if GitHub sync fails. To force a sync:
1. Uninstall and reinstall the app
2. Or check Logcat for error messages about the sync

### Image not showing?

1. Ensure the image URL uses the raw GitHub content format
2. Check if the image exists in the repository
3. Verify the image file extension matches the URL

## Benefits

✅ **No app rebuilds** - Add meals by editing JSON only  
✅ **Instant updates** - Changes appear after app restart  
✅ **Centralized management** - All meals in one JSON file  
✅ **Version control** - Track meal changes via Git history  
✅ **Offline support** - App continues with cached meals if sync fails  
✅ **Easy collaboration** - Multiple people can contribute meals via pull requests

## Notes

- Meals sync **only on app startup**, not in real-time
- If GitHub is unavailable, the app uses the last synced meals from local database
- The first time you set this up, you need to rebuild the app once to include the Retrofit dependencies and updated code
- After that, all future meal additions only require updating `meals.json` in GitHub

## Example Repository Structure

```
PlatePilot/
├── meals.json                    # Your meals data file
├── Recipe_image/                 # Folder for meal images
│   ├── oatmeal_berries.png
│   ├── chicken_salad.png
│   ├── quinoa_bowl.png
│   └── salmon_asparagus.png
└── README.md
```
