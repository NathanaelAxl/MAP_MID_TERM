package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentPaymentDetailBinding

class PaymentDetailFragment : Fragment() {

    private var _binding: FragmentPaymentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. TERIMA DATA (Dari TransactionFragment)
        // Default value dipakai jika data kosong (misal saat testing langsung)
        val title = arguments?.getString("title") ?: "Angsuran Pinjaman"
        val amount = arguments?.getDouble("amount") ?: 0.0
        val loanId = arguments?.getString("loanId") ?: ""

        // Update UI biar sesuai tagihan asli
        binding.tvPaymentAmount.text = "Rp ${"%,.0f".format(amount)}"

        // 2. BUNGKUS DATA (Untuk dikirim ke halaman selanjutnya)
        val nextBundle = Bundle().apply {
            putString("title", title)
            putDouble("amount", amount)
            putString("loanId", loanId)
        }

        // Navigasi ke VA
        binding.cardVirtualAccount.setOnClickListener {
            findNavController().navigate(R.id.action_paymentDetailFragment_to_virtualAccountFragment, nextBundle)
        }

        // Navigasi ke Upload Bukti
        binding.cardManualTransfer.setOnClickListener {
            findNavController().navigate(R.id.action_paymentDetailFragment_to_uploadProofFragment, nextBundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}