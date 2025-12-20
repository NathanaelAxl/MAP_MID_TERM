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
import com.example.map_mid_term.data.model.LoanApplication
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class LoanListActivity : AppCompatActivity() {

    private lateinit var adapter: LoanAdapter
    private val loans = ArrayList<LoanApplication>()
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

        fetchLoans()
    }

    private fun fetchLoans() {
        db.collection("loan_applications")
            .whereEqualTo("status", "approved")
            .get()
            .addOnSuccessListener { documents ->
                loans.clear()
                for (doc in documents) {
                    try {
                        val loan = doc.toObject(LoanApplication::class.java)
                        // Pastikan ID terisi
                        if (loan.id.isEmpty()) {
                            loan.id = doc.id
                        }
                        loans.add(loan)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteLoan(loan: LoanApplication) {
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

    private fun showLoanDialog(title: String, data: LoanApplication?) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_loan, null)
        val etMemberId = view.findViewById<EditText>(R.id.etMemberId)
        val etAmount = view.findViewById<EditText>(R.id.etLoanAmount)
        val etTenor = view.findViewById<EditText>(R.id.etLoanTenor)

        // Isi form jika edit
        data?.let {
            etMemberId.setText(it.userId)
            etAmount.setText(it.amount.toString())
            etTenor.setText(it.tenor.toString())
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setPositiveButton("Simpan") { _, _ ->
                val memberId = etMemberId.text.toString().trim()
                val amountInput = etAmount.text.toString().toDoubleOrNull() ?: 0.0
                val tenorInput = etTenor.text.toString().toIntOrNull() ?: 0

                if (memberId.isEmpty()) {
                    Toast.makeText(this, "Member ID Wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // --- LOGIC PERHITUNGAN BUNGA DI SINI ---
                val interestRateFix = 1.5 // Bunga 1.5%
                // Rumus: Pokok * (1.5 / 100) * Tenor Bulan
                val totalInterest = amountInput * (interestRateFix / 100) * tenorInput
                // Total yang harus dibayar = Pokok + Bunga
                val totalPayableFix = amountInput + totalInterest

                // Membuat object dengan data lengkap
                val newLoan = LoanApplication(
                    id = data?.id ?: "",
                    amount = amountInput, // Pokok
                    tenor = tenorInput,
                    reason = "Input Manual Admin",
                    status = "approved",
                    userId = memberId,
                    applicationDate = System.currentTimeMillis(),

                    // Field Baru:
                    interestRate = interestRateFix,
                    totalPayable = totalPayableFix, // Total Hutang
                    paidAmount = data?.paidAmount ?: 0.0 // Jika edit, pertahankan yg sdh dibayar
                )

                if (data == null) {
                    // Tambah Baru
                    val ref = db.collection("loan_applications").document()
                    val loanWithId = newLoan.copy(id = ref.id)

                    ref.set(loanWithId)
                        .addOnSuccessListener {
                            fetchLoans()
                            Toast.makeText(this, "Berhasil disimpan. Total Hutang: Rp ${totalPayableFix.toInt()}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    // Update
                    db.collection("loan_applications").document(data.id)
                        .set(newLoan)
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