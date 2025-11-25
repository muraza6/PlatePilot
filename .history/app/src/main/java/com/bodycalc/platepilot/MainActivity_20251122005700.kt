package com.bodycalc.platepilot

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.bodycalc.platepilot.ads.AdMobManager
import com.bodycalc.platepilot.data.local.PlatePilotDatabase
import com.bodycalc.platepilot.data.repository.MealRepository
import com.bodycalc.platepilot.notification.NotificationHelper
import com.bodycalc.platepilot.notification.NotificationScheduler
import com.bodycalc.platepilot.ui.components.BottomNavigationBar
import com.bodycalc.platepilot.ui.navigation.HorizontalPagerContent
import com.bodycalc.platepilot.ui.theme.PlatePilotTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var adMobManager: AdMobManager
    private var hasShownInterstitialThisSession = false // Track if ad was shown in this session
    
    companion object {
        private const val TAG = "MainActivity"
        // TODO: Replace this URL with your actual GitHub repository URL
        // Example: "https://raw.githubusercontent.com/yourusername/yourrepo/main/meals.json"
        private const val GITHUB_MEALS_URL = "https://raw.githubusercontent.com/musharrafraza/PlatePilot/main/meals.json"
    }
    
    // Notification permission launcher
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            setupNotifications()
        }
    }
    
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Force light mode for status bar and navigation bar
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        
        // Initialize notifications
        initializeNotifications()
        
        // Sync meals from GitHub on app startup
        syncMealsFromGitHub()
        
        // Initialize AdMob
        adMobManager = AdMobManager(this)
        adMobManager.initialize {
            // Load first interstitial ad after initialization
            adMobManager.loadInterstitialAd(
                onAdLoaded = {
                    android.util.Log.d("MainActivity", "Interstitial ad loaded successfully")
                },
                onAdFailed = {
                    android.util.Log.e("MainActivity", "Interstitial ad failed to load")
                }
            )
        }
        
        setContent {
            PlatePilotTheme {
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = { 4 }
                )
                val coroutineScope = rememberCoroutineScope()
                
                // Handle navigation from notification
                LaunchedEffect(Unit) {
                    intent?.getStringExtra("navigate_to")?.let { destination ->
                        when (destination) {
                            "water" -> pagerState.scrollToPage(2) // Water screen is at index 2
                        }
                    }
                }
                
                // Show interstitial ad only once per session
                LaunchedEffect(pagerState.currentPage) {
                    // Show ad only on first navigation and only once
                    if (!hasShownInterstitialThisSession && pagerState.currentPage != 0) {
                        android.util.Log.d("MainActivity", "Attempting to show interstitial ad (first time this session)")
                        if (adMobManager.isInterstitialAdReady()) {
                            adMobManager.showInterstitialAd(this@MainActivity) {
                                hasShownInterstitialThisSession = true
                            }
                        } else {
                            android.util.Log.d("MainActivity", "Interstitial ad not ready")
                        }
                    }
                }
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(
                            currentPage = pagerState.currentPage,
                            onNavigate = { page ->
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(page)
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        HorizontalPagerContent(
                            page = page,
                            currentPage = pagerState.currentPage,
                            adMobManager = adMobManager
                        )
                    }
                }
            }
        }
    }
    
    private fun initializeNotifications() {
        // Create notification channel
        NotificationHelper.createNotificationChannel(this)
        
        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    setupNotifications()
                }
                else -> {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            setupNotifications()
        }
    }
    
    private fun setupNotifications() {
        // Check if notifications are enabled in settings
        val sharedPreferences = getSharedPreferences("platepilot_settings", MODE_PRIVATE)
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        
        if (notificationsEnabled) {
            NotificationScheduler.scheduleDailyNotification(this)
        }
    }
}
