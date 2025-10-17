package com.example.map_mid_term.admin.model

object AdminDummyData {
    val loans = mutableListOf(
        AdminLoan("L001", "M001", 5000000.0, 5.0, "Disetujui"),
        AdminLoan("L002", "M002", 3000000.0, 4.5, "Menunggu Persetujuan"),
        AdminLoan("L003", "M003", 7000000.0, 6.0, "Sedang Diproses")
    )
}
