package com.example.map_mid_term.admin.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.admin.adapters.AdminLoanAdapter
import com.example.map_mid_term.admin.model.AdminDummyData
import com.example.map_mid_term.admin.model.AdminLoan
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminLoanListActivity : AppCompatActivity() {

    private lateinit var adapter: AdminLoanAdapter
    private val loans = AdminDummyData.loans

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loans)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)

        // --- PERBAIKAN DI SINI ---
        // 1. Daftarkan toolbar sebagai Action Bar
        setSupportActionBar(toolbar)

        // 2. Gunakan supportActionBar untuk mengatur judul dan tombol kembali
        supportActionBar?.title = "Data Pinjaman (Admin)"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // -------------------------

        // 3. Hapus baris ini (sudah ditangani oleh onSupportNavigateUp)
        // toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerLoans)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddLoan)

        adapter = AdminLoanAdapter(loans,
            onEdit = { loan -> showLoanDialog("Edit Pinjaman", loan) },
            onDelete = { loan ->
                loans.remove(loan)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fabAdd.setOnClickListener {
            showLoanDialog("Tambah Pinjaman", null)
        }
    }

    // 4. TAMBAHKAN FUNGSI INI (di luar onCreate)
    // Fungsi ini akan dipanggil ketika tombol panah kembali di toolbar ditekan
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun showLoanDialog(title: String, data: AdminLoan?) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_loan, null)
        val etId = view.findViewById<EditText>(R.id.etLoanId)
        val etMemberId = view.findViewById<EditText>(R.id.etMemberId)
        val etAmount = view.findViewById<EditText>(R.id.etLoanAmount)
        val etInterest = view.findViewById<EditText>(R.id.etInterestRate)

        data?.let {
            etId.setText(it.id)
            etMemberId.setText(it.memberId)
            etAmount.setText(it.amount.toString())
            etInterest.setText(it.interestRate.toString())
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setPositiveButton(if (data == null) "Simpan" else "Update") { _, _ ->
                val id = etId.text.toString().trim()
                val memberId = etMemberId.text.toString().trim()
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
                val interest = etInterest.text.toString().toDoubleOrNull() ?: 0.0

                if (data == null) {
                    loans.add(AdminLoan(id, memberId, amount, interest, "Menunggu Persetujuan"))
                    Toast.makeText(this, "Pinjaman baru ditambahkan", Toast.LENGTH_SHORT).show()
                } else {
                    val index = loans.indexOfFirst { it.id == data.id }
                    if (index != -1) {
                        loans[index] = data.copy(
                            id = id,
                            memberId = memberId,
                            amount = amount,
                            interestRate = interest
                        )
                        Toast.makeText(this, "Pinjaman diperbarui", Toast.LENGTH_SHORT).show()
                    }
                }

                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}