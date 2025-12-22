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
                Toast.makeText(context, "Navigasi error", Toast.LENGTH_SHORT).show()
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

        // REVISI PENTING: Gunakan "requestDate" sesuai database kamu!
        db.collection("loan_applications")
            .whereEqualTo("userId", userId)
            .orderBy("requestDate", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE

                if (!documents.isEmpty) {
                    val doc = documents.documents[0]

                    val status = doc.getString("status") ?: ""
                    val amountPokok = doc.getDouble("amount") ?: 0.0
                    val tenor = doc.getLong("tenor") ?: 1
                    val totalPayable = doc.getDouble("totalPayable") ?: amountPokok
                    val paidAmount = doc.getDouble("paidAmount") ?: 0.0

                    val remainingDebt = totalPayable - paidAmount
                    val monthlyInstallment = totalPayable / tenor
                    val id = doc.id

                    when (status) {
                        "approved" -> {
                            binding.tvLoanId.text = "ID: ${id.take(8).uppercase()}"
                            binding.tvLoanAmount.text = "Total Hutang: Rp ${"%,.0f".format(totalPayable)}"
                            binding.tvLoanTenor.text = "Tenor: $tenor Bulan"
                            binding.tvMonthlyInstallment.text = "Cicilan: Rp ${"%,.0f".format(monthlyInstallment)} /bln"

                            if (remainingDebt <= 0) {
                                binding.tvLoanStatus.text = "LUNAS"
                                binding.tvLoanStatus.setTextColor(android.graphics.Color.BLUE)
                            } else {
                                binding.tvLoanStatus.text = "Sisa: Rp ${"%,.0f".format(remainingDebt)}"
                                binding.tvLoanStatus.setTextColor(android.graphics.Color.RED)
                            }

                            binding.layoutLoanDetails.visibility = View.VISIBLE
                            binding.layoutNoLoan.visibility = View.GONE
                            binding.btnApplyLoan.visibility = View.GONE
                        }
                        "pending" -> {
                            // Tampilkan status pending (bisa reuse layout kosong atau buat layout khusus)
                            binding.layoutLoanDetails.visibility = View.GONE
                            binding.layoutNoLoan.visibility = View.VISIBLE
                            // Disable tombol ajukan lagi
                            binding.btnApplyLoan.visibility = View.GONE
                            // Opsional: Ubah teks di layout kosong untuk memberitahu user
                            Toast.makeText(context, "Pinjaman sedang diproses Admin", Toast.LENGTH_SHORT).show()
                        }
                        else -> { // Rejected / Paid
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