package com.bodycalc.platepilot.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
    val context = LocalContext.current
    
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
                    // Load image from drawable
                    val imageName = meal.imageUrl?.replace("-", "_")?.trim() ?: ""
                    val drawableId = if (imageName.isNotEmpty()) {
                        try {
                            context.resources.getIdentifier(
                                imageName,
                                "drawable",
                                context.packageName
                            )
                        } catch (e: Exception) {
                            0
                        }
                    } else {
                        0
                    }
                    
                    // Circular white background
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.95f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (drawableId != 0) {
                            Image(
                                painter = painterResource(id = drawableId),
                                contentDescription = meal.name,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Fallback emoji
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
            
            // Decorative food icon overlays
            if (showIcons) {
                when (meal.type) {
                    MealType.LUNCH -> {
                        // Try to load icon1 (apple/green icon)
                        val icon1Id = context.resources.getIdentifier("icon1", "drawable", context.packageName)
                        if (icon1Id != 0) {
                            Image(
                                painter = painterResource(id = icon1Id),
                                contentDescription = "Decoration",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp)
                                    .size(40.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        
                        // Try to load icon (lemon/middle icon)
                        val iconId = context.resources.getIdentifier("icon", "drawable", context.packageName)
                        if (iconId != 0) {
                            Image(
                                painter = painterResource(id = iconId),
                                contentDescription = "Decoration",
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 16.dp, bottom = 40.dp)
                                    .size(36.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        
                        // Bottom icon
                        val icon2Id = context.resources.getIdentifier("icon2", "drawable", context.packageName)
                        if (icon2Id != 0) {
                            Image(
                                painter = painterResource(id = icon2Id),
                                contentDescription = "Decoration",
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                                    .size(42.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                    MealType.DINNER -> {
                        // Top left icon for dinner
                        val icon1Id = context.resources.getIdentifier("icon1", "drawable", context.packageName)
                        if (icon1Id != 0) {
                            Image(
                                painter = painterResource(id = icon1Id),
                                contentDescription = "Decoration",
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(16.dp)
                                    .size(44.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        
                        // Bottom left icon
                        val iconId = context.resources.getIdentifier("icon", "drawable", context.packageName)
                        if (iconId != 0) {
                            Image(
                                painter = painterResource(id = iconId),
                                contentDescription = "Decoration",
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(start = 16.dp, bottom = 80.dp)
                                    .size(38.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
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
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
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

enum class FoodIconType {
    APPLE, LEMON, BOWL, MEAT, EGG
}

@Composable
fun FoodIcon(
    modifier: Modifier = Modifier,
    iconType: FoodIconType,
    color: Color
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.5.dp.toPx()
        
        when (iconType) {
            FoodIconType.APPLE -> {
                // Draw apple outline
                val applePath = Path().apply {
                    moveTo(size.width * 0.5f, size.height * 0.2f)
                    cubicTo(
                        size.width * 0.3f, size.height * 0.15f,
                        size.width * 0.15f, size.height * 0.35f,
                        size.width * 0.15f, size.height * 0.6f
                    )
                    cubicTo(
                        size.width * 0.15f, size.height * 0.85f,
                        size.width * 0.35f, size.height * 0.95f,
                        size.width * 0.5f, size.height * 0.95f
                    )
                    cubicTo(
                        size.width * 0.65f, size.height * 0.95f,
                        size.width * 0.85f, size.height * 0.85f,
                        size.width * 0.85f, size.height * 0.6f
                    )
                    cubicTo(
                        size.width * 0.85f, size.height * 0.35f,
                        size.width * 0.7f, size.height * 0.15f,
                        size.width * 0.5f, size.height * 0.2f
                    )
                }
                drawPath(applePath, color, style = Stroke(width = strokeWidth))
                
                // Leaf
                val leafPath = Path().apply {
                    moveTo(size.width * 0.5f, size.height * 0.2f)
                    quadraticBezierTo(
                        size.width * 0.6f, size.height * 0.1f,
                        size.width * 0.65f, size.height * 0.05f
                    )
                }
                drawPath(leafPath, color, style = Stroke(width = strokeWidth))
            }
            
            FoodIconType.LEMON -> {
                // Draw lemon outline
                drawOval(
                    color = color,
                    style = Stroke(width = strokeWidth)
                )
                // Cross section lines
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.3f, size.height * 0.5f),
                    end = Offset(size.width * 0.7f, size.height * 0.5f),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.5f, size.height * 0.3f),
                    end = Offset(size.width * 0.5f, size.height * 0.7f),
                    strokeWidth = strokeWidth
                )
            }
            
            FoodIconType.BOWL -> {
                // Draw bowl
                val bowlPath = Path().apply {
                    moveTo(size.width * 0.2f, size.height * 0.4f)
                    quadraticBezierTo(
                        size.width * 0.5f, size.height * 0.9f,
                        size.width * 0.8f, size.height * 0.4f
                    )
                }
                drawPath(bowlPath, color, style = Stroke(width = strokeWidth))
                
                // Salad leaves on top
                drawCircle(
                    color = color,
                    radius = size.width * 0.12f,
                    center = Offset(size.width * 0.35f, size.height * 0.25f),
                    style = Stroke(width = strokeWidth)
                )
                drawCircle(
                    color = color,
                    radius = size.width * 0.12f,
                    center = Offset(size.width * 0.65f, size.height * 0.25f),
                    style = Stroke(width = strokeWidth)
                )
            }
            
            FoodIconType.MEAT -> {
                // Draw meat/steak icon
                val meatPath = Path().apply {
                    moveTo(size.width * 0.3f, size.height * 0.3f)
                    lineTo(size.width * 0.4f, size.height * 0.2f)
                    lineTo(size.width * 0.7f, size.height * 0.3f)
                    lineTo(size.width * 0.8f, size.height * 0.5f)
                    lineTo(size.width * 0.7f, size.height * 0.8f)
                    lineTo(size.width * 0.3f, size.height * 0.7f)
                    close()
                }
                drawPath(meatPath, color, style = Stroke(width = strokeWidth))
            }
            
            FoodIconType.EGG -> {
                // Draw egg shape
                val eggPath = Path().apply {
                    moveTo(size.width * 0.5f, size.height * 0.15f)
                    cubicTo(
                        size.width * 0.25f, size.height * 0.2f,
                        size.width * 0.2f, size.height * 0.4f,
                        size.width * 0.2f, size.height * 0.6f
                    )
                    cubicTo(
                        size.width * 0.2f, size.height * 0.8f,
                        size.width * 0.35f, size.height * 0.95f,
                        size.width * 0.5f, size.height * 0.95f
                    )
                    cubicTo(
                        size.width * 0.65f, size.height * 0.95f,
                        size.width * 0.8f, size.height * 0.8f,
                        size.width * 0.8f, size.height * 0.6f
                    )
                    cubicTo(
                        size.width * 0.8f, size.height * 0.4f,
                        size.width * 0.75f, size.height * 0.2f,
                        size.width * 0.5f, size.height * 0.15f
                    )
                }
                drawPath(eggPath, color, style = Stroke(width = strokeWidth))
                
                // Yolk
                drawCircle(
                    color = color,
                    radius = size.width * 0.15f,
                    center = Offset(size.width * 0.5f, size.height * 0.55f),
                    style = Stroke(width = strokeWidth)
                )
            }
        }
    }
}
