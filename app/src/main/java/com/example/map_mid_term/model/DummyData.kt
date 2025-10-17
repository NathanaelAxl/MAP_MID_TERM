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
            role = "member",
            hasPaidMandatorySavings = true
        ),
        Member(
            id = "M002",
            name = "Budi Santoso",
            address = "Jl. Melati No.8, Bandung",
            phone = "08129876543",
            email = "budi@koperasi.com",
            password = "5678",
            role = "member",
            hasPaidMandatorySavings = false
        ),
        Member(
            id = "M003",
            name = "Citra Dewi",
            address = "Jl. Anggrek No.21, Surabaya",
            phone = "08121234567",
            email = "citra@koperasi.com",
            password = "9999",
            role = "member",
            hasPaidMandatorySavings = true
        ),
        Member(
            id = "A001",
            name = "Admin Koperasi",
            address = "Jl. Melur No.5, Jakarta",
            phone = "0813000000",
            email = "admin@koperasi.com",
            password = "admin123",
            role = "pengurus",
            hasPaidMandatorySavings = true
        )
    )

    val loans = listOf(
        Loan(id = "L001", memberId = "M001", amount = 5000000.0, interestRate = 5.0),
        Loan(id = "L002", memberId = "M002", amount = 3000000.0, interestRate = 4.5),
        Loan(id = "L003", memberId = "M003", amount = 7000000.0, interestRate = 6.0)
    )

    val loanApplications = mutableListOf<LoanApplication>()

    // ====================================================================
    // BAGIAN INI DIPERBAIKI:
    // Menambahkan 'date' dan 'description' untuk setiap transaksi.
    // ====================================================================
    val adminTransactions: List<AdminTransaction> = listOf(
        AdminTransaction(id = "T001", memberId = "M001", type = "Simpanan", amount = 25000000.0, date = "17-10-2025", description = "Simpanan Wajib"),
        AdminTransaction(id = "T002", memberId = "M002", type = "Simpanan", amount = 12000000.0, date = "16-10-2025", description = "Simpanan Pokok"),
        AdminTransaction(id = "T003", memberId = "M003", type = "Simpanan", amount = 7500000.0, date = "15-10-2025", description = "Simpanan Sukarela"),
        AdminTransaction(id = "T004", memberId = "M001", type = "Pinjaman", amount = 8000000.0, date = "14-10-2025", description = "Pinjaman Modal Usaha"),
        AdminTransaction(id = "T005", memberId = "M003", type = "Pinjaman", amount = 3000000.0, date = "13-10-2025", description = "Pinjaman Renovasi"),
        AdminTransaction(id = "T006", memberId = "M002", type = "Pinjaman", amount = 15000000.0, date = "12-10-2025", description = "Pinjaman Pendidikan")
    )

}