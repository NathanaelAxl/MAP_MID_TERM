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

        val userHasActiveLoan = true
        binding.cardUpcomingPayment.visibility = if (userHasActiveLoan) View.VISIBLE else View.GONE

        // --- Setup Listeners (Diperbarui) ---

        binding.btnPayNow.setOnClickListener {
            findNavController().navigate(R.id.action_transactionFragment_to_paymentDetailFragment)
        }

        binding.cardBayarAngsuran.setOnClickListener {
            findNavController().navigate(R.id.action_transactionFragment_to_paymentDetailFragment)
        }

        // KODE INI DIPERBARUI
        binding.cardTambahSimpanan.setOnClickListener {
            findNavController().navigate(R.id.action_transactionFragment_to_addSavingsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}