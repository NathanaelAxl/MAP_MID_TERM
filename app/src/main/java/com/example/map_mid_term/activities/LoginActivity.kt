package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.databinding.ActivityLoginBinding
import com.example.map_mid_term.model.DummyData

class LoginActivity : AppCompatActivity() {

    // Menggunakan ViewBinding
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val member = DummyData.members.find {
                it.email == email && it.password == password
            }

            if (member != null) {
                if (member.role == "pengurus") {
                    // Login sebagai admin
                    val intent = Intent(this, AdminActivity::class.java)
                    intent.putExtra("memberId", member.id)
                    startActivity(intent)
                } else {
                    // Login sebagai member biasa
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("memberId", member.id)
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

        // FUNGSI KLIK UNTUK PINDAH KE HALAMAN REGISTRASI
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
