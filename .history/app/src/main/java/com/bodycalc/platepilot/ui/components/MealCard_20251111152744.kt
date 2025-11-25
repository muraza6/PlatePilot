package com.bodycalc.platepilot.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bodycalc.platepilot.R
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
    val mealImageRes = when (meal.name) {
        "Oatmeal with Berries" -> R.drawable.oatmeal_berries
        "Grilled Chicken Salad" -> R.drawable.grilled_chicken_salad
        "Quinoa Stir-Fry" -> R.drawable.quinoa_stir_fry
        "Fruit Salad Bowl" -> R.drawable.fruit_salad_bowl
        else -> 0
    }
    
    val iconRes = when (meal.type) {
        MealType.LUNCH -> R.drawable.icon_apple
        MealType.DINNER -> R.drawable.icon_drumstick
        else -> R.drawable.icon_lemon
    }
    
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
        Box(modifier = Modifier.fillMaxSize()) {
            if (showIcons && iconRes != 0) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 16.dp)
                        .alpha(0.4f)
                )
            }
            
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${meal.type.name.lowercase().replaceFirstChar { it.uppercase() }}:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = meal.name,
                        fontSize = 16.sp,
                        color = Color(0xFF2A2A2A)
                    )
                }
                
                if (mealImageRes != 0) {
                    Image(
                        painter = painterResource(id = mealImageRes),
                        contentDescription = meal.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(110.dp)
                    )
                } else {
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
            .height(240.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$mealType:",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No meal planned",
                fontSize = 14.sp,
                color = Color(0xFF6B6B6B)
            )
        }
    }
}
