package com.example.map_mid_term.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.data.model.Transaction
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

    override fun onResume() {
        super.onResume()
        setupUI()
        loadUserData()
        calculateTotalBalance()
        loadAnnouncement()

        // PANGGIL FUNGSI INI AGAR STATUS MUNCUL
        checkLoanStatus()
    }

    // --- REVISI: Fungsi Cek Status Pinjaman ---
    private fun checkLoanStatus() {
        if (userId == null) return

        // Kita urutkan berdasarkan 'requestDate' (sesuai screenshot firebase kamu)
        db.collection("loan_applications")
            .whereEqualTo("userId", userId)
            .orderBy("requestDate", Query.Direction.DESCENDING) // Ambil yang paling baru
            .limit(1)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                if (snapshots != null && !snapshots.isEmpty) {
                    val document = snapshots.documents[0]
                    val status = document.getString("status") ?: ""

                    if (_binding != null) {
                        binding.tvPinjamanStatus.visibility = View.VISIBLE

                        when (status) {
                            "pending" -> {
                                binding.tvPinjamanStatus.text = "Menunggu Konfirmasi"
                                binding.tvPinjamanStatus.setTextColor(Color.parseColor("#F57C00")) // Orange
                            }
                            "approved" -> {
                                binding.tvPinjamanStatus.text = "Disetujui"
                                binding.tvPinjamanStatus.setTextColor(Color.parseColor("#388E3C")) // Hijau
                            }
                            "rejected" -> {
                                binding.tvPinjamanStatus.text = "Ditolak"
                                binding.tvPinjamanStatus.setTextColor(Color.RED)
                            }
                            "paid" -> {
                                binding.tvPinjamanStatus.text = "Lunas"
                                binding.tvPinjamanStatus.setTextColor(Color.BLUE)
                            }
                            else -> {
                                binding.tvPinjamanStatus.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    // Kalau user belum pernah pinjam
                    if (_binding != null) {
                        binding.tvPinjamanStatus.visibility = View.GONE
                    }
                }
            }
    }

    private fun loadAnnouncement() {
        db.collection("announcements").document("latest_info")
            .addSnapshotListener { document, e ->
                if (e != null || document == null || !document.exists()) {
                    if (_binding != null) binding.tvAnnouncementText.text = "Belum ada pengumuman terbaru."
                    return@addSnapshotListener
                }
                val message = document.getString("message")
                if (_binding != null) binding.tvAnnouncementText.text = message ?: "Tidak ada informasi."
            }
    }

    private fun setupUI() {
        binding.ivToggleBalance.setOnClickListener {
            isBalanceVisible = !isBalanceVisible
            updateBalanceUI(binding.tvTotalBalance.tag as? Double ?: 0.0)
        }

        binding.cardSimpanan.setOnClickListener {
            try { findNavController().navigate(R.id.action_homeFragment_to_addSavingsFragment) }
            catch (e: Exception) { Toast.makeText(context, "Navigasi belum dibuat", Toast.LENGTH_SHORT).show() }
        }

        binding.cardPinjaman.setOnClickListener {
            try { findNavController().navigate(R.id.action_homeFragment_to_loanApplicationFragment) } catch (e: Exception) { }
        }

        binding.cardLaporan.setOnClickListener {
            try { findNavController().navigate(R.id.action_homeFragment_to_historyFragment) } catch (e: Exception) { }
        }
    }

    private fun loadUserData() {
        val uid = userId ?: return
        db.collection("members").document(uid).get()
            .addOnSuccessListener { document ->
                if (_binding != null) {
                    val name = document.getString("name") ?: "Anggota"
                    val firstName = name.split(" ")[0]
                    binding.tvGreeting.text = "Selamat Datang, $firstName!"
                }
            }
    }

    private fun calculateTotalBalance() {
        val uid = userId ?: return
        db.collection("transactions")
            .whereEqualTo("userId", uid)
            .whereEqualTo("type", "credit")
            .get()
            .addOnSuccessListener { documents ->
                var total = 0.0
                for (doc in documents) {
                    val trx = doc.toObject(Transaction::class.java)
                    total += trx.amount
                }
                if (_binding != null) {
                    binding.tvTotalBalance.tag = total
                    updateBalanceUI(total)
                }
            }
            .addOnFailureListener {
                if (_binding != null) binding.tvTotalBalance.text = "Rp -"
            }
    }

    private fun updateBalanceUI(amount: Double) {
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