# 🚀 Quick Start Guide - PlatePilot

## Running the App

### Option 1: Using Android Studio (Recommended)

1. **Open the project**
   ```bash
   # Open Android Studio and select:
   # File → Open → Select the PlatePilot folder
   ```

2. **Sync Gradle files**
   - Android Studio will automatically prompt to sync
   - Or click: File → Sync Project with Gradle Files

3. **Select a device**
   - Use a physical Android device (USB debugging enabled)
   - Or create an emulator: Tools → Device Manager → Create Device

4. **Run the app**
   - Click the green "Run" button (▶️)
   - Or press: Shift + F10 (Windows/Linux) or Control + R (Mac)

### Option 2: Using Command Line

```bash
# Navigate to project directory
cd /Users/musharrafraza/AndroidStudioProjects/PlatePilot

# Build the app
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Or combine both steps
./gradlew installDebug
```

## First Launch Experience

When you first launch the app, you'll see:

1. **Home Screen** (Today's Meals)
   - Sample meal plan already loaded
   - 4 meal cards in beautiful pastel colors
   - Tap any card to see details

2. **Navigation**
   - Bottom bar with 3 tabs: Home, Plans, Profile
   - Tap Plans to customize your weekly meals
   - Tap Profile to set your goals

## App Navigation Guide

### 🏠 Home Tab
- **View**: Today's meal plan at a glance
- **Action**: Tap any meal card for full details
- **Features**: 
  - Swipe indicator dots (carousel ready)
  - Back arrow and notification icon in top bar

### 📅 Plans Tab
- **View**: Weekly calendar and meal library
- **Action**: 
  1. Select a day from the week view
  2. Choose a meal type section
  3. Tap a meal to assign it to that day
- **Features**:
  - Browse all available meals
  - Organize by meal type
  - See nutrition info

### 👤 Profile Tab
- **View**: Your personal settings
- **Actions**:
  1. Enter your name
  2. Set calorie goal (e.g., 2000)
  3. Set protein goal (e.g., 150g)
  4. Select fitness goal (Weight Loss/Maintenance/Muscle Gain)
  5. Choose dietary preferences
  6. Tap "Save Profile" button
- **Features**:
  - All data saved locally
  - Persists across app restarts

### 🍽️ Meal Detail Screen
- **View**: Complete meal information
- **Access**: Tap any meal card from Home
- **Features**:
  - Full nutrition breakdown
  - Ingredient list
  - Recipe instructions
  - Back button to return

## Sample Data Included

The app comes with 3 meals pre-configured:

### Breakfast: Oatmeal with Berries
- 350 calories
- 12g protein, 58g carbs, 8g fats
- Ingredients: Oats, Milk, Berries, Honey

### Lunch: Grilled Chicken Salad
- 420 calories
- 45g protein, 22g carbs, 18g fats
- Ingredients: Chicken, Greens, Tomatoes, Avocado

### Dinner: Quinoa Stir-Fry
- 485 calories
- 32g protein, 52g carbs, 15g fats
- Ingredients: Quinoa, Shrimp, Vegetables, Eggs

## Troubleshooting

### Build Issues

**Problem**: Gradle sync fails
```bash
# Solution: Clean and rebuild
./gradlew clean build
```

**Problem**: KSP errors
```bash
# Solution: Invalidate caches in Android Studio
# File → Invalidate Caches → Invalidate and Restart
```

### Runtime Issues

**Problem**: App crashes on launch
- Check Android version (minimum API 24 / Android 7.0)
- Clear app data: Settings → Apps → PlatePilot → Clear Data

**Problem**: Database errors
- Uninstall and reinstall the app
- Sample data will be reinitialized

### Device Requirements

- **Minimum**: Android 7.0 (API 24)
- **Target**: Android 15 (API 35)
- **RAM**: 2GB minimum
- **Storage**: 100MB free space

## Development Mode

### Enable Debug Mode
```bash
# Run with debugging
./gradlew installDebug

# View logs
adb logcat | grep PlatePilot
```

### Database Inspection
```bash
# Pull database from device
adb pull /data/data/com.bodycalc.platepilot/databases/plate_pilot_database
```

## Testing the App

### Manual Testing Checklist

- [ ] Launch app - Home screen loads
- [ ] Tap breakfast card - Detail screen opens
- [ ] Navigate to Plans tab
- [ ] Select a different day
- [ ] Assign a meal to that day
- [ ] Navigate to Profile tab
- [ ] Update calorie goal
- [ ] Save profile
- [ ] Return to Home tab
- [ ] All data persists

### Verify Features

1. **Bottom Navigation**
   - Tap each tab
   - Icons change color
   - Screens switch smoothly

2. **Meal Cards**
   - All 4 cards visible
   - Correct colors (Pink, Green, Blue)
   - Rounded corners and shadows

3. **Meal Details**
   - Nutrition grid displays
   - Ingredients list shows
   - Recipe instructions readable

4. **Profile Saving**
   - Enter data
   - Save
   - Close app
   - Reopen - data persists

## Performance

Expected performance on mid-range device:
- **App launch**: < 2 seconds
- **Screen transitions**: Instant
- **Database queries**: < 100ms
- **APK size**: ~15MB

## Next Steps

After testing the basic functionality:

1. **Customize Meals**
   - Add your own meals (future feature)
   - Upload meal images

2. **Plan Your Week**
   - Use Plans tab to organize
   - Set meals for each day

3. **Track Progress**
   - Monitor nutrition goals
   - Adjust as needed

## Support

For issues or questions:
1. Check DEVELOPMENT_SUMMARY.md
2. Review README.md
3. Examine error logs with `adb logcat`

---

**Happy Meal Planning! 🍽️**
