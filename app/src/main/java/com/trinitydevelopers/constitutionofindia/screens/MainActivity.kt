package com.trinitydevelopers.constitutionofindia.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.databinding.ActivityMainBinding
import com.trinitydevelopers.constitutionofindia.fragments.HomeFragment
import com.trinitydevelopers.constitutionofindia.fragments.NotesFragment

class  MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var preferences: SharedPreferences
    private lateinit var navigationView: NavigationView
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        // Load saved theme preference
        preferences = getSharedPreferences("settings", MODE_PRIVATE)
        if (preferences.getBoolean("night_mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
         navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


        // Calculate 50% of the screen width
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val drawerWidth = (screenWidth * 0.7).toInt()
        val layoutParams = navigationView.layoutParams
        layoutParams.width = drawerWidth
        navigationView.layoutParams = layoutParams

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.Deep_Blue)


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
            setToolbarVisibility(true)
        } else {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            setToolbarVisibility(currentFragment is HomeFragment)
        }


        // Add back stack listener
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            setToolbarVisibility(currentFragment is HomeFragment)
            updateNavigationView(currentFragment)

    }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                replaceFragment(HomeFragment())
                navigationView.setCheckedItem(R.id.nav_home)
            }
            R.id.nav_notification -> {
                startActivity(Intent(this, NotificationActivity::class.java))
                navigationView.setCheckedItem(R.id.nav_home)
            }
            R.id.nav_notes -> {
                replaceFragment(NotesFragment())
                navigationView.setCheckedItem(R.id.nav_notes)
            }
            R.id.nav_nightMode -> {
                toggleNightMode()
                navigationView.setCheckedItem(R.id.nav_nightMode)
            }
            R.id.nav_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                navigationView.setCheckedItem(R.id.nav_home)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setToolbarVisibility(visible: Boolean) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.visibility = if (visible) View.VISIBLE else View.GONE
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
    private fun toggleNightMode() {
        val nightMode = AppCompatDelegate.getDefaultNightMode()
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            preferences.edit().putBoolean("night_mode", false).apply()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            preferences.edit().putBoolean("night_mode", true).apply()
        }
        recreate() // Restart the activity to apply the theme change
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
            // If back to home, update the NavigationView
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            updateNavigationView(currentFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        // Ensure NavigationView is updated correctly
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        updateNavigationView(currentFragment)
    }

    private fun updateNavigationView(currentFragment: Fragment?) {
        when (currentFragment) {
            is HomeFragment -> navigationView.setCheckedItem(R.id.nav_home)
            is NotesFragment -> navigationView.setCheckedItem(R.id.nav_notes)
            else -> navigationView.setCheckedItem(R.id.nav_home)
        }
    }
}