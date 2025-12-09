package com.example.map_mid_term.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Navigasi Bawah (Bottom Navigation)
        // Pastikan ID 'fragmentContainerView' ada di layout activity_main.xml
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        // Sembunyikan BottomNav di halaman tertentu (opsional)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.transactionFragment,
                R.id.profileFragment, R.id.historyFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
                else -> {
                    // Sembunyikan di halaman detail/form agar lebih luas
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        // Variabel global untuk menyimpan ID user (bisa diakses dari fragment manapun)
        var memberId: String? = null
    }
}