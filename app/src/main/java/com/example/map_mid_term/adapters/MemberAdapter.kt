package com.example.map_mid_term.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.map_mid_term.R
import com.example.map_mid_term.model.Member

class MemberAdapter(
    private var memberList: List<Member>
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: ImageView = itemView.findViewById(R.id.ivMemberPhoto)
        val tvName: TextView = itemView.findViewById(R.id.tvMemberName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvMemberEmail)
        val tvPhone: TextView = itemView.findViewById(R.id.tvMemberPhone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = memberList[position]

        holder.tvName.text = member.name
        holder.tvEmail.text = member.email
        holder.tvPhone.text = member.phone

        val imageString = member.profileImageUrl

        if (!imageString.isNullOrEmpty()) {
            if (imageString.startsWith("http")) {
                // Tipe URL
                holder.ivPhoto.load(imageString) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
            } else {
                // Tipe Base64
                try {
                    // Coba decode dengan NO_WRAP dulu (sesuai cara simpan baru)
                    var decodedBytes = Base64.decode(imageString, Base64.NO_WRAP)

                    // Kalau gagal/kosong, coba DEFAULT (buat jaga-jaga data lama)
                    if (decodedBytes == null || decodedBytes.isEmpty()) {
                        decodedBytes = Base64.decode(imageString, Base64.DEFAULT)
                    }

                    val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                    holder.ivPhoto.load(decodedBitmap) {
                        crossfade(true)
                        transformations(CircleCropTransformation())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    holder.ivPhoto.setImageResource(android.R.drawable.sym_def_app_icon)
                }
            }
        } else {
            holder.ivPhoto.setImageResource(android.R.drawable.sym_def_app_icon)
        }
    }

    override fun getItemCount(): Int = memberList.size

    fun updateData(newList: List<Member>) {
        memberList = newList
        notifyDataSetChanged()
    }
}