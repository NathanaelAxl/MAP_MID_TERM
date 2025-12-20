package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.ActivityMainBinding
import com.example.map_mid_term.model.DummyData
import com.google.android.material.navigation.NavigationView

class       MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    companion object {
        var memberId: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Ambil memberId dari LoginActivity
        memberId = intent.getStringExtra("memberId")

        // ✅ Tampilkan pesan selamat datang kalau berhasil login
        val member = DummyData.members.find { it.id == memberId }
        member?.let {
            Toast.makeText(this, "Selamat datang, ${it.name}!", Toast.LENGTH_SHORT).show()
        }

        // --- NAVIGATION SETUP ---
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)

        // Tentukan top-level fragment
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.transactionFragment,
                R.id.historyFragment,
                R.id.profileFragment
            ),
            drawerLayout
        )

        // Hubungkan toolbar dan bottom nav
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)

        // --- HANDLE BOTTOM NAVIGATION ---
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> navController.navigate(R.id.homeFragment)
                R.id.transactionFragment -> navController.navigate(R.id.transactionFragment)
                R.id.historyFragment -> navController.navigate(R.id.historyFragment)
                R.id.profileFragment -> navController.navigate(R.id.profileFragment)
                else -> false
            }
            true
        }

        // --- HANDLE DRAWER MENU ---
        val navView: NavigationView = binding.navigationView
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment -> navController.navigate(R.id.homeFragment)
                R.id.activeLoanFragment -> navController.navigate(R.id.activeLoanFragment)
                R.id.profileFragment -> navController.navigate(R.id.profileFragment)
                R.id.machineLearningFragment -> navController.navigate(R.id.machineLearningFragment)
                R.id.menu_logout -> {
                    // 🔹 Hapus data session (optional)
                    memberId = null
                    // 🔹 Balik ke LoginActivity
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
