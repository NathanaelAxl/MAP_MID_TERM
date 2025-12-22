package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.databinding.ActivityRegisterBinding
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

        setLoading(true)

        // 1. Buat Akun di Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {
                    saveMemberData(userId, name, email, phone)
                }
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Gagal Daftar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveMemberData(userId: String, name: String, email: String, phone: String) {
        // REVISI PENTING: Gunakan HashMap agar field-nya pasti cocok dengan fitur lain
        val memberData = hashMapOf(
            "uid" to userId,
            "name" to name,
            "email" to email,
            "phone" to phone, // Pastikan namanya 'phone', bukan 'phoneNumber'
            "role" to "member",
            "createdAt" to System.currentTimeMillis()
        )

        // REVISI FATAL: Ubah "users" menjadi "members"
        // Agar terbaca di HomeFragment dan LoanApplicationFragment
        db.collection("members").document(userId).set(memberData)
            .addOnSuccessListener {
                Toast.makeText(this, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show()
                auth.signOut() // Logout agar user login manual

                // Pindah ke Login
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Gagal simpan data: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun setLoading(isLoading: Boolean) {
        binding.btnRegister.isEnabled = !isLoading
        binding.btnRegister.text = if (isLoading) "Loading..." else "Daftar Sekarang"
    }
}