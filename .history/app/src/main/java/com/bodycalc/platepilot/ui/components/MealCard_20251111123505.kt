package com.bodycalc.platepilot.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bodycalc.platepilot.data.model.Meal
import com.bodycalc.platepilot.data.model.MealType

@Composable
fun MealCard(
    meal: Meal,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showIcons: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Meal type and name
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${meal.type.name.lowercase().replaceFirstChar { it.uppercase() }}:",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = meal.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp
                        ),
                        color = Color(0xFF2A2A2A)
                    )
                }
                
                // Meal image container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (meal.imageUrl != null) {
                        AsyncImage(
                            model = meal.imageUrl,
                            contentDescription = meal.name,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Circular white background with shadow effect
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (meal.type) {
                                    MealType.BREAKFAST -> "🥣"
                                    MealType.LUNCH -> "🥗"
                                    MealType.DINNER -> "🍲"
                                    else -> "🍽️"
                                },
                                fontSize = 56.sp
                            )
                        }
                    }
                }
            }
            
            // Decorative food icons for lunch/dinner cards
            if (showIcons) {
                when (meal.type) {
                    MealType.LUNCH -> {
                        // Apple icon
                        FoodIcon(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .size(40.dp),
                            iconType = FoodIconType.APPLE,
                            color = Color(0xFF4A7C59)
                        )
                        // Lemon icon
                        FoodIcon(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp, bottom = 40.dp)
                                .size(36.dp),
                            iconType = FoodIconType.LEMON,
                            color = Color(0xFF4A7C59)
                        )
                        // Salad bowl icon
                        FoodIcon(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .size(42.dp),
                            iconType = FoodIconType.BOWL,
                            color = Color(0xFF4A7C59)
                        )
                    }
                    MealType.DINNER -> {
                        // Meat icon
                        FoodIcon(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                                .size(44.dp),
                            iconType = FoodIconType.MEAT,
                            color = Color(0xFF6B8E9D)
                        )
                        // Egg icon
                        FoodIcon(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 16.dp, bottom = 80.dp)
                                .size(38.dp),
                            iconType = FoodIconType.EGG,
                            color = Color(0xFF6B8E9D)
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun MealCardPlaceholder(
    mealType: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$mealType:",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No meal planned",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B6B6B)
            )
        }
    }
}
