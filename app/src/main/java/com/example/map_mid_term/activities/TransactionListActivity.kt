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
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        // Sekarang Adapter menerima 2 parameter: List dan Lambda Function
        adapter = AdminTransactionAdapter(transactionList) { transaction ->
            showDeleteConfirmationDialog(transaction)
        }

        binding.rvTransactionList.apply {
            layoutManager = LinearLayoutManager(this@TransactionListActivity)
            adapter = this@TransactionListActivity.adapter
        }
    }

    // --- REVISI: MENGGUNAKAN SNAPSHOT LISTENER (REALTIME) ---
    private fun fetchTransactions() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        // Ganti .get() dengan .addSnapshotListener
        db.collection("transactions")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { documents, error ->
                binding.progressBar.visibility = View.GONE

                if (error != null) {
                    Toast.makeText(this, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (documents != null) {
                    transactionList.clear() // Hapus data lama di list lokal

                    val newList = ArrayList<Transaction>()
                    for (doc in documents) {
                        try {
                            val trx = doc.toObject(Transaction::class.java)
                            // Manual set ID karena @Exclude di model
                            trx.id = doc.id
                            newList.add(trx)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    // Update Adapter
                    adapter.updateData(newList)

                    // Logic Tampilan Kosong
                    if (newList.isEmpty()) {
                        binding.tvEmpty.visibility = View.VISIBLE
                    } else {
                        binding.tvEmpty.visibility = View.GONE
                    }
                }
            }
    }

    private fun showDeleteConfirmationDialog(transaction: Transaction) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Transaksi?")
            .setMessage("Yakin ingin menghapus transaksi senilai Rp ${"%,.0f".format(transaction.amount)}?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deleteTransaction(transaction)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun deleteTransaction(transaction: Transaction) {
        binding.progressBar.visibility = View.VISIBLE

        db.collection("transactions").document(transaction.id)
            .delete()
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Transaksi dihapus", Toast.LENGTH_SHORT).show()

                // Panggil removeItem yang sudah kita buat di Adapter
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