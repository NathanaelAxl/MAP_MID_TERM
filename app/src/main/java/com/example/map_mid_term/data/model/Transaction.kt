package com.example.map_mid_term.data.model

import java.util.Date
import com.google.firebase.firestore.ServerTimestamp

data class Transaction(
    val id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val type: String = "",
    val proofImageUrl: String? = null,
    @ServerTimestamp
    val timestamp: Date? = null
)