package com.example.map_mid_term.model

import com.google.firebase.firestore.PropertyName

data class Member(
    @PropertyName("uid")
    var userId: String = "",

    @PropertyName("name")
    var name: String = "",

    @PropertyName("email")
    var email: String = "",

    @PropertyName("phone")
    var phone: String = "",

    @PropertyName("role")
    var role: String = "member",

    // TAMBAHKAN @PropertyName INI
    // Agar Firestore tahu persis field mana yang harus diambil
    @PropertyName("profileImageUrl")
    var profileImageUrl: String? = null
) {
    // Konstruktor kosong wajib
    constructor() : this("", "", "", "", "member", null)
}