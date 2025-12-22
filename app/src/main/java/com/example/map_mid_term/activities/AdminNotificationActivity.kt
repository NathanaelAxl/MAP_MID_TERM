package com.example.map_mid_term.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map_mid_term.adapters.AdminNotificationAdapter
import com.example.map_mid_term.data.model.LoanApplication
import com.example.map_mid_term.databinding.ActivityAdminNotificationBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminNotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminNotificationBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: AdminNotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchPendingLoans()

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        // Menggunakan Adapter Khusus Notifikasi
        adapter = AdminNotificationAdapter(
            arrayListOf(),
            onApprove = { loan -> updateLoanStatus(loan, "approved") },
            onReject = { loan -> updateLoanStatus(loan, "rejected") }
        )
        binding.rvNotifications.layoutManager = LinearLayoutManager(this)
        binding.rvNotifications.adapter = adapter
    }

    private fun fetchPendingLoans() {
        binding.progressBar.visibility = View.VISIBLE

        db.collection("loan_applications")
            .whereEqualTo("status", "pending") // Filter hanya yang Pending
            .orderBy("requestDate", Query.Direction.DESCENDING) // REVISI: requestDate (bukan applicationDate)
            .addSnapshotListener { documents, error ->
                binding.progressBar.visibility = View.GONE

                if (error != null) {
                    // Jika error index, cek Logcat untuk link pembuatan index
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (documents != null) {
                    val list = ArrayList<LoanApplication>()
                    for (doc in documents) {
                        try {
                            val loan = doc.toObject(LoanApplication::class.java)
                            loan.id = doc.id

                            // Fallback nama jika kosong
                            if (loan.userName.isEmpty()) {
                                loan.userName = "ID: ${loan.userId.take(5)}"
                            }

                            list.add(loan)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    adapter.updateData(list)

                    if (list.isEmpty()) {
                        binding.tvEmptyState.visibility = View.VISIBLE
                    } else {
                        binding.tvEmptyState.visibility = View.GONE
                    }
                }
            }
    }

    private fun updateLoanStatus(loan: LoanApplication, newStatus: String) {
        Toast.makeText(this, "Memproses...", Toast.LENGTH_SHORT).show()

        val updates = mutableMapOf<String, Any>(
            "status" to newStatus
        )

        // Logic jika disetujui (Hitung final hutang)
        if (newStatus == "approved") {
            // Samakan logic bunga dengan fragment user (1.5%)
            // Atau kalau mau hardcode 5% seperti rencana awal admin, silakan.
            // Di sini saya pakai 1.5% biar konsisten sama tampilan user.
            val rate = 1.5
            val totalInterest = loan.amount * (rate / 100) * loan.tenor
            val totalPayable = loan.amount + totalInterest

            updates["totalPayable"] = totalPayable
            updates["paidAmount"] = 0.0
        }

        db.collection("loan_applications").document(loan.id)
            .update(updates)
            .addOnSuccessListener {
                val pesan = if (newStatus == "approved") "Pinjaman Disetujui!" else "Pinjaman Ditolak"
                Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal update: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}