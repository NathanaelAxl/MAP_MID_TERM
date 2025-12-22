package com.example.map_mid_term.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.example.map_mid_term.R
import com.example.map_mid_term.activities.LoginActivity
import com.example.map_mid_term.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        // 1. Tombol Edit Profil
        binding.tvEditProfile.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            } catch (e: Exception) {
                Toast.makeText(context, "Navigasi Edit Profil Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // --- REVISI DI SINI: TAMBAHAN TOMBOL UBAH PASSWORD ---
        // Sebelumnya kode ini tidak ada, makanya tombolnya mati.
        binding.cardChangePassword.setOnClickListener {
            try {
                // Pastikan ID 'action_profileFragment_to_changePasswordFragment' ada di nav_graph.xml
                // Jika error, cek apakah fragment tujuannya sudah dibuat.
                findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
            } catch (e: Exception) {
                // Jika belum punya fragment Ubah Password, munculkan pesan ini
                Toast.makeText(context, "Fitur Ubah Sandi belum tersedia / ID Navigasi salah", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
        // -----------------------------------------------------

        // 3. Tombol Keluar
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        val userId = user?.uid ?: return

        binding.tvEmail.text = user.email

        db.collection("members").document(userId).get()
            .addOnSuccessListener { document ->
                if (_binding != null && document.exists()) {
                    val name = document.getString("name") ?: "Anggota"
                    val phone = document.getString("phone") ?: "-"
                    val photoStr = document.getString("profileImageUrl")

                    binding.tvName.text = name
                    binding.tvPhone.text = phone

                    if (!photoStr.isNullOrEmpty()) {
                        loadProfileImage(photoStr)
                    } else {
                        // Placeholder (Vector Drawable)
                        binding.ivProfilePhoto.setImageResource(R.drawable.ic_profile_placeholder)
                    }
                }
            }
    }

    private fun loadProfileImage(base64OrUrl: String) {
        try {
            if (base64OrUrl.length > 200 && !base64OrUrl.startsWith("http")) {
                // Tipe Base64 (Data dari Database)
                var decodedBytes = Base64.decode(base64OrUrl, Base64.NO_WRAP)
                if (decodedBytes == null || decodedBytes.isEmpty()) {
                    decodedBytes = Base64.decode(base64OrUrl, Base64.DEFAULT)
                }
                val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                binding.ivProfilePhoto.load(decodedBitmap) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }

            } else {
                // Tipe URL Online
                binding.ivProfilePhoto.load(base64OrUrl) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.ic_profile_placeholder)
                    error(R.drawable.ic_profile_placeholder)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.ivProfilePhoto.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}