package com.example.map_mid_term.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.data.model.LoanApplication
import com.example.map_mid_term.databinding.ItemAdminNotificationBinding

class AdminNotificationAdapter(
    private var loanList: ArrayList<LoanApplication>,
    private val onApprove: (LoanApplication) -> Unit,
    private val onReject: (LoanApplication) -> Unit
) : RecyclerView.Adapter<AdminNotificationAdapter.AdminViewHolder>() {

    inner class AdminViewHolder(val binding: ItemAdminNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val binding = ItemAdminNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdminViewHolder(binding)
    }

    override fun getItemCount(): Int = loanList.size

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val loan = loanList[position]
        holder.binding.apply {
            tvLoanAmount.text = "Rp ${"%,.0f".format(loan.amount)}"
            tvLoanTenor.text = "Tenor: ${loan.tenor} Bulan"
            tvLoanReason.text = loan.reason

            btnApprove.setOnClickListener { onApprove(loan) }
            btnReject.setOnClickListener { onReject(loan) }
        }
    }

    fun updateData(newList: List<LoanApplication>) {
        loanList.clear()
        loanList.addAll(newList)
        notifyDataSetChanged()
    }
}