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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.bodycalc.platepilot.ads.AdMobManager
import com.bodycalc.platepilot.billing.BillingManager
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
    private lateinit var billingManager: BillingManager
    private var hasShownInterstitialThisSession = false
    
    companion object {
        private const val TAG = "MainActivity"
        private const val GITHUB_MEALS_URL = "https://raw.githubusercontent.com/muraza6/PlatePilot/main/meals.json"
    }
    
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
        
        // ✅ Initialize Billing FIRST
        billingManager = BillingManager(this)
        billingManager.initialize {
            // After billing is initialized, check premium status
            if (!billingManager.isPremiumUser()) {
                // Only initialize ads if not premium
                initializeAds()
            }
        }
        
        setContent {
            PlatePilotTheme {
                val isPremium by billingManager.isPremium.collectAsState()
                
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = { 4 }
                )
                val coroutineScope = rememberCoroutineScope()
                
                // Handle navigation from notification
                LaunchedEffect(Unit) {
                    intent?.getStringExtra("navigate_to")?.let { destination ->
                        when (destination) {
                            "water" -> pagerState.scrollToPage(2)
                        }
                    }
                }
                
                // ✅ Show interstitial ad only if not premium
                LaunchedEffect(pagerState.currentPage) {
                    if (!isPremium && !hasShownInterstitialThisSession && pagerState.currentPage != 0) {
                        android.util.Log.d("MainActivity", "Attempting to show interstitial ad (first time this session)")
                        if (::adMobManager.isInitialized && adMobManager.isInterstitialAdReady()) {
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
                            // ✅ Only pass adMobManager if not premium
                            adMobManager = if (isPremium) null else (if (::adMobManager.isInitialized) adMobManager else null),
                            billingManager = billingManager
                        )
                    }
                }
            }
        }
    }
    
    // ✅ Separate ads initialization
    private fun initializeAds() {
        adMobManager = AdMobManager(this)
        adMobManager.initialize {
            adMobManager.loadInterstitialAd(
                onAdLoaded = {
                    android.util.Log.d("MainActivity", "Interstitial ad loaded successfully")
                },
                onAdFailed = {
                    android.util.Log.e("MainActivity", "Interstitial ad failed to load")
                }
            )
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
    
    private fun syncMealsFromGitHub() {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Starting GitHub meal sync on app startup")
                
                val database = PlatePilotDatabase.getDatabase(applicationContext)
                val repository = MealRepository(
                    database.mealDao(),
                    database.mealPlanDao(),
                    database.userProfileDao()
                )
                
                val result = repository.syncMealsFromGitHub(GITHUB_MEALS_URL)
                
                if (result.isSuccess) {
                    Log.d(TAG, "GitHub sync completed: ${result.getOrNull()}")
                } else {
                    Log.e(TAG, "GitHub sync failed: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during GitHub sync", e)
            }
        }
    }
    
    // ✅ Clean up billing on destroy
    override fun onDestroy() {
        super.onDestroy()
        if (::billingManager.isInitialized) {
            billingManager.destroy()
        }
    }
}