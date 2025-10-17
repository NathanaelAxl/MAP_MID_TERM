package com.example.map_mid_term.model

object DummyData {
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

    // Ini adalah daftar untuk pinjaman yang SUDAH DISETUJUI
    val loans = listOf(
        Loan(id = "L001", memberId = "M001", amount = 5000000.0, interestRate = 5.0),
        Loan(id = "L002", memberId = "M002", amount = 3000000.0, interestRate = 4.5),
        Loan(id = "L003", memberId = "M003", amount = 7000000.0, interestRate = 6.0)
    )

    // --- BAGIAN INI YANG PERLU ANDA TAMBAHKAN ---
    // Ini adalah daftar untuk PENGAJUAN PINJAMAN BARU
    // Kita gunakan 'mutableListOf' agar bisa ditambah datanya saat user mengajukan
    val loanApplications = mutableListOf<LoanApplication>()

    val adminTransactions = listOf(
        AdminTransaction("T001", "M001", "Simpanan", 500000.0, "Setoran bulanan", "2025-10-01"),
        AdminTransaction("T002", "M002", "Pinjaman", 1000000.0, "Pembayaran pinjaman", "2025-10-05"),
        AdminTransaction("T003", "M003", "Simpanan", 250000.0, "Simpanan tambahan", "2025-10-10")
    )

    val pendingLoans = mutableListOf(
        Loan("L001", "M001", 1000000.0, 5.0, "Sedang Diproses", 3),
        Loan("L002", "M002", 500000.0, 4.0, "Sedang Diproses", 2),
        Loan("L003", "M003", 2000000.0, 6.0, "Sedang Diproses", 5)
    )


}

