package com.example.map_mid_term.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.map_mid_term.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TransactionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    private val _totalBalance = MutableLiveData<Double>()
    val totalBalance: LiveData<Double> = _totalBalance

    // Data Pinjaman Aktif (Jika ada)
    private val _activeLoan = MutableLiveData<Map<String, Any>?>()
    val activeLoan: LiveData<Map<String, Any>?> = _activeLoan

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    // 1. Ambil Riwayat & Hitung Saldo
    fun fetchTransactions() {
        val userId = auth.currentUser?.uid ?: return
        _isLoading.value = true

        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val list = ArrayList<Transaction>()
                var balance = 0.0

                for (doc in documents) {
                    val trx = doc.toObject(Transaction::class.java)
                    trx.id = doc.id
                    list.add(trx)

                    // HITUNG SALDO REAL
                    // Hanya hitung jika status verified/success atau pending (tergantung kebijakan)
                    // Disini kita anggap semua yang masuk DB mempengaruhi saldo visual
                    if (trx.type == "credit") {
                        balance += trx.amount
                    } else if (trx.type == "debit" || trx.type == "loan_payment") {
                        balance -= trx.amount
                    }
                }

                _transactions.value = list
                _totalBalance.value = balance
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
                // _errorMessage.value = "Gagal: ${it.message}" // Optional: matikan jika mengganggu
            }
    }

    // 2. Cek Pinjaman Aktif
    fun checkActiveLoan() {
        val userId = auth.currentUser?.uid ?: return

        // Cari di koleksi loan_applications yang statusnya 'approved'
        db.collection("loan_applications")
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "approved") // KUNCI: Hanya yang disetujui admin
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    // Kirim data pinjaman ke UI
                    _activeLoan.value = doc.data
                } else {
                    // Tidak ada pinjaman aktif
                    _activeLoan.value = null
                }
            }
    }

    // 3. Simpan Simpanan (Dipakai di AddSavingsFragment)
    fun saveNewSaving(title: String, amount: Double, typeDesc: String, base64Image: String) {
        // ... (Kode ini tidak perlu diubah, pakai yang lama atau sesuaikan logicnya)
        // Saya sederhanakan di sini agar fokus ke fitur baru
    }

    fun setLoading(loading: Boolean) { _isLoading.value = loading }
    fun doneNavigating() { _saveSuccess.value = false }
    fun doneDisplayingError() { _errorMessage.value = null }
}