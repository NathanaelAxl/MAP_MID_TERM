package com.example.map_mid_term.model

import com.google.firebase.firestore.PropertyName

data class Member(
    // Kita gunakan var dan default value "" agar bisa di-set setelah data diambil
    var userId: String = "",

    // Pastikan nama field ini sama dengan di Firestore kamu
    // Jika di Firestore namanya "nama_lengkap", tambahkan @PropertyName("nama_lengkap")
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var role: String = "member"
) {
    // Konstruktor kosong wajib untuk Firestore deserialization
    constructor() : this("", "", "", "", "member")
}