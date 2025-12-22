package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.databinding.ActivityAdminDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // --- LOGIC TOMBOL BACK (LOGOUT) ---
        binding.btnLogoutAdmin.setOnClickListener {
            // 1. Sign Out dari Firebase
            FirebaseAuth.getInstance().signOut()

            // 2. Arahkan kembali ke Login Activity
            val intent = Intent(this, LoginActivity::class.java)
            // Clear history agar tidak bisa back ke dashboard lagi
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // --- MENU DASHBOARD (TETAP ADA) ---

        // 1. Data Anggota
        binding.menuDataAnggota.setOnClickListener {
            try {
                startActivity(Intent(this, MemberListActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Menu Anggota belum siap", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. Data Pinjaman
        binding.menuDataPinjaman.setOnClickListener {
            try {
                startActivity(Intent(this, LoanListActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Menu Pinjaman belum siap", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. Transaksi
        binding.menuTransaksi.setOnClickListener {
            try {
                startActivity(Intent(this, TransactionListActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Menu Transaksi belum siap", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Laporan
        binding.menuLaporan.setOnClickListener {
            try {
                startActivity(Intent(this, AdminReportActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Menu Laporan belum siap", Toast.LENGTH_SHORT).show()
            }
        }

        // 5. Notifikasi
        binding.menuNotifikasi.setOnClickListener {
            try {
                startActivity(Intent(this, AdminNotificationActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Menu Notifikasi belum siap", Toast.LENGTH_SHORT).show()
            }
        }

        // 6. Buat Pengumuman
        binding.menuKas.setOnClickListener {
            // Arahkan ke Activity baru
            startActivity(Intent(this, AdminAnnouncementActivity::class.java))
        }
    }
}