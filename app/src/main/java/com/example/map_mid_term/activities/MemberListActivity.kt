package com.example.map_mid_term.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.adapters.MemberAdapter
import com.example.map_mid_term.model.DummyData
import com.example.map_mid_term.model.Member
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Toast
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText

class MemberListActivity : AppCompatActivity() {

    private lateinit var adapter: MemberAdapter
    private val members = DummyData.members.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        // ==== 🧭 Tombol panah back ====
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Data Anggota"

        // ==== RecyclerView dan FAB ====
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerMembers)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        adapter = MemberAdapter(
            onDeleteClick = { member -> confirmDelete(member) },
            onEditClick = { member -> showMemberDialog("Edit Anggota", member) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.submitList(members.toList())

        fabAdd.setOnClickListener {
            showMemberDialog("Tambah Anggota", null)
        }
    }

    // ==== 🧭 Navigasi balik ke LoginActivity ====
    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }

    // ==== Dialog tambah/edit anggota ====
    private fun showMemberDialog(title: String, data: Member?) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_member, null)

        val etId = view.findViewById<EditText>(R.id.etId)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPhone = view.findViewById<EditText>(R.id.etPhone)
        val etAddress = view.findViewById<EditText>(R.id.etAddress)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)

        data?.let {
            etId.setText(it.id)
            etName.setText(it.name)
            etEmail.setText(it.email)
            etPhone.setText(it.phone)
            etAddress.setText(it.address)
            etPassword.setText(it.password)
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setPositiveButton(if (data == null) "Simpan" else "Update") { _, _ ->
                val id = etId.text.toString().trim()
                val name = etName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val address = etAddress.text.toString().trim()
                val password = etPassword.text.toString().trim()

                if (id.isEmpty() || name.isEmpty() || email.isEmpty()) {
                    Toast.makeText(this, "ID, Nama, dan Email wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (data == null) {
                    val newMember = Member(id, name, address, phone, email, password, "member")
                    members.add(newMember)
                    Toast.makeText(this, "Anggota baru ditambahkan", Toast.LENGTH_SHORT).show()
                } else {
                    val idx = members.indexOfFirst { it.id == data.id }
                    if (idx != -1) {
                        members[idx] = data.copy(
                            id = id,
                            name = name,
                            email = email,
                            phone = phone,
                            address = address,
                            password = password
                        )
                        Toast.makeText(this, "Data anggota diperbarui", Toast.LENGTH_SHORT).show()
                    }
                }

                adapter.submitList(members.toList())
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // ==== Konfirmasi hapus ====
    private fun confirmDelete(member: Member) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Anggota")
            .setMessage("Yakin ingin menghapus ${member.name}?")
            .setPositiveButton("Hapus") { _, _ ->
                members.remove(member)
                adapter.submitList(members.toList())
                Toast.makeText(this, "Data ${member.name} dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }


}
