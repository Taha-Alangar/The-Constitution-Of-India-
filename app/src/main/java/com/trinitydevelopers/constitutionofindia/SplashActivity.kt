package com.trinitydevelopers.constitutionofindia

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.trinitydevelopers.constitutionofindia.databinding.ActivitySplashBinding
import com.trinitydevelopers.constitutionofindia.screens.MainActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences

    private lateinit var binding:ActivitySplashBinding
    private lateinit var scaleUp:Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        preferences = getSharedPreferences("settings", MODE_PRIVATE)
        if (preferences.getBoolean("night_mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scaleUp= AnimationUtils.loadAnimation(this,R.anim.scale_up)

        binding.splashLogo.startAnimation(scaleUp)

        Handler(Looper.getMainLooper()).postDelayed({
            val onboardingComplete = preferences.getBoolean("onboarding_complete", false)
            if (onboardingComplete) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            finish()
        }, 1200)
    }
}