package com.example.map_mid_term.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
        binding.etLoanAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { calculateInstallment() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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
        // Sesuaikan ID RadioButton dengan layout XML Anda (misal rb_3_months)
        // Jika ID di XML adalah rb_3_bulan, ganti di sini.
        // Saya asumsikan ID standar rb_3_months dll.
        val checkedId = binding.rgTenor.checkedRadioButtonId
        // Anda perlu memastikan ID di XML fragment_loan_application.xml Anda sesuai.
        // Contoh sederhana jika ID-nya dinamis atau Anda pakai when:
        return when (checkedId) {
            binding.rgTenor.getChildAt(0).id -> 3 // Asumsi urutan 3, 6, 12
            binding.rgTenor.getChildAt(1).id -> 6
            binding.rgTenor.getChildAt(2).id -> 12
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

        val monthlyInstallment = (amount / tenor) + (amount * interestRate)

        // DATA YANG DIKIRIM KE FIREBASE
        val loanData = hashMapOf(
            "userId" to userId,
            "amount" to amount,
            "tenor" to tenor,
            "reason" to reason,
            "monthlyInstallment" to monthlyInstallment,
            "status" to "pending", // STATUS AWAL: PENDING
            "applicationDate" to System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance().collection("loan_applications")
            .add(loanData)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(context, "Pengajuan Berhasil! Menunggu persetujuan admin.", Toast.LENGTH_LONG).show()
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