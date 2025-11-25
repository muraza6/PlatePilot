package com.bodycalc.platepilot.utils

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

object FirebaseImageManager {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference.child("meal_images")
    
    /**
     * Get Firebase Storage URL for a cloud image
     * @param imagePath The path to the image in Firebase Storage (e.g., "pasta_carbonara.jpg")
     * @return The download URL string, or null if image not found or offline
     */
    suspend fun getImageUrl(imagePath: String): String? {
        return try {
            storageRef.child(imagePath).downloadUrl.await().toString()
        } catch (e: Exception) {
            null // Return null if image not found or no internet
        }
    }
    
    /**
     * Upload an image to Firebase Storage
     * @param imagePath The path where to store the image (e.g., "pasta_carbonara.jpg")
     * @param imageData The image data as ByteArray
     */
    suspend fun uploadImage(imagePath: String, imageData: ByteArray): Boolean {
        return try {
            storageRef.child(imagePath).putBytes(imageData).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
