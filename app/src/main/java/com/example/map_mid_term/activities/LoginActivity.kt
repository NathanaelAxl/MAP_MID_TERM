package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Tombol Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                performLogin(email, password)
            } else {
                Toast.makeText(this, "Isi email dan password", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Register
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin(email: String, pass: String) {
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Loading..."

        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    checkUserRoleAndRedirect(uid)
                } else {
                    // Jika UID null (sangat jarang), reset tombol
                    resetButton()
                    showErrorDialog("Login Gagal", "Gagal mendapatkan User ID.")
                }
            }
            .addOnFailureListener { e ->
                // Login gagal (password salah/email tidak ada)
                resetButton()
                showErrorDialog("Login Gagal", "Email atau password salah: ${e.message}")
            }
    }

    private fun checkUserRoleAndRedirect(uid: String) {
        db.collection("members").document(uid).get()
            .addOnSuccessListener { document ->
                resetButton() // Aktifkan tombol lagi sebelum pindah (untuk jaga-jaga)

                if (document.exists()) {
                    val role = document.getString("role")

                    if (role == "pengurus" || role == "admin") {
                        startActivity(Intent(this, AdminActivity::class.java))
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    finish() // Tutup LoginActivity
                } else {
                    showErrorDialog("Error Data", "Data user tidak ditemukan di database.")
                }
            }
            .addOnFailureListener { e ->
                resetButton()
                showErrorDialog("Error Database", "Gagal mengambil data user: ${e.message}")
            }
    }

    private fun resetButton() {
        binding.btnLogin.isEnabled = true
        binding.btnLogin.text = "Masuk"
    }

    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}