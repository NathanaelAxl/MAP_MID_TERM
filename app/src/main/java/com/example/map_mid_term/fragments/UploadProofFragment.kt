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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentUploadProofBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

class UploadProofFragment : Fragment() {

    private var _binding: FragmentUploadProofBinding? = null
    private val binding get() = _binding!!

    private var imageBase64: String? = null

    // Launcher Galeri
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Tampilkan Preview
            binding.ivProofPreview.setPadding(0, 0, 0, 0)
            binding.ivProofPreview.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            Glide.with(this).load(it).into(binding.ivProofPreview)

            // Proses Gambar (Background Thread)
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

        // Terima Data
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
            if (imageBase64 != null) {
                saveManualPayment(title, amount, loanId)
            } else {
                Toast.makeText(context, "Harap unggah bukti pembayaran", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveManualPayment(title: String, amount: Double, loanId: String) {
        binding.btnSubmitProof.isEnabled = false
        binding.btnSubmitProof.text = "Mengirim..."

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val transactionData = hashMapOf(
            "title" to title,
            "amount" to amount,
            "type" to "loan_payment",
            "method" to "manual_transfer",
            "status" to "pending", // Pending karena manual
            "loanId" to loanId,
            "timestamp" to System.currentTimeMillis(),
            "proofImageUrl" to imageBase64, // Gambar Base64
            "userId" to userId
        )

        FirebaseFirestore.getInstance().collection("transactions")
            .add(transactionData)
            .addOnSuccessListener {
                Toast.makeText(context, "Bukti Terkirim!", Toast.LENGTH_LONG).show()
                findNavController().popBackStack(R.id.homeFragment, false)
            }
            .addOnFailureListener {
                binding.btnSubmitProof.isEnabled = true
                binding.btnSubmitProof.text = "Kirim Bukti Pembayaran"
                Toast.makeText(context, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // --- TEKNIK BASE64 (Sama seperti AddSavings) ---
    private fun compressAndEncodeImage(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    binding.btnSubmitProof.isEnabled = false
                    binding.btnSubmitProof.text = "Memproses Gambar..."
                }

                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Resize max 800px biar ringan
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