package com.example.map_mid_term.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.data.model.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class AdminTransactionAdapter(
    private var transactionList: MutableList<Transaction>,
    private val onDeleteClick: (Transaction) -> Unit
) : RecyclerView.Adapter<AdminTransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvUserId: TextView = itemView.findViewById(R.id.tvUserId)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)

        // View Baru yang kita tambahkan di XML:
        val ivIcon: ImageView = itemView.findViewById(R.id.ivTransactionIcon)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteTransaction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val trx = transactionList[position]

        holder.tvDescription.text = trx.description
        holder.tvUserId.text = "ID: ${trx.userId}"
        holder.tvStatus.text = trx.status.uppercase()

        // Format Tanggal
        if (trx.timestamp != null) {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            holder.tvDate.text = sdf.format(trx.timestamp!!)
        } else {
            holder.tvDate.text = "-"
        }

        // Format Uang & Logika Icon
        val formattedAmount = "Rp ${"%,.0f".format(trx.amount)}"

        if (trx.type == "credit") {
            // UANG MASUK (Hijau & Panah Atas)
            holder.tvAmount.text = "+ $formattedAmount"
            holder.tvAmount.setTextColor(Color.parseColor("#2E7D32")) // Hijau

            holder.ivIcon.setImageResource(R.drawable.ic_arrow_upward) // Panah Naik
            holder.ivIcon.setColorFilter(Color.parseColor("#2E7D32")) // Icon Hijau
        } else {
            // UANG KELUAR (Merah & Panah Bawah)
            holder.tvAmount.text = "- $formattedAmount"
            holder.tvAmount.setTextColor(Color.parseColor("#D32F2F")) // Merah

            holder.ivIcon.setImageResource(R.drawable.ic_arrow_downward) // Panah Turun
            holder.ivIcon.setColorFilter(Color.parseColor("#D32F2F")) // Icon Merah
        }

        // Warna Status Badge
        if (trx.status == "success" || trx.status == "verified") {
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800"))
        }

        // Logika Klik Tombol Hapus
        holder.btnDelete.setOnClickListener {
            onDeleteClick(trx)
        }
    }

    override fun getItemCount(): Int = transactionList.size

    fun updateData(newList: List<Transaction>) {
        transactionList.clear()
        transactionList.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeItem(transaction: Transaction) {
        val position = transactionList.indexOf(transaction)
        if (position != -1) {
            transactionList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}