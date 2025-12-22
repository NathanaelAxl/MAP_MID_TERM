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

    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    private var activeLoanId: String = ""
    // REVISI: Gunakan Double untuk perhitungan matematika biar akurat
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
        viewModel.fetchTransactions()
        viewModel.checkActiveLoan()
    }

    private fun observeViewModel() {
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

        viewModel.activeLoan.observe(viewLifecycleOwner) { loanData ->
            if (loanData != null) {
                activeLoanId = loanData["id"] as? String ?: ""
                val status = loanData["status"] as? String ?: ""

                // --- REVISI: Cek Status Lunas ---
                // Jika status == "paid", sembunyikan tagihan
                if (status == "paid") {
                    binding.layoutUpcomingPayment.visibility = View.GONE
                    // Opsional: Bisa tampilkan pesan "Selamat, pinjaman lunas!" di tempat lain
                } else {
                    // Jika BELUM lunas ("approved"), Tampilkan Tagihan
                    binding.layoutUpcomingPayment.visibility = View.VISIBLE

                    // Perhitungan menggunakan Double
                    val totalPayable = (loanData["totalPayable"] as? Number)?.toDouble() ?: 0.0
                    val tenor = (loanData["tenor"] as? Number)?.toInt() ?: 1

                    monthlyBill = if (tenor > 0) totalPayable / tenor else 0.0
                    loanTitle = "Angsuran Bulan Ini ($tenor Bulan)"

                    // Tampilkan format uang
                    binding.tvPaymentAmount.text = "Rp ${"%,.0f".format(monthlyBill)}"
                    binding.tvPaymentTitle.text = loanTitle
                    binding.tvPaymentDueDate.text = "Jatuh Tempo: Segera"
                }

            } else {
                binding.layoutUpcomingPayment.visibility = View.GONE
                activeLoanId = ""
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
        binding.btnPayNow.setOnClickListener { navigateToPayment() }

        binding.cardBayarAngsuran.setOnClickListener {
            if (binding.layoutUpcomingPayment.visibility == View.VISIBLE) {
                navigateToPayment()
            } else {
                Toast.makeText(context, "Tidak ada tagihan aktif", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cardTambahSimpanan.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_transactionFragment_to_addSavingsFragment)
            } catch (e: Exception) {
                Toast.makeText(context, "Menu belum siap", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToPayment() {
        if (activeLoanId.isEmpty()) {
            Toast.makeText(context, "Menunggu data pinjaman...", Toast.LENGTH_SHORT).show()
            viewModel.checkActiveLoan()
            return
        }

        // --- REVISI: Gunakan putFloat agar sesuai dengan nav_graph.xml ---
        val bundle = Bundle().apply {
            putString("title", loanTitle)
            putFloat("amount", monthlyBill.toFloat()) // Konversi Double ke Float saat kirim
            putString("loanId", activeLoanId)
        }

        try {
            findNavController().navigate(R.id.action_transactionFragment_to_paymentDetailFragment, bundle)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("NAV_ERROR", "Gagal navigasi: ${e.message}")
            Toast.makeText(context, "Gagal membuka halaman bayar.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}