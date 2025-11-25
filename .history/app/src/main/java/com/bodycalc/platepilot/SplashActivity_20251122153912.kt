package com.bodycalc.platepilot

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class SplashActivity : ComponentActivity() {
    private var hasNavigated = false
    private val fallbackDelayMs = 5000L // 5 seconds fallback
    private val handler = Handler(Looper.getMainLooper())
    private var fallbackRunnable: Runnable? = null
    private var videoView: VideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fullscreen mode - hide system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController?.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContentView(R.layout.activity_splash)

        videoView = findViewById(R.id.videoView)
        startSplashVideo()
    }

    private fun startSplashVideo() {
        val vv = videoView ?: run {
            // If VideoView not found, fallback immediately
            navigateToMainActivity()
            return
        }

        // Set up fallback timer
        fallbackRunnable = Runnable { navigateToMainActivity() }
        handler.postDelayed(fallbackRunnable!!, fallbackDelayMs)

        try {
            // Load video from raw resources
            val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.splash_animation}")
            vv.setVideoURI(videoUri)

            vv.setOnPreparedListener { mp: MediaPlayer ->
                // Video is ready, cancel fallback and start playback
                fallbackRunnable?.let { handler.removeCallbacks(it) }
                
                // Set up completion fallback (video duration + 500ms buffer)
                fallbackRunnable = Runnable { navigateToMainActivity() }
                handler.postDelayed(fallbackRunnable!!, (mp.duration + 500).toLong())
                
                // Configure video
                mp.isLooping = false
                mp.setVolume(1.0f, 1.0f) // Full volume
                
                // Get screen dimensions
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val screenWidth = displayMetrics.widthPixels.toFloat()
                val screenHeight = displayMetrics.heightPixels.toFloat()
                
                // Video dimensions: 678 × 1204
                val videoWidth = 678f
                val videoHeight = 1204f
                
                // Calculate scales
                val scaleX = screenWidth / videoWidth
                val scaleY = screenHeight / videoHeight
                
                // Use weighted average to crop less (favor height scaling)
                // 30% width scale + 70% height scale = less cropping
                val scale = (scaleX * 0.3f) + (scaleY * 0.7f)
                
                // Apply scaling
                vv.scaleX = scale
                vv.scaleY = scale
                
                // Center the scaled video
                val scaledVideoWidth = videoWidth * scale
                val scaledVideoHeight = videoHeight * scale
                
                vv.translationX = (screenWidth - scaledVideoWidth) / 2f
                vv.translationY = (screenHeight - scaledVideoHeight) / 2f
                
                vv.start()
            }

            vv.setOnCompletionListener {
                // Video finished playing
                navigateToMainActivity()
            }

            vv.setOnErrorListener { _, what, extra ->
                // Log error and fallback
                android.util.Log.e("SplashActivity", "Video playback error: what=$what, extra=$extra")
                navigateToMainActivity()
                true
            }

            // Tap anywhere to skip video
            vv.setOnTouchListener { _: View, event: MotionEvent ->
                if (event.action == MotionEvent.ACTION_UP) {
                    navigateToMainActivity()
                }
                true
            }

        } catch (e: Exception) {
            // Resource not found or other error
            android.util.Log.e("SplashActivity", "Error loading splash video", e)
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        if (!hasNavigated) {
            hasNavigated = true
            handler.removeCallbacksAndMessages(null)
            
            // Stop video playback
            try {
                videoView?.stopPlayback()
            } catch (e: Exception) {
                android.util.Log.e("SplashActivity", "Error stopping video", e)
            }
            
            // Navigate to main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        videoView?.pause()
    }

    override fun onResume() {
        super.onResume()
        videoView?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        try {
            videoView?.stopPlayback()
        } catch (e: Exception) {}
    }
}