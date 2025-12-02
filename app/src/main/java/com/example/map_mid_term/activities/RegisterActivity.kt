package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.databinding.ActivityRegisterBinding
import com.example.map_mid_term.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnRegister.setOnClickListener {
            if (validateInput()) {
                registerUserToFirebase()
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun registerUserToFirebase() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Loading..."

        // 1. Buat Akun di Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {
                    // 2. Simpan Data Tambahan ke Firestore
                    val newUser = UserModel(
                        id = userId,
                        name = name,
                        email = email,
                        phone = phone,
                        role = "member"
                    )

                    db.collection("members").document(userId).set(newUser)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registrasi Berhasil!", Toast.LENGTH_LONG).show()
                            auth.signOut()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Gagal simpan data: ${e.message}", Toast.LENGTH_SHORT).show()
                            binding.btnRegister.isEnabled = true
                            binding.btnRegister.text = "Daftar"
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal Daftar: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.btnRegister.isEnabled = true
                binding.btnRegister.text = "Daftar"
            }
    }

    private fun validateInput(): Boolean {
        var isValid = true
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (name.isEmpty()) { binding.tilName.error = "Wajib diisi"; isValid = false } else binding.tilName.error = null
        if (email.isEmpty()) { binding.tilEmail.error = "Wajib diisi"; isValid = false } else binding.tilEmail.error = null
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { binding.tilEmail.error = "Email tidak valid"; isValid = false }
        if (phone.isEmpty()) { binding.tilPhone.error = "Wajib diisi"; isValid = false } else binding.tilPhone.error = null
        if (password.length < 6) { binding.tilPassword.error = "Min 6 karakter"; isValid = false } else binding.tilPassword.error = null
        if (password != confirmPassword) { binding.tilConfirmPassword.error = "Password tidak sama"; isValid = false } else binding.tilConfirmPassword.error = null

        return isValid
    }
}