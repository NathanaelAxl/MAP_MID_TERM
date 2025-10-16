package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map_mid_term.adapters.TransactionAdapter
import com.example.map_mid_term.databinding.FragmentHistoryBinding
import com.example.map_mid_term.model.Transaction

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val transactionList = arrayListOf<Transaction>()
        transactionList.add(Transaction("Setoran Sukarela", "15 Okt 2025", 50000.0, "credit"))
        transactionList.add(Transaction("Bayar Angsuran", "10 Okt 2025", 500000.0, "debit"))
        transactionList.add(Transaction("Penarikan Tunai", "02 Okt 2025", 100000.0, "debit"))
        transactionList.add(Transaction("Simpanan Wajib", "01 Okt 2025", 150000.0, "credit"))
        transactionList.add(Transaction("Setoran Sukarela", "28 Sep 2025", 75000.0, "credit"))
        transactionList.add(Transaction("Tarik Tunai", "20 Sep 2025", 200000.0, "debit"))

        val transactionAdapter = TransactionAdapter(transactionList)

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}