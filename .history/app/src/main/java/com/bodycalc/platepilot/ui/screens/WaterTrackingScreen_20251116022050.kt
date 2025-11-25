package com.bodycalc.platepilot.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bodycalc.platepilot.data.model.WaterLog
import com.bodycalc.platepilot.ui.components.BannerAdView
import com.bodycalc.platepilot.ui.viewmodel.WaterViewModel
import com.bodycalc.platepilot.ads.AdMobManager
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WaterTrackingScreen(
    adMobManager: AdMobManager,
    waterViewModel: WaterViewModel = viewModel()
) {
    val uiState by waterViewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF5F9FF),
                        Color(0xFFE8F4F8)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title
            Text(
                text = "H2Oasis",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50),
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Water Bottle Visualization
            WaterBottle(
                currentWater = uiState.totalWater,
                dailyGoal = uiState.dailyGoal
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Progress Text
            val percentage = ((uiState.totalWater.toFloat() / uiState.dailyGoal) * 100).toInt()
            Text(
                text = "${uiState.totalWater / 1000f} / ${uiState.dailyGoal / 1000f} L",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
            Text(
                text = "$percentage% Complete",
                fontSize = 16.sp,
                color = Color(0xFF7F8C8D)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(5) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == 0) Color(0xFF5DADE2) 
                                else Color(0xFFBDC3C7)
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Add Drink Button
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(min = 200.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD6EAF8)
                ),
                shape = RoundedCornerShape(28.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color(0xFF2C3E50)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Drink",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C3E50)
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Timeline of drinks
            if (uiState.waterLogs.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(uiState.waterLogs.take(5)) { log ->
                        DrinkTimelineItem(
                            waterLog = log,
                            onDelete = { waterViewModel.deleteWaterLog(log) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Ad Banner
            BannerAdView()
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Add Drink Dialog
        if (showAddDialog) {
            AddDrinkDialog(
                onDismiss = { showAddDialog = false },
                onAddWater = { amount ->
                    waterViewModel.addWater(amount)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun WaterBottle(
    currentWater: Int,
    dailyGoal: Int
) {
    val progress = (currentWater.toFloat() / dailyGoal).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "water_progress"
    )
    
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(280.dp)
            .drawBehind {
                val bottleWidth = size.width * 0.7f
                val bottleHeight = size.height * 0.85f
                val bottleLeft = (size.width - bottleWidth) / 2
                val bottleTop = size.height * 0.1f
                
                // Bottle neck
                val neckWidth = bottleWidth * 0.35f
                val neckHeight = size.height * 0.08f
                val neckLeft = (size.width - neckWidth) / 2
                
                // Draw water fill
                val waterHeight = bottleHeight * animatedProgress
                val waterTop = bottleTop + bottleHeight - waterHeight
                
                if (animatedProgress > 0) {
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFAED6F1).copy(alpha = 0.3f),
                                Color(0xFF5DADE2).copy(alpha = 0.4f),
                                Color(0xFF3498DB).copy(alpha = 0.5f)
                            ),
                            startY = waterTop,
                            endY = waterTop + waterHeight
                        ),
                        topLeft = Offset(bottleLeft + 5, waterTop),
                        size = Size(bottleWidth - 10, waterHeight),
                        cornerRadius = CornerRadius(16f, 16f)
                    )
                }
                
                // Draw bottle outline (neck)
                drawRoundRect(
                    color = Color(0xFF2C3E50),
                    topLeft = Offset(neckLeft, bottleTop - neckHeight),
                    size = Size(neckWidth, neckHeight),
                    cornerRadius = CornerRadius(8f, 8f),
                    style = Stroke(width = 3.dp.toPx())
                )
                
                // Draw bottle outline (body)
                drawRoundRect(
                    color = Color(0xFF2C3E50),
                    topLeft = Offset(bottleLeft, bottleTop),
                    size = Size(bottleWidth, bottleHeight),
                    cornerRadius = CornerRadius(16f, 16f),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
    )
}

@Composable
fun DrinkTimelineItem(
    waterLog: WaterLog,
    onDelete: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onDelete() }
    ) {
        // Icon based on amount
        val icon = when {
            waterLog.amount >= 500 -> Icons.Default.LocalDrink
            waterLog.amount >= 250 -> Icons.Default.LocalDrink
            else -> Icons.Default.LocalDrink
        }
        
        val iconColor = when {
            waterLog.amount >= 500 -> Color(0xFFE74C3C)
            waterLog.amount >= 250 -> Color(0xFF5DADE2)
            else -> Color(0xFF85C1E9)
        }
        
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = SimpleDateFormat("h a", Locale.getDefault())
                .format(Date(waterLog.timestamp)),
            fontSize = 12.sp,
            color = Color(0xFF7F8C8D)
        )
    }
}

@Composable
fun AddDrinkDialog(
    onDismiss: () -> Unit,
    onAddWater: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Drink",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Select amount:", fontSize = 14.sp, color = Color.Gray)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickAddOption(
                        amount = 150,
                        label = "150ml",
                        onClick = { onAddWater(150) }
                    )
                    QuickAddOption(
                        amount = 250,
                        label = "250ml",
                        onClick = { onAddWater(250) }
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickAddOption(
                        amount = 500,
                        label = "500ml",
                        onClick = { onAddWater(500) }
                    )
                    QuickAddOption(
                        amount = 1000,
                        label = "1L",
                        onClick = { onAddWater(1000) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun QuickAddOption(
    amount: Int,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFEBF5FB)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2C3E50)
        )
    }
}
