package com.example.map_mid_term.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CenterCrop
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
            // MENGGUNAKAN ID DARI item_transaction.xml
            tvTransactionDescription.text = transaction.title
            tvTransactionDate.text = transaction.timestamp?.let { dateFormat.format(it) } ?: "N/A"

            // Mengatur warna, ikon, dan format jumlah uang berdasarkan tipe transaksi
            if (transaction.type == "credit") {
                tvTransactionAmount.text = "+ Rp${"%,.0f".format(transaction.amount)}"
                tvTransactionAmount.setTextColor(ContextCompat.getColor(context, R.color.green))
                ivTransactionIcon.setImageResource(R.drawable.ic_arrow_downward)
            } else { // "debit"
                tvTransactionAmount.text = "- Rp${"%,.0f".format(transaction.amount)}"
                tvTransactionAmount.setTextColor(ContextCompat.getColor(context, R.color.red))
                ivTransactionIcon.setImageResource(R.drawable.ic_arrow_upward)
            }

            // Memuat gambar bukti transaksi menggunakan Coil
            if (!transaction.proofImageUrl.isNullOrEmpty()) {
                ivProofImage.visibility = View.VISIBLE
                ivProofImage.load(transaction.proofImageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_image_placeholder)
                    error(R.drawable.ic_image_placeholder)
                    // Sekarang `transformations` tidak akan bingung lagi
                    transformations(CenterCrop())
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
