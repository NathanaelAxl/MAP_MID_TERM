package com.example.map_mid_term.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.data.model.Transaction
import com.example.map_mid_term.databinding.ItemTransactionBinding // Pastikan XML item_transaction ada
import java.text.SimpleDateFormat
import java.util.Locale

// REVISI: Tambahkan parameter 'onDeleteClick' di konstruktor
class AdminTransactionAdapter(
    private var transactionList: ArrayList<Transaction>,
    private val onDeleteClick: (Transaction) -> Unit // Callback untuk tombol hapus
) : RecyclerView.Adapter<AdminTransactionAdapter.AdminTransactionViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    inner class AdminTransactionViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminTransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdminTransactionViewHolder(binding)
    }

    override fun getItemCount(): Int = transactionList.size

    override fun onBindViewHolder(holder: AdminTransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        val context = holder.itemView.context

        holder.binding.apply {
            tvTransactionDescription.text = transaction.description

            // Gunakan .date sesuai model terbaru
            tvTransactionDate.text = try {
                transaction.date?.let { dateFormat.format(it) } ?: "-"
            } catch (e: Exception) { "-" }

            val amountStr = "Rp ${"%,.0f".format(transaction.amount)}"

            if (transaction.type == "credit") {
                tvTransactionAmount.text = "+ $amountStr"
                tvTransactionAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
                ivTransactionIcon.setImageResource(android.R.drawable.stat_sys_download)
            } else {
                tvTransactionAmount.text = "- $amountStr"
                tvTransactionAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                ivTransactionIcon.setImageResource(android.R.drawable.stat_sys_upload)
            }

            // --- LOGIC TOMBOL HAPUS ---
            // Asumsi di item_transaction.xml ada tombol/icon sampah dengan ID btnDelete
            // Kalau tidak ada, kamu bisa pasang di root view (itemView.setOnClickListener)
            // Di sini saya pakai itemView.setOnLongClickListener sebagai alternatif jika tidak ada tombol delete khusus
            root.setOnLongClickListener {
                onDeleteClick(transaction)
                true
            }

            // Jika kamu punya tombol sampah di XML, uncomment ini:
            /*
            ivDelete.setOnClickListener {
                onDeleteClick(transaction)
            }
            */
        }
    }

    fun updateData(newList: List<Transaction>) {
        transactionList.clear()
        transactionList.addAll(newList)
        notifyDataSetChanged()
    }

    // REVISI: Fungsi ini yang dicari oleh Activity
    fun removeItem(transaction: Transaction) {
        val position = transactionList.indexOf(transaction)
        if (position != -1) {
            transactionList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}