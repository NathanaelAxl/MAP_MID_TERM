package com.example.map_mid_term.data.model

data class LoanApplication(
    var id: String = "", // ID Dokumen Firestore
    val amount: Double = 0.0,
    val tenor: Int = 0,
    val reason: String = "",
    val status: String = "",
    val userId: String = "",
    val applicationDate: Long = 0
) {
    // Konstruktor kosong wajib untuk Firestore
    constructor() : this("", 0.0, 0, "", "", "", 0)
}