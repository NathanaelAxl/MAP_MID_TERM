package com.example.map_mid_term.model

data class AdminNotification(
    val loanId: String,
    val memberId: String,
    val amount: Double,
    val status: String
)
