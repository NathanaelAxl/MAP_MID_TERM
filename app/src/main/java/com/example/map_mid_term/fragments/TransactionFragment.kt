package com.example.map_mid_term.fragments

import android.os.Bundle
import android.util.Log
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

    // Gunakan ViewModel
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    // Variabel Data
    private var activeLoanId: String = ""
    private var monthlyBill: Double = 0.0
    private var loanTitle: String = "Angsuran Pinjaman"

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
        // Refresh data setiap kali halaman dibuka
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

        // 2. Data Tagihan Aktif
        viewModel.activeLoan.observe(viewLifecycleOwner) { loanData ->
            if (loanData != null) {
                binding.layoutUpcomingPayment.visibility = View.VISIBLE

                // Ambil ID Dokumen (Pastikan ViewModel mengirim field 'id')
                activeLoanId = loanData["id"] as? String ?: ""

                // Ambil data angka dengan aman (Safe Casting)
                val totalPayable = (loanData["totalPayable"] as? Number)?.toDouble() ?: 0.0
                val tenor = (loanData["tenor"] as? Number)?.toInt() ?: 1

                // Hitung cicilan
                monthlyBill = if (tenor > 0) totalPayable / tenor else 0.0
                loanTitle = "Angsuran Bulan Ini ($tenor Bulan)"

                // Update UI
                binding.tvPaymentAmount.text = "Rp ${"%,.0f".format(monthlyBill)}"
                binding.tvPaymentTitle.text = loanTitle
                binding.tvPaymentDueDate.text = "Jatuh Tempo: Segera"

            } else {
                binding.layoutUpcomingPayment.visibility = View.GONE
                activeLoanId = "" // Reset ID jika tidak ada tagihan
            }
        }

        // 3. Loading State
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupRecyclerView() {
        // Inisialisasi Adapter dengan list kosong dulu
        transactionAdapter = TransactionAdapter(arrayListOf())
        binding.rvLatestTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupListeners() {
        // Tombol Bayar di Kartu Tagihan
        binding.btnPayNow.setOnClickListener {
            navigateToPayment()
        }

        // Tombol Menu Kotak "Bayar Angsuran"
        binding.cardBayarAngsuran.setOnClickListener {
            if (binding.layoutUpcomingPayment.visibility == View.VISIBLE) {
                navigateToPayment()
            } else {
                Toast.makeText(context, "Tidak ada tagihan aktif", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Tambah Simpanan
        binding.cardTambahSimpanan.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_transactionFragment_to_addSavingsFragment)
            } catch (e: Exception) {
                Toast.makeText(context, "Menu belum siap", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToPayment() {
        // 1. Cek Validasi ID
        if (activeLoanId.isEmpty()) {
            Toast.makeText(context, "Menunggu data pinjaman...", Toast.LENGTH_SHORT).show()
            viewModel.checkActiveLoan() // Coba ambil ulang data
            return
        }

        // 2. Siapkan Data (Bundle)
        // Kita kirim Double, nanti PaymentDetailFragment yang ubah ke Float untuk Upload
        val bundle = Bundle().apply {
            putString("title", loanTitle)
            putFloat("amount", monthlyBill.toFloat())
            putString("loanId", activeLoanId)
        }

        // 3. Eksekusi Navigasi dengan TRY-CATCH (Anti Force Close)
        try {
            findNavController().navigate(R.id.action_transactionFragment_to_paymentDetailFragment, bundle)
        } catch (e: Exception) {
            // Jika error, logcat akan mencatatnya dan Toast akan muncul
            e.printStackTrace()
            Log.e("NAV_ERROR", "Gagal navigasi: ${e.message}")
            Toast.makeText(context, "Gagal membuka halaman bayar. Coba Clean Project.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}