package com.bodycalc.platepilot

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class SplashActivity : ComponentActivity() {
    private var hasNavigated = false
    private val fallbackDelayMs = 3000L
    private val handler = Handler(Looper.getMainLooper())
    private var fallbackRunnable: Runnable? = null
    private var videoView: VideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fullscreen
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController?.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContentView(R.layout.activity_splash)

        videoView = findViewById(R.id.videoView) // ensure your layout has VideoView with id videoView
        startSplashVideo()
    }

    private fun startSplashVideo() {
        val vv = videoView ?: return

        // Fallback: if anything fails, navigate after fallbackDelayMs
        fallbackRunnable = Runnable { navigateToMainActivity() }
        handler.postDelayed(fallbackRunnable!!, fallbackDelayMs)

        try {
            val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.splash_animation}")
            vv.setVideoURI(videoUri)

            vv.setOnPreparedListener { mp: MediaPlayer ->
                // Cancel fallback once video prepared and start playback
                fallbackRunnable?.let { handler.removeCallbacks(it) }
                fallbackRunnable = Runnable { navigateToMainActivity() } // keep fallback in case playback hangs
                handler.postDelayed(fallbackRunnable!!, (mp.duration + 500).toLong())
                mp.isLooping = false
                vv.start()
            }

            vv.setOnCompletionListener {
                navigateToMainActivity()
            }

            vv.setOnErrorListener { _, _, _ ->
                // On error fall back to navigation
                navigateToMainActivity()
                true
            }

            // Tap to skip
            vv.setOnTouchListener { _: View, event: MotionEvent ->
                if (event.action == MotionEvent.ACTION_UP) {
                    navigateToMainActivity()
                }
                true
            }
        } catch (e: Exception) {
            // If resource not found or other error, fallback
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        if (!hasNavigated) {
            hasNavigated = true
            handler.removeCallbacksAndMessages(null)
            try {
                videoView?.stopPlayback()
            } catch (_: Exception) {}
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
        videoView?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        try {
            videoView?.stopPlayback()
        } catch (_: Exception) {}
    }
}
```// filepath: /Users/musharrafraza/AndroidStudioProjects/PlatePilot/app/src/main/java/com/bodycalc/platepilot/SplashActivity.kt
package com.bodycalc.platepilot

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class SplashActivity : ComponentActivity() {
    private var hasNavigated = false
    private val fallbackDelayMs = 3000L
    private val handler = Handler(Looper.getMainLooper())
    private var fallbackRunnable: Runnable? = null
    private var videoView: VideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fullscreen
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController?.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContentView(R.layout.activity_splash)

        videoView = findViewById(R.id.videoView) // ensure your layout has VideoView with id videoView
        startSplashVideo()
    }

    private fun startSplashVideo() {
        val vv = videoView ?: return

        // Fallback: if anything fails, navigate after fallbackDelayMs
        fallbackRunnable = Runnable { navigateToMainActivity() }
        handler.postDelayed(fallbackRunnable!!, fallbackDelayMs)

        try {
            val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.splash_animation}")
            vv.setVideoURI(videoUri)

            vv.setOnPreparedListener { mp: MediaPlayer ->
                // Cancel fallback once video prepared and start playback
                fallbackRunnable?.let { handler.removeCallbacks(it) }
                fallbackRunnable = Runnable { navigateToMainActivity() } // keep fallback in case playback hangs
                handler.postDelayed(fallbackRunnable!!, (mp.duration + 500).toLong())
                mp.isLooping = false
                vv.start()
            }

            vv.setOnCompletionListener {
                navigateToMainActivity()
            }

            vv.setOnErrorListener { _, _, _ ->
                // On error fall back to navigation
                navigateToMainActivity()
                true
            }

            // Tap to skip
            vv.setOnTouchListener { _: View, event: MotionEvent ->
                if (event.action == MotionEvent.ACTION_UP) {
                    navigateToMainActivity()
                }
                true
            }
        } catch (e: Exception) {
            // If resource not found or other error, fallback
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        if (!hasNavigated) {
            hasNavigated = true
            handler.removeCallbacksAndMessages(null)
            try {
                videoView?.stopPlayback()
            } catch (_: Exception) {}
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
        videoView?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        try {
            videoView?.stopPlayback()
        } catch (_: Exception) {}
    }
}