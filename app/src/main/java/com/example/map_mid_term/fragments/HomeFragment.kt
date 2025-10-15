package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var isBalanceVisible = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivToggleBalance.setOnClickListener {
            if (isBalanceVisible) {
                binding.tvTotalBalance.text = "Rp ••••••••"
                binding.ivToggleBalance.setImageResource(R.drawable.ic_eye_close)
            } else {
                binding.tvTotalBalance.text = "Rp 1.550.000,00"
                binding.ivToggleBalance.setImageResource(R.drawable.ic_eye_open)
            }
            isBalanceVisible = !isBalanceVisible
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}