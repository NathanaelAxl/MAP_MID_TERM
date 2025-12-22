package com.example.map_mid_term.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.map_mid_term.R
import com.example.map_mid_term.data.model.Transaction
import com.example.map_mid_term.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(private var transactionList: ArrayList<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    // Format tanggal: 22 Dec 2025
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    inner class TransactionViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun getItemCount(): Int = transactionList.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        val context = holder.itemView.context

        holder.binding.apply {

            // --- 1. DESCRIPTION (Sesuai Model: var description) ---
            if (transaction.description.isNotEmpty()) {
                tvTransactionDescription.text = transaction.description
            } else {
                tvTransactionDescription.text = "Transaksi"
            }

            // --- 2. DATE (Sesuai Model: var date: Date?) ---
            // Karena tipe datanya sudah 'Date?', kita tidak perlu .toDate() atau konversi Long.
            // Langsung format saja.
            val dateObj = transaction.date
            if (dateObj != null) {
                tvTransactionDate.text = dateFormat.format(dateObj)
            } else {
                tvTransactionDate.text = "-"
            }

            // --- 3. AMOUNT & TYPE (Logic Warna & Icon) ---
            // Kita cek tipe transaksi (Case Insensitive biar aman)
            val type = transaction.type
            val isIncome = type.equals("credit", ignoreCase = true) ||
                    type.equals("Pemasukan", ignoreCase = true) ||
                    type.equals("Simpanan", ignoreCase = true)

            if (isIncome) {
                // Uang Masuk: Hijau
                tvTransactionAmount.text = "+ Rp${"%,.0f".format(transaction.amount)}"
                tvTransactionAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))

                ivTransactionIcon.setImageResource(android.R.drawable.stat_sys_download)
                ivTransactionIcon.setColorFilter(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            } else {
                // Uang Keluar: Merah
                tvTransactionAmount.text = "- Rp${"%,.0f".format(transaction.amount)}"
                tvTransactionAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))

                ivTransactionIcon.setImageResource(android.R.drawable.stat_sys_upload)
                ivTransactionIcon.setColorFilter(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            }

            // --- 4. PROOF IMAGE (Bukti Transfer) ---
            val proofString = transaction.proofImageUrl

            if (!proofString.isNullOrEmpty()) {
                ivProofImage.visibility = View.VISIBLE

                if (proofString.startsWith("http")) {
                    // Jika URL (Internet)
                    ivProofImage.load(proofString) {
                        crossfade(true)
                        placeholder(android.R.drawable.ic_menu_gallery)
                    }
                } else {
                    // Jika Base64 (String panjang)
                    try {
                        val decodedBytes = Base64.decode(proofString, Base64.DEFAULT)
                        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                        ivProofImage.load(decodedBitmap) {
                            crossfade(true)
                        }
                    } catch (e: Exception) {
                        ivProofImage.visibility = View.GONE
                    }
                }
            } else {
                ivProofImage.visibility = View.GONE
            }
        }
    }

    fun updateData(newTransactionList: List<Transaction>) {
        transactionList.clear()
        transactionList.addAll(newTransactionList)
        notifyDataSetChanged()
    }
}