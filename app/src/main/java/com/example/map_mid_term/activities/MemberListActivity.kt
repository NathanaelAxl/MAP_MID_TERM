package com.example.map_mid_term.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.map_mid_term.adapters.MemberAdapter
import com.example.map_mid_term.databinding.ActivityMemberListBinding
// REVISI: Gunakan model 'Member', bukan 'User'
import com.example.map_mid_term.model.Member
import com.google.firebase.firestore.FirebaseFirestore

class MemberListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemberListBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: MemberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        fetchMembers()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        // Inisialisasi adapter dengan list Member kosong
        adapter = MemberAdapter(arrayListOf())

        binding.rvMemberList.apply {
            layoutManager = GridLayoutManager(this@MemberListActivity, 2)
            adapter = this@MemberListActivity.adapter
        }
    }

    private fun fetchMembers() {
        binding.progressBar.visibility = View.VISIBLE

        db.collection("members")
            .whereEqualTo("role", "member")
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE

                // REVISI: Gunakan ArrayList<Member>
                val memberList = ArrayList<Member>()

                for (doc in documents) {
                    try {
                        // Convert dokumen ke object Member
                        val member = doc.toObject(Member::class.java)

                        // Pastikan ID terisi (opsional, jaga-jaga kalau field uid kosong di db)
                        if (member.userId.isEmpty()) {
                            member.userId = doc.id
                        }

                        memberList.add(member)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Kirim data ke adapter (sekarang tipenya sudah cocok: List<Member>)
                adapter.updateData(memberList)

                if (memberList.isEmpty()) {
                    Toast.makeText(this, "Belum ada anggota", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
    }
}