package com.example.map_mid_term.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.databinding.ActivityAdminLoanDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date

class AdminLoanDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminLoanDetailBinding
    private val db = FirebaseFirestore.getInstance()
    private var currentLoanId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoanDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Pinjaman"

        currentLoanId = intent.getStringExtra("loanId") ?: ""
        if (currentLoanId.isEmpty()) {
            finish()
            return
        }

        fetchLoanDetails(currentLoanId)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun fetchLoanDetails(loanId: String) {
        binding.progressBar.visibility = View.VISIBLE
        db.collection("loan_applications").document(loanId).get()
            .addOnSuccessListener { document ->
                binding.progressBar.visibility = View.GONE
                if (document.exists()) {
                    val userName = document.getString("userName") ?: "-"
                    val amount = document.getDouble("amount") ?: 0.0
                    val tenor = document.getLong("tenor")?.toInt() ?: 0
                    val reason = document.getString("reason") ?: "-"
                    val status = document.getString("status") ?: "pending"
                    val userId = document.getString("userId") ?: ""

                    binding.tvLoanId.text = "ID: $loanId"
                    binding.tvMemberName.text = "Nama: $userName"
                    binding.tvAmount.text = "Jumlah: Rp ${"%,.0f".format(amount)}"
                    binding.tvTenor.text = "Tenor: $tenor Bulan"
                    binding.tvReason.text = "Alasan: $reason"
                    binding.tvStatus.text = status.uppercase()

                    if (status != "pending") {
                        disableButtons()
                    } else {
                        binding.btnAccept.setOnClickListener {
                            approveLoan(loanId, userId, amount, tenor)
                        }
                        binding.btnDecline.setOnClickListener {
                            rejectLoan(loanId)
                        }
                    }
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
            }
    }

    private fun disableButtons() {
        binding.btnAccept.isEnabled = false
        binding.btnDecline.isEnabled = false
        binding.btnAccept.text = "Selesai"
        binding.btnDecline.visibility = View.GONE
    }

    private fun approveLoan(loanId: String, userId: String, amount: Double, tenor: Int) {
        if (userId.isEmpty()) return

        binding.progressBar.visibility = View.VISIBLE
        binding.btnAccept.isEnabled = false
        binding.btnDecline.isEnabled = false

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, tenor)
        val dueDate = calendar.time

        val batch = db.batch()
        val loanRef = db.collection("loan_applications").document(loanId)

        // 1. Update Status Pinjaman
        batch.update(loanRef, mapOf(
            "status" to "approved",
            "dueDate" to dueDate
        ))

        // 2. Create Transaction (Saldo Masuk)
        val transactionRef = db.collection("transactions").document()
        val transactionData = hashMapOf(
            "userId" to userId,
            "amount" to amount,
            "type" to "credit",
            "date" to Date(), // Ini akan dibaca sebagai .date di model Transaction
            "description" to "Pencairan Pinjaman",
            "status" to "success"
        )
        batch.set(transactionRef, transactionData)

        batch.commit()
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Pinjaman Disetujui", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                binding.btnAccept.isEnabled = true
                binding.btnDecline.isEnabled = true
                Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun rejectLoan(loanId: String) {
        binding.progressBar.visibility = View.VISIBLE
        db.collection("loan_applications").document(loanId)
            .update("status", "rejected")
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Ditolak", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
            }
    }
}