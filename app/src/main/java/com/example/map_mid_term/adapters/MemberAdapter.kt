package com.example.map_mid_term.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.R
import com.example.map_mid_term.model.Member

class MemberAdapter(
private val onDeleteClick: (Member) -> Unit,
private val onEditClick: (Member) -> Unit
) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    private val members = mutableListOf<Member>()

    fun submitList(list: List<Member>) {
        members.clear()
        members.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount() = members.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.findViewById<TextView>(R.id.tvName)
        private val email = view.findViewById<TextView>(R.id.tvEmail)
        private val phone = view.findViewById<TextView>(R.id.tvPhone)
        private val address = view.findViewById<TextView>(R.id.tvAddress)
        private val btnEdit = view.findViewById<ImageButton>(R.id.btnEdit)
        private val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        fun bind(member: Member) {
            name.text = member.name
            email.text = member.email
            phone.text = member.phone
            address.text = member.address

            btnEdit.setOnClickListener { onEditClick(member) }
            btnDelete.setOnClickListener { onDeleteClick(member) }
        }
    }
}
