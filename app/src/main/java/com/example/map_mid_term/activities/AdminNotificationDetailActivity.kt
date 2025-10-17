package com.example.map_mid_term.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.R
import com.google.android.material.appbar.MaterialToolbar

class AdminNotificationDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_notification_detail)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.title = "Detail Pengajuan Pinjaman"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val tvLoanId = findViewById<TextView>(R.id.tvLoanId)
        val tvMemberId = findViewById<TextView>(R.id.tvMemberId)
        val tvAmount = findViewById<TextView>(R.id.tvAmount)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)

        val btnAccept = findViewById<Button>(R.id.btnAccept)
        val btnDecline = findViewById<Button>(R.id.btnDecline)

        val loanId = intent.getStringExtra("loanId")
        val memberId = intent.getStringExtra("memberId")
        val amount = intent.getDoubleExtra("amount", 0.0)
        val status = intent.getStringExtra("status")

        tvLoanId.text = "ID Pinjaman: $loanId"
        tvMemberId.text = "Anggota: $memberId"
        tvAmount.text = "Jumlah: Rp $amount"
        tvStatus.text = "Status: $status"

        btnAccept.setOnClickListener {
            Toast.makeText(this, "Pengajuan $loanId disetujui ✅", Toast.LENGTH_SHORT).show()
        }

        btnDecline.setOnClickListener {
            Toast.makeText(this, "Pengajuan $loanId ditolak ❌", Toast.LENGTH_SHORT).show()
        }
    }
}
