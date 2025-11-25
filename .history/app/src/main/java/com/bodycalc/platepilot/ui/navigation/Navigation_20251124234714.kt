package com.bodycalc.platepilot.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bodycalc.platepilot.data.model.Meal
import com.bodycalc.platepilot.ui.screens.HomeScreen
import com.bodycalc.platepilot.ui.screens.MealDetailScreen
import com.bodycalc.platepilot.ui.screens.PlansScreen
import com.bodycalc.platepilot.ui.screens.ProfileScreen
import com.bodycalc.platepilot.ui.screens.SettingsScreen
import com.bodycalc.platepilot.ui.screens.WaterTrackingScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Plans : Screen("plans")
    object Profile : Screen("profile")
    object MealDetail : Screen("meal_detail")
    object Settings : Screen("settings")
}

@Composable
fun HorizontalPagerContent(
    page: Int,
    currentPage: Int,
    adMobManager: com.bodycalc.platepilot.ads.AdMobManager? = null
) {
    var selectedMeal by remember { mutableStateOf<Meal?>(null) }
    var selectedMealId by remember { mutableStateOf<Long?>(null) }
    
    // ✅ FIX: Use remember with key to prevent NavController recreation
    val navController = remember(page) { androidx.navigation.compose.rememberNavController() }
    
    // Create shared ViewModels at this level so they persist across navigation
    val settingsViewModel: com.bodycalc.platepilot.ui.viewmodel.SettingsViewModel = 
        androidx.lifecycle.viewmodel.compose.viewModel()
    val homeViewModel: com.bodycalc.platepilot.ui.viewmodel.HomeViewModel = 
        androidx.lifecycle.viewmodel.compose.viewModel()
    
    // ✅ FIX: Add logging to track navigation state
    LaunchedEffect(page, currentPage) {
        android.util.Log.d("Navigation", "HorizontalPagerContent: page=$page, currentPage=$currentPage")
    }
    
    when (page) {
        0 -> {
            // ✅ FIX: Add key to NavHost to prevent recreation issues
            NavHost(
                navController = navController,
                startDestination = "home_main",
                modifier = androidx.compose.ui.Modifier.fillMaxSize()
            ) {
                composable("home_main") {
                    android.util.Log.d("Navigation", "Composing home_main")
                    HomeScreen(
                        onMealClick = { meal ->
                            selectedMeal = meal
                            navController.navigate("meal_detail")
                        },
                        onSettingsClick = {
                            android.util.Log.d("Navigation", "Navigating to settings")
                            navController.navigate("settings")
                        },
                        viewModel = homeViewModel,
                        settingsViewModel = settingsViewModel,
                        currentPage = currentPage,
                        adMobManager = adMobManager
                    )
                }
                composable("meal_detail") {
                    android.util.Log.d("Navigation", "Composing meal_detail")
                    selectedMeal?.let { meal ->
                        MealDetailScreen(
                            meal = meal,
                            onBackClick = {
                                selectedMeal = null
                                android.util.Log.d("Navigation", "Popping from meal_detail")
                                navController.popBackStack()
                            }
                        )
                    }
                }
                composable("settings") {
                    android.util.Log.d("Navigation", "Composing settings")
                    SettingsScreen(
                        onBackClick = {
                            android.util.Log.d("Navigation", "Back from settings - current route: ${navController.currentBackStackEntry?.destination?.route}")
                            
                            // ✅ FIX: Multiple fallback strategies
                            try {
                                if (navController.popBackStack()) {
                                    android.util.Log.d("Navigation", "Successfully popped back stack")
                                } else {
                                    android.util.Log.e("Navigation", "PopBackStack returned false - manually navigating")
                                    navController.navigate("home_main") {
                                        popUpTo("home_main") { inclusive = true }
                                    }
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("Navigation", "Error during navigation", e)
                            }
                        },
                        viewModel = settingsViewModel
                    )
                }
            }
        }
        1 -> {
            NavHost(
                navController = navController,
                startDestination = "plans_main"
            ) {
                composable("plans_main") {
                    PlansScreen(
                        onMealClick = { mealId ->
                            selectedMealId = mealId
                            navController.navigate("plans_meal_detail")
                        }
                    )
                }
                composable("plans_meal_detail") {
                    selectedMealId?.let { mealId ->
                        val context = androidx.compose.ui.platform.LocalContext.current
                        val database = com.bodycalc.platepilot.data.local.PlatePilotDatabase.getDatabase(context)
                        val repository = com.bodycalc.platepilot.data.repository.MealRepository(
                            database.mealDao(),
                            database.mealPlanDao(),
                            database.userProfileDao()
                        )
                        
                        var meal by remember { mutableStateOf<Meal?>(null) }
                        
                        LaunchedEffect(mealId) {
                            meal = repository.getMealById(mealId)
                        }
                        
                        meal?.let {
                            MealDetailScreen(
                                meal = it,
                                onBackClick = {
                                    selectedMealId = null
                                    navController.popBackStack()
                                }
                            )
                        } ?: run {
                            Box(
                                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                androidx.compose.material3.CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
        2 -> {
            WaterTrackingScreen(
                adMobManager = adMobManager
            )
        }
        3 -> ProfileScreen(
            homeViewModel = homeViewModel
        )
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    var selectedMeal by remember { mutableStateOf<Meal?>(null) }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onMealClick = { meal ->
                    selectedMeal = meal
                    navController.navigate(Screen.MealDetail.route)
                }
            )
        }
        
        composable(Screen.Plans.route) {
            PlansScreen()
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
        
        composable(Screen.MealDetail.route) {
            selectedMeal?.let { meal ->
                MealDetailScreen(
                    meal = meal,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}