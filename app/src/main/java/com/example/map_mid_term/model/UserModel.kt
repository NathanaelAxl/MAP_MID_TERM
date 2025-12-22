package com.example.map_mid_term.data.model

data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var role: String = "",
    var profileImageUrl: String? = null
)