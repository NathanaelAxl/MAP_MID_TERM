package com.example.map_mid_term.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.databinding.ActivityAdminAnnouncementBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AdminAnnouncementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAnnouncementBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAnnouncementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnSave.setOnClickListener {
            val text = binding.etAnnouncement.text.toString().trim()
            if (text.isNotEmpty()) {
                saveAnnouncement(text)
            } else {
                Toast.makeText(this, "Isi pengumuman tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveAnnouncement(text: String) {
        binding.btnSave.isEnabled = false
        binding.btnSave.text = "Menyimpan..."

        val data = hashMapOf(
            "message" to text,
            "timestamp" to Date(),
            "updatedBy" to "Admin"
        )

        // Simpan ke dokumen statis 'latest_info' agar mudah diambil User
        db.collection("announcements").document("latest_info")
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Pengumuman berhasil diterbitkan!", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener {
                binding.btnSave.isEnabled = true
                binding.btnSave.text = "Terbitkan Pengumuman"
                Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}