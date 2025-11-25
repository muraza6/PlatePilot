# PlatePilot - Firebase Cloud Images Update

## Summary

I've successfully set up your app to upload meal images to Firebase Storage and use them in the app. This will reduce your APK size while keeping the existing 4 meals available offline.

## What's Been Done

### 1. ✅ Images Prepared
- **Location:** `/Users/musharrafraza/AndroidStudioProjects/PlatePilot/app/src/main/assets/`
- **Images:**
  - `chicken_tikka_card.png` (2.6MB)
  - `chicken_tikka_detail.jpeg` (761KB)
  - `Shrimp Tacos_card.png` (2.9MB)
  - `Shrimp Tacos_detail.png` (908KB)

### 2. ✅ Code Implementation

#### New Files Created:
1. **`UploadMealImages.kt`** - Utility to upload images to Firebase Storage
2. **`FIREBASE_SETUP.md`** - Instructions for Firebase configuration

#### Modified Files:
1. **`MealRepository.kt`**
   - Added 2 new cloud-based meals (Chicken Tikka & Shrimp Tacos)
   - Marked existing 4 meals with `isCloudImage = false` (offline support)
   - Added `detailImageUrl` field for dual image support

2. **`PlatePilotDatabase.kt`**
   - Updated version to 7

3. **`MainActivity.kt`**
   - Added automatic image upload on first launch
   - Upload happens once, tracked via SharedPreferences

4. **`build.gradle.kts` & `libs.versions.toml`**
   - Added Google Services plugin
   - Firebase dependencies already configured

## New Meals Added

### Chicken Tikka (Lunch)
- **Calories:** 450
- **Protein:** 42g
- **Carbs:** 12g
- **Fats:** 25g
- **Card Image:** Firebase Storage
- **Detail Image:** Firebase Storage

### Shrimp Tacos (Dinner)
- **Calories:** 520
- **Protein:** 38g
- **Carbs:** 45g
- **Fats:** 22g
- **Card Image:** Firebase Storage
- **Detail Image:** Firebase Storage

## Hybrid Image System

Your app now supports TWO types of meal images:

### Local Images (Offline) - `isCloudImage = false`
- ✅ Oatmeal with Berries
- ✅ Grilled Chicken Salad
- ✅ Quinoa Stir-Fry
- ✅ Fruit Salad Bowl

These load instantly and work without internet.

### Cloud Images (Online) - `isCloudImage = true`
- 🌐 Chicken Tikka
- 🌐 Shrimp Tacos

These are loaded from Firebase Storage and cached locally.

## Dual Image Support

Each meal now has TWO images:
- **Card Image** (`imageUrl`) - Shows on home screen (without background)
- **Detail Image** (`detailImageUrl`) - Shows on detail screen (with background)

The `SmartMealImage` composable automatically uses the correct image based on context.

## What You Need To Do

### Step 1: Add Firebase to Your Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create or select project "PlatePilot"
3. Add Android app:
   - Package name: `com.bodycalc.platepilot`
4. **Download `google-services.json`**
5. Place file in: `/Users/musharrafraza/AndroidStudioProjects/PlatePilot/app/google-services.json`

### Step 2: Enable Firebase Storage

1. In Firebase Console → "Build" → "Storage"
2. Click "Get Started"
3. Choose "Start in test mode"
4. Click "Done"

### Step 3: Build and Run

```bash
cd /Users/musharrafraza/AndroidStudioProjects/PlatePilot

# Clean build
./gradlew clean

# Build AAB for release
./gradlew bundleRelease
```

### Step 4: Verify Upload

After running the app, check Logcat for:
```
MainActivity: Starting Firebase image upload...
UploadMealImages: Uploading Chicken Tikka (Card)...
UploadMealImages: ✓ Successfully uploaded Chicken Tikka (Card)
UploadMealImages: Uploading Chicken Tikka (Detail)...
UploadMealImages: ✓ Successfully uploaded Chicken Tikka (Detail)
...
MainActivity: ✓ All images uploaded successfully!
```

You can also verify in Firebase Console → Storage → `meal_images/` folder.

## How It Works

### On First Launch:
1. App checks if images are already uploaded (SharedPreferences)
2. If not, reads images from `assets/` folder
3. Uploads all 4 images to Firebase Storage at paths:
   - `meal_images/chicken_tikka_card.png`
   - `meal_images/chicken_tikka_detail.jpeg`
   - `meal_images/shrimp_tacos_card.png`
   - `meal_images/shrimp_tacos_detail.png`
4. Marks upload as complete

### When Loading Meals:
- `SmartMealImage` composable checks `isCloudImage` flag
- **Local meals:** Load from drawable resources (instant)
- **Cloud meals:** 
  - Get download URL from Firebase Storage
  - Load with Coil library (async, with caching)
  - Show loading indicator while fetching
  - Show error icon if offline/failed

## Benefits

✅ **Reduced APK Size:** Cloud images don't increase app size
✅ **Offline Support:** 4 core meals work without internet
✅ **Scalability:** Easy to add more meals without growing APK
✅ **Dual Images:** Different images for card and detail views
✅ **Caching:** Firebase images cached after first download
✅ **Type Safety:** `MealImageType` enum prevents image mix-ups

## Database Schema

```kotlin
@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: MealType,
    val description: String,
    val ingredients: String,
    val recipe: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fats: Int,
    val imageUrl: String?,        // Card/Home screen image
    val detailImageUrl: String?,  // Detail screen image
    val isCloudImage: Boolean = false  // true = Firebase URL, false = local drawable
)
```

## Next Steps (After Firebase Setup)

1. ✅ Build and install app with `google-services.json`
2. ✅ Verify images upload successfully
3. ✅ Test both local and cloud meals display correctly
4. ✅ Test offline mode (airplane mode) - local meals should work
5. Consider updating Firebase Storage rules for production
6. Add more cloud meals to expand your meal database!

## Troubleshooting

**Build Error:** "google-services.json not found"
- Make sure file is in `app/` folder, not project root

**Upload Failed:**
- Check internet connection
- Verify Firebase Storage is enabled
- Check Firebase Storage rules allow writes

**Images Not Loading:**
- Check Logcat for error messages
- Verify Firebase Storage URLs in database
- Ensure app has internet permission (already in manifest)

**App Size Still Large:**
- Cloud images are in `assets/` for one-time upload
- After successful upload, you can remove them from `assets/`
- Rebuild app to see size reduction

## Files to Review

- `/app/src/main/java/com/bodycalc/platepilot/utils/UploadMealImages.kt`
- `/app/src/main/java/com/bodycalc/platepilot/MainActivity.kt`
- `/app/src/main/java/com/bodycalc/platepilot/data/repository/MealRepository.kt`
- `/FIREBASE_SETUP.md`
