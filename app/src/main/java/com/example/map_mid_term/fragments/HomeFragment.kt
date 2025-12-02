package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.activities.MainActivity
import com.example.map_mid_term.databinding.FragmentHomeBinding
import com.example.map_mid_term.model.DummyData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

    override fun onResume() {
        super.onResume()

        // Ambil data Member ID dari Activity (jika masih pakai dummy)
        val memberId = MainActivity.memberId

        // Setup UI
        setupGreetingFromFirebase()
        setupBalanceToggle()
        setupQuickActions(memberId)
    }

    // 1. FUNGSI MENAMPILKAN NAMA DARI DATABASE
    private fun setupGreetingFromFirebase() {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        if (user != null) {
            db.collection("members").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists() && _binding != null) { // Cek binding null biar gak crash
                        val fullName = document.getString("name") ?: "Anggota"
                        val firstName = fullName.split(" ")[0]

                        // Set text manual tanpa resource string biar aman dari error format
                        binding.tvGreeting.text = "Selamat Datang, $firstName!"
                    }
                }
                .addOnFailureListener {
                    if (_binding != null) {
                        binding.tvGreeting.text = "Selamat Datang, Anggota!"
                    }
                }
        } else {
            binding.tvGreeting.text = "Selamat Datang!"
        }
    }

    // 2. FUNGSI TOGGLE SALDO (Mata Tertutup/Terbuka)
    private fun setupBalanceToggle() {
        binding.ivToggleBalance.setOnClickListener {
            if (isBalanceVisible) {
                // Jika ingin sembunyikan saldo
                binding.tvTotalBalance.text = "Rp *********"
                binding.ivToggleBalance.setImageResource(R.drawable.ic_eye_close)
            } else {
                // Jika ingin tampilkan saldo
                binding.tvTotalBalance.text = "Rp 1.550.000,00"
                binding.ivToggleBalance.setImageResource(R.drawable.ic_eye_open)
            }
            isBalanceVisible = !isBalanceVisible
        }
    }

    // 3. FUNGSI TOMBOL-TOMBOL (Dengan Error Handling Aman)
    private fun setupQuickActions(memberId: String?) {
        val application = DummyData.loanApplications.lastOrNull { it.memberId == memberId }

        if (application != null) {
            // Tampilan jika user punya pinjaman aktif
            binding.tvPinjamanTitle.text = "Status Pinjaman"
            binding.tvPinjamanStatus.text = application.status

            binding.cardPinjaman.setOnClickListener {
                // VERSI AMAN: Tampilkan Toast saja untuk detail status (Anti Error)
                Toast.makeText(requireContext(), "Status: ${application.status}", Toast.LENGTH_SHORT).show()

                // Jika navigasi error, biarkan baris di bawah ini dikomentari dulu
                // findNavController().navigate(R.id.action_homeFragment_to_loanStatusDetailFragment)
            }

            // Ganti warna ikon sesuai status
            val statusColor = when (application.status) {
                "Diterima" -> ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
                "Ditolak" -> ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
                else -> ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark)
            }
            binding.ivPinjamanIcon.setColorFilter(statusColor)

        } else {
            // Tampilan jika tidak ada pinjaman
            binding.tvPinjamanTitle.text = "Pinjaman"
            binding.tvPinjamanStatus.text = "Ajukan pinjaman baru"
            binding.ivPinjamanIcon.clearColorFilter()

            binding.cardPinjaman.setOnClickListener {
                safeNavigate(R.id.action_homeFragment_to_loanApplicationFragment)
            }
        }

        binding.cardSimpanan.setOnClickListener {
            safeNavigate(R.id.action_homeFragment_to_savingsFragment)
        }

        binding.cardLaporan.setOnClickListener {
            safeNavigate(R.id.action_homeFragment_to_monthlyReportFragment)
        }
    }

    // Fungsi pembantu biar navigasi gak force close kalau ID salah
    private fun safeNavigate(actionId: Int) {
        try {
            findNavController().navigate(actionId)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Halaman belum tersedia", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}