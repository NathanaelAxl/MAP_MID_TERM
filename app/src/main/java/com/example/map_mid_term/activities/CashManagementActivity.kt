package com.example.map_mid_term.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.R
import com.google.android.material.appbar.MaterialToolbar

class CashManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_management)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}
