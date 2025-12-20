package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.databinding.FragmentEditProfileBinding // Pastikan nama XML kamu fragment_edit_profile.xml
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        loadCurrentUserData()
    }

    private fun setupListeners() {
        // 1. Tombol Kembali (Back)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 2. Tombol Simpan
        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.etEditName.text.toString().trim()
            val newPhone = binding.etEditPhone.text.toString().trim()

            if (validateInput(newName, newPhone)) {
                saveProfileChanges(newName, newPhone)
            }
        }
    }

    private fun loadCurrentUserData() {
        val userId = auth.currentUser?.uid ?: return
        val email = auth.currentUser?.email // Email ambil dari Auth saja, lebih akurat

        // Set Email (Read Only)
        binding.etEditEmail.setText(email)

        // Set Loading Awal
        binding.progressBar.visibility = View.VISIBLE
        binding.etEditName.isEnabled = false
        binding.etEditPhone.isEnabled = false

        // Ambil Nama & HP dari Firestore
        db.collection("members").document(userId).get()
            .addOnSuccessListener { document ->
                binding.progressBar.visibility = View.GONE
                binding.etEditName.isEnabled = true
                binding.etEditPhone.isEnabled = true

                if (document.exists()) {
                    val name = document.getString("name") ?: ""
                    val phone = document.getString("phone") ?: ""

                    binding.etEditName.setText(name)
                    binding.etEditPhone.setText(phone)
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "Gagal memuat data profil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfileChanges(name: String, phone: String) {
        val userId = auth.currentUser?.uid ?: return

        // UI Loading State
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSaveProfile.isEnabled = false
        binding.btnSaveProfile.text = "Menyimpan..."

        val updates = mapOf(
            "name" to name,
            "phone" to phone
        )

        db.collection("members").document(userId)
            .update(updates)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "Profil Berhasil Diupdate!", Toast.LENGTH_LONG).show()

                // Kembali ke halaman Profil otomatis setelah sukses
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.btnSaveProfile.isEnabled = true
                binding.btnSaveProfile.text = "Simpan Perubahan"
                Toast.makeText(context, "Gagal update: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateInput(name: String, phone: String): Boolean {
        if (name.isEmpty()) {
            binding.tilEditName.error = "Nama tidak boleh kosong"
            return false
        }
        if (phone.isEmpty()) {
            binding.tilEditPhone.error = "Nomor telepon tidak boleh kosong"
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}