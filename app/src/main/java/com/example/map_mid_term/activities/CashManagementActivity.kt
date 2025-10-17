package com.example.map_mid_term.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.R

class CashManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_management)

        supportActionBar?.apply {
            title = "Kelola Kas Koperasi"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
