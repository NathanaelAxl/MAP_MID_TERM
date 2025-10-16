package com.example.map_mid_term.model

data class Transaction(
    val description: String,
    val date: String,
    val amount: Double,
    val type: String // “credit” atau “debit”
)
