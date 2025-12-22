package com.example.map_mid_term

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. Inisialisasi Firebase
        FirebaseApp.initializeApp(this)

        // 2. Setup App Check
        val firebaseAppCheck = FirebaseAppCheck.getInstance()

        // Kita paksa pakai Debug Factory dulu tanpa logika if/else yang rumit
        // agar kita yakin 100% mode debug jalan.
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )

        // --- BAGIAN INI YANG AKAN MEMAKSA TOKEN MUNCUL ---
        // Kita baca langsung dari tempat penyimpanannya
        try {
            val prefs = getSharedPreferences("com.google.firebase.appcheck.debug.store", Context.MODE_PRIVATE)
            val token = prefs.getString("com.google.firebase.appcheck.debug.DEBUG_SECRET", "Token belum dibuat oleh Firebase")

            // Log dengan Level Error (E) supaya warnanya MERAH dan mencolok
            Log.e("!!!_TOKEN_SAYA_!!!", "========================================")
            Log.e("!!!_TOKEN_SAYA_!!!", "INI TOKEN DEBUG KAMU (COPY PASTE INI):")
            Log.e("!!!_TOKEN_SAYA_!!!", token.toString())
            Log.e("!!!_TOKEN_SAYA_!!!", "========================================")

        } catch (e: Exception) {
            Log.e("!!!_TOKEN_SAYA_!!!", "Gagal membaca token: ${e.message}")
        }
        // -------------------------------------------------
    }
}