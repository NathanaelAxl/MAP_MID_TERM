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
            tvTransactionDescription.text = transaction.title

            // Handle tanggal null dengan aman
            tvTransactionDate.text = try {
                transaction.timestamp?.let { dateFormat.format(it) } ?: "N/A"
            } catch (e: Exception) { "-" }

            if (transaction.type == "credit") {
                tvTransactionAmount.text = "+ Rp${"%,.0f".format(transaction.amount)}"
                // Menggunakan warna standar Android biar aman
                tvTransactionAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
                ivTransactionIcon.setImageResource(R.drawable.ic_arrow_downward)
            } else {
                tvTransactionAmount.text = "- Rp${"%,.0f".format(transaction.amount)}"
                tvTransactionAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                ivTransactionIcon.setImageResource(R.drawable.ic_arrow_upward)
            }

            // --- LOAD GAMBAR ---
            if (!transaction.proofImageUrl.isNullOrEmpty()) {
                ivProofImage.visibility = View.VISIBLE
                val imageString = transaction.proofImageUrl!!

                // Cek apakah Base64 atau URL
                if (imageString.length > 200 && !imageString.startsWith("http")) {
                    try {
                        val decodedString = Base64.decode(imageString, Base64.DEFAULT)
                        val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                        ivProofImage.load(decodedBitmap) {
                            crossfade(true)
                            // TIDAK ADA transformation() DISINI. SUDAH DIATUR DI XML.
                        }
                    } catch (e: Exception) {
                        ivProofImage.visibility = View.GONE
                    }
                } else {
                    ivProofImage.load(imageString) {
                        crossfade(true)
                        // placeholder(R.drawable.ic_image_placeholder) // Uncomment jika punya icon ini
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