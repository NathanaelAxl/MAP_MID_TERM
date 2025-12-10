package com.example.map_mid_term.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.model.AdminNotification

class AdminNotificationAdapter(
    private val items: List<AdminNotification>,
    private val onItemClick: (AdminNotification) -> Unit
) : RecyclerView.Adapter<AdminNotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLoanId: TextView = view.findViewById(R.id.tvLoanId)
        val tvMemberId: TextView = view.findViewById(R.id.tvMemberId)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvLoanId.text = "ID Pinjaman: ${item.loanId}"
        holder.tvMemberId.text = "Anggota: ${item.memberId}"
        holder.tvAmount.text = "Jumlah: Rp ${item.amount}"
        holder.tvStatus.text = "Status: ${item.status}"

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = items.size
}
