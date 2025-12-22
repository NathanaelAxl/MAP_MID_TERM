package com.example.map_mid_term.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.data.model.LoanApplication

class LoanAdapter(
    private val loans: MutableList<LoanApplication>,
    private val onEdit: (LoanApplication) -> Unit,
    private val onDelete: (LoanApplication) -> Unit
) : RecyclerView.Adapter<LoanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLoanId: TextView = view.findViewById(R.id.tvLoanId)
        val tvMemberId: TextView = view.findViewById(R.id.tvMemberId)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvInterest: TextView = view.findViewById(R.id.tvInterest)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEditLoan)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteLoan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_loan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val loan = loans[position]

        // PERBAIKAN DI SINI:
        // 1. Ganti loanId menjadi id
        holder.tvLoanId.text = "ID: ${loan.id}"

        // 2. Hapus referensi 'name' karena tidak ada di model. Cukup tampilkan User ID.
        holder.tvMemberId.text = "User ID: ${loan.userId}"

        holder.tvAmount.text = "Rp ${loan.amount}"
        holder.tvInterest.text = "Tenor: ${loan.tenor} Bulan"

        holder.btnEdit.setOnClickListener { onEdit(loan) }
        holder.btnDelete.setOnClickListener { onDelete(loan) }
    }

    override fun getItemCount() = loans.size
}