package com.example.map_mid_term.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.model.AdminTransaction

class TransactionAdminAdapter(
    private val transactionList: MutableList<AdminTransaction>
) : RecyclerView.Adapter<TransactionAdminAdapter.AdminViewHolder>() {

    class AdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTransactionId: TextView = itemView.findViewById(R.id.tvTransactionId)
        val tvTransactionMember: TextView = itemView.findViewById(R.id.tvTransactionMember)
        val tvTransactionType: TextView = itemView.findViewById(R.id.tvTransactionType)
        val tvTransactionAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteTransaction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_admin, parent, false)
        return AdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.tvTransactionId.text = "ID: ${transaction.id}"
        holder.tvTransactionMember.text = "Member: ${transaction.memberId}"
        holder.tvTransactionType.text = "Jenis: ${transaction.type}"
        holder.tvTransactionAmount.text = "Rp ${transaction.amount}"

        holder.btnDelete.setOnClickListener {
            transactionList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int = transactionList.size
}
