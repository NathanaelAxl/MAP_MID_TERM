package com.example.map_mid_term.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.activities.MainActivity
import com.example.map_mid_term.databinding.FragmentLoanApplicationBinding
import com.example.map_mid_term.model.DummyData
import com.example.map_mid_term.model.LoanApplication
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

class LoanApplicationFragment : Fragment() {

    private var _binding: FragmentLoanApplicationBinding? = null
    private val binding get() = _binding!!
    private val DUMMY_INTEREST_RATE = 0.015

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoanApplicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinner()
        setupListeners()
        binding.btnSubmitLoan.setOnClickListener {
            if (validateForm()) {
                showConfirmationDialog()
            }
        }
    }

    private fun setupSpinner() {
        val tenorOptions = arrayOf("Pilih Tenor", "3 Bulan", "6 Bulan", "9 Bulan", "12 Bulan")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tenorOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTenor.adapter = adapter
    }

    private fun setupListeners() {
        binding.etLoanAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateInstallment()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.spinnerTenor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calculateInstallment()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // FUNGSI INI DIPERBAIKI UNTUK MENCEGAH CRASH
    private fun calculateInstallment() {
        val amountString = binding.etLoanAmount.text.toString()

        // Jika input kosong ATAU user belum memilih tenor, reset saja dan berhenti.
        // Ini mencegah error NumberFormatException saat halaman pertama kali load.
        if (amountString.isEmpty() || binding.spinnerTenor.selectedItemPosition == 0) {
            resetCalculationTexts()
            return // Keluar dari fungsi lebih awal
        }

        // Kode di bawah ini HANYA akan berjalan jika input dan tenor sudah valid.
        val loanAmount = amountString.toDouble()
        val tenorInMonths = binding.spinnerTenor.selectedItem.toString().split(" ")[0].toInt()

        val pokokCicilan = loanAmount / tenorInMonths
        val bungaCicilan = loanAmount * DUMMY_INTEREST_RATE
        val totalAngsuran = pokokCicilan + bungaCicilan

        val localeID = Locale("in", "ID")
        val currencyFormat = NumberFormat.getCurrencyInstance(localeID)
        currencyFormat.maximumFractionDigits = 0

        binding.tvPokokCicilan.text = currencyFormat.format(pokokCicilan)
        binding.tvBungaCicilan.text = currencyFormat.format(bungaCicilan)
        binding.tvTotalAngsuran.text = currencyFormat.format(totalAngsuran)
    }

    private fun resetCalculationTexts() {
        binding.tvPokokCicilan.text = "Rp 0"
        binding.tvBungaCicilan.text = "Rp 0"
        binding.tvTotalAngsuran.text = "Rp 0"
    }

    private fun validateForm(): Boolean {
        var isValid = true
        if (binding.etLoanAmount.text.toString().isEmpty()) {
            binding.tilLoanAmount.error = "Jumlah pinjaman tidak boleh kosong"
            isValid = false
        } else {
            binding.tilLoanAmount.error = null
        }
        if (binding.spinnerTenor.selectedItemPosition == 0) {
            Toast.makeText(context, "Silakan pilih tenor pinjaman", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        return isValid
    }

    private fun showConfirmationDialog() {
        val amount = binding.etLoanAmount.text.toString().toDouble()
        val tenor = binding.spinnerTenor.selectedItem.toString()
        val total = binding.tvTotalAngsuran.text.toString()

        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Pengajuan Pinjaman")
            .setMessage("Anda akan mengajukan pinjaman sebesar ${NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(amount)} dengan tenor $tenor. \n\nTotal angsuran per bulan adalah $total. \n\nApakah Anda yakin ingin melanjutkan?")
            .setPositiveButton("Ya, Lanjutkan") { dialog, _ ->
                submitApplication()
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun submitApplication() {
        val parentActivity = activity as? MainActivity
        val memberId = parentActivity?.memberId
        if (memberId == null) {
            Toast.makeText(context, "Gagal mendapatkan ID Anggota. Silakan coba lagi.", Toast.LENGTH_LONG).show()
            return
        }

        val loanAmount = binding.etLoanAmount.text.toString().toDouble()
        val tenorInMonths = binding.spinnerTenor.selectedItem.toString().split(" ")[0].toInt()

        val newApplication = LoanApplication(
            id = UUID.randomUUID().toString(),
            memberId = memberId,
            amount = loanAmount,
            tenor = tenorInMonths,
            status = "Sedang Diproses"
        )

        DummyData.loanApplications.add(newApplication)
        Toast.makeText(context, "Pengajuan pinjaman berhasil dikirim!", Toast.LENGTH_LONG).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}