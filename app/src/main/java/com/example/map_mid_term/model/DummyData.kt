package com.example.map_mid_term.model

object DummyData {

    val members = listOf(
        Member(
            id = "M001",
            name = "Andi Saputra",
            address = "Jl. Mawar No.12, Jakarta",
            phone = "08123456789"
        ),
        Member(
            id = "M002",
            name = "Budi Santoso",
            address = "Jl. Melati No.8, Bandung",
            phone = "08129876543"
        ),
        Member(
            id = "M003",
            name = "Citra Dewi",
            address = "Jl. Anggrek No.21, Surabaya",
            phone = "08121234567"
        )
    )

    val loans = listOf(
        Loan(
            id = "L001",
            memberId = "M001",
            amount = 5000000.0,
            interestRate = 5.0
        ),
        Loan(
            id = "L002",
            memberId = "M002",
            amount = 3000000.0,
            interestRate = 4.5
        ),
        Loan(
            id = "L003",
            memberId = "M003",
            amount = 7000000.0,
            interestRate = 6.0
        )
    )
}
