package com.bodycalc.platepilot

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class SplashActivity : ComponentActivity() {
    private var hasNavigated = false
    private val fallbackDelayMs = 5000L
    private val handler = Handler(Looper.getMainLooper())
    private var fallbackRunnable: Runnable? = null
    private var videoView: VideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fullscreen mode - hide system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContentView(R.layout.activity_splash)
        videoView = findViewById(R.id.videoView)
        startSplashVideo()
    }

    private fun startSplashVideo() {
        val vv = videoView ?: run {
            navigateToMainActivity()
            return
        }

        fallbackRunnable = Runnable { navigateToMainActivity() }
        handler.postDelayed(fallbackRunnable!!, fallbackDelayMs)

        try {
            val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.splash_animation}")
            vv.setVideoURI(videoUri)

            vv.setOnPreparedListener { mp: MediaPlayer ->
                fallbackRunnable?.let { handler.removeCallbacks(it) }
                
                fallbackRunnable = Runnable { navigateToMainActivity() }
                handler.postDelayed(fallbackRunnable!!, (mp.duration + 500).toLong())
                
                mp.isLooping = false
                mp.setVolume(1.0f, 1.0f)

                // ✅ FIX: Resize VideoView to maintain aspect ratio and cover screen
                val dm = resources.displayMetrics
                val screenW = dm.widthPixels
                val screenH = dm.heightPixels

                val videoW = mp.videoWidth
                val videoH = mp.videoHeight

                // Calculate scale to cover entire screen (crop if needed)
                val scaleX = screenW.toFloat() / videoW
                val scaleY = screenH.toFloat() / videoH
                val scale = maxOf(scaleX, scaleY)

                val newWidth = (videoW * scale).toInt()
                val newHeight = (videoH * scale).toInt()

                // Update VideoView layout params to new size, centered
                vv.layoutParams = FrameLayout.LayoutParams(newWidth, newHeight).apply {
                    gravity = android.view.Gravity.CENTER
                }

                // Reset any transforms (not needed with layout approach)
                vv.scaleX = 1f
                vv.scaleY = 1f
                vv.translationX = 0f
                vv.translationY = 0f

                vv.start()
            }

            vv.setOnCompletionListener { navigateToMainActivity() }

            vv.setOnErrorListener { _, what, extra ->
                android.util.Log.e("SplashActivity", "Video error: what=$what, extra=$extra")
                navigateToMainActivity()
                true
            }

            vv.setOnTouchListener { _: View, event: MotionEvent ->
                if (event.action == MotionEvent.ACTION_UP) navigateToMainActivity()
                true
            }

        } catch (e: Exception) {
            android.util.Log.e("SplashActivity", "Error loading splash video", e)
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        if (!hasNavigated) {
            hasNavigated = true
            handler.removeCallbacksAndMessages(null)
            try { videoView?.stopPlayback() } catch (_: Exception) {}
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        videoView?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (!hasNavigated) videoView?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        try { videoView?.stopPlayback() } catch (_: Exception) {}
    }
}