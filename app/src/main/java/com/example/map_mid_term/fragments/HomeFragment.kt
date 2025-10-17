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

    // Gunakan onResume() agar UI selalu update saat kembali ke halaman ini
    override fun onResume() {
        super.onResume()
        val parentActivity = activity as? MainActivity
        val memberId = MainActivity.memberId

        // Panggil fungsi-fungsi setup
        setupGreeting(memberId)
        setupBalanceToggle()
        setupQuickActions(memberId)
    }

    // FUNGSI INI SEKARANG BERDIRI SENDIRI DENGAN BENAR
    private fun setupGreeting(memberId: String?) {
        if (memberId != null) {
            val member = DummyData.members.find { it.id == memberId }
            member?.let {
                val firstName = it.name.split(" ")[0]
                binding.tvGreeting.text = getString(R.string.welcome_greeting, firstName)
            }
        } else {
            binding.tvGreeting.text = getString(R.string.welcome_default)
        }
    }

    // FUNGSI INI SEKARANG BERDIRI SENDIRI DENGAN BENAR
    private fun setupBalanceToggle() {
        binding.ivToggleBalance.setOnClickListener {
            if (isBalanceVisible) {
                binding.tvTotalBalance.text = getString(R.string.balance_hidden)
                binding.ivToggleBalance.setImageResource(R.drawable.ic_eye_close)
            } else {
                binding.tvTotalBalance.text = "Rp 1.550.000,00" // Nanti bisa diganti data dinamis
                binding.ivToggleBalance.setImageResource(R.drawable.ic_eye_open)
            }
            isBalanceVisible = !isBalanceVisible
        }
    }

    // FUNGSI INI SEKARANG BERDIRI SENDIRI DENGAN BENAR
    private fun setupQuickActions(memberId: String?) {
        // Cek pengajuan pinjaman terakhir dari anggota yang sedang login
        val application = DummyData.loanApplications.lastOrNull { it.memberId == memberId }

        if (application != null) {
            // JIKA ADA PENGAJUAN, tampilkan status
            binding.tvPinjamanTitle.text = "Status Pinjaman"
            binding.tvPinjamanStatus.text = application.status

            binding.cardPinjaman.setOnClickListener {
                // Buat action untuk navigasi sambil mengirim ID aplikasi
                val action = HomeFragmentDirections.actionHomeFragmentToLoanStatusDetailFragment(application.id)
                findNavController().navigate(action)
            }

            // Atur warna ikon berdasarkan status
            val statusColor = when (application.status) {
                "Diterima" -> ContextCompat.getColor(requireContext(), R.color.green_status)
                "Ditolak" -> ContextCompat.getColor(requireContext(), R.color.red_status)
                else -> ContextCompat.getColor(requireContext(), R.color.orange_status)
            }
            binding.ivPinjamanIcon.setColorFilter(statusColor)

        } else {
            // JIKA TIDAK ADA PENGAJUAN, tampilkan tombol "Ajukan"
            binding.tvPinjamanTitle.text = "Pinjaman"
            binding.tvPinjamanStatus.text = "Ajukan pinjaman baru"
            binding.ivPinjamanIcon.clearColorFilter() // Hapus filter warna
            binding.cardPinjaman.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_loanApplicationFragment)
            }
        }

        // Listener untuk kartu lain (tetap sama)
        binding.cardSimpanan.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_savingsFragment)
        }

        binding.cardLaporan.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_monthlyReportFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

