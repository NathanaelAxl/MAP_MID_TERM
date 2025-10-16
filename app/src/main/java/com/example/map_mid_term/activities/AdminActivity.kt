package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar   // ✅ tambahkan ini
import com.example.map_mid_term.R
import com.google.android.material.card.MaterialCardView

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // === Toolbar Setup ===
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Dashboard Admin"

        // === Header Admin ===
        val adminName = "Admin Koperasi"
        val tvAdminWelcome = findViewById<TextView>(R.id.tvAdminWelcome)
        tvAdminWelcome.text = "Halo, $adminName 👋"

        // === Kartu Navigasi Dashboard ===
        val cardMembers = findViewById<MaterialCardView>(R.id.cardManageMembers)
        val cardLoans = findViewById<MaterialCardView>(R.id.cardManageLoans)
        val cardTransactions = findViewById<MaterialCardView>(R.id.cardTransactions)
        val cardReports = findViewById<MaterialCardView>(R.id.cardReports)
        val cardNotifications = findViewById<MaterialCardView>(R.id.cardNotifications)
        val cardCash = findViewById<MaterialCardView>(R.id.cardCash)

        cardMembers.setOnClickListener {
            startActivity(Intent(this, MemberListActivity::class.java))
        }
        cardLoans.setOnClickListener {
            Toast.makeText(this, "Kelola Data Pinjaman", Toast.LENGTH_SHORT).show()
        }
        cardTransactions.setOnClickListener {
            Toast.makeText(this, "Transaksi Koperasi", Toast.LENGTH_SHORT).show()
        }
        cardReports.setOnClickListener {
            Toast.makeText(this, "Laporan Keuangan", Toast.LENGTH_SHORT).show()
        }
        cardNotifications.setOnClickListener {
            Toast.makeText(this, "Kirim Notifikasi", Toast.LENGTH_SHORT).show()
        }
        cardCash.setOnClickListener {
            Toast.makeText(this, "Kelola Kas Koperasi", Toast.LENGTH_SHORT).show()
        }
        cardLoans.setOnClickListener {
            startActivity(Intent(this, LoanListActivity::class.java))
        }
        cardTransactions.setOnClickListener {
            startActivity(Intent(this, TransactionListActivity::class.java))
        }

    }

    // ✅ Letakkan di luar onCreate(), bukan di dalam
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }


}
