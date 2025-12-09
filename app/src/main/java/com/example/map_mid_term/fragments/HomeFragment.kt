package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Pake ViewModel
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentHomeBinding
import com.example.map_mid_term.viewmodels.TransactionViewModel // Import ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Hubungkan dengan ViewModel yang sama dengan TransactionFragment
    private val viewModel: TransactionViewModel by viewModels()
    private var isBalanceVisible = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupGreetingFromFirebase()
        setupBalanceToggle()

        // --- AMBIL DATA REAL-TIME ---
        viewModel.fetchTransactions()
        observeData()

        setupQuickActions()
    }

    private fun observeData() {
        // Update Saldo otomatis saat data transaksi berubah
        viewModel.totalBalance.observe(viewLifecycleOwner) { balance ->
            if (isBalanceVisible) {
                binding.tvTotalBalance.text = "Rp ${"%,.0f".format(balance)}"
            }
        }
    }

    private fun setupGreetingFromFirebase() {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        if (user != null) {
            db.collection("members").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists() && _binding != null) {
                        val fullName = document.getString("name") ?: "Anggota"
                        val firstName = fullName.split(" ")[0]
                        binding.tvGreeting.text = "Selamat Datang, $firstName!"
                    }
                }
        }
    }

    private fun setupBalanceToggle() {
        binding.ivToggleBalance.setOnClickListener {
            isBalanceVisible = !isBalanceVisible
            if (isBalanceVisible) {
                // Tampilkan saldo terakhir dari ViewModel (jika ada)
                val currentBalance = viewModel.totalBalance.value ?: 0.0
                binding.tvTotalBalance.text = "Rp ${"%,.0f".format(currentBalance)}"
                binding.ivToggleBalance.setImageResource(R.drawable.ic_eye_open)
            } else {
                binding.tvTotalBalance.text = "Rp *********"
                binding.ivToggleBalance.setImageResource(R.drawable.ic_eye_close)
            }
        }
    }

    private fun setupQuickActions() {
        // Navigasi Standar
        binding.cardPinjaman.setOnClickListener {
            // Nanti kita arahkan ke Form Pengajuan
            safeNavigate(R.id.action_homeFragment_to_loanApplicationFragment)
        }

        binding.cardSimpanan.setOnClickListener {
            safeNavigate(R.id.action_homeFragment_to_savingsFragment) // Atau addSavingsFragment
        }

        binding.cardLaporan.setOnClickListener {
            // Bisa diganti jadi tombol Transfer
            safeNavigate(R.id.action_homeFragment_to_transactionFragment) // Ke Riwayat
        }
    }

    private fun safeNavigate(actionId: Int) {
        try {
            findNavController().navigate(actionId)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Menu belum tersedia", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}