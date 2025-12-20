package com.example.map_mid_term.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.data.model.LoanApplication
import com.example.map_mid_term.databinding.ItemAdminNotificationBinding
import java.text.SimpleDateFormat
import java.util.Locale

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
            // Tampilkan Data
            // Note: Kita butuh field 'userName' di LoanApplication, atau ambil manual dari collection user.
            // Untuk simplifikasi, kita pakai ID user atau placeholder dulu jika belum ada field nama.
            tvUserName.text = "User ID: ${loan.userId.take(5)}..."

            tvLoanAmount.text = "Rp ${"%,.0f".format(loan.amount)}"
            tvLoanTenor.text = "${loan.tenor} Bulan"
            tvLoanReason.text = loan.reason

            // Format Tanggal
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            tvDate.text = try {
                sdf.format(loan.applicationDate)
            } catch (e: Exception) { "-" }

            // Listener Tombol
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