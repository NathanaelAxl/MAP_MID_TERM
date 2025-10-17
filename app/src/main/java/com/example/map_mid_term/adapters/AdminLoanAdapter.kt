package com.example.map_mid_term.admin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.admin.model.AdminLoan

class AdminLoanAdapter(
    private val loans: List<AdminLoan>,
    private val onEdit: (AdminLoan) -> Unit,
    private val onDelete: (AdminLoan) -> Unit
) : RecyclerView.Adapter<AdminLoanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLoanId: TextView = view.findViewById(R.id.tvLoanId)
        val tvMemberId: TextView = view.findViewById(R.id.tvMemberId)
        val tvAmount: TextView = view.findViewById(R.id.tvLoanAmount)
        val tvInterest: TextView = view.findViewById(R.id.tvInterestRate)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEditLoan)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteLoan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_loan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val loan = loans[position]
        holder.tvLoanId.text = "ID Pinjaman: ${loan.id}"
        holder.tvMemberId.text = "Anggota: ${loan.memberId}"
        holder.tvAmount.text = "Rp ${loan.amount}"
        holder.tvInterest.text = "Bunga: ${loan.interestRate}%"
        holder.tvStatus.text = "Status: ${loan.status}"

        holder.btnEdit.setOnClickListener { onEdit(loan) }
        holder.btnDelete.setOnClickListener { onDelete(loan) }
    }

    override fun getItemCount() = loans.size
}
