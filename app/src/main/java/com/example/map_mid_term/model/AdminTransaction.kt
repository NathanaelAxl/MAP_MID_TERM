package com.example.map_mid_term.model

data class AdminTransaction(
    val id: String,
    val memberId: String,
    val type: String,
    val amount: Double,
    val description: String,
    val date: String
)
