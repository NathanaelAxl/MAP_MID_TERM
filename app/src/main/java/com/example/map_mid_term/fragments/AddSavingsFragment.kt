package com.example.map_mid_term.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentAddSavingsBinding
import com.example.map_mid_term.viewmodels.TransactionViewModel

class AddSavingsFragment : Fragment() {

    private var _binding: FragmentAddSavingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionViewModel by viewModels()

    private var imageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            // Sekarang Glide.with(this) tidak akan bingung lagi
            Glide.with(this)
                .load(it)
                .centerCrop()
                .into(binding.ivProofPreview)
        }
    }

    private val nominalSimpananWajib = 100000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSavingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUIListeners()
        observeViewModel()
    }

    private fun setupUIListeners() {
        binding.rgSavingsType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_wajib) {
                binding.etDepositAmount.setText(nominalSimpananWajib.toString())
                binding.etDepositAmount.isEnabled = false
            } else {
                binding.etDepositAmount.setText("")
                binding.etDepositAmount.isEnabled = true
            }
        }

        binding.btnUploadProof.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSaveTransaction.setOnClickListener {
            if (validateInput()) {
                saveTransaction()
            }
        }
    }

    private fun saveTransaction() {
        val amount = binding.etDepositAmount.text.toString().toDouble()
        val type = if (binding.rbWajib.isChecked) "Simpanan Wajib" else "Simpanan Sukarela"
        val title = type
        viewModel.saveNewSaving(title, amount, type, imageUri)
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSaveTransaction.isEnabled = !isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                viewModel.doneNavigating()
            }
        }
    }

    private fun validateInput(): Boolean {
        if (binding.rgSavingsType.checkedRadioButtonId == -1) {
            Toast.makeText(context, "Silakan pilih jenis simpanan", Toast.LENGTH_SHORT).show()
            return false
        }
        val amountText = binding.etDepositAmount.text.toString()
        if (amountText.isEmpty() || amountText.toDoubleOrNull() ?: 0.0 <= 0) {
            binding.tilDepositAmount.error = "Jumlah setoran tidak valid"
            return false
        }
        if (imageUri == null) {
            Toast.makeText(context, "Silakan upload bukti pembayaran", Toast.LENGTH_SHORT).show()
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
