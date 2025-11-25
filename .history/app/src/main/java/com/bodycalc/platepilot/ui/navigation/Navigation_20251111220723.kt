package com.bodycalc.platepilot.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bodycalc.platepilot.data.model.Meal
import com.bodycalc.platepilot.ui.screens.HomeScreen
import com.bodycalc.platepilot.ui.screens.MealDetailScreen
import com.bodycalc.platepilot.ui.screens.PlansScreen
import com.bodycalc.platepilot.ui.screens.ProfileScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Plans : Screen("plans")
    object Profile : Screen("profile")
    object MealDetail : Screen("meal_detail")
}

@Composable
fun HorizontalPagerContent(
    page: Int,
    currentPage: Int
) {
    var selectedMeal by remember { mutableStateOf<Meal?>(null) }
    val navController = rememberNavController()
    
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
                        currentPage = currentPage
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
            }
        }
        1 -> PlansScreen()
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
