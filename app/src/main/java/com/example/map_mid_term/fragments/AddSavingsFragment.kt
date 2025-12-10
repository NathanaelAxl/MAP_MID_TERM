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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentAddSavingsBinding
import com.example.map_mid_term.viewmodels.TransactionViewModel
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

    // Diasumsikan kamu sudah membuat TransactionViewModel
    private val viewModel: TransactionViewModel by viewModels()

    private var imageBase64: String? = null

    // Launcher untuk membuka Galeri
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // ID IV yang benar: iv_preview_proof
            binding.ivProofPreview.visibility = View.VISIBLE
            binding.layoutPlaceholderImage.visibility = View.GONE
            binding.ivRemoveImage.visibility = View.VISIBLE

            // Gunakan Glide
            Glide.with(this)
                .load(it)
                .centerCrop()
                .into(binding.ivProofPreview)

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
        observeViewModel()

        // Setup tombol hapus gambar
        binding.ivRemoveImage.setOnClickListener {
            imageBase64 = null
            binding.ivProofPreview.setImageURI(null)
            binding.ivProofPreview.visibility = View.GONE
            binding.layoutPlaceholderImage.visibility = View.VISIBLE
            binding.ivRemoveImage.visibility = View.GONE
        }
    }

    private fun setupUIListeners() {
        binding.rgSavingsType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_wajib) {
                // Saat pilih Wajib, isi otomatis tapi BIARKAN user mengedit jika mau
                binding.etDepositAmount.setText(nominalSimpananWajib.toString())
                binding.etDepositAmount.isEnabled = false
            } else {
                binding.etDepositAmount.setText("")
                binding.etDepositAmount.isEnabled = true
            }
        }

        // PERBAIKAN: Pasang listener di CARD (Kotak Besar), bukan di tombol btnSelectImage yang hidden
        binding.cardImagePicker.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSaveTransaction.setOnClickListener {
            if (validateInput()) {
                saveTransaction()
            }
        }
    }

    // Menyimpan Transaksi ke Firestore
    private fun saveTransaction() {
        viewModel.setLoading(true)

        val amountStr = binding.etDepositAmount.text.toString().trim()
        val amount = amountStr.toDoubleOrNull() ?: 0.0

        // Cek Radio Button yang tercentang di Radio Group
        val type = if (binding.rgSavingsType.checkedRadioButtonId == R.id.rb_wajib) "Wajib" else "Sukarela"
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null || imageBase64 == null) {
            viewModel.setLoading(false)
            Toast.makeText(requireContext(), "Data user atau gambar tidak lengkap", Toast.LENGTH_SHORT).show()
            return
        }

        val transactionData = hashMapOf(
            "title" to "Simpanan $type",
            "amount" to amount,
            "type" to "credit",
            "timestamp" to System.currentTimeMillis(),
            "proofImageUrl" to imageBase64,
            "userId" to userId
        )

        FirebaseFirestore.getInstance().collection("transactions")
            .add(transactionData)
            .addOnSuccessListener {
                viewModel.setLoading(false)
                Toast.makeText(requireContext(), "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                viewModel.setLoading(false)
                Toast.makeText(requireContext(), "Gagal menyimpan: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSaveTransaction.isEnabled = !isLoading
            binding.cardImagePicker.isEnabled = !isLoading

            // Tampilkan progress bar (asumsi ID progress_bar ada di XML)
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun validateInput(): Boolean {
        if (binding.rgSavingsType.checkedRadioButtonId == -1) {
            Toast.makeText(requireContext(), "Silakan pilih jenis simpanan", Toast.LENGTH_SHORT).show()
            return false
        }

        val amountText = binding.etDepositAmount.text.toString()
        if (amountText.isEmpty() || amountText.toDoubleOrNull() ?: 0.0 <= 0) {
            binding.tilDepositAmount.error = "Jumlah setoran tidak valid"
            return false
        }

        if (imageBase64 == null) {
            Toast.makeText(requireContext(), "Silakan pilih bukti pembayaran", Toast.LENGTH_SHORT).show()
            return false
        }
        binding.tilDepositAmount.error = null
        return true
    }

    private fun compressAndEncodeImage(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    binding.btnSaveTransaction.isEnabled = false
                }

                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                val compressedBitmap = compressBitmap(bitmap, 800)

                val byteArrayOutputStream = ByteArrayOutputStream()
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                val encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT)

                withContext(Dispatchers.Main) {
                    imageBase64 = encodedString
                    val sizeInKB = encodedString.toByteArray().size / 1024
                    Toast.makeText(requireContext(), "Gambar siap ($sizeInKB KB)", Toast.LENGTH_SHORT).show()
                    binding.btnSaveTransaction.isEnabled = true
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.btnSaveTransaction.isEnabled = true
                    Toast.makeText(requireContext(), "Gagal memproses gambar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}