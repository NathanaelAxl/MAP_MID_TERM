package com.example.map_mid_term.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentLoanApplicationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoanApplicationFragment : Fragment() {

    private var _binding: FragmentLoanApplicationBinding? = null
    private val binding get() = _binding!!

    // Bunga fix 1.5% per bulan
    private val interestRate = 0.015

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoanApplicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalculationListener()

        binding.btnSubmitLoan.setOnClickListener {
            if (validateInput()) {
                submitLoanApplication()
            }
        }
    }

    private fun setupCalculationListener() {
        // Hitung ulang setiap kali teks berubah
        binding.etLoanAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { calculateInstallment() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Hitung ulang setiap kali tenor berubah
        binding.rgTenor.setOnCheckedChangeListener { _, _ ->
            calculateInstallment()
        }
    }

    private fun calculateInstallment() {
        val amountStr = binding.etLoanAmount.text.toString()
        if (amountStr.isEmpty()) {
            resetSimulation()
            return
        }

        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val tenor = getSelectedTenor()

        if (tenor == 0) return

        val pokok = amount / tenor
        val bunga = amount * interestRate
        val total = pokok + bunga

        binding.tvPokok.text = "Rp ${"%,.0f".format(pokok)}"
        binding.tvBunga.text = "Rp ${"%,.0f".format(bunga)}"
        binding.tvTotalInstallment.text = "Rp ${"%,.0f".format(total)}"
    }

    private fun resetSimulation() {
        binding.tvPokok.text = "Rp 0"
        binding.tvBunga.text = "Rp 0"
        binding.tvTotalInstallment.text = "Rp 0"
    }

    private fun getSelectedTenor(): Int {
        return when (binding.rgTenor.checkedRadioButtonId) {
            R.id.rb_3_months -> 3
            R.id.rb_6_months -> 6
            R.id.rb_12_months -> 12
            else -> 0
        }
    }

    private fun submitLoanApplication() {
        setLoading(true)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(context, "Sesi habis", Toast.LENGTH_SHORT).show()
            setLoading(false)
            return
        }

        val amount = binding.etLoanAmount.text.toString().toDouble()
        val tenor = getSelectedTenor()
        val reason = binding.etReason.text.toString()

        // Hitung total yang harus dibayar per bulan (untuk disimpan di DB)
        val monthlyInstallment = (amount / tenor) + (amount * interestRate)

        val loanData = hashMapOf(
            "userId" to userId,
            "amount" to amount,
            "tenor" to tenor,
            "reason" to reason,
            "monthlyInstallment" to monthlyInstallment,
            "status" to "pending", // Status awal PENDING
            "applicationDate" to System.currentTimeMillis()
        )

        // Simpan ke koleksi 'loan_applications'
        FirebaseFirestore.getInstance().collection("loan_applications")
            .add(loanData)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(context, "Pengajuan Berhasil Dikirim!", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener {
                setLoading(false)
                Toast.makeText(context, "Gagal mengirim: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateInput(): Boolean {
        if (binding.etLoanAmount.text.isNullOrEmpty()) {
            binding.tilLoanAmount.error = "Isi jumlah pinjaman"
            return false
        }
        if (getSelectedTenor() == 0) {
            Toast.makeText(context, "Pilih tenor pinjaman", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etReason.text.isNullOrEmpty()) {
            binding.tilReason.error = "Isi keperluan pinjaman"
            return false
        }
        return true
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSubmitLoan.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}