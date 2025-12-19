package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        // Setup Drawer Toggle (Tombol Hamburger)
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        updateDrawerHeader()

        // Atur tampilan Toolbar & BottomNav
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.transactionFragment,
                R.id.profileFragment, R.id.historyFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    supportActionBar?.show()
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    // --- BAGIAN INI YANG SAYA PERBAIKI SESUAI XML KAMU ---
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Ambil NavController untuk navigasi
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        when (item.itemId) {
            R.id.homeFragment -> {
                // Arahkan ke Home
                navController.navigate(R.id.homeFragment)
            }
            R.id.activeLoanFragment -> {
                // Arahkan ke Fragment Pinjaman Aktif (Pastikan ID ini ada di nav_graph)
                // Jika belum ada di nav_graph, baris ini akan error saat runtime
                try { navController.navigate(R.id.activeLoanFragment) } catch(e: Exception) {
                    Toast.makeText(this, "Halaman belum dibuat", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.profileFragment -> {
                navController.navigate(R.id.profileFragment)
            }
            R.id.machineLearningFragment -> {
                // Ini tadi nav_ml_project, sekarang disesuaikan
                Toast.makeText(this, "Fitur Medical Check (ML)", Toast.LENGTH_SHORT).show()
                // navController.navigate(R.id.machineLearningFragment) // Uncomment jika sudah ada di graph
            }
            R.id.menu_logout -> {
                // Ini tadi nav_logout, sekarang disesuaikan
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun updateDrawerHeader() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Pastikan layout nav_header_main.xml memiliki TextView dengan ID ini
            val headerView = binding.navView.getHeaderView(0)
            val tvName = headerView.findViewById<TextView>(R.id.tv_header_name)
            val tvEmail = headerView.findViewById<TextView>(R.id.tv_header_email)

            db.collection("members").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        tvName.text = document.getString("name") ?: "User"
                        tvEmail.text = document.getString("email") ?: auth.currentUser?.email
                    }
                }
        }
    }

    companion object {
        var memberId: String? = null
    }
}