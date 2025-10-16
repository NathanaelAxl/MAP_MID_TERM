package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentAddSavingsBinding

class AddSavingsFragment : Fragment() {

    private var _binding: FragmentAddSavingsBinding? = null
    private val binding get() = _binding!!

    // Nominal simpanan wajib (dummy data)
    private val NOMINAL_SIMPANAN_WAJIB = 100000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSavingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRadioGroupListener()

        binding.btnContinueToPayment.setOnClickListener {
            if (validateInput()) {
                // Navigasi ke halaman pilihan metode pembayaran
                findNavController().navigate(R.id.action_addSavingsFragment_to_paymentDetailFragment)
            }
        }
    }

    private fun setupRadioGroupListener() {
        binding.rgSavingsType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_wajib) {
                // Jika Simpanan Wajib dipilih
                binding.etDepositAmount.setText(NOMINAL_SIMPANAN_WAJIB.toString())
                binding.etDepositAmount.isEnabled = false // Field tidak bisa diubah
            } else {
                // Jika Simpanan Sukarela dipilih
                binding.etDepositAmount.setText("")
                binding.etDepositAmount.isEnabled = true // Field bisa diisi bebas
            }
        }
    }

    private fun validateInput(): Boolean {
        if (binding.rgSavingsType.checkedRadioButtonId == -1) {
            Toast.makeText(context, "Silakan pilih jenis simpanan", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etDepositAmount.text.isNullOrEmpty()) {
            binding.tilDepositAmount.error = "Jumlah setoran tidak boleh kosong"
            return false
        }
        binding.tilDepositAmount.error = null
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}