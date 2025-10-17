package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.R
import com.google.android.material.card.MaterialCardView

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val adminName = "Admin Koperasi"
        findViewById<TextView>(R.id.tvAdminWelcome).text = "Halo, $adminName 👋"

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
            startActivity(Intent(this, LoanListActivity::class.java))
        }

        cardTransactions.setOnClickListener {
            startActivity(Intent(this, TransactionListActivity::class.java))
        }

        cardReports.setOnClickListener {
            startActivity(Intent(this, AdminReportActivity::class.java))
        }

        cardNotifications.setOnClickListener {
            startActivity(Intent(this, AdminNotificationActivity::class.java))
        }

        cardCash.setOnClickListener {
            startActivity(Intent(this, CashManagementActivity::class.java))
        }
    }
}
