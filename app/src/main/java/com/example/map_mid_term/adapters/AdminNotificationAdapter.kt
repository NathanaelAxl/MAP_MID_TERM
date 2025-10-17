package com.example.map_mid_term.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.model.Loan

class AdminNotificationAdapter(
    private val loanList: List<Loan>,
    private val onItemClick: (Loan) -> Unit
) : RecyclerView.Adapter<AdminNotificationAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLoanId: TextView = view.findViewById(R.id.tvLoanId)
        val tvMemberId: TextView = view.findViewById(R.id.tvMemberId)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnDetail: Button = view.findViewById(R.id.btnDetail)
        val cardContainer: CardView = view.findViewById(R.id.cardContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val loan = loanList[position]

        holder.tvLoanId.text = "ID Pinjaman: ${loan.id}"
        holder.tvMemberId.text = "Anggota: ${loan.memberId}"
        holder.tvAmount.text = "Jumlah: Rp ${loan.amount}"
        holder.tvStatus.text = "Status: ${loan.status}"

        // Klik tombol Detail → buka halaman detail
        holder.btnDetail.setOnClickListener {
            onItemClick(loan)
        }
    }

    override fun getItemCount(): Int = loanList.size
}
