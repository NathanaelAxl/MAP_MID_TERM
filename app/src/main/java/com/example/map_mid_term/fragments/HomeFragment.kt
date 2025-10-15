package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map_mid_term.R
import com.example.map_mid_term.adapters.TransactionAdapter
import com.example.map_mid_term.databinding.FragmentHomeBinding
import com.example.map_mid_term.model.Transaction

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var isBalanceVisible = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivToggleBalance.setOnClickListener {
            if (isBalanceVisible) {
                binding.tvTotalBalance.text = "Rp ••••••••"
                binding.ivToggleBalance.setImageResource(R.drawable.ic_eye_close)
            } else {
                binding.tvTotalBalance.text = "Rp 1.550.000,00"
                binding.ivToggleBalance.setImageResource(R.drawable.ic_eye_open)
            }
            isBalanceVisible = !isBalanceVisible
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val transactionList = arrayListOf<Transaction>()
        transactionList.add(Transaction("Setoran Sukarela", "15 Okt 2025", 50000.0, "credit"))
        transactionList.add(Transaction("Bayar Angsuran", "10 Okt 2025", 500000.0, "debit"))
        transactionList.add(Transaction("Penarikan Tunai", "02 Okt 2025", 100000.0, "debit"))
        transactionList.add(Transaction("Simpanan Wajib", "01 Okt 2025", 150000.0, "credit"))

        val transactionAdapter = TransactionAdapter(transactionList)

        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}