package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.map_mid_term.databinding.FragmentActiveLoanBinding

class ActiveLoanFragment : Fragment() {

    private var _binding: FragmentActiveLoanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveLoanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Contoh data pinjaman aktif
        val loanId = "LN-09876-ZYX"
        val amount = "Rp 10.000.000"
        val tenor = "12 Bulan"
        val remaining = "Sisa 5 Bulan"
        val status = "Aktif"

        binding.tvLoanId.text = loanId
        binding.tvLoanAmount.text = amount
        binding.tvLoanTenor.text = tenor
        binding.tvLoanRemaining.text = remaining
        binding.tvLoanStatus.text = status

        // Tombol kembali
        binding.btnBackToHome.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
