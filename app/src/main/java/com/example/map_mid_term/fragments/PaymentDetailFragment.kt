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

        // 1. TERIMA DATA (Dari TransactionFragment)
        val title = arguments?.getString("title") ?: "Angsuran Pinjaman"
        val amount = arguments?.getDouble("amount") ?: 0.0
        val loanId = arguments?.getString("loanId") ?: ""

        // --- VALIDASI PENTING ---
        // Jika loanId kosong, berarti ada yang salah. Jangan biarkan lanjut bayar.
        if (loanId.isEmpty()) {
            Toast.makeText(context, "Data ID Pinjaman tidak ditemukan!", Toast.LENGTH_LONG).show()
            findNavController().popBackStack() // Kembali ke menu sebelumnya
            return
        }

        // Update UI
        binding.tvPaymentAmount.text = "Rp ${"%,.0f".format(amount)}"

        // 2. BUNGKUS DATA (Untuk dikirim ke langkah terakhir)
        val nextBundle = Bundle().apply {
            putString("title", title)
            putFloat("amount", amount.toFloat())
            putString("loanId", loanId) // Ini "kunci" yang harus dibawa sampai akhir
        }

        // Navigasi ke VA
        binding.cardVirtualAccount.setOnClickListener {
            // Pastikan ID action ini benar ada di nav_graph.xml
            try {
                findNavController().navigate(R.id.action_paymentDetailFragment_to_virtualAccountFragment, nextBundle)
            } catch (e: Exception) {
                Toast.makeText(context, "Fitur VA belum siap", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigasi ke Upload Bukti
        binding.cardManualTransfer.setOnClickListener {
            try {
                // Pastikan ID ini sudah bersih di XML (lihat langkah 2)
                findNavController().navigate(R.id.action_paymentDetailFragment_to_uploadProofFragment, nextBundle)
            } catch (e: Exception) {
                // Tampilkan pesan error asli di logcat agar ketahuan penyebabnya
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