package com.example.map_mid_term.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentVirtualAccountBinding

class VirtualAccountFragment : Fragment() {

    private var _binding: FragmentVirtualAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVirtualAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCopyVa.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val vaNumber = binding.tvVaNumber.text.toString().replace(" ", "")
            val clip = ClipData.newPlainText("VA Number", vaNumber)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Nomor Virtual Account disalin!", Toast.LENGTH_SHORT).show()
        }

        binding.btnFinishPayment.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}