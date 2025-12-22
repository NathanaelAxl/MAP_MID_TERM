package com.example.map_mid_term.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var isBalanceVisible = true
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()

        monitorUserProfile()
        monitorTotalBalance()
        monitorLoanStatus()
        loadAnnouncement()
    }

    private fun monitorUserProfile() {
        val uid = userId ?: return
        db.collection("members").document(uid)
            .addSnapshotListener { document, _ ->
                if (_binding != null && document != null) {
                    val name = document.getString("name") ?: "Anggota"
                    val firstName = name.split(" ")[0]
                    binding.tvGreeting.text = "Selamat Datang, $firstName!"
                }
            }
    }

    private fun monitorTotalBalance() {
        val uid = userId ?: return

        db.collection("transactions")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { documents, error ->
                if (error != null) return@addSnapshotListener

                var total = 0.0
                if (documents != null) {
                    for (doc in documents) {
                        val amount = doc.getDouble("amount") ?: 0.0
                        val type = doc.getString("type") ?: ""

                        if (type.equals("Pemasukan", ignoreCase = true) ||
                            type.equals("credit", ignoreCase = true) ||
                            type.equals("Simpanan", ignoreCase = true)) {
                            total += amount
                        } else {
                            total -= amount
                        }
                    }
                }

                if (_binding != null) {
                    binding.tvTotalBalance.tag = total
                    updateBalanceUI(total)
                }
            }
    }

    // --- REVISI UTAMA: LOGIKA STATUS PINJAMAN ---
    private fun monitorLoanStatus() {
        if (userId == null) return
        db.collection("loan_applications")
            .whereEqualTo("userId", userId)
            .orderBy("requestDate", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                if (_binding != null) {
                    if (snapshots != null && !snapshots.isEmpty) {
                        val document = snapshots.documents[0]
                        val status = document.getString("status") ?: ""

                        binding.tvPinjamanStatus.visibility = View.VISIBLE
                        when (status) {
                            "pending" -> {
                                binding.tvPinjamanStatus.text = "Menunggu Konfirmasi"
                                binding.tvPinjamanStatus.setTextColor(Color.parseColor("#F57C00"))
                                binding.cardPinjaman.isEnabled = false
                            }
                            "approved" -> {
                                binding.tvPinjamanStatus.text = "Pinjaman Aktif"
                                binding.tvPinjamanStatus.setTextColor(Color.parseColor("#388E3C"))
                                binding.cardPinjaman.isEnabled = true
                            }
                            "rejected" -> {
                                binding.tvPinjamanStatus.text = "Ditolak"
                                binding.tvPinjamanStatus.setTextColor(Color.RED)
                                binding.cardPinjaman.isEnabled = true
                            }
                            "paid" -> {
                                // SKENARIO LUNAS:
                                // Tampilkan teks LUNAS berwarna BIRU
                                binding.tvPinjamanStatus.text = "Lunas"
                                binding.tvPinjamanStatus.setTextColor(Color.BLUE)
                                // Tombol aktif kembali (Balik Normal) agar user bisa klik
                                // Nanti di LoanFragment user bisa lihat history atau ajukan baru
                                binding.cardPinjaman.isEnabled = true
                            }
                            else -> binding.tvPinjamanStatus.visibility = View.GONE
                        }
                    } else {
                        binding.tvPinjamanStatus.visibility = View.GONE
                    }
                }
            }
    }

    private fun loadAnnouncement() {
        db.collection("announcements").document("latest_info")
            .addSnapshotListener { document, _ ->
                val message = document?.getString("message")
                if (_binding != null) binding.tvAnnouncementText.text = message ?: "Tidak ada informasi."
            }
    }

    private fun setupUI() {
        binding.ivToggleBalance.setOnClickListener {
            isBalanceVisible = !isBalanceVisible
            val currentAmount = binding.tvTotalBalance.tag as? Double ?: 0.0
            updateBalanceUI(currentAmount)
        }
        binding.cardSimpanan.setOnClickListener {
            safeNavigate(R.id.action_homeFragment_to_addSavingsFragment)
        }
        binding.cardPinjaman.setOnClickListener {
            safeNavigate(R.id.action_homeFragment_to_loanApplicationFragment)
        }
        binding.cardLaporan.setOnClickListener {
            safeNavigate(R.id.action_homeFragment_to_historyFragment)
        }
    }

    private fun safeNavigate(actionId: Int) {
        try { findNavController().navigate(actionId) } catch (e: Exception) { e.printStackTrace() }
    }

    private fun updateBalanceUI(amount: Double) {
        if (_binding == null) return
        if (isBalanceVisible) {
            binding.tvTotalBalance.text = "Rp ${"%,.0f".format(amount)}"
            binding.ivToggleBalance.setImageResource(R.drawable.ic_eye_open)
        } else {
            binding.tvTotalBalance.text = "Rp *********"
            binding.ivToggleBalance.setImageResource(R.drawable.ic_eye_close)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}