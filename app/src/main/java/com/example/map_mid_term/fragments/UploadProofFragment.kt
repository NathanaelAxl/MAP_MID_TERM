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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentUploadProofBinding
import com.example.map_mid_term.viewmodels.TransactionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class UploadProofFragment : Fragment() {

    private var _binding: FragmentUploadProofBinding? = null
    private val binding get() = _binding!!

    // 1. Panggil ViewModel (Otak Pembayaran)
    private val viewModel: TransactionViewModel by viewModels()

    private var imageBase64: String? = null

    // Launcher Galeri
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.ivProofPreview.setPadding(0, 0, 0, 0)
            binding.ivProofPreview.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            Glide.with(this).load(it).into(binding.ivProofPreview)
            compressAndEncodeImage(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadProofBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("title") ?: "Pembayaran Manual"
        val amount = arguments?.getDouble("amount") ?: 0.0
        val loanId = arguments?.getString("loanId") ?: ""

        // Tombol Galeri
        binding.btnOpenGallery.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnOpenCamera.setOnClickListener {
            Toast.makeText(context, "Gunakan Galeri untuk demo", Toast.LENGTH_SHORT).show()
        }

        // Tombol Kirim
        binding.btnSubmitProof.setOnClickListener {
            if (loanId.isEmpty()) {
                Toast.makeText(context, "ID Pinjaman tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageBase64 != null) {
                // PANGGIL FUNGSI EKSEKUSI PEMBAYARAN
                executePayment(loanId, amount)
            } else {
                Toast.makeText(context, "Harap unggah bukti pembayaran", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun executePayment(loanId: String, amount: Double) {
        binding.btnSubmitProof.isEnabled = false
        binding.btnSubmitProof.text = "Memproses Pembayaran..."

        // 2. Panggil ViewModel.payInstallment
        // Ini akan melakukan 2 hal sekaligus (Atomic):
        // a. Mengurangi hutang di 'loan_applications'
        // b. Mencatat history di 'transactions'
        viewModel.payInstallment(
            loanId = loanId,
            paymentAmount = amount,
            proofImageBase64 = imageBase64, // Bukti foto dikirim ke sini
            onSuccess = {
                binding.btnSubmitProof.isEnabled = true
                Toast.makeText(context, "Pembayaran Berhasil! Hutang Berkurang.", Toast.LENGTH_LONG).show()

                // Kembali ke halaman utama (Home)
                findNavController().popBackStack(R.id.homeFragment, false)
            },
            onError = { errorMsg ->
                binding.btnSubmitProof.isEnabled = true
                binding.btnSubmitProof.text = "Kirim Bukti Pembayaran"
                Toast.makeText(context, "Gagal: $errorMsg", Toast.LENGTH_LONG).show()
            }
        )
    }

    // --- TEKNIK BASE64 ---
    private fun compressAndEncodeImage(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    binding.btnSubmitProof.isEnabled = false
                    binding.btnSubmitProof.text = "Memproses Gambar..."
                }

                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val compressedBitmap = compressBitmap(bitmap, 800)
                val outputStream = ByteArrayOutputStream()
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                val encodedString = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)

                withContext(Dispatchers.Main) {
                    imageBase64 = encodedString
                    binding.btnSubmitProof.isEnabled = true
                    binding.btnSubmitProof.text = "Kirim Bukti Pembayaran"
                    Toast.makeText(context, "Gambar siap!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Gagal proses gambar", Toast.LENGTH_SHORT).show()
                }
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}