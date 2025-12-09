package com.example.map_mid_term.data.model

import java.util.Date
import com.google.firebase.firestore.ServerTimestamp

data class Transaction(
    var id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val type: String = "", // "credit" (masuk) atau "debit" (keluar) atau "loan_payment"
    val timestamp: Long = 0,
    val status: String = "success", // "pending", "verified", "success"
    val proofImageUrl: String? = null, // Bisa URL atau Base64 String
    val userId: String = ""
) {
    // Konstruktor kosong wajib untuk Firestore
    constructor() : this("", "", 0.0, "", 0, "", null, "")
}