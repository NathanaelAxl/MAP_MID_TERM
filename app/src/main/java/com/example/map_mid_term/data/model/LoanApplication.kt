package com.example.map_mid_term.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class LoanApplication(
    var id: String = "",
    var userId: String = "",
    var userName: String = "", // Tambahkan ini biar admin enak baca
    var amount: Double = 0.0,
    var tenor: Int = 0, // Bulan
    var interestRate: Double = 0.0, // Bunga
    var totalPayable: Double = 0.0, // Total yang harus dibayar
    var paidAmount: Double = 0.0, // Yang sudah dibayar
    var status: String = "pending", // pending, approved, rejected, paid
    var reason: String = "", // Alasan pinjaman

    @ServerTimestamp
    var requestDate: Date? = null,
    var dueDate: Date? = null
)