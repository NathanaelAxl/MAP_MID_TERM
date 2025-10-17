package com.example.map_mid_term.model

data class Loan(
    val id: String,
    val memberId: String,
    val amount: Double,
    val interestRate: Double,
    val status: String = "Aktif",     // default: Aktif
    val durationMonths: Int = 12      // default: 12 bulan
)
