package com.example.map_mid_term.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map_mid_term.adapters.TransactionAdminAdapter
import com.example.map_mid_term.data.model.Transaction
import com.example.map_mid_term.databinding.ActivityAdminTransactionBinding // Pastikan nama XML-mu benar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminTransactionBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: TransactionAdminAdapter
    private var transactionList = ArrayList<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadTransactions()
        setupListeners()
    }

    private fun setupListeners() {
        // Tombol Back (Pastikan ID di XML adalah toolbar atau btnBack)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Opsional: Jika pakai swipe refresh layout
        // binding.swipeRefresh.setOnRefreshListener { loadTransactions() }
    }

    private fun setupRecyclerView() {
        // Inisialisasi Adapter dengan Callback Delete
        adapter = TransactionAdminAdapter(transactionList) { transaction ->
            // Ini kode yang jalan saat tombol "Hapus" di adapter ditekan
            showDeleteConfirmationDialog(transaction)
        }

        binding.rvTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.adapter = adapter
    }

    private fun loadTransactions() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmptyState.visibility = View.GONE

        // Ambil data transaksi, urutkan dari yang terbaru (DESCENDING)
        db.collection("transactions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE
                transactionList.clear()

                for (document in documents) {
                    try {
                        // Convert document ke object Transaction
                        val transaction = document.toObject(Transaction::class.java)
                        transaction.id = document.id // Penting: Simpan ID dokumen biar bisa dihapus nanti
                        transactionList.add(transaction)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (transactionList.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                } else {
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Gagal memuat data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // --- FITUR HAPUS DATA ---

    private fun showDeleteConfirmationDialog(transaction: Transaction) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Transaksi?")
            .setMessage("Data transaksi sebesar Rp ${transaction.amount} akan dihapus permanen. Lanjutkan?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deleteTransactionFromFirestore(transaction)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteTransactionFromFirestore(transaction: Transaction) {
        binding.progressBar.visibility = View.VISIBLE

        db.collection("transactions").document(transaction.id)
            .delete()
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Transaksi berhasil dihapus", Toast.LENGTH_SHORT).show()

                // Update tampilan List secara efisien tanpa loading ulang dari server
                adapter.removeItem(transaction)

                // Cek jika list jadi kosong setelah dihapus
                if (adapter.itemCount == 0) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Gagal menghapus: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}