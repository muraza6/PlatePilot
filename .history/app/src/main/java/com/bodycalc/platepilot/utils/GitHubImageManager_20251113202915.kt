package com.bodycalc.platepilot.utils

/**
 * Utility object to manage GitHub-hosted meal images
 * 
 * Images are stored in: https://github.com/muraza6/meal-images
 * Using GitHub raw CDN for direct image access
 */
object GitHubImageManager {
    
    private const val GITHUB_USERNAME = "muraza6"
    private const val REPO_NAME = "meal-images"
    private const val BRANCH = "main"
    
    // GitHub raw CDN URL for direct file access
    private const val BASE_URL = "https://raw.githubusercontent.com/$GITHUB_USERNAME/$REPO_NAME/$BRANCH"
    
    /**
     * Get the full GitHub raw URL for an image
     * 
     * @param imagePath Path to the image in the repository (e.g., "chicken_tikka_card.png")
     * @return Full URL to the image on GitHub raw CDN
     * 
     * Example:
     * getImageUrl("chicken_tikka_card.png") 
     * -> "https://raw.githubusercontent.com/muraza6/meal-images/main/chicken_tikka_card.png"
     */
    fun getImageUrl(imagePath: String): String {
        return "$BASE_URL/$imagePath"
    }
    
    /**
     * Check if a meal uses cloud (GitHub) images
     * This is determined by the presence of "raw.githubusercontent.com" in the URL
     */
    fun isCloudImage(imageUrl: String?): Boolean {
        return imageUrl?.contains("raw.githubusercontent.com") == true
    }
}
