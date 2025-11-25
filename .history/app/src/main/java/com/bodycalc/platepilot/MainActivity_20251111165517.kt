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
import com.bodycalc.platepilot.ui.components.BottomNavigationBar
import com.bodycalc.platepilot.ui.navigation.HorizontalPagerContent
import com.bodycalc.platepilot.ui.theme.PlatePilotTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlatePilotTheme {
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = { 3 }
                )
                val coroutineScope = rememberCoroutineScope()
                
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
                            currentPage = pagerState.currentPage
                        )
                    }
                }
            }
        }
    }
}
