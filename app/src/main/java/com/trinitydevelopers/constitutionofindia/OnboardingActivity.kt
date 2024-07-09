package com.trinitydevelopers.constitutionofindia

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.trinitydevelopers.constitutionofindia.adapters.IntroSlideAdapter
import com.trinitydevelopers.constitutionofindia.model.IntroSlide
import com.trinitydevelopers.constitutionofindia.screens.MainActivity

class OnboardingActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences

    private val introSliderAdapter= IntroSlideAdapter(
        listOf(
            IntroSlide("JUDGEMENT","Know all about all the Supreme Court Judgements",R.drawable.onboarding_one),
            IntroSlide("THE CONSTITUTION OF INDIA","Know all about all the laws of India",R.drawable.onboarding_two),
            IntroSlide("LAW AND ORDER'S","Know all about all the Guidance of India",R.drawable.onboarding_three)
        )
    )
    private lateinit var introSlideViewPager: ViewPager2
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var nextTV: TextView
    private lateinit var skipTv:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        preferences = getSharedPreferences("settings", MODE_PRIVATE)
        if (preferences.getBoolean("night_mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        introSlideViewPager=findViewById(R.id.viewPager2)
        indicatorContainer=findViewById(R.id.indicatorContainer)
        nextTV=findViewById(R.id.nextTV)
        skipTv=findViewById(R.id.skipTV)

        introSlideViewPager.adapter=introSliderAdapter

        setUpIndicators()
        setCurrentIndicator(1)

        introSlideViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                // Check if the user is on the third page
                if (position == 2) { // Indexing starts from 0, so 2 represents the third page
                    nextTV.text = "Next"// Change text to "Login"
                    skipTv.text=""
                    skipTv.setOnClickListener {
                        completeOnboarding()
                    }
                } else {
                    nextTV.text = "Next" // Change text back to "Next" for other pages

                    skipTv.text="skip"
                }
            }
        })
        nextTV.setOnClickListener {
            if(introSlideViewPager.currentItem+1<introSliderAdapter.itemCount){
                introSlideViewPager.currentItem +=1

            }else{
                completeOnboarding()
            }
        }
        skipTv.setOnClickListener {
            completeOnboarding()
        }
    }
    private fun completeOnboarding() {
        preferences.edit().putBoolean("onboarding_complete", true).apply()
        Intent(applicationContext, MainActivity::class.java).also {
            startActivity(it)
        }
        finish()
    }

    private fun setUpIndicators(){
        val indicators= arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
        val layoutParams:LinearLayout.LayoutParams=
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8,0,8,0)
        for (i in indicators.indices){
            indicators[i]= ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams=layoutParams
            }
            indicatorContainer.addView(indicators[i])
        }
    }
    private fun setCurrentIndicator(index:Int){
        val childCount=indicatorContainer.childCount
        for (i in 0 until childCount){
            val imageView=indicatorContainer[i] as ImageView
            if (i==index){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            }else{
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }
}