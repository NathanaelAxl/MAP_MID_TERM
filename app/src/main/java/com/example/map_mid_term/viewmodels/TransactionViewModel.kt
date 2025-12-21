package com.example.map_mid_term.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.map_mid_term.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.util.Date

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

    private var transactionListener: ListenerRegistration? = null
    private var loanListener: ListenerRegistration? = null

    // 1. Ambil Riwayat & Hitung Saldo
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
                        // Gunakan try-catch saat konversi agar tidak force close jika data kotor
                        try {
                            val trx = doc.toObject(Transaction::class.java)
                            trx.id = doc.id
                            list.add(trx)

                            if (trx.type == "credit") {
                                balance += trx.amount
                            } else if (trx.type == "debit") {
                                balance -= trx.amount
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    _transactions.value = list
                    _totalBalance.value = balance
                    _isLoading.value = false
                }
            }
    }

    // 2. Cek Pinjaman Aktif
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
                    val loanData = doc.data?.toMutableMap()

                    if (loanData != null) {
                        loanData["id"] = doc.id
                        _activeLoan.value = loanData
                    }
                } else {
                    _activeLoan.value = null
                }
            }
    }

    // 3. FUNGSI BAYAR ANGSURAN (UPDATE ID: 12548)
    fun payInstallment(
        loanId: String,
        paymentAmount: Double,
        proofImageBase64: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val loanRef = db.collection("loan_applications").document(loanId)
        val transactionRef = db.collection("transactions").document()

        db.runTransaction { transaction ->
            // A. BACA DATA
            val snapshot = transaction.get(loanRef)

            val totalPayable = snapshot.getDouble("totalPayable") ?: 0.0
            val currentPaid = snapshot.getDouble("paidAmount") ?: 0.0
            val userId = snapshot.getString("userId") ?: ""

            // B. HITUNG
            val newPaidAmount = currentPaid + paymentAmount
            val newStatus = if (newPaidAmount >= (totalPayable - 1.0)) "paid" else "approved"

            // C. UPDATE
            transaction.update(loanRef, "paidAmount", newPaidAmount)
            transaction.update(loanRef, "status", newStatus)

            // D. BIKIN TRANSAKSI BARU (REVISI DI SINI)
            val newTrx = Transaction(
                id = transactionRef.id,
                description = "Bayar Angsuran", // Ganti 'title' jadi 'description'
                amount = paymentAmount,
                type = "loan_payment",
                timestamp = Date(), // Ganti System.currentTimeMillis() jadi Date()
                status = "success",
                proofImageUrl = proofImageBase64,
                userId = userId
            )
            transaction.set(transactionRef, newTrx)
        }
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Gagal") }
    }

    override fun onCleared() {
        super.onCleared()
        transactionListener?.remove()
        loanListener?.remove()
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}