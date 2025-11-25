package com.bodycalc.platepilot.ui.components

import android.view.LayoutInflater
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bodycalc.platepilot.R
import com.bodycalc.platepilot.ads.AdMobManager
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

@Composable
fun NativeAdCard(
    adMobManager: AdMobManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    
    LaunchedEffect(Unit) {
        adMobManager.loadNativeAd(
            onAdLoaded = { ad ->
                nativeAd = ad
            }
        )
    }
    
    DisposableEffect(Unit) {
        onDispose {
            nativeAd?.destroy()
        }
    }
    
    nativeAd?.let { ad ->
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            AndroidView(
                factory = { context ->
                    val adView = LayoutInflater.from(context)
                        .inflate(R.layout.native_ad_layout, null) as NativeAdView
                    
                    // Populate the native ad view with data
                    adView.headlineView = adView.findViewById(R.id.ad_headline)
                    adView.bodyView = adView.findViewById(R.id.ad_body)
                    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
                    adView.iconView = adView.findViewById(R.id.ad_icon)
                    adView.mediaView = adView.findViewById(R.id.ad_media)
                    
                    // Set the ad
                    adView.setNativeAd(ad)
                    adView
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
