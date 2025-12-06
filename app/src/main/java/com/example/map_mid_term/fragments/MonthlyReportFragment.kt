package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map_mid_term.R
import com.example.map_mid_term.adapters.TransactionAdapter
import com.example.map_mid_term.databinding.FragmentMonthlyReportBinding
import com.example.map_mid_term.data.model.Transaction
import java.util.Calendar

class MonthlyReportFragment : Fragment() {

    private var _binding: FragmentMonthlyReportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthlyReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupRecyclerView()

        binding.btnExportPdf.setOnClickListener {
            Toast.makeText(context, "Memulai proses unduh laporan...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSpinners() {
        // Setup Spinner Bulan
        val months = resources.getStringArray(R.array.months_array)
        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        binding.spinnerMonth.adapter = monthAdapter

        // Setup Spinner Tahun
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear - 5..currentYear).map { it.toString() }.reversed()
        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        binding.spinnerYear.adapter = yearAdapter
    }

    private fun setupRecyclerView() {
        val transactionList = arrayListOf<Transaction>()
        transactionList.add(Transaction("Bayar Angsuran", "10 Okt 2025", 500000.0, "debit"))
        transactionList.add(Transaction("Penarikan Tunai", "02 Okt 2025", 100000.0, "debit"))
        transactionList.add(Transaction("Simpanan Wajib", "01 Okt 2025", 150000.0, "credit"))

        val transactionAdapter = TransactionAdapter(transactionList)
        binding.rvReportTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}