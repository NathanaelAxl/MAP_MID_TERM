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
            try {
                findNavController().navigate(R.id.action_activeLoanFragment_to_loanApplicationFragment)
            } catch (e: Exception) {
                // Fallback ID jika action belum dibuat
                findNavController().navigate(R.id.loanApplicationFragment)
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

        // Sembunyikan layout pending jika ada (Anda perlu menambahkan layout ini di XML nanti)
        // Atau kita gunakan layoutNoLoan untuk menampilkan pesan pending sementara

        // Cari pinjaman terbaru user (urutkan berdasarkan tanggal)
        db.collection("loan_applications")
            .whereEqualTo("userId", userId)
            .orderBy("applicationDate", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE

                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    val status = doc.getString("status") ?: ""
                    val amount = doc.getDouble("amount") ?: 0.0
                    val tenor = doc.getLong("tenor") ?: 0
                    val monthly = doc.getDouble("monthlyInstallment") ?: 0.0
                    val id = doc.id

                    when (status) {
                        "approved" -> {
                            // TAMPILKAN DETAIL TAGIHAN
                            binding.tvLoanId.text = id.take(8).uppercase()
                            binding.tvLoanAmount.text = "Rp ${"%,.0f".format(amount)}"
                            binding.tvLoanTenor.text = "$tenor Bulan"
                            binding.tvMonthlyInstallment.text = "Rp ${"%,.0f".format(monthly)}"
                            binding.tvLoanStatus.text = "Disetujui (Aktif)"
                            binding.tvLoanStatus.setTextColor(android.graphics.Color.GREEN)

                            binding.layoutLoanDetails.visibility = View.VISIBLE
                            binding.layoutNoLoan.visibility = View.GONE
                        }
                        "pending" -> {
                            // TAMPILKAN STATUS ON PROGRESS
                            // Kita bisa manipulasi UI 'layoutNoLoan' untuk menampilkan pesan ini
                            binding.layoutLoanDetails.visibility = View.GONE
                            binding.layoutNoLoan.visibility = View.VISIBLE

                            // Ganti teks di layout kosong jadi pesan pending
                            // Asumsi di layout_active_loan.xml ada TextView di dalam layout_no_loan
                            // Cari TextView pesan kosong (biasanya index ke-1 di LinearLayout layout_no_loan)
                            // Atau lebih aman via ID jika sudah di set di XML.
                            // Di sini saya pakai findViewById manual atau asumsi binding

                            // Cara cepat: Kita ubah text view "Tidak ada pinjaman"
                            // Pastikan ID di XML layout_no_loan punya TextView pesan
                            // Misal: binding.tvEmptyMessage.text = "Pengajuan Sedang Diproses Admin"

                            // Trik: Sembunyikan tombol ajukan jika sedang pending
                            binding.btnApplyLoan.visibility = View.GONE

                            // Tampilkan pesan lewat Toast atau ubah UI jika memungkinkan
                            Toast.makeText(context, "Status: Pengajuan Sedang Diproses (On Progress)", Toast.LENGTH_LONG).show()

                            // Jika mau ubah teks di layoutNoLoan secara dinamis (tambahkan ID di XML dulu)
                            // binding.tvEmptyMessage.text = "Pengajuan Sedang Diproses"
                        }
                        else -> {
                            // REJECTED atau status lain -> Tampilkan Kosong (Boleh ajukan lagi)
                            binding.btnApplyLoan.visibility = View.VISIBLE
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
                Toast.makeText(context, "Gagal memuat: ${it.message}", Toast.LENGTH_SHORT).show()
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