package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.activities.MainActivity
import com.example.map_mid_term.databinding.FragmentHomeBinding
import com.example.map_mid_term.model.DummyData

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

        val parentActivity = activity as? MainActivity
        val memberId = parentActivity?.memberId

        if (memberId != null) {
            val member = DummyData.members.find { it.id == memberId }
            member?.let {
                val firstName = it.name.split(" ")[0]
                binding.tvGreeting.text = "Selamat Datang, $firstName!"
            }
        } else {
            binding.tvGreeting.text = "Selamat Datang!"
        }

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

        binding.btnAjukanPinjamanCepat.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_loanApplicationFragment)
        }

        binding.cardSimpanan.setOnClickListener {
            Toast.makeText(context, "Membuka halaman detail simpanan...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}