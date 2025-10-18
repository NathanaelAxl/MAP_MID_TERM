package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listener untuk tombol Daftar
        binding.btnRegister.setOnClickListener {
            if (validateInput()) {
                // TODO: Nanti di sini logika untuk menyimpan data ke database
                Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_LONG).show()

                // Kembali ke halaman Login setelah berhasil
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Tutup activity ini
            }
        }

        // Listener untuk teks "Login"
        binding.tvLogin.setOnClickListener {
            // Cukup tutup activity ini untuk kembali ke halaman Login
            finish()
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Validasi Nama
        if (name.isEmpty()) {
            binding.tilName.error = "Nama lengkap tidak boleh kosong"
            isValid = false
        } else {
            binding.tilName.error = null
        }

        // Validasi Email
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email tidak boleh kosong"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Format email tidak valid"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        // Validasi Nomor Telepon
        if (phone.isEmpty()) {
            binding.tilPhone.error = "Nomor telepon tidak boleh kosong"
            isValid = false
        } else {
            binding.tilPhone.error = null
        }

        // Validasi Password
        if (password.isEmpty()) {
            binding.tilPassword.error = "Kata sandi tidak boleh kosong"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Kata sandi minimal 6 karakter"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        // Validasi Konfirmasi Password
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "Konfirmasi kata sandi tidak boleh kosong"
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Kata sandi tidak cocok"
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }

        return isValid
    }
}
