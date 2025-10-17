package com.example.map_mid_term.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.adapters.TransactionAdminAdapter
import com.example.map_mid_term.model.DummyData
import com.google.android.material.appbar.MaterialToolbar

class TransactionListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.title = "Data Transaksi"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerTransactions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TransactionAdminAdapter(DummyData.adminTransactions.toMutableList())
    }
}
