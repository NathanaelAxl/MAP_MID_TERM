package com.example.map_mid_term.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.R
import com.example.map_mid_term.model.DummyData

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            val member = DummyData.members.find {
                it.email == email && it.password == password
            }

            if (member != null) {
                if (member.role == "pengurus") {
                    // Jika role pengurus, arahkan ke dashboard admin (bisa fragment berbeda)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("memberId", member.id)
                    intent.putExtra("isAdmin", true) // kirim flag tambahan
                    startActivity(intent)
                } else {
                    // Jika anggota biasa
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("memberId", member.id)
                    intent.putExtra("isAdmin", false)
                    startActivity(intent)
                }
                finish()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Login Gagal")
                    .setMessage("Email atau password salah. Silakan coba lagi.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

    }
}
