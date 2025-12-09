package com.example.map_mid_term.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map_mid_term.adapters.MemberAdapter
import com.example.map_mid_term.databinding.ActivityMembersBinding // Sesuaikan nama layout
import com.example.map_mid_term.model.Member
import com.google.firebase.firestore.FirebaseFirestore

class MemberListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMembersBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: MemberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchMembers()

        // Tombol Back (asumsi ada di toolbar)
        // binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = MemberAdapter(arrayListOf())
        binding.rvMembers.layoutManager = LinearLayoutManager(this)
        binding.rvMembers.adapter = adapter
    }

    private fun fetchMembers() {
        binding.progressBar.visibility = View.VISIBLE

        db.collection("members")
            .get() // Ambil semua anggota
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE
                val list = ArrayList<Member>()
                for (doc in documents) {
                    val member = doc.toObject(Member::class.java)
                    member.userId = doc.id
                    list.add(member)
                }
                adapter.updateData(list)

                if (list.isEmpty()) {
                    Toast.makeText(this, "Belum ada anggota", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}