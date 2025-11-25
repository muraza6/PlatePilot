package com.bodycalc.platepilot.utils

/**
 * Utility object to manage GitHub-hosted meal images
 * 
 * Images are stored in: https://github.com/muraza6/meal-images
 * Organized by branches: Nonveg_meals, Breakfast_normal, etc.
 * Using GitHub raw CDN for direct image access
 */
object GitHubImageManager {
    
    private const val GITHUB_USERNAME = "muraza6"
    private const val REPO_NAME = "meal-images"
    
    // GitHub raw CDN URL for direct file access
    private const val BASE_URL = "https://raw.githubusercontent.com/$GITHUB_USERNAME/$REPO_NAME"
    
    /**
     * Get the full GitHub raw URL for an image from a specific branch
     * 
     * @param branch Branch name (e.g., "Nonveg_meals", "Breakfast_normal")
     * @param imagePath Path to the image in the repository (e.g., "chicken_tikka_card.png")
     * @return Full URL to the image on GitHub raw CDN
     * 
     * Example:
     * getImageUrl("Nonveg_meals", "chicken_tikka_card.png") 
     * -> "https://raw.githubusercontent.com/muraza6/meal-images/Nonveg_meals/chicken_tikka_card.png"
     */
    fun getImageUrl(branch: String, imagePath: String): String {
        return "$BASE_URL/$branch/$imagePath"
    }
    
    /**
     * Convenience method for non-veg meals
     */
    fun getNonVegImageUrl(imagePath: String): String {
        return getImageUrl("Nonveg_meals", imagePath)
    }
    
    /**
     * Convenience method for breakfast meals
     */
    fun getBreakfastImageUrl(imagePath: String): String {
        return getImageUrl("Breakfast_normal", imagePath)
    }
    
    /**
     * Check if a meal uses cloud (GitHub) images
     * This is determined by the presence of "raw.githubusercontent.com" in the URL
     */
    fun isCloudImage(imageUrl: String?): Boolean {
        return imageUrl?.contains("raw.githubusercontent.com") == true
    }
}
