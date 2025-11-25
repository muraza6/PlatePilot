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
import coil.request.ImageRequest
import com.bodycalc.platepilot.R
import com.bodycalc.platepilot.data.model.Meal
import com.bodycalc.platepilot.data.model.MealType
import android.content.Context
import androidx.compose.ui.platform.LocalContext

enum class MealImageType {
    CARD,   // For HomeScreen cards (without background)
    DETAIL  // For MealDetailScreen (with background)
}

/**
 * Smart Image Loader that handles both local drawable images and GitHub-hosted cloud images
 * - Local images (isCloudImage = false): Loads from drawable resources instantly (offline support)
 * - Cloud images (isCloudImage = true): Loads from GitHub raw URLs (requires internet)
 * - Supports different images for card view vs detail view
 */
@Composable
fun SmartMealImage(
    meal: Meal,
    imageType: MealImageType = MealImageType.CARD,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    // Select appropriate image URL based on view type
    val selectedImageUrl = when (imageType) {
        MealImageType.CARD -> meal.imageUrl
        MealImageType.DETAIL -> meal.detailImageUrl ?: meal.imageUrl // Fallback to card image if detail not set
    }
    
    if (meal.isCloudImage) {
        // CLOUD IMAGE - Load from GitHub raw URL
        if (!selectedImageUrl.isNullOrEmpty()) {
            SubcomposeAsyncImage(
                model = selectedImageUrl,
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
                        Text("Image unavailable", fontSize = 12.sp)
                    }
                }
            )
        } else {
            // Offline - cloud image URL not available
            Box(
                modifier = modifier.background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Offline - Image unavailable", fontSize = 12.sp)
            }
        }
    } else {
        // LOCAL IMAGE - Load from drawable resources
        val resourceId = when (selectedImageUrl) {
            // Card images (without background)
            "oatmeal_berries_new" -> R.drawable.oatmeal_berries_new
            "grilled_chicken_salad_new" -> R.drawable.grilled_chicken_salad_new
            "quinoa_stir_fry_new" -> R.drawable.quinoa_stir_fry_new
            "fruit_salad_bowl_new" -> R.drawable.fruit_salad_bowl_new
            
            // Detail images (with background)
            "recipe_oats" -> R.drawable.recipe_oats
            "recipe_grilled_chicken_salad" -> R.drawable.recipe_grilled_chicken_salad
            "recipe_quinoa_stir_fry_bowl_image" -> R.drawable.recipe_quinoa_stir_fry_bowl_image
            "recipe_fruit_salad_bowl" -> R.drawable.recipe_fruit_salad_bowl
            
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
