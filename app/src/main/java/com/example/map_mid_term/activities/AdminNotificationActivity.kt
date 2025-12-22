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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.Timestamp
import java.util.HashMap
import com.google.firebase.firestore.SetOptions

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
        // Tampilkan loading/toast
        Toast.makeText(this, "Memproses ${newStatus}...", Toast.LENGTH_SHORT).show()

        // 1. Siapkan Batch (Agar semua operasi jalan bersamaan)
        val batch = db.batch()

        // Referensi Dokumen Pinjaman
        val loanRef = db.collection("loan_applications").document(loan.id)

        // --- UPDATE 1: Status Pinjaman ---
        val updates = HashMap<String, Any>()
        updates["status"] = newStatus

        if (newStatus == "approved") {
            val rate = 1.5 // Bunga 1.5%
            val totalInterest = loan.amount * (rate / 100) * loan.tenor
            val totalPayable = loan.amount + totalInterest
            updates["totalPayable"] = totalPayable
            updates["paidAmount"] = 0.0

            // --- UPDATE 2: Tambah Saldo User (Hanya jika Approved) ---
            val userRef = db.collection("users").document(loan.userId)
            // FieldValue.increment adalah cara aman menambah angka di database tanpa perlu baca data lama dulu
            val dataSaldo = hashMapOf<String, Any>(
                "saldo" to FieldValue.increment(loan.amount)
            )
            // Menggunakan set(..., SetOptions.merge()) tidak akan error walau user tidak ditemukan
            batch.set(userRef, dataSaldo, SetOptions.merge())

            // --- UPDATE 3: Buat Catatan History (Hanya jika Approved) ---
            val transactionRef = db.collection("transactions").document() // Auto ID
            val transactionData = hashMapOf(
                "id" to transactionRef.id,
                "userId" to loan.userId,
                "title" to "Pinjaman Disetujui", // Judul transaksi
                "type" to "Pemasukan", // atau "Loan" tergantung filter kamu
                "amount" to loan.amount,
                "date" to Timestamp.now(), // Penting untuk sorting history
                "status" to "success"
            )
            batch.set(transactionRef, transactionData)
        }

        // Masukkan update pinjaman ke dalam batch
        batch.update(loanRef, updates)

        // --- EKSEKUSI SEMUA SEKALIGUS ---
        batch.commit()
            .addOnSuccessListener {
                val pesan = if (newStatus == "approved") "Pinjaman Disetujui & Saldo Cair!" else "Pinjaman Ditolak"
                Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
                // Data di adapter akan refresh otomatis karena kamu pakai addSnapshotListener di fetchPendingLoans
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memproses: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}