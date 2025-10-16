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

    // ... (onCreateView, onViewCreated, setupSpinner, setupListeners, calculateInstallment, resetCalculationTexts, validateForm tetap sama) ...

    private fun showConfirmationDialog() {
        val amount = binding.etLoanAmount.text.toString().toDouble()
        val tenor = binding.spinnerTenor.selectedItem.toString()
        val total = binding.tvTotalAngsuran.text.toString()

        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Pengajuan Pinjaman")
            .setMessage("Anda akan mengajukan pinjaman sebesar ${NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(amount)} dengan tenor $tenor. \n\nTotal angsuran per bulan adalah $total. \n\nApakah Anda yakin ingin melanjutkan?")
            .setPositiveButton("Ya, Lanjutkan") { dialog, _ ->
                submitApplication() // PANGGIL FUNGSI YANG BARU
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // FUNGSI INI DIPERBARUI TOTAL
    private fun submitApplication() {
        val parentActivity = activity as? MainActivity
        val memberId = parentActivity?.memberId

        if (memberId == null) {
            Toast.makeText(context, "Gagal mendapatkan ID Anggota. Silakan coba lagi.", Toast.LENGTH_LONG).show()
            return
        }

        // 1. Ambil data dari form
        val loanAmount = binding.etLoanAmount.text.toString().toDouble()
        val tenorInMonths = binding.spinnerTenor.selectedItem.toString().split(" ")[0].toInt()

        // 2. Buat objek pengajuan baru
        val newApplication = LoanApplication(
            id = UUID.randomUUID().toString(), // Buat ID unik
            memberId = memberId,
            amount = loanAmount,
            tenor = tenorInMonths,
            status = "Sedang Diproses" // Status awal
        )

        // 3. Simpan ke DummyData (simulasi database)
        DummyData.loanApplications.add(newApplication)

        // 4. Beri feedback dan kembali ke halaman sebelumnya
        Toast.makeText(context, "Pengajuan pinjaman berhasil dikirim!", Toast.LENGTH_LONG).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
