package com.example.map_mid_term.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.R

class AdminLoanDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_loan_detail)

        // Toolbar back
        supportActionBar?.apply {
            title = "Detail Pinjaman"
            setDisplayHomeAsUpEnabled(true)
        }

        // Ambil data dari intent
        val loanId = intent.getStringExtra("loanId")
        val memberId = intent.getStringExtra("memberId")
        val amount = intent.getDoubleExtra("amount", 0.0)
        val tenor = intent.getDoubleExtra("tenor", 0.0)
        val status = intent.getStringExtra("status")

        // Binding elemen UI
        findViewById<TextView>(R.id.tvLoanId).text = "ID Pinjaman: $loanId"
        findViewById<TextView>(R.id.tvMemberId).text = "Anggota: $memberId"
        findViewById<TextView>(R.id.tvAmount).text = "Jumlah: Rp $amount"
        findViewById<TextView>(R.id.tvTenor).text = "Tenor: $tenor Bulan"
        findViewById<TextView>(R.id.tvStatus).text = "Status: $status"

        findViewById<Button>(R.id.btnAccept).setOnClickListener {
            Toast.makeText(this, "Pengajuan $loanId diterima ✅", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.btnDecline).setOnClickListener {
            Toast.makeText(this, "Pengajuan $loanId ditolak ❌", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
