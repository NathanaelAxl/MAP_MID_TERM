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

    // Gunakan ViewModel yang sama, yang sudah kita siapkan
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Asumsi: Kita akan selalu menampilkan tagihan jika ada,
        // logika lebih kompleks bisa ditambahkan nanti dari ViewModel
        binding.cardUpcomingPayment.visibility = View.VISIBLE

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Minta ViewModel untuk mengambil 5 transaksi terbaru dari Firestore
        // Kita letakkan di onResume agar data selalu refresh saat kembali ke halaman ini
        viewModel.fetchTransactions()
    }

    private fun setupRecyclerView() {
        // Inisialisasi adapter dengan list kosong
        transactionAdapter = TransactionAdapter(arrayListOf())
        binding.rvLatestTransactions.apply { // Menggunakan ID yang benar
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
            // Optional: untuk scrolling lebih lancar di dalam NestedScrollView
            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModel() {
        // Mengamati daftar transaksi
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            if (transactions.isNullOrEmpty()) {
                binding.tvNoTransactions.visibility = View.VISIBLE // Menggunakan ID yang benar
                binding.rvLatestTransactions.visibility = View.GONE
            } else {
                binding.tvNoTransactions.visibility = View.GONE
                binding.rvLatestTransactions.visibility = View.VISIBLE
                transactionAdapter.updateData(transactions) // Update data di adapter
            }
        }

        // Mengamati status loading untuk menampilkan ProgressBar
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Menggunakan ID yang benar, dan import View yang benar
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Mengamati pesan error
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnPayNow.setOnClickListener {
            findNavController().navigate(R.id.action_transactionFragment_to_paymentDetailFragment)
        }

        binding.cardBayarAngsuran.setOnClickListener {
            findNavController().navigate(R.id.action_transactionFragment_to_paymentDetailFragment)
        }

        binding.cardTambahSimpanan.setOnClickListener {
            findNavController().navigate(R.id.action_transactionFragment_to_addSavingsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
