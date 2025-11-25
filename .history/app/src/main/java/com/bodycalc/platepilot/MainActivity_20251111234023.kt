package com.bodycalc.platepilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.bodycalc.platepilot.ads.AdMobManager
import com.bodycalc.platepilot.ui.components.BottomNavigationBar
import com.bodycalc.platepilot.ui.navigation.HorizontalPagerContent
import com.bodycalc.platepilot.ui.theme.PlatePilotTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var adMobManager: AdMobManager
    private var adCounter = 0 // Track page changes
    
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Force light mode for status bar and navigation bar
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        
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
                    pageCount = { 3 }
                )
                val coroutineScope = rememberCoroutineScope()
                
                // Show interstitial ad less frequently
                LaunchedEffect(pagerState.currentPage) {
                    adCounter++
                    // Show ad only after every 5 page changes
                    if (adCounter >= 5) {
                        android.util.Log.d("MainActivity", "Attempting to show interstitial ad (counter: $adCounter)")
                        if (adMobManager.isInterstitialAdReady()) {
                            adMobManager.showInterstitialAd(this@MainActivity)
                            adCounter = 0 // Reset counter after showing ad
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
}
