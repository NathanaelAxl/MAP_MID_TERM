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
        fetchPendingLoans() // Mulai memantau data

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = AdminNotificationAdapter(
            arrayListOf(),
            onApprove = { loan -> updateLoanStatus(loan, "approved") },
            onReject = { loan -> updateLoanStatus(loan, "rejected") }
        )
        binding.rvNotifications.layoutManager = LinearLayoutManager(this)
        binding.rvNotifications.adapter = adapter
    }

    // FUNGSI REAL-TIME: Memantau pengajuan yang 'pending'
    private fun fetchPendingLoans() {
        binding.progressBar.visibility = View.VISIBLE

        db.collection("loan_applications")
            .whereEqualTo("status", "pending") // Hanya ambil yang belum diproses
            .orderBy("applicationDate", Query.Direction.DESCENDING)
            .addSnapshotListener { documents, error ->
                binding.progressBar.visibility = View.GONE

                if (error != null) {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (documents != null) {
                    val list = ArrayList<LoanApplication>()
                    for (doc in documents) {
                        try {
                            val loan = doc.toObject(LoanApplication::class.java)
                            loan.id = doc.id // Penting: Simpan ID dokumen
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

    // --- REVISI UTAMA ADA DI SINI ---
    private fun updateLoanStatus(loan: LoanApplication, newStatus: String) {
        Toast.makeText(this, "Memproses...", Toast.LENGTH_SHORT).show()

        // Siapkan map data yang akan diupdate
        val updates = mutableMapOf<String, Any>(
            "status" to newStatus
        )

        // JIKA DI-APPROVE: Hitung Bunga & Total Hutang
        if (newStatus == "approved") {
            val rate = 1.5 // Bunga 1.5%

            // Rumus: Pokok * (1.5/100) * Tenor
            val totalInterest = loan.amount * (rate / 100) * loan.tenor

            // Total Bayar = Pokok + Total Bunga
            val totalPayable = loan.amount + totalInterest

            // Masukkan ke dalam map updates
            updates["totalPayable"] = totalPayable
            updates["interestRate"] = rate
            updates["paidAmount"] = 0.0 // Reset pembayaran jadi 0
        }

        // Eksekusi Update ke Firestore
        db.collection("loan_applications").document(loan.id)
            .update(updates)
            .addOnSuccessListener {
                val pesan = if (newStatus == "approved") "Pinjaman Disetujui!" else "Pinjaman Ditolak"
                Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
                // Data di list otomatis hilang karena listener
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal update: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}