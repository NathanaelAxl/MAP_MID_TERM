package com.example.map_mid_term.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentAddSavingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Date

class AddSavingsFragment : Fragment() {

    private var _binding: FragmentAddSavingsBinding? = null
    private val binding get() = _binding!!

    private var imageBase64: String? = null
    private val nominalSimpananWajib = 100000.0

    // --- 1. LAUNCHER IZIN KAMERA (BARU) ---
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Kalau diizinkan user, langsung buka kamera
            openCamera()
        } else {
            Toast.makeText(requireContext(), "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { processImageUri(it) }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            if (bitmap != null) {
                processBitmap(bitmap)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSavingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUIListeners()
    }

    private fun setupUIListeners() {
        binding.rgSavingsType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_wajib) {
                binding.etDepositAmount.setText(nominalSimpananWajib.toInt().toString())
                binding.etDepositAmount.isEnabled = false
            } else {
                binding.etDepositAmount.setText("")
                binding.etDepositAmount.isEnabled = true
            }
        }

        binding.cardImagePicker.setOnClickListener {
            showImageSourceDialog()
        }

        binding.ivRemoveImage.setOnClickListener {
            resetImageSelection()
        }

        binding.btnSaveTransaction.setOnClickListener {
            if (validateInput()) {
                saveTransaction()
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Ambil Foto (Kamera)", "Pilih dari Galeri")
        AlertDialog.Builder(requireContext())
            .setTitle("Upload Bukti Transfer")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen() // Panggil fungsi cek izin
                    1 -> galleryLauncher.launch("image/*")
                }
            }
            .show()
    }

    // --- 2. FUNGSI CEK IZIN SEBELUM BUKA KAMERA (BARU) ---
    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Izin sudah ada, buka kamera
                openCamera()
            }
            else -> {
                // Belum ada izin, minta izin dulu
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            cameraLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gagal membuka kamera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processImageUri(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            processBitmap(bitmap)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gagal memuat gambar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processBitmap(bitmap: Bitmap) {
        binding.ivProofPreview.setImageBitmap(bitmap)
        binding.ivProofPreview.visibility = View.VISIBLE
        binding.layoutPlaceholderImage.visibility = View.GONE
        binding.ivRemoveImage.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            val compressedBase64 = compressBitmapToBase64(bitmap)
            withContext(Dispatchers.Main) {
                imageBase64 = compressedBase64
            }
        }
    }

    private fun resetImageSelection() {
        imageBase64 = null
        binding.ivProofPreview.setImageDrawable(null)
        binding.ivProofPreview.visibility = View.GONE
        binding.layoutPlaceholderImage.visibility = View.VISIBLE
        binding.ivRemoveImage.visibility = View.GONE
    }

    private fun saveTransaction() {
        setLoading(true)

        val amountStr = binding.etDepositAmount.text.toString().trim()
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val typeLabel = if (binding.rgSavingsType.checkedRadioButtonId == R.id.rb_wajib) "Wajib" else "Sukarela"
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) return

        val transactionData = hashMapOf(
            "userId" to userId,
            "amount" to amount,
            "type" to "credit",
            "title" to "Simpanan $typeLabel",
            "description" to "Setoran Simpanan $typeLabel",
            "status" to "pending_verification",
            "timestamp" to Date(),
            "proofImageUrl" to imageBase64
        )

        FirebaseFirestore.getInstance().collection("transactions")
            .add(transactionData)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(requireContext(), "Berhasil! Menunggu verifikasi admin.", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(requireContext(), "Gagal: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun compressBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 600, 600 * bitmap.height / bitmap.width, false)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun validateInput(): Boolean {
        if (binding.rgSavingsType.checkedRadioButtonId == -1) {
            Toast.makeText(requireContext(), "Pilih jenis simpanan", Toast.LENGTH_SHORT).show()
            return false
        }
        val amount = binding.etDepositAmount.text.toString().toDoubleOrNull() ?: 0.0
        if (amount <= 0) {
            binding.tilDepositAmount.error = "Nominal tidak valid"
            return false
        }
        if (imageBase64 == null) {
            Toast.makeText(requireContext(), "Wajib upload bukti transfer", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSaveTransaction.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}