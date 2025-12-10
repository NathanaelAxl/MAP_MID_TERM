package com.example.map_mid_term.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.map_mid_term.databinding.ItemMemberBinding
import com.example.map_mid_term.model.Member

class MemberAdapter(private val memberList: ArrayList<Member>) :
    RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemberViewHolder(binding)
    }

    override fun getItemCount(): Int = memberList.size

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = memberList[position]
        holder.binding.apply {
            tvMemberName.text = member.name
            tvMemberEmail.text = member.email
            tvMemberPhone.text = member.phone
        }
    }

    fun updateData(newList: List<Member>) {
        memberList.clear()
        memberList.addAll(newList)
        notifyDataSetChanged()
    }
}