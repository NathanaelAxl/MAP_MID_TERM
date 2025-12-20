package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentActiveLoanBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ActiveLoanFragment : Fragment() {

    private var _binding: FragmentActiveLoanBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveLoanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackToHome.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnApplyLoan.setOnClickListener {
            // Navigasi ke form pengajuan
            try {
                findNavController().navigate(R.id.action_activeLoanFragment_to_loanApplicationFragment)
            } catch (e: Exception) {
                // Fallback jika ID action belum ada (bisa pakai ID fragment tujuan langsung)
                // findNavController().navigate(R.id.loanApplicationFragment)
                Toast.makeText(context, "Navigasi belum di-setup", Toast.LENGTH_SHORT).show()
            }
        }

        fetchLoanStatus()
    }

    private fun fetchLoanStatus() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            showEmptyState()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.layoutLoanDetails.visibility = View.GONE
        binding.layoutNoLoan.visibility = View.GONE

        // Ambil data pinjaman terakhir user
        db.collection("loan_applications")
            .whereEqualTo("userId", userId)
            .orderBy("applicationDate", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE

                if (!documents.isEmpty) {
                    val doc = documents.documents[0]

                    // Ambil Field Dasar
                    val status = doc.getString("status") ?: ""
                    val amountPokok = doc.getDouble("amount") ?: 0.0
                    val tenor = doc.getLong("tenor") ?: 1

                    // Ambil Field Kalkulasi (Logika Baru)
                    // Jika totalPayable null (data lama), pakai amountPokok
                    val totalPayable = doc.getDouble("totalPayable") ?: amountPokok
                    val paidAmount = doc.getDouble("paidAmount") ?: 0.0

                    // Hitung Sisa & Cicilan
                    val remainingDebt = totalPayable - paidAmount
                    val monthlyInstallment = totalPayable / tenor

                    val id = doc.id

                    when (status) {
                        "approved" -> {
                            // --- TAMPILKAN DATA REAL ---
                            binding.tvLoanId.text = "ID: ${id.take(8).uppercase()}"

                            // 1. Total Hutang (Pokok + Bunga)
                            binding.tvLoanAmount.text = "Total Hutang: Rp ${"%,.0f".format(totalPayable)}"

                            // 2. Tenor
                            binding.tvLoanTenor.text = "Tenor: $tenor Bulan"

                            // 3. Cicilan Per Bulan
                            binding.tvMonthlyInstallment.text = "Cicilan: Rp ${"%,.0f".format(monthlyInstallment)} /bln"

                            // 4. Status Pembayaran (Sisa Hutang)
                            // Anda bisa manfaatkan TextView status atau buat TextView baru di XML untuk "Sisa"
                            if (remainingDebt <= 0) {
                                binding.tvLoanStatus.text = "LUNAS"
                                binding.tvLoanStatus.setTextColor(android.graphics.Color.BLUE)
                            } else {
                                binding.tvLoanStatus.text = "Sisa: Rp ${"%,.0f".format(remainingDebt)}"
                                binding.tvLoanStatus.setTextColor(android.graphics.Color.RED)
                            }

                            // Tampilkan Layout
                            binding.layoutLoanDetails.visibility = View.VISIBLE
                            binding.layoutNoLoan.visibility = View.GONE

                            // Sembunyikan tombol ajukan jika masih punya hutang aktif
                            binding.btnApplyLoan.visibility = View.GONE
                        }

                        "paid" -> {
                            // Status Lunas (History) - Tampilkan Kosong agar bisa ajukan baru
                            // Atau tampilkan layout khusus "Selamat Anda Lunas"
                            showEmptyState()
                        }

                        "pending" -> {
                            // Tampilkan UI Sedang Diproses
                            binding.layoutLoanDetails.visibility = View.GONE
                            binding.layoutNoLoan.visibility = View.VISIBLE

                            // Manipulasi text di layout kosong (opsional, perlu ID di XML)
                            // binding.tvEmptyTitle.text = "Sedang Diproses"

                            binding.btnApplyLoan.visibility = View.GONE // Jangan ajukan lagi
                            Toast.makeText(context, "Pengajuan Anda sedang diperiksa Admin", Toast.LENGTH_LONG).show()
                        }

                        else -> { // Rejected atau lainnya
                            showEmptyState()
                        }
                    }
                } else {
                    showEmptyState()
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                showEmptyState()
                Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEmptyState() {
        binding.layoutLoanDetails.visibility = View.GONE
        binding.layoutNoLoan.visibility = View.VISIBLE
        binding.btnApplyLoan.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}