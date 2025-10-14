package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.map_mid_term.R
import com.example.map_mid_term.fragments.*
import com.example.map_mid_term.model.DummyData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ Ambil data member dari intent dan tampilkan nama di Toast
        val memberId = intent.getStringExtra("memberId")
        val member = DummyData.members.find { it.id == memberId }

        member?.let {
            Toast.makeText(this, "Selamat datang, ${it.name}!", Toast.LENGTH_SHORT).show()
        }

        // Hubungkan komponen UI
        drawerLayout = findViewById(R.id.drawerLayout)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        navigationView = findViewById(R.id.navigationView)

        // Setup toolbar dengan tombol hamburger
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Tampilkan fragment awal (default = Simpanan)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SavingsFragment())
            .commit()

        // ======== BOTTOM NAVIGATION =========
        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_savings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, SavingsFragment())
                        .commit()
                    true
                }
                R.id.nav_camera -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, CameraFragment())
                        .commit()
                    true
                }
                R.id.nav_location -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, LocationFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

        // ======== NAVIGATION DRAWER =========
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, SavingsFragment())
                        .commit()
                }
                R.id.menu_loans -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, LoansFragment())
                        .commit()
                }
                R.id.menu_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ProfileFragment())
                        .commit()
                }
                R.id.menu_logout -> {
                    // contoh logout ke LoginActivity
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }
}
