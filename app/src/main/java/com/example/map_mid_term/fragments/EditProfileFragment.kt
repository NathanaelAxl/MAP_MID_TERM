package com.example.map_mid_term.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.example.map_mid_term.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Variabel untuk menampung string foto baru
    private var newProfileImageBase64: String? = null

    // Launcher untuk buka Galeri
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Tampilkan preview foto yang dipilih
            binding.ivProfilePhoto.setImageURI(it)
            // Proses gambar (Kompres & Convert ke Base64)
            processImage(it)
        }
    }

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
        // 1. Tombol Kembali
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 2. Klik Foto / Teks Ganti Foto untuk buka Galeri
        binding.ivProfilePhoto.setOnClickListener { openGallery() }
        binding.tvChangePhoto.setOnClickListener { openGallery() }

        // 3. Tombol Simpan
        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.etEditName.text.toString().trim()
            val newPhone = binding.etEditPhone.text.toString().trim()

            if (validateInput(newName, newPhone)) {
                saveProfileChanges(newName, newPhone)
            }
        }
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun loadCurrentUserData() {
        val userId = auth.currentUser?.uid ?: return
        val email = auth.currentUser?.email

        binding.etEditEmail.setText(email)
        binding.progressBar.visibility = View.VISIBLE

        db.collection("members").document(userId).get()
            .addOnSuccessListener { document ->
                if (_binding == null) return@addOnSuccessListener
                binding.progressBar.visibility = View.GONE

                if (document.exists()) {
                    val name = document.getString("name") ?: ""
                    val phone = document.getString("phone") ?: ""
                    val photoUrl = document.getString("profileImageUrl") // Ambil data foto

                    binding.etEditName.setText(name)
                    binding.etEditPhone.setText(phone)

                    // Tampilkan Foto Profil jika ada
                    if (!photoUrl.isNullOrEmpty()) {
                        loadProfileImage(photoUrl)
                    }
                }
            }
            .addOnFailureListener {
                if (_binding != null) binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "Gagal memuat profil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProfileImage(base64OrUrl: String) {
        try {
            if (base64OrUrl.length > 200 && !base64OrUrl.startsWith("http")) {
                // Decode Base64 (untuk foto yang diupload dari HP)
                val decodedString = Base64.decode(base64OrUrl, Base64.DEFAULT)
                val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                binding.ivProfilePhoto.setImageBitmap(decodedBitmap)
            } else {
                // Load URL biasa (jika ada)
                binding.ivProfilePhoto.load(base64OrUrl) {
                    transformations(CircleCropTransformation())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processImage(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            // Kompres Gambar (Maks lebar 600px biar ringan untuk Firestore)
            val scaledBitmap = compressBitmap(originalBitmap, 600)

            // Ubah ke Base64 String
            newProfileImageBase64 = bitmapToBase64(scaledBitmap)

        } catch (e: Exception) {
            Toast.makeText(context, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfileChanges(name: String, phone: String) {
        val userId = auth.currentUser?.uid ?: return

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSaveProfile.isEnabled = false
        binding.btnSaveProfile.text = "Menyimpan..."

        // Siapkan data update
        val updates = mutableMapOf<String, Any>(
            "name" to name,
            "phone" to phone
        )

        // Hanya update foto jika user memilih foto baru
        if (newProfileImageBase64 != null) {
            updates["profileImageUrl"] = newProfileImageBase64!!
        }

        db.collection("members").document(userId)
            .update(updates)
            .addOnSuccessListener {
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Profil Berhasil Diupdate!", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
            }
            .addOnFailureListener { e ->
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSaveProfile.isEnabled = true
                    binding.btnSaveProfile.text = "Simpan Perubahan"
                    Toast.makeText(context, "Gagal update: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // --- Fungsi Bantuan untuk Gambar ---

    private fun compressBitmap(bitmap: Bitmap, maxWidth: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxWidth
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxWidth
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        // Kompres ke JPEG kualitas 50% (biar lebih ringan lagi dan gak lemot)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // REVISI PENTING: Gunakan NO_WRAP agar string jadi satu baris (tanpa enter)
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
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