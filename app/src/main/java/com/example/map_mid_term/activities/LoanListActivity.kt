package com.example.map_mid_term.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.adapters.LoanAdapter
import com.example.map_mid_term.model.DummyData
import com.example.map_mid_term.model.Loan
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LoanListActivity : AppCompatActivity() {

    private lateinit var adapter: LoanAdapter
    private val loans = DummyData.loans.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loans)

        // === Toolbar ===
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Data Pinjaman"

        val rvLoans = findViewById<RecyclerView>(R.id.recyclerLoans)
        val fabAddLoan = findViewById<FloatingActionButton>(R.id.fabAddLoan)

        adapter = LoanAdapter(loans,
            onEdit = { loan -> showLoanDialog("Edit Pinjaman", loan) },
            onDelete = { loan ->
                loans.remove(loan)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Pinjaman dihapus", Toast.LENGTH_SHORT).show()
            }
        )

        rvLoans.layoutManager = LinearLayoutManager(this)
        rvLoans.adapter = adapter

        fabAddLoan.setOnClickListener {
            showLoanDialog("Tambah Pinjaman", null)
        }
    }

    private fun showLoanDialog(title: String, data: Loan?) {
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

                if (id.isEmpty() || memberId.isEmpty()) {
                    Toast.makeText(this, "ID dan Member wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (data == null) {
                    val newLoan = Loan(id, memberId, amount, interest)
                    loans.add(newLoan)
                } else {
                    val idx = loans.indexOfFirst { it.id == data.id }
                    if (idx != -1) loans[idx] = data.copy(
                        id = id,
                        memberId = memberId,
                        amount = amount,
                        interestRate = interest
                    )
                }

                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
