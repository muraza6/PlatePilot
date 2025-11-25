package com.bodycalc.platepilot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.bodycalc.platepilot.ui.theme.PastelBlue
import com.bodycalc.platepilot.ui.theme.PastelGreen
import com.bodycalc.platepilot.ui.theme.PastelPink
import com.bodycalc.platepilot.ui.viewmodel.PlansViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(
    viewModel: PlansViewModel = viewModel(),
    onMealClick: (Long) -> Unit = {}
) {
    val weekDates by viewModel.weekDates.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val mealsByType by viewModel.mealsByType.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meal Plans",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
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
                .padding(16.dp)
        ) {
            // Week selector
            Text(
                text = "Select a day to plan",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekDates.forEach { date ->
                    DaySelector(
                        date = date,
                        isSelected = date == selectedDate,
                        onClick = { viewModel.selectDate(date) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Meal type sections - Display only
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    MealTypeSection(
                        title = "Breakfast",
                        meals = mealsByType[MealType.BREAKFAST] ?: emptyList(),
                        backgroundColor = PastelPink,
                        onMealClick = onMealClick
                    )
                }
                
                item {
                    MealTypeSection(
                        title = "Lunch",
                        meals = mealsByType[MealType.LUNCH] ?: emptyList(),
                        backgroundColor = PastelGreen,
                        onMealClick = onMealClick
                    )
                }
                
                item {
                    MealTypeSection(
                        title = "Snack",
                        meals = mealsByType[MealType.SNACK] ?: emptyList(),
                        backgroundColor = PastelPink,
                        onMealClick = onMealClick
                    )
                }
                
                item {
                    MealTypeSection(
                        title = "Dinner",
                        meals = mealsByType[MealType.DINNER] ?: emptyList(),
                        backgroundColor = PastelBlue,
                        onMealClick = onMealClick
                    )
                }
            }
        }
    }
}

@Composable
fun DaySelector(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dayOfWeek = date.format(DateTimeFormatter.ofPattern("EEE"))
    val dayOfMonth = date.dayOfMonth
    
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PastelGreen else Color.White)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayOfWeek,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Color.White else Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = dayOfMonth.toString(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun MealTypeSection(
    title: String,
    meals: List<Meal>,
    backgroundColor: Color,
    onMealClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (meals.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = "No $title meal assigned",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            }
        } else {
            meals.forEach { meal ->
                MealOptionCard(
                    meal = meal,
                    backgroundColor = backgroundColor,
                    onClick = { onMealClick(meal.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MealOptionCard(
    meal: Meal,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${meal.calories} cal | ${meal.protein}g protein",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            Text(
                text = "🍽️",
                fontSize = 32.sp
            )
        }
    }
}
