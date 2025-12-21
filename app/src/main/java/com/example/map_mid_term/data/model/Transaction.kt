package com.example.map_mid_term.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Transaction(
    var id: String = "",
    var userId: String = "",
    var amount: Double = 0.0,
    var type: String = "", // "credit" atau "debit"

    // Ganti 'title' jadi 'description' supaya cocok dengan kode UploadProofFragment kita tadi
    var description: String = "",

    var status: String = "success",
    var proofImageUrl: String? = null,

    // PENTING: Gunakan Date? agar cocok dengan format Timestamp di Firestore
    @ServerTimestamp
    var timestamp: Date? = null,

    // Tambahan field (opsional) biar data lokasi & loanId tidak hilang saat diambil
    var location: String = "",
    var loanId: String = ""
)
// TIDAK PERLU constructor() tambahan lagi.
// Karena semua field sudah ada nilai default-nya (= ...), Kotlin otomatis membuatkan constructor kosong.