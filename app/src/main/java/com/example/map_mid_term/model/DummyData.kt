package com.example.map_mid_term.model

object DummyData {

    val members = listOf(
        Member(
            id = "M001",
            name = "Andi Saputra",
            address = "Jl. Mawar No.12, Jakarta",
            phone = "08123456789",
            email = "andi@koperasi.com",
            password = "1234"
        ),
        Member(
            id = "M002",
            name = "Budi Santoso",
            address = "Jl. Melati No.8, Bandung",
            phone = "08129876543",
            email = "budi@koperasi.com",
            password = "5678"
        ),
        Member(
            id = "M003",
            name = "Citra Dewi",
            address = "Jl. Anggrek No.21, Surabaya",
            phone = "08121234567",
            email = "citra@koperasi.com",
            password = "9999"
        )
    )

    val loans = listOf(
        Loan("L001", "M001", 5000000.0, 5.0),
        Loan("L002", "M002", 3000000.0, 4.5),
        Loan("L003", "M003", 7000000.0, 6.0)
    )
}
