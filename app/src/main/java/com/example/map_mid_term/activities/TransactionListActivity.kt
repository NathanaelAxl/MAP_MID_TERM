package com.example.map_mid_term.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.adapters.TransactionAdapter // Ganti ke Adapter yang sudah fix
import com.example.map_mid_term.data.model.Transaction
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TransactionListActivity : AppCompatActivity() {

    // Gunakan TransactionAdapter yang sudah support gambar Base64
    private lateinit var adapter: TransactionAdapter
    private val transactionList = ArrayList<Transaction>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.title = "Semua Transaksi"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerTransactions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter
        adapter = TransactionAdapter(transactionList)
        recyclerView.adapter = adapter

        fetchTransactions()
    }

    private fun fetchTransactions() {
        // Ambil semua transaksi dari semua user (karena ini Admin)
        db.collection("transactions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                transactionList.clear()
                for (doc in documents) {
                    val trx = doc.toObject(Transaction::class.java)
                    trx.id = doc.id
                    transactionList.add(trx)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memuat data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}