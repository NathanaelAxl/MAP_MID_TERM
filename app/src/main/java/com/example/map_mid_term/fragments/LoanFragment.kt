package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentLoansBinding

class LoanFragment : Fragment() {

    private var _binding: FragmentLoansBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoansBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userHasActiveLoan = false

        if (userHasActiveLoan) {
            binding.activeLoanView.visibility = View.VISIBLE
            binding.noLoanView.visibility = View.GONE
        } else {
            binding.activeLoanView.visibility = View.GONE
            binding.noLoanView.visibility = View.VISIBLE
        }

        binding.btnAjukanPinjamanDariKosong.setOnClickListener {
            findNavController().navigate(R.id.action_loansFragment_to_loanApplicationFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}