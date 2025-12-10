package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentLoanStatusDetailBinding
import com.example.map_mid_term.model.DummyData
import java.text.NumberFormat
import java.util.Locale

class LoanStatusDetailFragment : Fragment() {

    private var _binding: FragmentLoanStatusDetailBinding? = null
    private val binding get() = _binding!!

    // Menerima argumen dari Navigasi
    private val args: LoanStatusDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoanStatusDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val applicationId = args.applicationId
        val application = DummyData.loanApplications.find { it.id == applicationId }

        if (application != null) {
            val localeID = Locale("in", "ID")
            val currencyFormat = NumberFormat.getCurrencyInstance(localeID)
            currencyFormat.maximumFractionDigits = 0

            binding.tvStatusDetail.text = application.status
            binding.tvApplicationId.text = application.id
            binding.tvApplicationAmount.text = currencyFormat.format(application.amount)
            binding.tvApplicationTenor.text = "${application.tenor} Bulan"

            val statusColor = when (application.status) {
                "Diterima" -> ContextCompat.getColor(requireContext(), R.color.green_status)
                "Ditolak" -> ContextCompat.getColor(requireContext(), R.color.red_status)
                else -> ContextCompat.getColor(requireContext(), R.color.orange_status)
            }
            binding.tvStatusDetail.setTextColor(statusColor)
        }

        binding.btnBackToHome.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}