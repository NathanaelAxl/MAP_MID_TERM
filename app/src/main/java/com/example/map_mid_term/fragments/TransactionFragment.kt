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

    // Variabel untuk menyimpan data pinjaman aktif
    private var activeLoanId: String = ""
    private var monthlyBill: Double = 0.0

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
        viewModel.fetchTransactions()
        viewModel.checkActiveLoan()
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
                // ADA PINJAMAN -> Tampilkan Kartu
                binding.layoutUpcomingPayment.visibility = View.VISIBLE

                // --- AMBIL DATA REAL DARI VIEWMODEL ---
                activeLoanId = loanData["id"] as? String ?: "" // Ambil ID Dokumen Asli

                val totalPayable = (loanData["totalPayable"] as? Number)?.toDouble() ?: 0.0
                val tenor = (loanData["tenor"] as? Number)?.toInt() ?: 1

                // Hitung cicilan per bulan: Total Hutang / Tenor
                // (Atau kamu bisa simpan field 'monthlyInstallment' di DB jika mau fix)
                monthlyBill = if (tenor > 0) totalPayable / tenor else 0.0

                binding.tvPaymentAmount.text = "Rp ${"%,.0f".format(monthlyBill)}"
                binding.tvPaymentTitle.text = "Angsuran Bulan Ini ($tenor Bulan)"
                binding.tvPaymentDueDate.text = "Jatuh Tempo: Segera"

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
            navigateToPayment()
        }

        // Klik Menu "Bayar Angsuran" (Menu kotak)
        binding.cardBayarAngsuran.setOnClickListener {
            if (binding.layoutUpcomingPayment.visibility == View.VISIBLE) {
                navigateToPayment()
            } else {
                Toast.makeText(context, "Tidak ada tagihan aktif", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cardTambahSimpanan.setOnClickListener {
            findNavController().navigate(R.id.action_transactionFragment_to_addSavingsFragment)
        }
    }

    private fun navigateToPayment() {
        if (activeLoanId.isEmpty()) {
            Toast.makeText(context, "Data pinjaman belum siap", Toast.LENGTH_SHORT).show()
            return
        }

        // Kirim ID dan Nominal ke PaymentDetailFragment
        val bundle = Bundle().apply {
            putString("title", "Bayar Angsuran")
            putDouble("amount", monthlyBill)
            putString("loanId", activeLoanId) // Kirim ID Asli
        }
        findNavController().navigate(R.id.action_transactionFragment_to_paymentDetailFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}