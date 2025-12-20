package com.example.map_mid_term.data.model

data class LoanApplication(
    var id: String = "",
    val amount: Double = 0.0,      // Pokok Pinjaman
    val tenor: Int = 0,            // Lama pinjam (bulan)
    val reason: String = "",
    val status: String = "",
    val userId: String = "",
    val applicationDate: Long = 0,

    // --- TAMBAHAN BARU UNTUK BUNGA (Wajib ada) ---
    val interestRate: Double = 1.5, // Default 1.5%
    val totalPayable: Double = 0.0, // Total (Pokok + Bunga)
    val paidAmount: Double = 0.0    // Yang sudah dibayar
) {
    // Konstruktor kosong wajib untuk Firestore
    constructor() : this("", 0.0, 0, "", "", "", 0, 1.5, 0.0, 0.0)
}