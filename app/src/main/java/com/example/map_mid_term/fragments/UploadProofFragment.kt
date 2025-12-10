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

    // Variabel untuk menyimpan string gambar
    private var imageBase64: String? = null

    // Launcher Galeri
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Tampilkan Preview
            binding.ivProofPreview.setPadding(0, 0, 0, 0)
            binding.ivProofPreview.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP

            Glide.with(this)
                .load(it)
                .into(binding.ivProofPreview)

            // Proses Kompresi
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

        // 1. Terima Data dari PaymentDetailFragment
        val title = arguments?.getString("title") ?: "Pembayaran Tagihan"
        val amount = arguments?.getDouble("amount") ?: 0.0
        val loanId = arguments?.getString("loanId") ?: ""

        // 2. Setup Tombol
        binding.btnOpenGallery.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnOpenCamera.setOnClickListener {
            Toast.makeText(context, "Gunakan Galeri untuk demo ini", Toast.LENGTH_SHORT).show()
        }

        // 3. Logic Simpan
        binding.btnSubmitProof.setOnClickListener {
            if (imageBase64 == null) {
                Toast.makeText(context, "Harap unggah bukti pembayaran!", Toast.LENGTH_SHORT).show()
            } else {
                saveTransactionToFirestore(title, amount, loanId)
            }
        }
    }

    private fun saveTransactionToFirestore(title: String, amount: Double, loanId: String) {
        setLoading(true)
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(context, "Sesi habis, login ulang", Toast.LENGTH_SHORT).show()
            setLoading(false)
            return
        }

        val transactionData = hashMapOf(
            "title" to title,
            "amount" to amount,
            "type" to "loan_payment", // Tipe khusus bayar hutang (Uang Keluar)
            "method" to "manual_transfer",
            "status" to "pending", // Status pending menunggu admin
            "loanId" to loanId,
            "timestamp" to System.currentTimeMillis(),
            "proofImageUrl" to imageBase64, // Simpan Gambar
            "userId" to userId
        )

        FirebaseFirestore.getInstance().collection("transactions")
            .add(transactionData)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(context, "Bukti Terkirim! Menunggu Verifikasi.", Toast.LENGTH_LONG).show()
                // Kembali ke Home
                findNavController().popBackStack(R.id.homeFragment, false)
            }
            .addOnFailureListener {
                setLoading(false)
                Toast.makeText(context, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnSubmitProof.isEnabled = !isLoading
        binding.btnSubmitProof.text = if (isLoading) "Mengirim..." else "Kirim Bukti Pembayaran"
    }

    // --- LOGIKA KOMPRESI GAMBAR (Copy dari AddSavings) ---
    private fun compressAndEncodeImage(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) { setLoading(true) }

                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Resize max 800px
                val compressedBitmap = compressBitmap(bitmap, 800)

                val outputStream = ByteArrayOutputStream()
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                val encodedString = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)

                withContext(Dispatchers.Main) {
                    imageBase64 = encodedString
                    setLoading(false)
                    Toast.makeText(context, "Gambar siap!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setLoading(false)
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