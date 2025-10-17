package com.example.map_mid_term.model

data class Loan(
    val id: String,
    val memberId: String,
    val amount: Double,
    val interestRate: Double,
    val status: String = "Aktif",          // Tambahan opsional
    val durationMonths: Int = 12           // Tambahan opsional
)
