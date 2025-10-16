package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map_mid_term.R
import com.example.map_mid_term.adapters.TransactionAdapter
import com.example.map_mid_term.databinding.FragmentSavingsBinding
import com.example.map_mid_term.model.Transaction

class SavingsFragment : Fragment() {

    private var _binding: FragmentSavingsBinding? = null
    private val binding get() = _binding!!
    private var isBalanceVisible = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBalanceToggle()
        setupRecyclerView()

        binding.btnSeeAllSavingsHistory.setOnClickListener {
            // Arahkan ke halaman riwayat utama
            findNavController().navigate(R.id.historyFragment)
        }
    }

    private fun setupBalanceToggle() {
        binding.ivToggleSavingsBalance.setOnClickListener {
            if (isBalanceVisible) {
                binding.tvTotalSavingsBalance.text = "Rp ••••••••"
                binding.ivToggleSavingsBalance.setImageResource(R.drawable.ic_eye_close)
            } else {
                binding.tvTotalSavingsBalance.text = "Rp 1.550.000,00" // Ganti dengan data asli nanti
                binding.ivToggleSavingsBalance.setImageResource(R.drawable.ic_eye_open)
            }
            isBalanceVisible = !isBalanceVisible
        }
    }

    private fun setupRecyclerView() {
        // Data bohongan khusus transaksi simpanan
        val savingsTransactionList = arrayListOf<Transaction>()
        savingsTransactionList.add(Transaction("Setoran Sukarela", "15 Okt 2025", 50000.0, "credit"))
        savingsTransactionList.add(Transaction("Simpanan Wajib", "01 Okt 2025", 150000.0, "credit"))
        savingsTransactionList.add(Transaction("Setoran Sukarela", "28 Sep 2025", 75000.0, "credit"))

        val transactionAdapter = TransactionAdapter(savingsTransactionList)
        binding.rvSavingsTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
