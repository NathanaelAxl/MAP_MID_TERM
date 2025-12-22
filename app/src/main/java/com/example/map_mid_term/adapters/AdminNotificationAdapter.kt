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

            // 1. Tampilkan Nama (Prioritas userName, kalau kosong baru pakai userId)
            val displayName = if (loan.userName.isNotEmpty()) loan.userName else "ID: ${loan.userId}"
            tvUserName.text = displayName

            // 2. Format Uang
            tvLoanAmount.text = "Rp ${"%,.0f".format(loan.amount)}"

            // 3. Tenor & Alasan
            tvLoanTenor.text = "${loan.tenor} Bulan"
            tvLoanReason.text = loan.reason

            // 4. Format Tanggal (PERBAIKAN ERROR DI SINI)
            // Ganti 'applicationDate' menjadi 'requestDate'
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

            // Cek null dulu biar aman
            if (loan.requestDate != null) {
                tvDate.text = sdf.format(loan.requestDate!!)
            } else {
                tvDate.text = "-"
            }

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