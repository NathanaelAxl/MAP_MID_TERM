package com.example.map_mid_term.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map_mid_term.adapters.AdminTransactionAdapter
import com.example.map_mid_term.data.model.Transaction
import com.example.map_mid_term.databinding.ActivityTransactionListBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TransactionListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionListBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: AdminTransactionAdapter
    // Kita simpan list di level Activity agar mudah dikelola
    private var transactionList = ArrayList<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        fetchTransactions()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        // PERBAIKAN DI SINI:
        // Kita kirimkan lambda function (kurung kurawal) sebagai parameter kedua 'onDeleteClick'
        adapter = AdminTransactionAdapter(transactionList) { transaction ->
            // Kode ini akan jalan saat tombol sampah ditekan di Adapter
            showDeleteConfirmationDialog(transaction)
        }

        binding.rvTransactionList.apply {
            layoutManager = LinearLayoutManager(this@TransactionListActivity)
            adapter = this@TransactionListActivity.adapter
        }
    }

    private fun fetchTransactions() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        db.collection("transactions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE
                transactionList.clear()

                for (doc in documents) {
                    try {
                        val trx = doc.toObject(Transaction::class.java)
                        trx.id = doc.id
                        transactionList.add(trx)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Beritahu adapter bahwa data berubah
                adapter.notifyDataSetChanged()

                if (transactionList.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
    }

    // --- LOGIKA HAPUS DATA ---

    private fun showDeleteConfirmationDialog(transaction: Transaction) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Transaksi?")
            .setMessage("Yakin ingin menghapus transaksi senilai Rp ${"%,.0f".format(transaction.amount)}?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deleteTransaction(transaction)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteTransaction(transaction: Transaction) {
        binding.progressBar.visibility = View.VISIBLE

        db.collection("transactions").document(transaction.id)
            .delete()
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Transaksi berhasil dihapus", Toast.LENGTH_SHORT).show()

                // Hapus dari tampilan tanpa reload internet (Biar cepat & hemat kuota)
                adapter.removeItem(transaction)

                if (adapter.itemCount == 0) {
                    binding.tvEmpty.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Gagal menghapus", Toast.LENGTH_SHORT).show()
            }
    }
}