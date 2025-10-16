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
}

