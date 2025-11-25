package com.bodycalc.platepilot

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class SplashActivity : ComponentActivity() {
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hide system bars for fullscreen experience
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController?.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        
        setContentView(R.layout.activity_splash)
        
        videoView = findViewById(R.id.splash_video)
        
        // Set video URI
        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.splash_animation}")
        videoView.setVideoURI(videoUri)
        
        // Start playing video
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = false
            
            // Scale video to fill screen
            val videoWidth = mediaPlayer.videoWidth.toFloat()
            val videoHeight = mediaPlayer.videoHeight.toFloat()
            val screenWidth = resources.displayMetrics.widthPixels.toFloat()
            val screenHeight = resources.displayMetrics.heightPixels.toFloat()
            
            val scaleX = screenWidth / videoWidth
            val scaleY = screenHeight / videoHeight
            val scale = maxOf(scaleX, scaleY)
            
            val scaledWidth = (videoWidth * scale).toInt()
            val scaledHeight = (videoHeight * scale).toInt()
            
            val layoutParams = videoView.layoutParams
            layoutParams.width = scaledWidth
            layoutParams.height = scaledHeight
            videoView.layoutParams = layoutParams
            
            videoView.start()
        }
        
        // Navigate to MainActivity when video completes
        videoView.setOnCompletionListener {
            navigateToMainActivity()
        }
        
        // Error handling
        videoView.setOnErrorListener { _, _, _ ->
            navigateToMainActivity()
            true
        }
    }
    
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    override fun onPause() {
        super.onPause()
        if (videoView.isPlaying) {
            videoView.pause()
        }
    }
}
