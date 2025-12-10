package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.adapters.AdminNotificationAdapter
import com.example.map_mid_term.model.AdminNotification
import com.google.android.material.appbar.MaterialToolbar

class AdminNotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_notification)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.title = "Notifikasi Pengajuan"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerAdminNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val notifications = listOf(
            AdminNotification("L001", "M001", 1000000.0, "Sedang Diproses"),
            AdminNotification("L002", "M002", 2000000.0, "Menunggu Persetujuan"),
            AdminNotification("L003", "M003", 500000.0, "Disetujui")
        )

        recyclerView.adapter = AdminNotificationAdapter(notifications) { notif ->
            val intent = Intent(this, AdminNotificationDetailActivity::class.java)
            intent.putExtra("loanId", notif.loanId)
            intent.putExtra("memberId", notif.memberId)
            intent.putExtra("amount", notif.amount)
            intent.putExtra("status", notif.status)
            startActivity(intent)
        }
    }
}
