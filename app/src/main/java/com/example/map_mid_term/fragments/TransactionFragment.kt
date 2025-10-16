// File: app/src/main/java/com/example/map_mid_term/fragments/TransactionFragment.kt
package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentTransactionBinding

class TransactionFragment : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Simulasi jika user punya pinjaman aktif. Ubah jadi 'false' untuk menyembunyikan kartu.
        val userHasActiveLoan = true

        if (userHasActiveLoan) {
            binding.cardUpcomingPayment.visibility = View.VISIBLE
        } else {
            binding.cardUpcomingPayment.visibility = View.GONE
        }

        // --- Setup Listeners ---
        binding.btnPayNow.setOnClickListener {
            // TODO: Nanti arahkan ke halaman detail pembayaran
            Toast.makeText(context, "Membuka halaman pembayaran...", Toast.LENGTH_SHORT).show()
        }

        binding.cardBayarAngsuran.setOnClickListener {
            // TODO: Nanti arahkan ke halaman detail pembayaran
            Toast.makeText(context, "Membuka halaman pembayaran...", Toast.LENGTH_SHORT).show()
        }

        binding.cardTambahSimpanan.setOnClickListener {
            // TODO: Nanti arahkan ke halaman tambah simpanan
            Toast.makeText(context, "Membuka halaman tambah simpanan...", Toast.LENGTH_SHORT).show()
        }

        binding.cardAjukanPinjaman.setOnClickListener {
            // Arahkan ke formulir pengajuan pinjaman
            findNavController().navigate(R.id.action_transactionFragment_to_loanApplicationFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}