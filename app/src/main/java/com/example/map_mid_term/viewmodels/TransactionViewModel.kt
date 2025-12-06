package com.example.map_mid_term.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.map_mid_term.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class TransactionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val tag = "TransactionViewModel"

    // ... (LiveData yang sudah ada: _transactions, _isLoading, _errorMessage) ...
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> get() = _saveSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // --- FUNGSI BARU UNTUK MENYIMPAN SIMPANAN ---
    fun saveNewSaving(title: String, amount: Double, type: String, imageUri: Uri?) {
        _isLoading.value = true

        if (imageUri == null) {
            // Jika tidak ada bukti upload, langsung simpan ke firestore
            saveTransactionToFirestore(title, amount, type, null)
        } else {
            // Jika ada bukti upload, upload dulu ke storage
            uploadImageAndSave(title, amount, type, imageUri)
        }
    }

    private fun uploadImageAndSave(title: String, amount: Double, type: String, imageUri: Uri) {
        val userId = auth.currentUser?.uid ?: run {
            _errorMessage.value = "User tidak ditemukan!"
            _isLoading.value = false
            return
        }

        val fileName = "proof_${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child("users/$userId/transaction_proofs/$fileName")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Setelah dapat URL, baru simpan ke firestore
                    saveTransactionToFirestore(title, amount, type, downloadUrl.toString())
                }
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Gagal mengupload gambar: ${e.message}"
                _isLoading.value = false
                Log.e(tag, "Image upload failed", e)
            }
    }

    private fun saveTransactionToFirestore(title: String, amount: Double, type: String, imageUrl: String?) {
        val userId = auth.currentUser?.uid ?: run {
            _errorMessage.value = "Tidak bisa menyimpan, user tidak login!"
            _isLoading.value = false
            return
        }

        val newTransactionRef = db.collection("users").document(userId).collection("transactions").document()

        val transaction = Transaction(
            id = newTransactionRef.id,
            title = title,
            amount = amount,
            type = type,
            proofImageUrl = imageUrl
            // timestamp akan diisi otomatis oleh server
        )

        newTransactionRef.set(transaction)
            .addOnSuccessListener {
                _isLoading.value = false
                _saveSuccess.value = true // Beri sinyal sukses
                Log.d(tag, "Transaksi berhasil disimpan!")
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _errorMessage.value = "Gagal menyimpan transaksi: ${e.message}"
                Log.e(tag, "Error saving transaction", e)
            }
    }

    // Fungsi untuk mereset status sukses agar tidak terpanggil terus-menerus
    fun doneNavigating() {
        _saveSuccess.value = false
    }

    // ... (Fungsi fetchTransactions dan fetchAllTransactions tetap di sini) ...
    fun fetchTransactions() { /* ... kode Anda sebelumnya ... */ }
    fun fetchAllTransactions() { /* ... kode Anda sebelumnya ... */ }
}
