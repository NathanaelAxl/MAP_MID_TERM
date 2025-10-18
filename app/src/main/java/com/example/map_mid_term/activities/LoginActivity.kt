package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.databinding.ActivityLoginBinding
import com.example.map_mid_term.model.DummyData

class   LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val member = DummyData.members.find {
                it.email == email && it.password == password
            }

            if (member != null) {
                // 🔹 Simpan memberId ke MainActivity (opsional tambahan)
                MainActivity.memberId = member.id

                // 🔹 Cek role
                val intent = if (member.role == "pengurus") {
                    Intent(this, AdminActivity::class.java)
                } else {
                    Intent(this, MainActivity::class.java)
                }

                // 🔹 Kirim memberId lewat intent juga
                intent.putExtra("memberId", member.id)
                startActivity(intent)
                finish()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Login Gagal")
                    .setMessage("Email atau password salah. Silakan coba lagi.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
