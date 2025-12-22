package com.example.map_mid_term.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map_mid_term.adapters.AdminLoanAdapter
import com.example.map_mid_term.data.model.LoanApplication
import com.example.map_mid_term.databinding.ActivityLoansBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LoanListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoansBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: AdminLoanAdapter
    private var loanList = ArrayList<LoanApplication>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoansBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        fetchLoans()
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        // DI SINI KITA KIRIMKAN 3 FUNGSI: DELETE, APPROVE, REJECT
        adapter = AdminLoanAdapter(
            loanList,
            onDeleteClick = { loan -> showDeleteDialog(loan) },
            onApproveClick = { loan -> updateLoanStatus(loan, "approved") },
            onRejectClick = { loan -> updateLoanStatus(loan, "rejected") }
        )

        binding.recyclerLoans.apply {
            layoutManager = LinearLayoutManager(this@LoanListActivity)
            adapter = this@LoanListActivity.adapter
        }
    }

    private fun fetchLoans() {
        db.collection("loan_applications")
            .orderBy("requestDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                loanList.clear()
                for (doc in documents) {
                    try {
                        val loan = doc.toObject(LoanApplication::class.java)
                        loan.id = doc.id
                        if (loan.userName.isEmpty()) {
                            loan.userName = "Peminjam (ID: ${loan.userId.take(4)})"
                        }
                        loanList.add(loan)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                adapter.notifyDataSetChanged()

                if (loanList.isEmpty()) {
                    Toast.makeText(this, "Belum ada data pinjaman", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // --- FUNGSI UPDATE STATUS (Approve/Reject) ---
    private fun updateLoanStatus(loan: LoanApplication, newStatus: String) {
        val message = if (newStatus == "approved") "menyetujui" else "menolak"

        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Yakin ingin $message pengajuan ini?")
            .setPositiveButton("Ya") { dialog, _ ->

                db.collection("loan_applications").document(loan.id)
                    .update("status", newStatus)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Status berhasil diperbarui!", Toast.LENGTH_SHORT).show()

                        // Update tampilan lokal biar gak perlu reload
                        loan.status = newStatus
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal update: ${it.message}", Toast.LENGTH_SHORT).show()
                    }

                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showDeleteDialog(loan: LoanApplication) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Data?")
            .setMessage("Yakin ingin menghapus data ini permanen?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deleteLoan(loan)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun deleteLoan(loan: LoanApplication) {
        db.collection("loan_applications").document(loan.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show()
                adapter.removeItem(loan)
            }
    }
}