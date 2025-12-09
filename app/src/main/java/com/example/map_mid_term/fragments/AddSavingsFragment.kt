package com.example.map_mid_term.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast // <--- INI WAJIB ADA
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentAddSavingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

class AddSavingsFragment : Fragment() {

    private var _binding: FragmentAddSavingsBinding? = null
    private val binding get() = _binding!!

    // Variabel gambar Base64
    private var imageBase64: String? = null

    // Launcher Galeri
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Tampilkan Preview pakai Glide
            binding.ivProofPreview.visibility = View.VISIBLE

            // Gunakan Glide untuk load preview
            Glide.with(this)
                .load(it)
                .centerCrop()
                .into(binding.ivProofPreview)

            // Proses Kompresi ke Base64
            compressAndEncodeImage(it)
        }
    }

    private val nominalSimpananWajib = 100000

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
        // Logic Radio Button
        binding.rgSavingsType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_wajib) {
                binding.etDepositAmount.setText(nominalSimpananWajib.toString())
                binding.etDepositAmount.isEnabled = false
            } else {
                binding.etDepositAmount.setText("")
                binding.etDepositAmount.isEnabled = true
            }
        }

        // Tombol Pilih Gambar
        binding.btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Tombol Simpan
        binding.btnSaveTransaction.setOnClickListener {
            if (validateInput()) {
                saveTransactionToFirebase()
            }
        }
    }

    private fun saveTransactionToFirebase() {
        setLoading(true)

        val amount = binding.etDepositAmount.text.toString().toDoubleOrNull() ?: 0.0
        val type = if (binding.rbWajib.isChecked) "Simpanan Wajib" else "Simpanan Sukarela"
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(requireContext(), "User tidak terdeteksi", Toast.LENGTH_SHORT).show()
            setLoading(false)
            return
        }

        // Data Firestore
        val transactionData = hashMapOf(
            "title" to type,
            "amount" to amount,
            "type" to "credit",
            "timestamp" to System.currentTimeMillis(),
            "proofImageUrl" to imageBase64, // Simpan Base64 di sini
            "userId" to userId
        )

        // Simpan
        FirebaseFirestore.getInstance().collection("transactions")
            .add(transactionData)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(requireContext(), "Transaksi Berhasil!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(requireContext(), "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateInput(): Boolean {
        if (binding.rgSavingsType.checkedRadioButtonId == -1) {
            Toast.makeText(requireContext(), "Pilih jenis simpanan", Toast.LENGTH_SHORT).show()
            return false
        }
        val amountText = binding.etDepositAmount.text.toString()
        if (amountText.isEmpty() || amountText.toDoubleOrNull() ?: 0.0 <= 0) {
            binding.tilDepositAmount.error = "Nominal tidak valid"
            return false
        }
        if (imageBase64 == null) {
            Toast.makeText(requireContext(), "Upload bukti pembayaran wajib", Toast.LENGTH_SHORT).show()
            return false
        }
        binding.tilDepositAmount.error = null
        return true
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSaveTransaction.isEnabled = !isLoading
        binding.btnSelectImage.isEnabled = !isLoading
    }

    private fun compressAndEncodeImage(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) { setLoading(true) }

                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Resize & Kompres
                val compressedBitmap = compressBitmap(bitmap, 800)
                val byteArrayOutputStream = ByteArrayOutputStream()
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                val encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT)

                withContext(Dispatchers.Main) {
                    imageBase64 = encodedString
                    val kbSize = encodedString.length / 1024
                    Toast.makeText(requireContext(), "Gambar siap ($kbSize KB)", Toast.LENGTH_SHORT).show()
                    setLoading(false)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setLoading(false)
                    Toast.makeText(requireContext(), "Error gambar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun compressBitmap(bitmap: Bitmap, maxWidth: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        if (width <= maxWidth) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        if (ratio > 1) {
            width = maxWidth
            height = (width / ratio).toInt()
        } else {
            height = maxWidth
            width = (height * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}