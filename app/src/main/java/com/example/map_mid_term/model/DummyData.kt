package com.example.map_mid_term.model

object DummyData {

    // --- DATA ANGGOTA ---
    val members = listOf(
        Member(
            id = "M001",
            name = "Andi Saputra",
            address = "Jl. Mawar No.12, Jakarta",
            phone = "08123456789",
            email = "andi@koperasi.com",
            password = "1234",
            role = "member"
        ),
        Member(
            id = "M002",
            name = "Budi Santoso",
            address = "Jl. Melati No.8, Bandung",
            phone = "08129876543",
            email = "budi@koperasi.com",
            password = "5678",
            role = "member"
        ),
        Member(
            id = "M003",
            name = "Citra Dewi",
            address = "Jl. Anggrek No.21, Surabaya",
            phone = "08121234567",
            email = "citra@koperasi.com",
            password = "9999",
            role = "member"
        ),
        Member(
            id = "A001",
            name = "Admin Koperasi",
            address = "Jl. Melur No.5, Jakarta",
            phone = "0813000000",
            email = "admin@koperasi.com",
            password = "admin123",
            role = "pengurus"
        )
    )

    // --- DATA PINJAMAN YANG AKTIF (sudah disetujui & sedang berjalan) ---
    val activeLoans = mutableListOf(
        Loan(id = "L001", memberId = "M001", amount = 5_000_000.0, interestRate = 5.0, status = "Aktif", durationMonths = 12),
        Loan(id = "L002", memberId = "M002", amount = 3_000_000.0, interestRate = 4.5, status = "Aktif", durationMonths = 10),
        Loan(id = "L003", memberId = "M003", amount = 7_000_000.0, interestRate = 6.0, status = "Aktif", durationMonths = 8)
    )

    // --- DATA PENGAJUAN PINJAMAN (belum disetujui, bisa ditambah user) ---
    val loanApplications = mutableListOf<LoanApplication>()

    // --- DATA TRANSAKSI ADMIN ---
    val adminTransactions = listOf(
        AdminTransaction("T001", "M001", "Simpanan", 500_000.0, "Setoran bulanan", "2025-10-01"),
        AdminTransaction("T002", "M002", "Pinjaman", 1_000_000.0, "Pembayaran pinjaman", "2025-10-05"),
        AdminTransaction("T003", "M003", "Simpanan", 250_000.0, "Simpanan tambahan", "2025-10-10")
    )

    // --- DATA PINJAMAN DALAM PROSES ---
    val pendingLoans = mutableListOf(
        Loan("L004", "M001", 1_000_000.0, 5.0, "Sedang Diproses", 3),
        Loan("L005", "M002", 500_000.0, 4.0, "Sedang Diproses", 2),
        Loan("L006", "M003", 2_000_000.0, 6.0, "Sedang Diproses", 5)
    )
}
