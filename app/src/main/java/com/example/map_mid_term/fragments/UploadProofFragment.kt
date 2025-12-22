package com.example.map_mid_term.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.activities.CameraActivity
import com.example.map_mid_term.databinding.FragmentUploadProofBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.util.Date

class UploadProofFragment : Fragment() {

    private var _binding: FragmentUploadProofBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Data Penampung
    private var amount: Float = 0f
    private var loanId: String = ""

    private var currentBitmap: Bitmap? = null
    private var locationString: String = "Lokasi: Tidak terdeteksi"

    // Launcher Kamera
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val photoPath = data?.getStringExtra("photo_path")
            val loc = data?.getStringExtra("location_result")

            if (photoPath != null) {
                // Resize di awal saat load file untuk hemat memori
                currentBitmap = decodeSampledBitmapFromFile(photoPath, 600, 600)
                locationString = loc ?: "Lokasi Tersimpan"
                updateUIPreview()
            }
        }
    }

    // Launcher Galeri
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(imageUri)
                    val originalBitmap = BitmapFactory.decodeStream(inputStream)

                    // Langsung resize saat ambil dari galeri
                    currentBitmap = resizeBitmap(originalBitmap, 600) // Max 600px
                    locationString = "Upload dari Galeri"
                    updateUIPreview()
                } catch (e: Exception) {
                    Toast.makeText(context, "Gagal memuat gambar galeri", Toast.LENGTH_SHORT).show()
                }
            }
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

        amount = arguments?.getFloat("amount") ?: 0f
        loanId = arguments?.getString("loanId") ?: ""

        binding.btnOpenCamera.setOnClickListener {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            cameraLauncher.launch(intent)
        }

        binding.btnOpenGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }

        binding.btnSubmitPayment.setOnClickListener {
            if (currentBitmap != null) {
                processAndUploadImage()
            } else {
                Toast.makeText(context, "Mohon sertakan bukti foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUIPreview() {
        if (currentBitmap != null) {
            binding.ivProofPreview.setImageBitmap(currentBitmap)
            binding.ivProofPreview.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            binding.tvLocationInfo.text = locationString
            binding.btnSubmitPayment.isEnabled = true
        }
    }

    private fun processAndUploadImage() {
        binding.btnSubmitPayment.isEnabled = false
        binding.btnSubmitPayment.text = "Mengompres Data..."

        // Gunakan Thread/Handler biar UI tidak macet saat kompresi
        try {
            val base64Image = convertBitmapToBase64(currentBitmap)

            // CEK UKURAN STRING (PENTING!)
            // Firestore max 1MB (sekitar 1.4 juta karakter base64)
            // Kita batasi di 900.000 karakter biar aman
            if (base64Image.length > 900_000) {
                binding.btnSubmitPayment.isEnabled = true
                binding.btnSubmitPayment.text = "Kirim Pembayaran"
                Toast.makeText(context, "Ukuran foto terlalu besar. Silakan ambil ulang.", Toast.LENGTH_LONG).show()
                return
            }

            uploadTransactionToFirestore(base64Image)

        } catch (e: Exception) {
            e.printStackTrace()
            binding.btnSubmitPayment.isEnabled = true
            Toast.makeText(context, "Gagal memproses gambar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // --- REVISI UTAMA: AUTO-PAYMENT LOGIC ---
    private fun uploadTransactionToFirestore(base64Image: String) {
        binding.btnSubmitPayment.text = "Mengirim..."

        val userId = auth.currentUser?.uid ?: return
        val batch = db.batch()

        // 1. Buat Dokumen Transaksi Baru
        val transactionRef = db.collection("transactions").document()
        val transactionData = hashMapOf(
            "id" to transactionRef.id,
            "userId" to userId,
            "loanId" to loanId, // Simpan ID pinjaman yg dibayar
            "amount" to amount.toDouble(),
            // PENTING: Gunakan 'type' yang membuat warna merah di adapter ("Pengeluaran" atau "debit")
            "type" to "loan_payment",
            "description" to "Bayar Angsuran Pinjaman",
            "date" to Date(), // Gunakan Date object (bukan timestamp)
            "location" to locationString,
            "proofImageUrl" to base64Image,
            "status" to "success" // Langsung sukses
        )
        batch.set(transactionRef, transactionData)

        // 2. Potong Saldo User
        val userRef = db.collection("users").document(userId)
        batch.update(userRef, "saldo", FieldValue.increment(-amount.toDouble()))

        // 3. Update Status Pinjaman (Opsional: Update jumlah yang sudah dibayar)
        if (loanId.isNotEmpty()) {
            val loanRef = db.collection("loan_applications").document(loanId)
            // Tambah field 'paidAmount' di dokumen pinjaman
            batch.update(loanRef, "paidAmount", FieldValue.increment(amount.toDouble()))

            // CATATAN: Idealnya kita cek kalau lunas, status jadi "paid".
            // Tapi karena batch operation tidak bisa baca data (hanya tulis),
            // Kita cukup update nominal bayarnya dulu.
        }

        // --- EKSEKUSI SEMUA ---
        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(context, "Pembayaran Berhasil!", Toast.LENGTH_LONG).show()
                // Kembali ke Home
                findNavController().popBackStack(com.example.map_mid_term.R.id.homeFragment, false)
            }
            .addOnFailureListener { e ->
                binding.btnSubmitPayment.isEnabled = true
                binding.btnSubmitPayment.text = "Kirim Pembayaran"
                Log.e("UPLOAD_ERROR", e.message.toString())
                Toast.makeText(context, "Gagal Kirim: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // --- FUNGSI KOMPRESI & RESIZE (VERSI LEBIH KUAT) ---

    private fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
        try {
            if (source.height >= source.width) {
                if (source.height <= maxLength) return source
                val aspectRatio = source.width.toDouble() / source.height.toDouble()
                val targetWidth = (maxLength * aspectRatio).toInt()
                return Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
            } else {
                if (source.width <= maxLength) return source
                val aspectRatio = source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()
                return Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
            }
        } catch (e: Exception) {
            return source
        }
    }

    private fun convertBitmapToBase64(bitmap: Bitmap?): String {
        if (bitmap == null) return ""

        // 1. Pastikan resize dulu ke maksimal 600px (Kecil tapi cukup jelas di HP)
        val resized = resizeBitmap(bitmap, 600)

        // 2. Kompres ke JPEG Quality 50%
        val outputStream = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Helper untuk load file tanpa OutOfMemory
    private fun decodeSampledBitmapFromFile(path: String, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(path, options)

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false

        return BitmapFactory.decodeFile(path, options)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}