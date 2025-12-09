package com.example.map_mid_term.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.adapters.LoanAdapter
import com.example.map_mid_term.model.Loan
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class LoanListActivity : AppCompatActivity() {

    private lateinit var adapter: LoanAdapter
    private val loans = ArrayList<Loan>() // Gunakan ArrayList kosong, bukan DummyData
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loans)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Data Pinjaman"

        val rvLoans = findViewById<RecyclerView>(R.id.recyclerLoans)
        val fabAddLoan = findViewById<FloatingActionButton>(R.id.fabAddLoan)

        // Setup Adapter
        adapter = LoanAdapter(loans,
            onEdit = { loan -> showLoanDialog("Edit Pinjaman", loan) },
            onDelete = { loan -> deleteLoan(loan) }
        )

        rvLoans.layoutManager = LinearLayoutManager(this)
        rvLoans.adapter = adapter

        fabAddLoan.setOnClickListener {
            showLoanDialog("Tambah Pinjaman Manual", null)
        }

        // AMBIL DATA DARI DATABASE
        fetchLoans()
    }

    private fun fetchLoans() {
        // Ambil data dari koleksi 'loan_applications' yang statusnya 'approved' (Aktif)
        db.collection("loan_applications")
            .whereEqualTo("status", "approved")
            .get()
            .addOnSuccessListener { documents ->
                loans.clear()
                for (doc in documents) {
                    // Mapping manual dari Firestore ke Model Loan
                    val id = doc.id
                    val userId = doc.getString("userId") ?: ""
                    val amount = doc.getDouble("amount") ?: 0.0
                    val tenor = doc.getLong("tenor")?.toInt() ?: 0
                    val status = doc.getString("status") ?: "Aktif"

                    // Asumsi Model Loan kamu: Loan(id, memberId, amount, interest, status, duration)
                    // Interest kita anggap default 1.5 jika tidak ada di DB
                    loans.add(Loan(id, userId, amount, 1.5, status, tenor))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteLoan(loan: Loan) {
        db.collection("loan_applications").document(loan.id)
            .delete()
            .addOnSuccessListener {
                loans.remove(loan)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Pinjaman dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menghapus", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoanDialog(title: String, data: Loan?) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_loan, null)
        val etMemberId = view.findViewById<EditText>(R.id.etMemberId)
        val etAmount = view.findViewById<EditText>(R.id.etLoanAmount)
        val etTenor = view.findViewById<EditText>(R.id.etLoanTenor)

        // Isi form jika edit
        data?.let {
            etMemberId.setText(it.memberId)
            etAmount.setText(it.amount.toString())
            etTenor.setText(it.durationMonths.toString())
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setPositiveButton("Simpan") { _, _ ->
                val memberId = etMemberId.text.toString().trim()
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
                val tenor = etTenor.text.toString().toIntOrNull() ?: 0

                if (memberId.isEmpty()) {
                    Toast.makeText(this, "Member ID Wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Simpan ke Firestore
                val loanData = hashMapOf(
                    "userId" to memberId, // Kita pakai input manual sbg userId
                    "amount" to amount,
                    "tenor" to tenor,
                    "status" to "approved", // Langsung aktif karena input admin
                    "applicationDate" to System.currentTimeMillis()
                )

                if (data == null) {
                    // Tambah Baru
                    db.collection("loan_applications").add(loanData)
                        .addOnSuccessListener {
                            fetchLoans() // Refresh list
                            Toast.makeText(this, "Berhasil disimpan", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Update
                    db.collection("loan_applications").document(data.id).update(loanData as Map<String, Any>)
                        .addOnSuccessListener {
                            fetchLoans()
                            Toast.makeText(this, "Berhasil diupdate", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}