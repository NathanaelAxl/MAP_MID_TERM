package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.model.DummyData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // === Ambil data login dari intent ===
        val memberId = intent.getStringExtra("memberId")
        val member = DummyData.members.find { it.id == memberId }
        member?.let {
            Toast.makeText(this, "Selamat datang, ${it.name}!", Toast.LENGTH_SHORT).show()
        }

        // === Setup Navigation ===
        drawerLayout = findViewById(R.id.drawerLayout)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // === Toolbar ===
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // === Top Level Destinations (untuk tombol hamburger) ===
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.savingsFragment,
                R.id.loansFragment,
                R.id.profileFragment
            ),
            drawerLayout // ← ini penting supaya muncul hamburger icon
        )

        // === Sinkronisasi Toolbar dengan NavController ===
        setupActionBarWithNavController(navController, appBarConfiguration)

        // === Bottom Navigation ===
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setupWithNavController(navController)

        // === Drawer Navigation ===
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setupWithNavController(navController)

        // === Logout manual ===
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }
    }

    // === Support tombol hamburger ===
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
