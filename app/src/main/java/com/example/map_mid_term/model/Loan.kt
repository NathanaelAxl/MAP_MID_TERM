package com.example.map_mid_term.model

data class Loan(
    val id: String,
    val memberId: String,
    val amount: Double,
    val interestRate: Double,
    var status: String = "Sedang Diproses",
    var tenor: Int = 0
)
