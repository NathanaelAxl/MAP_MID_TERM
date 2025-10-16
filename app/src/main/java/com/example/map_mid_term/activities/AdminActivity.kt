package com.example.map_mid_term.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.R

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Ambil ID admin kalau mau pakai
        val adminId = intent.getStringExtra("memberId")

        // TODO: nanti bisa tambahkan logika menampilkan data laporan, saldo, dsb
    }
}
