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

    // Inisialisasi Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Opsi: Auto login jika user sebelumnya sudah pernah login
        if (auth.currentUser != null) {
            checkUserRoleAndRedirect(auth.currentUser!!.uid)
            return
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                performLogin(email, password)
            } else {
                Toast.makeText(this, "Isi email dan password", Toast.LENGTH_SHORT).show()
            }
        }

        // FUNGSI KLIK UNTUK PINDAH KE HALAMAN REGISTRASI
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin(email: String, pass: String) {
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Loading..."

        // 1. Cek Email & Password di Firebase Authentication
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    // Jika Auth sukses, lanjutkan cek Role di Firestore
                    checkUserRoleAndRedirect(uid)
                }
            }
            .addOnFailureListener {
                // Login gagal
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = "Masuk"
                showErrorDialog("Login Gagal", "Email atau password salah. Silakan coba lagi.")
            }
    }

    private fun checkUserRoleAndRedirect(uid: String) {
        // 2. Ambil data user dari koleksi "members" di Firestore
        db.collection("members").document(uid).get()
            .addOnSuccessListener { document ->
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = "Masuk"

                if (document.exists()) {
                    val role = document.getString("role")

                    if (role == "pengurus") {
                        // Login sebagai admin
                        startActivity(Intent(this, AdminActivity::class.java))
                    } else {
                        // Login sebagai member biasa
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    finish()
                } else {
                    // Ini seharusnya tidak terjadi jika Register sukses
                    showErrorDialog("Error Data", "Data user tidak ditemukan di database.")
                }
            }
            .addOnFailureListener {
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = "Masuk"
                showErrorDialog("Error Database", "Gagal mengambil data user: ${it.message}")
            }
    }

    // Fungsi pengganti AlertDialog biasa agar tidak mengganggu UI
    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}