package com.bodycalc.platepilot.ads

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

class AdMobManager(private val context: Context) {
    
    companion object {
        private const val TAG = "AdMobManager"
        // Test Ad Unit IDs for testing
        const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712" // Test Interstitial
        const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111" // Test Banner
        const val NATIVE_ADVANCED_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110" // Test Native Advanced
        
        // Production Ad Unit IDs (commented out for now)
        // const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-1580021219841396/8616784823"
        // const val BANNER_AD_UNIT_ID = "ca-app-pub-1580021219841396/3743183354"
        // const val NATIVE_ADVANCED_AD_UNIT_ID = "ca-app-pub-1580021219841396/7995512889"
    }
    
    private var interstitialAd: InterstitialAd? = null
    private var isLoadingInterstitial = false
    
    fun initialize(onInitialized: () -> Unit = {}) {
        MobileAds.initialize(context) {
            Log.d(TAG, "AdMob initialized")
            onInitialized()
        }
    }
    
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    // Interstitial Ad Functions
    fun loadInterstitialAd(onAdLoaded: () -> Unit = {}, onAdFailed: () -> Unit = {}) {
        if (!isNetworkAvailable()) {
            Log.d(TAG, "No internet connection, skipping ad load")
            onAdFailed()
            return
        }
        
        if (isLoadingInterstitial) {
            Log.d(TAG, "Interstitial ad is already loading")
            return
        }
        
        isLoadingInterstitial = true
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded")
                    interstitialAd = ad
                    isLoadingInterstitial = false
                    onAdLoaded()
                    
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Interstitial ad dismissed")
                            interstitialAd = null
                            // Preload next ad if network is available
                            if (isNetworkAvailable()) {
                                loadInterstitialAd()
                            }
                        }
                        
                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                            interstitialAd = null
                        }
                        
                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Interstitial ad showed")
                        }
                    }
                }
                
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                    interstitialAd = null
                    isLoadingInterstitial = false
                    onAdFailed()
                }
            }
        )
    }
    
    fun showInterstitialAd(activity: Activity, onAdClosed: () -> Unit = {}) {
        if (!isNetworkAvailable()) {
            Log.d(TAG, "No internet connection, skipping ad display")
            onAdClosed()
            return
        }
        
        if (interstitialAd != null) {
            // Set up callback for when ad is closed
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad dismissed")
                    interstitialAd = null
                    onAdClosed() // Call the callback when ad is closed
                    // Preload next ad if network is available
                    if (isNetworkAvailable()) {
                        loadInterstitialAd()
                    }
                }
                
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                    interstitialAd = null
                    onAdClosed()
                }
                
                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad showed")
                }
            }
            
            interstitialAd?.show(activity)
        } else {
            Log.d(TAG, "Interstitial ad wasn't ready yet")
            onAdClosed()
            // Try to load for next time if network is available
            if (isNetworkAvailable()) {
                loadInterstitialAd()
            }
        }
    }
    
    fun isInterstitialAdReady(): Boolean = interstitialAd != null && isNetworkAvailable()
    
    // Native Advanced Ad Functions
    fun loadNativeAd(onAdLoaded: (NativeAd) -> Unit, onAdFailed: () -> Unit = {}) {
        if (!isNetworkAvailable()) {
            Log.d(TAG, "No internet connection, skipping native ad load")
            onAdFailed()
            return
        }
        
        val adLoader = com.google.android.gms.ads.AdLoader.Builder(context, NATIVE_ADVANCED_AD_UNIT_ID)
            .forNativeAd { nativeAd ->
                Log.d(TAG, "Native ad loaded")
                onAdLoaded(nativeAd)
            }
            .withAdListener(object : com.google.android.gms.ads.AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Native ad failed to load: ${loadAdError.message}")
                    onAdFailed()
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            )
            .build()
        
        adLoader.loadAd(AdRequest.Builder().build())
    }
}
