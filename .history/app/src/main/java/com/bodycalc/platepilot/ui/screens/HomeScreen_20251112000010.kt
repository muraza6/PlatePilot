package com.bodycalc.platepilot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bodycalc.platepilot.data.model.Meal
import com.bodycalc.platepilot.data.model.MealType
import com.bodycalc.platepilot.ui.components.BannerAdView
import com.bodycalc.platepilot.ui.components.MealCard
import com.bodycalc.platepilot.ui.components.MealCardPlaceholder
import com.bodycalc.platepilot.ui.theme.PastelBlue
import com.bodycalc.platepilot.ui.theme.PastelGreen
import com.bodycalc.platepilot.ui.theme.PastelPink
import com.bodycalc.platepilot.ui.viewmodel.HomeViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMealClick: (Meal) -> Unit,
    onBackClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(),
    currentPage: Int = 0,
    adMobManager: com.bodycalc.platepilot.ads.AdMobManager? = null
) {
    val breakfast by viewModel.breakfast.collectAsState()
    val lunch by viewModel.lunch.collectAsState()
    val lunch2 by viewModel.lunch2.collectAsState()
    val dinner by viewModel.dinner.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Today's",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Meal cards grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    // Breakfast
                    item {
                        if (breakfast != null) {
                            MealCard(
                                meal = breakfast!!,
                                backgroundColor = PastelPink,
                                onClick = { onMealClick(breakfast!!) },
                                showIcons = false
                            )
                        } else {
                            MealCardPlaceholder(
                                mealType = "Breakfast",
                                backgroundColor = PastelPink
                            )
                        }
                    }
                    
                    // First Lunch
                    item {
                        if (lunch != null) {
                            MealCard(
                                meal = lunch!!,
                                backgroundColor = PastelGreen,
                                onClick = { onMealClick(lunch!!) },
                                showIcons = true
                            )
                        } else {
                            MealCardPlaceholder(
                                mealType = "Lunch",
                                backgroundColor = PastelGreen
                            )
                        }
                    }
                    
                    // Second Lunch (if exists)
                    item {
                        if (lunch2 != null) {
                            MealCard(
                                meal = lunch2!!,
                                backgroundColor = PastelPink,
                                onClick = { onMealClick(lunch2!!) },
                                showIcons = false
                            )
                        } else {
                            MealCardPlaceholder(
                                mealType = "Lunch",
                                backgroundColor = PastelPink
                            )
                        }
                    }
                    
                    // Dinner
                    item {
                        if (dinner != null) {
                            MealCard(
                                meal = dinner!!,
                                backgroundColor = PastelBlue,
                                onClick = { onMealClick(dinner!!) },
                                showIcons = true
                            )
                        } else {
                            MealCardPlaceholder(
                                mealType = "Dinner",
                                backgroundColor = PastelBlue
                            )
                        }
                    }
                }
                
                // Banner Ad
                if (adMobManager != null) {
                    BannerAdView(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Page indicator dots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 100.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        index == currentPage && index == 0 -> PastelPink
                                        index == currentPage && index == 1 -> PastelGreen
                                        index == currentPage && index == 2 -> PastelBlue
                                        else -> Color.LightGray
                                    }
                                )
                        )
                        if (index < 2) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        }
    }
}
