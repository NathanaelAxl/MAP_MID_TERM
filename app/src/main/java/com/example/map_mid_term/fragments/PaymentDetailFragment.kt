package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        // 1. TERIMA DATA (Gunakan FLOAT agar sesuai Nav Graph)
        val title = arguments?.getString("title") ?: "Angsuran Pinjaman"

        // Ambil sebagai Float
        val amountFloat = arguments?.getFloat("amount") ?: 0f
        // Ubah ke Double agar format string di bawah aman
        val amountDouble = amountFloat.toDouble()

        val loanId = arguments?.getString("loanId") ?: ""

        if (loanId.isEmpty()) {
            Toast.makeText(context, "Data ID Pinjaman tidak ditemukan!", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        // Update UI (Gunakan amountDouble untuk format rupiah)
        binding.tvPaymentAmount.text = "Rp ${"%,.0f".format(amountDouble)}"

        // 2. SIAPKAN DATA UNTUK TAHAP SELANJUTNYA
        val nextBundle = Bundle().apply {
            putString("title", title)
            putFloat("amount", amountFloat) // Kirim Float lagi ke Upload/VA
            putString("loanId", loanId)
        }

        // Navigasi ke VA
        binding.cardVirtualAccount.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_paymentDetailFragment_to_virtualAccountFragment, nextBundle)
            } catch (e: Exception) {
                Toast.makeText(context, "Fitur VA belum siap", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigasi ke Upload Bukti
        binding.cardManualTransfer.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_paymentDetailFragment_to_uploadProofFragment, nextBundle)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Gagal Navigasi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}