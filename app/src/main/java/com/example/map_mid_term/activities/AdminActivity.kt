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
        // 1. Data Anggota
        binding.menuDataAnggota.setOnClickListener {
            startActivity(Intent(this, MemberListActivity::class.java))
        }

        // 2. Data Pinjaman (Nanti diganti ke LoanListActivity jika sudah siap)
        binding.menuDataPinjaman.setOnClickListener {
            startActivity(Intent(this, LoanListActivity::class.java))
        }

        // 3. Transaksi (Nanti diganti ke TransactionListActivity jika sudah siap)
        binding.menuTransaksi.setOnClickListener {
            startActivity(Intent(this, TransactionListActivity::class.java))
        }

        // 4. LAPORAN (INI YANG KITA PERBAIKI)
        binding.menuLaporan.setOnClickListener {
            // Dulu: Toast "Segera Hadir"
            // Sekarang: Buka AdminReportActivity
            startActivity(Intent(this, AdminReportActivity::class.java))
        }

        // 5. Notifikasi
        binding.menuNotifikasi.setOnClickListener {
            startActivity(Intent(this, AdminNotificationActivity::class.java))
        }

        // Logout
        binding.btnLogoutAdmin.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}