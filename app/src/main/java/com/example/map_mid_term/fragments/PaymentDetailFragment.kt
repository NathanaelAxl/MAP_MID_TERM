package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentPaymentDetailBinding

class PaymentDetailFragment : Fragment() {

    private var _binding: FragmentPaymentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardVirtualAccount.setOnClickListener {
            findNavController().navigate(R.id.action_paymentDetailFragment_to_virtualAccountFragment)
        }

        binding.cardManualTransfer.setOnClickListener {
            findNavController().navigate(R.id.action_paymentDetailFragment_to_uploadProofFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
