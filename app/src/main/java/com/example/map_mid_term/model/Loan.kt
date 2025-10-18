package com.example.map_mid_term.model

data class Loan(
    val id: String,
    val memberId: String,
    val amount: Double,
    val interestRate: Double,
    val status: String = "Aktif",
    val durationMonths: Int = 12
)
