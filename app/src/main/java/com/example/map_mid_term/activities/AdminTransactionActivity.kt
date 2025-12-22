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

class AdminTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionListBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: AdminTransactionAdapter
    private var transactionList = ArrayList<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupRecyclerView()
        loadTransactions()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        // Fix: Masukkan lambda function sebagai parameter kedua
        adapter = AdminTransactionAdapter(transactionList) { transaction ->
            showDeleteConfirmationDialog(transaction)
        }

        binding.rvTransactionList.layoutManager = LinearLayoutManager(this)
        binding.rvTransactionList.adapter = adapter
    }

    private fun loadTransactions() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        db.collection("transactions")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE

                val newList = ArrayList<Transaction>()
                for (document in documents) {
                    try {
                        val transaction = document.toObject(Transaction::class.java)
                        transaction.id = document.id
                        newList.add(transaction)
                    } catch (e: Exception) { e.printStackTrace() }
                }

                adapter.updateData(newList)

                if (newList.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Gagal: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmationDialog(transaction: Transaction) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Transaksi?")
            .setMessage("Hapus permanen Rp ${"%,.0f".format(transaction.amount)}?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deleteTransactionFromFirestore(transaction)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun deleteTransactionFromFirestore(transaction: Transaction) {
        binding.progressBar.visibility = View.VISIBLE
        db.collection("transactions").document(transaction.id)
            .delete()
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                adapter.removeItem(transaction) // Fix: removeItem sudah ada sekarang
                if (adapter.itemCount == 0) binding.tvEmpty.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}