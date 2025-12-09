package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map_mid_term.R
import com.example.map_mid_term.adapters.TransactionAdapter
import com.example.map_mid_term.databinding.FragmentTransactionBinding
import com.example.map_mid_term.viewmodels.TransactionViewModel

class TransactionFragment : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    // Data pinjaman aktif (disimpan sementara untuk dikirim ke halaman bayar)
    private var activeLoanId: String = ""
    private var activeLoanAmount: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Ambil data terbaru setiap kali halaman dibuka
        viewModel.fetchTransactions()
        viewModel.checkActiveLoan() // Cek apakah ada hutang?
    }

    private fun observeViewModel() {
        // 1. List Transaksi
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            if (transactions.isNullOrEmpty()) {
                binding.tvNoTransactions.visibility = View.VISIBLE
                binding.rvLatestTransactions.visibility = View.GONE
            } else {
                binding.tvNoTransactions.visibility = View.GONE
                binding.rvLatestTransactions.visibility = View.VISIBLE
                transactionAdapter.updateData(transactions)
            }
        }

        // 2. Data Pinjaman Aktif (Tagihan)
        viewModel.activeLoan.observe(viewLifecycleOwner) { loanData ->
            if (loanData != null) {
                // ADA PINJAMAN AKTIF -> Tampilkan Kartu
                binding.layoutUpcomingPayment.visibility = View.VISIBLE

                val monthly = loanData["monthlyInstallment"] as? Double ?: 0.0
                // Simpan untuk navigasi bayar
                activeLoanAmount = monthly
                // Jika ID dokumen disimpan di map, ambil. Jika tidak, pakai dummy dulu atau ambil dr snapshot
                activeLoanId = "LOAN-ACT"

                binding.tvPaymentAmount.text = "Rp ${"%,.0f".format(monthly)}"
                binding.tvPaymentTitle.text = "Angsuran Bulan Ini"
                binding.tvPaymentDueDate.text = "Jatuh Tempo: 25 Bulan Ini"
            } else {
                // TIDAK ADA PINJAMAN -> Sembunyikan Kartu
                binding.layoutUpcomingPayment.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(arrayListOf())
        binding.rvLatestTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupListeners() {
        // Klik Bayar Sekarang (Dari kartu tagihan)
        binding.btnPayNow.setOnClickListener {
            navigateToPayment(activeLoanAmount)
        }

        // Klik Menu "Bayar Angsuran"
        binding.cardBayarAngsuran.setOnClickListener {
            if (binding.layoutUpcomingPayment.visibility == View.VISIBLE) {
                navigateToPayment(activeLoanAmount)
            } else {
                Toast.makeText(context, "Tidak ada tagihan aktif", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cardTambahSimpanan.setOnClickListener {
            findNavController().navigate(R.id.action_transactionFragment_to_addSavingsFragment)
        }
    }

    private fun navigateToPayment(amount: Double) {
        val bundle = Bundle().apply {
            putString("title", "Bayar Angsuran")
            putDouble("amount", amount)
            putString("loanId", activeLoanId)
        }
        findNavController().navigate(R.id.action_transactionFragment_to_paymentDetailFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}