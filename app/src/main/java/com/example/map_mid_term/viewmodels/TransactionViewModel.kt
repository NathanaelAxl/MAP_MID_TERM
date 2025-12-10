package com.example.map_mid_term.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.map_mid_term.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class TransactionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    private val _totalBalance = MutableLiveData<Double>()
    val totalBalance: LiveData<Double> = _totalBalance

    private val _activeLoan = MutableLiveData<Map<String, Any>?>()
    val activeLoan: LiveData<Map<String, Any>?> = _activeLoan

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // --- VARIABEL PENYIMPAN CCTV (LISTENER) ---
    private var transactionListener: ListenerRegistration? = null
    private var loanListener: ListenerRegistration? = null

    // 1. Ambil Riwayat & Hitung Saldo (SECARA REAL-TIME)
    fun fetchTransactions() {
        val userId = auth.currentUser?.uid ?: return

        if (transactionListener != null) return

        _isLoading.value = true

        transactionListener = db.collection("transactions")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { documents, error ->
                if (error != null) {
                    _isLoading.value = false
                    return@addSnapshotListener
                }

                if (documents != null) {
                    val list = ArrayList<Transaction>()
                    var balance = 0.0

                    for (doc in documents) {
                        val trx = doc.toObject(Transaction::class.java)
                        trx.id = doc.id
                        list.add(trx)

                        // --- LOGIKA SALDO DIPERBAIKI ---
                        // credit = Simpanan (Menambah Saldo)
                        // debit = Penarikan (Mengurangi Saldo)
                        // loan_payment = Bayar Hutang via Transfer (TIDAK Mengurangi Saldo Simpanan)

                        if (trx.type == "credit") {
                            balance += trx.amount
                        } else if (trx.type == "debit") {
                            // Hanya kurangi jika tipenya benar-benar penarikan saldo
                            balance -= trx.amount
                        }
                        // Catatan: loan_payment diabaikan dari perhitungan saldo
                        // karena pembayarannya dari luar (Transfer Bank), bukan potong saldo.
                    }

                    _transactions.value = list
                    _totalBalance.value = balance
                    _isLoading.value = false
                }
            }
    }

    // 2. Cek Pinjaman Aktif (SECARA REAL-TIME)
    fun checkActiveLoan() {
        val userId = auth.currentUser?.uid ?: return

        if (loanListener != null) return

        loanListener = db.collection("loan_applications")
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "approved")
            .limit(1)
            .addSnapshotListener { documents, error ->
                if (error != null) return@addSnapshotListener

                if (documents != null && !documents.isEmpty) {
                    val doc = documents.documents[0]
                    _activeLoan.value = doc.data
                } else {
                    _activeLoan.value = null
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        transactionListener?.remove()
        loanListener?.remove()
    }

    fun setLoading(loading: Boolean) { _isLoading.value = loading }
    fun doneNavigating() { }
    fun doneDisplayingError() { _errorMessage.value = null }
    fun saveNewSaving(title: String, amount: Double, typeDesc: String, base64Image: String) { }
}