package com.bodycalc.platepilot.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.bodycalc.platepilot.R
import com.bodycalc.platepilot.data.model.Meal
import com.bodycalc.platepilot.data.model.MealType

/**
 * Smart Image Loader that handles both local drawable images and Firebase cloud images
 * - Local images (isCloudImage = false): Loads from drawable resources instantly (offline support)
 * - Cloud images (isCloudImage = true): Loads from Firebase Storage URL (requires internet)
 */
@Composable
fun SmartMealImage(
    meal: Meal,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (meal.isCloudImage) {
        // CLOUD IMAGE - Load from Firebase Storage URL
        if (!meal.imageUrl.isNullOrEmpty()) {
            SubcomposeAsyncImage(
                model = meal.imageUrl,
                contentDescription = meal.name,
                modifier = modifier,
                contentScale = contentScale,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Image unavailable")
                    }
                }
            )
        } else {
            // Offline - cloud image URL not available
            Box(
                modifier = modifier.background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Offline - Image unavailable")
            }
        }
    } else {
        // LOCAL IMAGE - Load from drawable resources
        val resourceId = when (meal.imageUrl) {
            "oats" -> R.drawable.oatmeal_berries_new
            "grilled_chicken_salad" -> R.drawable.grilled_chicken_salad_new
            "quinoa_stir_fry_bowl_image" -> R.drawable.quinoa_stir_fry_new
            "fruit_salad_bowl" -> R.drawable.fruit_salad_bowl_new
            else -> 0
        }
        
        if (resourceId != 0) {
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = meal.name,
                modifier = modifier,
                contentScale = contentScale
            )
        } else {
            // Fallback emoji if no image found
            Box(
                modifier = modifier.background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (meal.type) {
                        MealType.BREAKFAST -> "🥣"
                        MealType.LUNCH -> "🥗"
                        MealType.DINNER -> "🍲"
                        MealType.SNACK -> "🍎"
                    },
                    fontSize = 56.sp
                )
            }
        }
    }
}
