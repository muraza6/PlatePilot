package com.bodycalc.platepilot.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bodycalc.platepilot.data.model.Meal
import com.bodycalc.platepilot.ui.components.BannerAdView
import com.bodycalc.platepilot.ui.components.MealCard
import com.bodycalc.platepilot.ui.components.MealCardPlaceholder
import com.bodycalc.platepilot.ui.theme.PastelBlue
import com.bodycalc.platepilot.ui.theme.PastelGreen
import com.bodycalc.platepilot.ui.theme.PastelPink
import com.bodycalc.platepilot.ui.viewmodel.HomeViewModel
import com.bodycalc.platepilot.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onMealClick: (Meal) -> Unit,
    onSettingsClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    currentPage: Int = 0,
    adMobManager: com.bodycalc.platepilot.ads.AdMobManager? = null
) {
    val context = LocalContext.current
    
    // ✅ Create pager for 7 days
    val dayPagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 7 }
    )
    val coroutineScope = rememberCoroutineScope()
    
    val breakfast by viewModel.breakfast.collectAsState()
    val lunch by viewModel.lunch.collectAsState()
    val lunch2 by viewModel.lunch2.collectAsState()
    val dinner by viewModel.dinner.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // ✅ Update meals when day changes
    LaunchedEffect(dayPagerState.currentPage) {
        val today = java.time.LocalDate.now()
        val targetDate = today.plusDays(dayPagerState.currentPage.toLong())
        android.util.Log.d("HomeScreen", "Day changed to: ${dayPagerState.currentPage}, Loading date: $targetDate")
        viewModel.navigateToDate(targetDate)
    }
    
    // ✅ Calculate actual dates for each day
    val today = remember { java.time.LocalDate.now() }
    val dayLabels = remember(today) {
        (0..6).map { offset ->
            val date = today.plusDays(offset.toLong())
            when (offset) {
                0 -> "Today (${date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd"))})"
                1 -> "Tomorrow (${date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd"))})"
                else -> date.format(java.time.format.DateTimeFormatter.ofPattern("EEE, MMM dd"))
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = dayLabels[dayPagerState.currentPage],
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = { 
                    // Empty - no back button
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
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
        ) {
            // ✅ HorizontalPager for swiping between days
            HorizontalPager(
                state = dayPagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (isLoading || currentDate == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
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
                                        mealType = "Snack",
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
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // ✅ Day indicator dots - now represents days, not screens
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(7) { index ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    index == dayPagerState.currentPage -> when (index % 3) {
                                        0 -> PastelPink
                                        1 -> PastelGreen
                                        else -> PastelBlue
                                    }
                                    else -> Color.LightGray
                                }
                            )
                    )
                    if (index < 6) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}