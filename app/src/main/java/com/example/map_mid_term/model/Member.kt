package com.example.map_mid_term.model

data class Member(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val email: String,
    val password: String,
    val role: String,
    var hasPaidMandatorySavings: Boolean = false
)
