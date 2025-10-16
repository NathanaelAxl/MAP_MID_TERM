package com.example.map_mid_term.model

data class LoanApplication(
    val id: String,
    val memberId: String,
    val amount: Double,
    val tenor: Int, // dalam bulan
    val status: String // "Sedang Diproses", "Diterima", "Ditolak"
)
