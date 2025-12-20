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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdminAdapter(
    private var transactionList: MutableList<Transaction>,
    // Callback: Supaya Activity tahu item mana yang mau dihapus
    private val onDeleteClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdminAdapter.AdminViewHolder>() {

    class AdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ID ini merujuk ke 'item_transaction_admin.xml'
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvTransactionType: TextView = itemView.findViewById(R.id.tvTransactionType)
        val tvTransactionAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivTransactionIcon)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteTransaction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_admin, parent, false)
        return AdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val transaction = transactionList[position]

        // 1. Tampilkan User ID
        holder.tvUserName.text = transaction.userId

        // 2. Format Tanggal
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateString = try {
            dateFormat.format(transaction.timestamp)
        } catch (e: Exception) { "-" }

        // 3. Deskripsi Otomatis (Mengatasi error 'unresolved reference: description')
        val deskripsi = if (transaction.type == "credit") "Simpanan" else "Pinjaman/Angsuran"
        holder.tvTransactionType.text = "$deskripsi • $dateString"

        // 4. Nominal & Warna
        val formattedAmount = formatRupiah(transaction.amount)
        if (transaction.type == "credit") {
            // Uang Masuk (Hijau)
            holder.tvTransactionAmount.text = "+ $formattedAmount"
            holder.tvTransactionAmount.setTextColor(Color.parseColor("#4CAF50"))
            holder.ivIcon.setImageResource(android.R.drawable.stat_sys_download)
        } else {
            // Uang Keluar (Merah)
            holder.tvTransactionAmount.text = "- $formattedAmount"
            holder.tvTransactionAmount.setTextColor(Color.RED)
            holder.ivIcon.setImageResource(android.R.drawable.stat_sys_upload)
        }

        // 5. Tombol Hapus
        holder.btnDelete.setOnClickListener {
            onDeleteClick(transaction)
        }
    }

    override fun getItemCount(): Int = transactionList.size

    // Fungsi Helper
    fun removeItem(transaction: Transaction) {
        val position = transactionList.indexOf(transaction)
        if (position != -1) {
            transactionList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateData(newList: List<Transaction>) {
        transactionList.clear()
        transactionList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun formatRupiah(number: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(number).replace("Rp", "Rp ")
    }
}