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
            // --- JUDUL OTOMATIS (PENGGANTI TITLE) ---
            val judulTransaksi = if (transaction.type == "credit") {
                "Simpanan Masuk"
            } else {
                "Pembayaran/Angsuran"
            }
            tvTransactionDescription.text = judulTransaksi

            // --- TANGGAL ---
            tvTransactionDate.text = try {
                transaction.timestamp?.let { dateFormat.format(it) } ?: "N/A"
            } catch (e: Exception) { "-" }

            // --- NOMINAL & WARNA ---
            if (transaction.type == "credit") {
                // Uang Masuk (Hijau)
                tvTransactionAmount.text = "+ Rp${"%,.0f".format(transaction.amount)}"
                tvTransactionAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
                // Pakai icon bawaan Android
                ivTransactionIcon.setImageResource(android.R.drawable.stat_sys_download)
            } else {
                // Uang Keluar (Merah)
                tvTransactionAmount.text = "- Rp${"%,.0f".format(transaction.amount)}"
                tvTransactionAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                // Pakai icon bawaan Android
                ivTransactionIcon.setImageResource(android.R.drawable.stat_sys_upload)
            }

            // --- LOAD GAMBAR BUKTI (Base64 / URL) ---
            // Asumsi: Di model Transaction kamu ada field 'proofImageUrl'
            // Jika error di baris ini, pastikan Transaction.kt punya var proofImageUrl: String? = null
            if (transaction.proofImageUrl != null && transaction.proofImageUrl!!.isNotEmpty()) {
                ivProofImage.visibility = View.VISIBLE
                val imageString = transaction.proofImageUrl!!

                if (imageString.length > 200 && !imageString.startsWith("http")) {
                    try {
                        // Decode Base64
                        val decodedString = Base64.decode(imageString, Base64.DEFAULT)
                        val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                        ivProofImage.load(decodedBitmap) {
                            crossfade(true)
                        }
                    } catch (e: Exception) {
                        ivProofImage.visibility = View.GONE
                    }
                } else {
                    // Load URL Biasa
                    ivProofImage.load(imageString) {
                        crossfade(true)
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