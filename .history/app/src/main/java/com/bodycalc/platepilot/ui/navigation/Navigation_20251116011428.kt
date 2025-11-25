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
    val navController = rememberNavController()
    
    // Create shared ViewModels at this level so they persist across navigation
    val settingsViewModel: com.bodycalc.platepilot.ui.viewmodel.SettingsViewModel = 
        androidx.lifecycle.viewmodel.compose.viewModel()
    val homeViewModel: com.bodycalc.platepilot.ui.viewmodel.HomeViewModel = 
        androidx.lifecycle.viewmodel.compose.viewModel()
    
    when (page) {
        0 -> {
            NavHost(
                navController = navController,
                startDestination = "home_main"
            ) {
                composable("home_main") {
                    HomeScreen(
                        onMealClick = { meal ->
                            selectedMeal = meal
                            navController.navigate("meal_detail")
                        },
                        onBackClick = { },
                        onSettingsClick = {
                            navController.navigate("settings")
                        },
                        viewModel = homeViewModel,
                        settingsViewModel = settingsViewModel,
                        currentPage = currentPage,
                        adMobManager = adMobManager
                    )
                }
                composable("meal_detail") {
                    selectedMeal?.let { meal ->
                        MealDetailScreen(
                            meal = meal,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
                composable("settings") {
                    SettingsScreen(
                        onBackClick = {
                            navController.popBackStack()
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
                        // Create a repository to fetch the meal
                        val context = androidx.compose.ui.platform.LocalContext.current
                        val database = com.bodycalc.platepilot.data.local.PlatePilotDatabase.getDatabase(context)
                        val repository = com.bodycalc.platepilot.data.repository.MealRepository(
                            database.mealDao(),
                            database.mealPlanDao(),
                            database.userProfileDao()
                        )
                        
                        // Load the meal by ID
                        var meal by remember { mutableStateOf<Meal?>(null) }
                        
                        LaunchedEffect(mealId) {
                            meal = repository.getMealById(mealId)
                        }
                        
                        meal?.let {
                            MealDetailScreen(
                                meal = it,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        } ?: run {
                            // Show loading while meal is being fetched
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
        2 -> ProfileScreen()
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
                },
                onBackClick = { /* Optional: handle back navigation */ }
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
