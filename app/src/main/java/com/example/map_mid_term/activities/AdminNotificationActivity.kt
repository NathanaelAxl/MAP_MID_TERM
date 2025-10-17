package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.adapters.AdminNotificationAdapter
import com.example.map_mid_term.model.Loan

class AdminNotificationActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminNotificationAdapter
    private lateinit var loanRequests: MutableList<Loan>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_notification)

        // Toolbar back
        supportActionBar?.apply {
            title = "Notifikasi Pengajuan"
            setDisplayHomeAsUpEnabled(true)
        }

        // Dummy data (ganti nanti pakai data nyata)
        loanRequests = mutableListOf(
            Loan("L001", "M001", 1000000.0, 3.0, "Sedang Diproses"),
            Loan("L002", "M002", 2000000.0, 6.0, "Menunggu Persetujuan"),
            Loan("L003", "M003", 500000.0, 1.5, "Disetujui")
        )

        recyclerView = findViewById(R.id.recyclerNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AdminNotificationAdapter(loanRequests) { loan ->
            val intent = Intent(this, AdminLoanDetailActivity::class.java)
            intent.putExtra("loanId", loan.id)
            intent.putExtra("memberId", loan.memberId)
            intent.putExtra("amount", loan.amount)
            intent.putExtra("tenor", loan.interestRate)
            intent.putExtra("status", loan.status)
            startActivity(intent)
        }


        recyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
