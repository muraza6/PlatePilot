# PlatePilot - Meal Planning Android App

A modern Android meal planning application built with Jetpack Compose and following clean architecture principles.

## Features

### 🏠 Home Screen
- Display today's meal plan with four meal cards
- Beautiful pastel-themed cards (pink, green, blue)
- Swipeable carousel for different days
- Click on any meal to view detailed information

### 📅 Plans Screen
- Weekly meal planner with calendar view
- Browse available meals by type (Breakfast, Lunch, Dinner)
- Assign meals to specific days
- Customize your weekly meal plan

### 👤 Profile Screen
- Set your name and personal goals
- Configure daily calorie and protein targets
- Select fitness goals (Weight Loss, Maintenance, Muscle Gain)
- Choose dietary preferences (Vegetarian, Vegan, Keto, etc.)
- Save your preferences locally

### 🍽️ Meal Detail Screen
- View full meal information
- Nutrition breakdown (Calories, Protein, Carbs, Fats)
- Detailed ingredient list
- Step-by-step recipe instructions

## Tech Stack

### Architecture
- **Clean Architecture**: Separation of concerns with data, domain, and UI layers
- **MVVM Pattern**: ViewModel for business logic, UI observes state

### Libraries & Frameworks
- **Jetpack Compose**: Modern declarative UI framework
- **Room Database**: Local data persistence
- **Navigation Component**: Type-safe navigation
- **Coroutines & Flow**: Asynchronous programming and reactive data
- **ViewModel & LiveData**: Lifecycle-aware data management
- **Coil**: Image loading library
- **Material 3**: Latest Material Design components

### Project Structure
```
app/
├── data/
│   ├── local/
│   │   ├── PlatePilotDatabase.kt
│   │   ├── MealDao.kt
│   │   ├── MealPlanDao.kt
│   │   ├── UserProfileDao.kt
│   │   └── Converters.kt
│   ├── model/
│   │   ├── Meal.kt
│   │   ├── MealPlan.kt
│   │   └── UserProfile.kt
│   └── repository/
│       └── MealRepository.kt
├── ui/
│   ├── components/
│   │   ├── MealCard.kt
│   │   └── BottomNavigationBar.kt
│   ├── screens/
│   │   ├── HomeScreen.kt
│   │   ├── PlansScreen.kt
│   │   ├── ProfileScreen.kt
│   │   └── MealDetailScreen.kt
│   ├── viewmodel/
│   │   ├── HomeViewModel.kt
│   │   ├── PlansViewModel.kt
│   │   └── ProfileViewModel.kt
│   ├── navigation/
│   │   └── Navigation.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt
```

## Design Features

### Color Scheme
- **Pastel Pink** (#FFC1CC): Breakfast and some lunch cards
- **Pastel Green** (#B5EAD7): Lunch and plans section
- **Pastel Blue** (#B8D4E8): Dinner cards
- **Light Background** (#F5F5F5): App background
- **Card Background** (#FFFFFF): Card containers

### UI Components
- Rounded corner cards (20dp radius)
- Soft drop shadows (4dp elevation)
- Circular bottom navigation icons
- Smooth page indicators
- Material 3 components throughout

## Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 11 or higher
- Android SDK 24+ (minimum)
- Android SDK 35 (target)

### Installation

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the app on an emulator or physical device

### Build & Run

```bash
# Build the project
./gradlew build

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

## Sample Data

The app comes pre-populated with sample meals:
- **Breakfast**: Oatmeal with Berries (350 cal)
- **Lunch**: Grilled Chicken Salad (420 cal)
- **Dinner**: Quinoa Stir-Fry (485 cal)

## Database Schema

### Meals Table
- id, name, type, imageUrl, description
- ingredients, recipe
- calories, protein, carbs, fats

### Meal Plans Table
- id, date, breakfastId, lunchId, lunch2Id, dinnerId, snackIds

### User Profile Table
- id, name, dietaryPreferences, allergies
- dailyCalorieGoal, proteinGoal, carbsGoal, fatsGoal
- fitnessGoal

## Future Enhancements

- [ ] Cloud sync for meal plans
- [ ] Barcode scanner for ingredients
- [ ] Recipe sharing with friends
- [ ] Shopping list generation
- [ ] Meal prep timer
- [ ] Weekly nutrition analytics
- [ ] Custom meal creation
- [ ] Integration with fitness trackers

## License

This project is created for educational purposes.

## Contact

For questions or feedback, please open an issue on GitHub.

---

Built with ❤️ using Jetpack Compose
