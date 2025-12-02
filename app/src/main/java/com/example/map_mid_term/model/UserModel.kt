package com.example.map_mid_term.model

data class UserModel(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var role: String = "member" // Default role user baru
)