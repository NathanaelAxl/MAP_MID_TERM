package com.example.map_mid_term.fragments

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Launcher Galeri untuk Ganti Avatar
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            processAndUploadImage(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserProfile()
        setupListeners()
    }

    private fun setupListeners() {
        // 1. Ganti Foto Profil (Klik Gambar atau Ikon Kamera)
        binding.ivProfilePicture.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        // Asumsi ada ikon edit kecil di layout, jika tidak ada, klik gambar saja cukup

        // 2. Ubah Kata Sandi
        binding.cardChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        // 3. Logout
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            // Kembali ke LoginActivity (Sesuaikan dengan routing aplikasimu)
            // findNavController().navigate(R.id.action_profileFragment_to_loginActivity)
            // Atau finish activity jika pakai Activity stack
            activity?.finish()
        }

        // 4. Edit Profil (Nama/HP) - Opsional jika mau diaktifkan
        binding.tvEditProfile.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            } catch (e: Exception) {
                Toast.makeText(context, "Navigasi belum diatur", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUserProfile() {
        val user = auth.currentUser
        val userId = user?.uid ?: return

        // Set data statis dari Auth (Email)
        binding.tvUserEmail.text = user.email

        // Ambil data detail dari Firestore (Nama, No HP, Foto Profil)
        // Pastikan nama koleksinya 'members' atau 'users' (sesuaikan dengan db kamu)
        db.collection("members").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "Nama Pengguna"
                    val phone = document.getString("phone") ?: "-"
                    val profileBase64 = document.getString("profileImage")

                    binding.tvUserName.text = name
                    binding.tvUserPhone.text = phone

                    // Load Foto Profil jika ada
                    if (!profileBase64.isNullOrEmpty()) {
                        decodeAndDisplayImage(profileBase64)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal memuat profil", Toast.LENGTH_SHORT).show()
            }
    }

    // --- LOGIC FOTO PROFIL (BASE64) ---

    private fun processAndUploadImage(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Mengunggah foto...", Toast.LENGTH_SHORT).show()
                }

                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Compress biar tidak berat di Firestore
                val compressedBitmap = compressBitmap(bitmap, 600) // Max 600px

                val outputStream = ByteArrayOutputStream()
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
                val byteArray = outputStream.toByteArray()
                val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

                // Simpan ke Firestore
                uploadToFirestore(base64String)

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun uploadToFirestore(base64String: String) {
        val userId = auth.currentUser?.uid ?: return

        try {
            db.collection("members").document(userId)
                .update("profileImage", base64String)
                .addOnSuccessListener {
                    Toast.makeText(context, "Foto Profil Diperbarui!", Toast.LENGTH_SHORT).show()
                    decodeAndDisplayImage(base64String)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Gagal simpan ke database", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun decodeAndDisplayImage(base64String: String) {
        try {
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            // Tampilkan dengan Glide (biar mulus & circle crop)
            Glide.with(this)
                .load(decodedImage)
                .transform(CircleCrop()) // Bikin bulat otomatis
                .into(binding.ivProfilePicture)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun compressBitmap(bitmap: Bitmap, maxWidth: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        if (width <= maxWidth) return bitmap
        val ratio = width.toFloat() / height.toFloat()
        val newHeight = (maxWidth / ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, maxWidth, newHeight, false)
    }

    // --- LOGIC GANTI PASSWORD ---

    private fun showChangePasswordDialog() {
        // Inflate layout yang baru kita buat
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null)
        val etNewPassword = dialogView.findViewById<EditText>(R.id.etNewPassword)

        AlertDialog.Builder(requireContext())
            .setTitle("Ubah Kata Sandi")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val newPass = etNewPassword.text.toString()
                if (newPass.length >= 6) {
                    updatePasswordFirebase(newPass)
                } else {
                    Toast.makeText(context, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updatePasswordFirebase(newPass: String) {
        val user = auth.currentUser

        // Update Password
        user?.updatePassword(newPass)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Password Berhasil Diubah!", Toast.LENGTH_LONG).show()
                } else {
                    // Jika gagal (biasanya karena sesi login sudah terlalu lama)
                    Toast.makeText(context, "Gagal: Silakan Logout dan Login ulang terlebih dahulu.", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}