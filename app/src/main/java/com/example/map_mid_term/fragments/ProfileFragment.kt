package com.example.map_mid_term.fragments

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.activities.LoginActivity
import com.example.map_mid_term.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ambil data asli dari Firestore
        loadUserProfile()

        // 2. Siapkan tombol-tombol
        setupClickListeners()
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Tampilkan loading state sementara (opsional)
            binding.tvProfileName.text = "Memuat..."

            db.collection("members").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Ambil data dari database
                        val name = document.getString("name") ?: "Tanpa Nama"
                        val email = document.getString("email") ?: auth.currentUser?.email
                        val phone = document.getString("phone") ?: "-"

                        // Masukkan ke UI (pastikan binding tidak null)
                        if (_binding != null) {
                            binding.tvProfileName.text = name
                            binding.tvProfileEmail.text = email
                            binding.tvProfilePhone.text = phone

                            // Status keanggotaan (Default Aktif dulu untuk demo)
                            binding.tvMembershipStatus.text = "Anggota Aktif"
                            val background = binding.tvMembershipStatus.background as? GradientDrawable
                            background?.setColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
                        }
                    }
                }
                .addOnFailureListener {
                    if (_binding != null) {
                        binding.tvProfileName.text = "Gagal memuat"
                        Toast.makeText(context, "Gagal mengambil data profil", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun setupClickListeners() {
        // Tombol Edit (Navigasi aman)
        binding.btnEditProfile.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            } catch (e: Exception) {
                Toast.makeText(context, "Fitur Edit Profil segera hadir", Toast.LENGTH_SHORT).show()
            }
        }

        // --- FITUR LOGOUT (PENTING UNTUK DEMO) ---
        binding.menuLogout.setOnClickListener {
            performLogout()
        }

        // Tombol menu lainnya (Toast saja biar aman)
        binding.menuChangePassword.setOnClickListener {
            Toast.makeText(context, "Fitur Ubah Sandi berjalan", Toast.LENGTH_SHORT).show()
        }

        binding.menuHelpCenter.setOnClickListener {
            Toast.makeText(context, "Membuka Pusat Bantuan...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performLogout() {
        // 1. Sign out dari Firebase
        auth.signOut()

        // 2. Pindah ke Halaman Login & Hapus history navigasi
        // (Agar pas ditekan 'Back' tidak balik ke dalam aplikasi lagi)
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // 3. Tutup fragment/activity saat ini (opsional karena flag di atas sudah handle)
        activity?.finish()

        Toast.makeText(context, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}