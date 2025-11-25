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
            adMobManager.loadInterstitialAd()
        }
        
        setContent {
            PlatePilotTheme {
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = { 3 }
                )
                val coroutineScope = rememberCoroutineScope()
                
                // Show interstitial ad when user navigates between pages
                LaunchedEffect(pagerState.currentPage) {
                    // Show ad every 3 page changes (adjust frequency as needed)
                    if (pagerState.currentPage % 3 == 0 && pagerState.currentPage > 0) {
                        if (adMobManager.isInterstitialAdReady()) {
                            adMobManager.showInterstitialAd(this@MainActivity)
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
