package com.example.map_mid_term.activities

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.map_mid_term.fragments.ProfileFragment
import com.example.map_mid_term.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Hubungkan tombol
        val btnProfile = findViewById<Button>(R.id.btnProfile)
        val btnSavings = findViewById<Button>(R.id.btnSavings)
        val btnLoans = findViewById<Button>(R.id.btnLoans)
        val btnLocation = findViewById<Button>(R.id.btnLocation)
        val btnCamera = findViewById<Button>(R.id.btnCamera)

        // Nanti ini bisa diarahkan ke fragment / activity lain
        btnProfile.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .commit()
        }

        btnSavings.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SavingsFragment())
                .commit()
        }

        btnLoans.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LoansFragment())
                .commit()
        }

        btnLocation.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LocationFragment())
                .commit()
        }

        btnCamera.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CameraFragment())
                .commit()
        }
    }
}
