package com.example.platepilot.utils

import android.content.Context
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

/**
 * Utility class to upload meal images to Firebase Storage
 * This is a one-time upload script for development purposes
 */
class UploadMealImages {
    
    companion object {
        private const val TAG = "UploadMealImages"
        
        /**
         * Upload all meal images from the temp_meal_images folder to Firebase Storage
         * Images should be copied to app's internal storage first, then uploaded
         */
        suspend fun uploadImagesFromAssets(context: Context): Boolean {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            
            // Define the images to upload
            val imagesToUpload = listOf(
                ImageUploadInfo(
                    localFileName = "chicken_tikka_card.png",
                    firebasePath = "meal_images/chicken_tikka_card.png",
                    mealName = "Chicken Tikka (Card)"
                ),
                ImageUploadInfo(
                    localFileName = "chicken_tikka_detail.jpeg",
                    firebasePath = "meal_images/chicken_tikka_detail.jpeg",
                    mealName = "Chicken Tikka (Detail)"
                ),
                ImageUploadInfo(
                    localFileName = "Shrimp Tacos_card.png",
                    firebasePath = "meal_images/shrimp_tacos_card.png",
                    mealName = "Shrimp Tacos (Card)"
                ),
                ImageUploadInfo(
                    localFileName = "Shrimp Tacos_detail.png",
                    firebasePath = "meal_images/shrimp_tacos_detail.png",
                    mealName = "Shrimp Tacos (Detail)"
                )
            )
            
            var allSuccess = true
            
            for (imageInfo in imagesToUpload) {
                try {
                    Log.d(TAG, "Uploading ${imageInfo.mealName}...")
                    
                    // Read the image file from assets
                    val imageBytes = try {
                        context.assets.open(imageInfo.localFileName).use { it.readBytes() }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to read ${imageInfo.localFileName} from assets: ${e.message}")
                        allSuccess = false
                        continue
                    }
                    
                    // Upload to Firebase Storage
                    val imageRef = storageRef.child(imageInfo.firebasePath)
                    imageRef.putBytes(imageBytes).await()
                    
                    Log.d(TAG, "✓ Successfully uploaded ${imageInfo.mealName}")
                    
                } catch (e: Exception) {
                    Log.e(TAG, "✗ Failed to upload ${imageInfo.mealName}: ${e.message}")
                    allSuccess = false
                }
            }
            
            return allSuccess
        }
        
        /**
         * Get the download URLs for uploaded images
         */
        suspend fun getImageUrls(): Map<String, String> {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            
            val urls = mutableMapOf<String, String>()
            
            val paths = listOf(
                "chicken_tikka_card" to "meal_images/chicken_tikka_card.png",
                "chicken_tikka_detail" to "meal_images/chicken_tikka_detail.jpeg",
                "shrimp_tacos_card" to "meal_images/shrimp_tacos_card.png",
                "shrimp_tacos_detail" to "meal_images/shrimp_tacos_detail.png"
            )
            
            for ((key, path) in paths) {
                try {
                    val url = storageRef.child(path).downloadUrl.await().toString()
                    urls[key] = url
                    Log.d(TAG, "$key: $url")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to get URL for $key: ${e.message}")
                }
            }
            
            return urls
        }
    }
    
    data class ImageUploadInfo(
        val localFileName: String,
        val firebasePath: String,
        val mealName: String
    )
}
