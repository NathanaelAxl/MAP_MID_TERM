package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Hide status bar & action bar
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
        supportActionBar?.hide()

        val logoContainer = findViewById<View>(R.id.logoContainer)

        // 1. MULAI FADE IN (Durasi 1.5 detik sesuai XML)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        logoContainer.startAnimation(fadeIn)

        // 2. TUNGGU SEBENTAR, LALU FADE OUT
        // Kita set delay 2000ms (biar logo sempat terlihat jelas setelah fade in selesai)
        Handler(Looper.getMainLooper()).postDelayed({
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
            logoContainer.startAnimation(fadeOut)

            // Saat fade out mulai, kita buat container jadi invisible setelah animasi selesai
            // (Ini trik visual biar gak nge-blink)
            logoContainer.visibility = View.GONE
        }, 2500) // 1500ms (fadeIn) + 1000ms (tunggu) = 2500ms mulai fade out

        // 3. PINDAH HALAMAN SETELAH SEMUA SELESAI
        // Total waktu: 2500ms + 1000ms (durasi fade out) = 3500ms
        Handler(Looper.getMainLooper()).postDelayed({

            // --- LOGIKA NAVIGASI ---
            // Sesuai request kamu: Ingin selalu masuk Login dulu.
            // Maka kita arahkan paksa ke LoginActivity.

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // Transisi Activity (Biar mulus perpindahannya)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            finish()
        }, 3500)
    }
}