package com.bodycalc.platepilot.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bodycalc.platepilot.data.model.WaterLog
import com.bodycalc.platepilot.ui.components.BannerAdView
import com.bodycalc.platepilot.ui.viewmodel.WaterViewModel
import com.bodycalc.platepilot.ads.AdMobManager
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WaterTrackingScreen(
    adMobManager: AdMobManager? = null,
    waterViewModel: WaterViewModel = viewModel()
) {
    val uiState by waterViewModel.uiState.collectAsState()
    var showInfoDialog by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF5F7),
                        Color(0xFFF0F4FF),
                        Color(0xFFF5F0FF)
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
            Spacer(modifier = Modifier.height(40.dp))
            
            // Title with Info Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(40.dp)) // Balance the layout
                
                Text(
                    text = "Water Tracking",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
                
                IconButton(
                    onClick = { showInfoDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Water intake information",
                        tint = Color(0xFF5DADE2),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Water Glass Visualization
            WaterGlass(
                currentWater = uiState.totalWater,
                dailyGoal = uiState.dailyGoal
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Progress Text (next to glass)
            val percentage = ((uiState.totalWater.toFloat() / uiState.dailyGoal) * 100).toInt()
            Text(
                text = "${uiState.totalWater / 1000f} L / ${uiState.dailyGoal / 1000f} L",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
            Text(
                text = "$percentage% Complete",
                fontSize = 14.sp,
                color = Color(0xFF7F8C8D)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Reset Button
            if (uiState.totalWater > 0) {
                TextButton(
                    onClick = { waterViewModel.resetToday() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFE74C3C)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Log Water Button
            Button(
                onClick = { waterViewModel.addWater(250) },
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(min = 200.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB8D4F1)
                ),
                shape = RoundedCornerShape(28.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log Water",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Circular Progress Ring with water drop
            WaterProgressRing(
                currentWater = uiState.totalWater,
                dailyGoal = uiState.dailyGoal
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Quick Add Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickAddOption(
                    icon = Icons.Default.LocalDrink,
                    label = "250 ml",
                    onClick = { waterViewModel.addWater(250) },
                    color = Color(0xFFE8D4F8)
                )
                QuickAddOption(
                    icon = Icons.Default.LocalDrink,
                    label = "250 ml",
                    onClick = { waterViewModel.addWater(250) },
                    color = Color(0xFFD4E4F8)
                )
                QuickAddOption(
                    icon = Icons.Default.Coffee,
                    label = "CeP",
                    onClick = { waterViewModel.addWater(200) },
                    color = Color(0xFFF8D4E4)
                )
                QuickAddOption(
                    icon = Icons.Default.EmojiFoodBeverage,
                    label = "Tea",
                    onClick = { waterViewModel.addWater(200) },
                    color = Color(0xFFE8F8D4)
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Ad Banner
            BannerAdView(
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WaterGlass(
    currentWater: Int,
    dailyGoal: Int
) {
    val progress = (currentWater.toFloat() / dailyGoal).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "water_progress"
    )
    
    Canvas(
        modifier = Modifier
            .width(200.dp)
            .height(260.dp)
    ) {
        val glassPath = createGlassPath(size)
        
        // Draw water fill inside the glass
        if (animatedProgress > 0) {
            val waterHeight = size.height * animatedProgress
            val waterTop = size.height - waterHeight
            
            // Clip to glass path so water stays inside
            clipPath(path = glassPath) {
                // Create water path with wave effect
                val waterPath = Path().apply {
                    val waveAmplitude = 6f
                    val waveFrequency = 0.08f
                    
                    // Start at left edge
                    moveTo(0f, waterTop)
                    
                    // Draw wavy top line
                    var x = 0f
                    while (x <= size.width) {
                        val wave = sin(x * waveFrequency).toFloat() * waveAmplitude
                        lineTo(x, waterTop + wave)
                        x += 8
                    }
                    
                    // Complete the water shape
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                
                // Draw water with blue gradient
                drawPath(
                    path = waterPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE8D4F8).copy(alpha = 0.3f),
                            Color(0xFFD4E4F8).copy(alpha = 0.5f),
                            Color(0xFF89C4F4).copy(alpha = 0.65f),
                            Color(0xFF5DADE2).copy(alpha = 0.75f)
                        ),
                        startY = waterTop,
                        endY = size.height
                    )
                )
            }
        }
        
        // Draw glass outline
        drawPath(
            path = glassPath,
            color = Color(0xFF2C3E50),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

fun createGlassPath(size: Size): Path {
    val width = size.width
    val height = size.height

    // Trapezoidal glass shape - wider at top, narrower at bottom
    val bottomWidthRatio = 0.50f  // Narrower bottom
    val topWidthRatio = 0.88f     // Wider top for open rim
    val baseRoundingRadius = 0.12f * height // Soft curve at the base

    val path = Path()

    // Calculate key coordinates
    val topLeftX = width * ((1f - topWidthRatio) / 2)
    val topRightX = width * (1f - (1f - topWidthRatio) / 2)
    
    val bottomLeftX = width * ((1f - bottomWidthRatio) / 2)
    val bottomRightX = width * (1f - (1f - bottomWidthRatio) / 2)

    // Start at bottom-left, just above the rounded base
    path.moveTo(bottomLeftX, height - baseRoundingRadius)

    // Soft curve at bottom-left corner
    path.cubicTo(
        x1 = bottomLeftX,
        y1 = height - (baseRoundingRadius * 0.4f),
        x2 = bottomLeftX + (baseRoundingRadius * 0.6f),
        y2 = height,
        x3 = bottomLeftX + baseRoundingRadius,
        y3 = height
    )

    // Straight line across the bottom (with slight curve)
    path.lineTo(bottomRightX - baseRoundingRadius, height)

    // Soft curve at bottom-right corner
    path.cubicTo(
        x1 = bottomRightX - (baseRoundingRadius * 0.6f),
        y1 = height,
        x2 = bottomRightX,
        y2 = height - (baseRoundingRadius * 0.4f),
        x3 = bottomRightX,
        y3 = height - baseRoundingRadius
    )

    // Straight line up the right side (slight taper outward)
    path.lineTo(topRightX, 0f)

    // Straight line across the top rim (open top)
    path.lineTo(topLeftX, 0f)

    // Straight line down the left side (completing the trapezoid)
    path.lineTo(bottomLeftX, height - baseRoundingRadius)
    
    path.close()
    
    return path
}

@Composable
fun WaterProgressRing(
    currentWater: Int,
    dailyGoal: Int
) {
    val progress = (currentWater.toFloat() / dailyGoal).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "ring_progress"
    )
    
    Box(
        modifier = Modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        // Progress ring with gradient
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)
            
            // Background ring (light gray)
            drawCircle(
                color = Color(0xFFE8E8E8),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )
            
            // Progress ring (gradient pink to blue)
            val sweepAngle = 360f * animatedProgress
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFFFFC4DD),
                        Color(0xFFB8D4F1),
                        Color(0xFFFFC4DD)
                    )
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
        }
        
        // Water drop icon in center
        Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = Color(0xFF89C4F4)
        )
    }
}

@Composable
fun QuickAddOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF2C3E50),
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF7F8C8D)
        )
    }
}
