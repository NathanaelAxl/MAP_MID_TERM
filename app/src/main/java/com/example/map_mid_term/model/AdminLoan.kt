package com.example.map_mid_term.admin.model

data class AdminLoan(
    val id: String,
    val memberId: String,
    val amount: Double,
    val interestRate: Double,
    val status: String = "Sedang Diproses"
)
