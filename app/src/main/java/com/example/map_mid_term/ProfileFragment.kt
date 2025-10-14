package com.example.map_mid_term

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val textView = TextView(requireContext())
        textView.text = "Ini halaman Profil Anggota"
        textView.textSize = 18f
        textView.setPadding(16, 16, 16, 16)
        return textView
    }
}
