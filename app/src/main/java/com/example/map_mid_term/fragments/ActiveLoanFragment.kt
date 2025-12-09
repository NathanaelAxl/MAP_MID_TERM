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

        // Tombol Kembali
        binding.btnBackToHome.setOnClickListener {
            // Kembali ke halaman sebelumnya
            findNavController().popBackStack()
        }

        // Tombol Ajukan (jika kosong)
        binding.btnApplyLoan.setOnClickListener {
            // Arahkan ke formulir pengajuan jika ada di nav graph
            try {
                findNavController().navigate(R.id.action_activeLoanFragment_to_loanApplicationFragment)
            } catch (e: Exception) {
                // Fallback manual jika action belum dibuat di nav_graph
                findNavController().navigate(R.id.loanApplicationFragment)
            }
        }

        // Ambil Data
        fetchActiveLoan()
    }

    private fun fetchActiveLoan() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            showEmptyState()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.layoutLoanDetails.visibility = View.GONE
        binding.layoutNoLoan.visibility = View.GONE

        // Cari pinjaman user yang statusnya "approved"
        db.collection("loan_applications")
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "approved")
            .limit(1) // Ambil 1 saja yang terbaru/aktif
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE

                if (!documents.isEmpty) {
                    // ADA PINJAMAN AKTIF
                    val doc = documents.documents[0]
                    val amount = doc.getDouble("amount") ?: 0.0
                    val tenor = doc.getLong("tenor") ?: 0
                    val monthly = doc.getDouble("monthlyInstallment") ?: 0.0
                    val id = doc.id

                    // Isi Data ke UI
                    binding.tvLoanId.text = id.take(8).uppercase() // Ambil 8 karakter ID aja biar rapi
                    binding.tvLoanAmount.text = "Rp ${"%,.0f".format(amount)}"
                    binding.tvLoanTenor.text = "$tenor Bulan"
                    binding.tvMonthlyInstallment.text = "Rp ${"%,.0f".format(monthly)}"
                    binding.tvLoanStatus.text = "Disetujui (Aktif)"

                    // Tampilkan Card
                    binding.layoutLoanDetails.visibility = View.VISIBLE
                    binding.layoutNoLoan.visibility = View.GONE
                } else {
                    // TIDAK ADA
                    showEmptyState()
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                showEmptyState()
                Toast.makeText(context, "Gagal memuat data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEmptyState() {
        binding.layoutLoanDetails.visibility = View.GONE
        binding.layoutNoLoan.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}