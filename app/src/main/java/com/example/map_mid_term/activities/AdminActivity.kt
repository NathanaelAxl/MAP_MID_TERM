package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.title = "Dashboard Admin"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        findViewById<MaterialCardView>(R.id.cardManageMembers).setOnClickListener {
            startActivity(Intent(this, MemberListActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.cardManageLoans).setOnClickListener {
            startActivity(Intent(this, LoanListActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.cardTransactions).setOnClickListener {
            startActivity(Intent(this, TransactionListActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.cardReports).setOnClickListener {
            startActivity(Intent(this, AdminReportActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.cardNotifications).setOnClickListener {
            startActivity(Intent(this, AdminNotificationActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.cardCash).setOnClickListener {
            startActivity(Intent(this, CashManagementActivity::class.java))
        }
    }
}
