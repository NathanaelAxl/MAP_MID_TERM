package com.example.map_mid_term.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.adapters.TransactionAdminAdapter
import com.example.map_mid_term.model.DummyData

class TransactionListActivity : AppCompatActivity() {

    private lateinit var adapter: TransactionAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        supportActionBar?.title = "Data Transaksi"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerTransactions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TransactionAdminAdapter(DummyData.adminTransactions.toMutableList())

        recyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
