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
import com.example.map_mid_term.databinding.FragmentSavingsBinding
import com.example.map_mid_term.viewmodels.TransactionViewModel
import com.example.map_mid_term.data.model.Transaction // Pastikan import ini ada

class SavingsFragment : Fragment() {

    private var _binding: FragmentSavingsBinding? = null
    private val binding get() = _binding!!

    // Gunakan ViewModel yang sama
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    private var isBalanceVisible = true
    private var currentTotalBalance = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupBalanceToggle()
        observeData()

        binding.btnSeeAllSavingsHistory.setOnClickListener {
            // Arahkan ke halaman riwayat utama (TransactionFragment)
            try {
                findNavController().navigate(R.id.action_savingsFragment_to_transactionFragment)
                // Pastikan action ini ada di nav_graph, kalau tidak pakai ID global
                // findNavController().navigate(R.id.transactionFragment)
            } catch (e: Exception) {
                // Fallback jika belum di wiring
                Toast.makeText(context, "Buka tab Transaksi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Ambil data terbaru saat halaman dibuka
        viewModel.fetchTransactions()
    }

    private fun observeData() {
        viewModel.transactions.observe(viewLifecycleOwner) { allTransactions ->
            // 1. Filter hanya transaksi "Masuk" (Credit)
            val savingsList = allTransactions.filter { it.type == "credit" }

            // 2. Update RecyclerView dengan data asli
            transactionAdapter.updateData(savingsList)

            // 3. Hitung-hitungan Saldo
            var totalWajib = 0.0
            var totalSukarela = 0.0
            var totalPokok = 0.0 // Bisa di-hardcode 250rb kalau mau

            for (item in savingsList) {
                // Logika deteksi jenis simpanan dari Judul
                val title = item.title.lowercase()
                if (title.contains("wajib")) {
                    totalWajib += item.amount
                } else if (title.contains("sukarela")) {
                    totalSukarela += item.amount
                } else if (title.contains("pokok")) {
                    totalPokok += item.amount
                } else {
                    // Default masuk sukarela jika tidak jelas
                    totalSukarela += item.amount
                }
            }

            currentTotalBalance = totalWajib + totalSukarela + totalPokok

            // 4. Tampilkan ke UI
            binding.tvWajibBalance.text = "Rp ${"%,.0f".format(totalWajib)}"
            binding.tvSukarelaBalance.text = "Rp ${"%,.0f".format(totalSukarela)}"

            // Simpanan pokok bisa kita anggap statis Rp 250.000 jika belum ada fitur bayar pokok
            // Atau biarkan 0 jika ingin strict
            // binding.tvPokokBalance.text = "Rp ${"%,.0f".format(totalPokok)}"

            updateTotalBalanceUI()
        }
    }

    private fun setupBalanceToggle() {
        binding.ivToggleSavingsBalance.setOnClickListener {
            isBalanceVisible = !isBalanceVisible
            updateTotalBalanceUI()
        }
    }

    private fun updateTotalBalanceUI() {
        if (isBalanceVisible) {
            binding.tvTotalSavingsBalance.text = "Rp ${"%,.0f".format(currentTotalBalance)}"
            binding.ivToggleSavingsBalance.setImageResource(R.drawable.ic_eye_open)
        } else {
            binding.tvTotalSavingsBalance.text = "Rp *********"
            binding.ivToggleSavingsBalance.setImageResource(R.drawable.ic_eye_close)
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(arrayListOf())
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