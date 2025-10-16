package com.example.map_mid_term.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.model.Transaction
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(private val transactionList: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.iv_transaction_icon)
        val descriptionText: TextView = itemView.findViewById(R.id.tv_transaction_description)
        val dateText: TextView = itemView.findViewById(R.id.tv_transaction_date)
        val amountText: TextView = itemView.findViewById(R.id.tv_transaction_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]

        holder.descriptionText.text = transaction.description
        holder.dateText.text = transaction.date

        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0
        val formattedAmount = numberFormat.format(transaction.amount)

        if (transaction.type == "credit") {
            holder.amountText.text = "+ $formattedAmount"
            holder.amountText.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
            holder.icon.setImageResource(R.drawable.ic_arrow_upward)
        } else {
            holder.amountText.text = "- $formattedAmount"
            holder.amountText.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            holder.icon.setImageResource(R.drawable.ic_arrow_downward)
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }
}