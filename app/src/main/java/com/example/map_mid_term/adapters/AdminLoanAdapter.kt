package com.example.map_mid_term.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.data.model.LoanApplication
import com.google.android.material.button.MaterialButton

class AdminLoanAdapter(
    private var loanList: MutableList<LoanApplication>,
    // Callback Aksi
    private val onDeleteClick: (LoanApplication) -> Unit,
    private val onApproveClick: (LoanApplication) -> Unit,
    private val onRejectClick: (LoanApplication) -> Unit
) : RecyclerView.Adapter<AdminLoanAdapter.LoanViewHolder>() {

    class LoanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStatus: TextView = itemView.findViewById(R.id.tvLoanStatus)
        val tvName: TextView = itemView.findViewById(R.id.tvBorrowerName)
        val tvAmount: TextView = itemView.findViewById(R.id.tvLoanAmount)
        val tvTenor: TextView = itemView.findViewById(R.id.tvLoanTenor)
        val pbProgress: ProgressBar = itemView.findViewById(R.id.pbLoanProgress)
        val tvProgressText: TextView = itemView.findViewById(R.id.tvProgressText)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteLoan)

        // Layout Tombol Aksi
        val layoutActions: LinearLayout = itemView.findViewById(R.id.layoutActionButtons)
        val btnApprove: MaterialButton = itemView.findViewById(R.id.btnApprove)
        val btnReject: MaterialButton = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_loan, parent, false)
        return LoanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
        val loan = loanList[position]

        // 1. Data Teks
        holder.tvName.text = if (loan.userName.isNotEmpty()) loan.userName else "User: ${loan.userId}"
        holder.tvAmount.text = "Rp ${"%,.0f".format(loan.amount)}"
        holder.tvTenor.text = "• ${loan.tenor} Bulan"

        // 2. Status & Warna
        holder.tvStatus.text = loan.status.uppercase()
        when (loan.status) {
            "approved" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#1976D2")) // Biru
                holder.tvStatus.setBackgroundColor(Color.parseColor("#E3F2FD"))
                // Sembunyikan tombol aksi kalau sudah approved
                holder.layoutActions.visibility = View.GONE
            }
            "paid" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#388E3C")) // Hijau
                holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"))
                holder.layoutActions.visibility = View.GONE
            }
            "rejected" -> {
                holder.tvStatus.setTextColor(Color.RED)
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"))
                holder.layoutActions.visibility = View.GONE
            }
            "pending" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#F57C00")) // Orange
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FFF3E0"))
                // TAMPILKAN TOMBOL AKSI HANYA DI PENDING
                holder.layoutActions.visibility = View.VISIBLE
            }
            else -> {
                holder.tvStatus.setTextColor(Color.DKGRAY)
                holder.layoutActions.visibility = View.GONE
            }
        }

        // 3. Progress Bar
        val progress = if (loan.totalPayable > 0) {
            ((loan.paidAmount / loan.totalPayable) * 100).toInt()
        } else 0
        holder.pbProgress.progress = progress
        holder.tvProgressText.text = "Terbayar: $progress%"

        // 4. Listeners (Tombol ditekan)
        holder.btnDelete.setOnClickListener { onDeleteClick(loan) }
        holder.btnApprove.setOnClickListener { onApproveClick(loan) }
        holder.btnReject.setOnClickListener { onRejectClick(loan) }
    }

    override fun getItemCount(): Int = loanList.size

    fun updateData(newList: List<LoanApplication>) {
        loanList.clear()
        loanList.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeItem(loan: LoanApplication) {
        val position = loanList.indexOf(loan)
        if (position != -1) {
            loanList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}