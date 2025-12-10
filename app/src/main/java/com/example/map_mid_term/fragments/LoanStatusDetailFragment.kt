package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentLoanStatusDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

// NAMA CLASS SUDAH DIPERBAIKI
class LoanStatusDetailFragment : Fragment() {

    private var _binding: FragmentLoanStatusDetailBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    // Argumen dari navigasi (ID Pinjaman)
    private val args: LoanStatusDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoanStatusDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val applicationId = args.applicationId
        fetchLoanDetails(applicationId)

        binding.btnBackToHome.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun fetchLoanDetails(documentId: String) {
        db.collection("loan_applications").document(documentId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val localeID = Locale("in", "ID")
                    val currencyFormat = NumberFormat.getCurrencyInstance(localeID)
                    currencyFormat.maximumFractionDigits = 0

                    val status = document.getString("status") ?: "-"
                    val amount = document.getDouble("amount") ?: 0.0
                    val tenor = document.getLong("tenor") ?: 0

                    binding.tvStatusDetail.text = status.uppercase()
                    binding.tvApplicationId.text = document.id
                    binding.tvApplicationAmount.text = currencyFormat.format(amount)
                    binding.tvApplicationTenor.text = "$tenor Bulan"

                    val statusColor = when (status) {
                        "approved" -> ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
                        "rejected" -> ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
                        else -> ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark)
                    }
                    binding.tvStatusDetail.setTextColor(statusColor)
                } else {
                    Toast.makeText(context, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}