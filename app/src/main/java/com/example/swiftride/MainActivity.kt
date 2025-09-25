package com.example.swiftride

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            val openFragment = intent.getStringExtra("openFragment")

            val startFragment: Fragment = when (openFragment) {
                "home" -> HomeFragment()       // 👈 explicit case for home
                "booking" -> BookingFragment()
                "dashboard" -> DashboardFragment()
                "cars" -> CarListFragment()
                else -> HomeFragment()         // 👈 fallback still goes to Home
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, startFragment)
                .commit()

            // Highlight correct tab in bottom nav
            when (openFragment) {
                "home" -> bottomNav.selectedItemId = R.id.nav_home
                "booking" -> bottomNav.selectedItemId = R.id.nav_bookings
                "dashboard" -> bottomNav.selectedItemId = R.id.nav_dashboard
                "cars" -> bottomNav.selectedItemId = R.id.nav_cars
                else -> bottomNav.selectedItemId = R.id.nav_home
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_bookings -> BookingFragment()
                R.id.nav_cars -> CarListFragment()
                R.id.nav_dashboard -> DashboardFragment()
                else -> HomeFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit()

            true
        }

        // Hide bottom nav for special fragments if needed
        supportFragmentManager.addOnBackStackChangedListener {
            toggleBottomNav()
        }
    }

    private fun toggleBottomNav() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is CarListFragment) {
            bottomNav.visibility = BottomNavigationView.GONE
        } else {
            bottomNav.visibility = BottomNavigationView.VISIBLE
        }
    }
}
