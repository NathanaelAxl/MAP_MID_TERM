package com.example.map_mid_term.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentVirtualAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class VirtualAccountFragment : Fragment() {

    private var _binding: FragmentVirtualAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVirtualAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Terima Data Tagihan
        val title = arguments?.getString("title") ?: "Pembayaran VA"
        val amount = arguments?.getDouble("amount") ?: 0.0
        val loanId = arguments?.getString("loanId") ?: ""

        // Update UI
        binding.tvTotalPayment.text = "Rp ${"%,.0f".format(amount)}"

        // Fitur Salin Nomor VA
        binding.btnCopyVa.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val vaNumber = binding.tvVaNumber.text.toString().replace(" ", "")
            val clip = ClipData.newPlainText("VA Number", vaNumber)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Nomor VA disalin!", Toast.LENGTH_SHORT).show()
        }

        // --- LOGIKA SIMPAN KE DATABASE ---
        binding.btnFinishPayment.setOnClickListener {
            savePayment(title, amount, loanId)
        }
    }

    private fun savePayment(title: String, amount: Double, loanId: String) {
        // Matikan tombol biar gak dipencet 2x
        binding.btnFinishPayment.isEnabled = false
        binding.btnFinishPayment.text = "Memproses..."

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) return

        // Data Transaksi
        val transactionData = hashMapOf(
            "title" to title,
            "amount" to amount,
            "type" to "loan_payment", // Tipe khusus bayar hutang
            "method" to "virtual_account",
            "loanId" to loanId,
            "status" to "success", // VA langsung sukses
            "timestamp" to System.currentTimeMillis(),
            "userId" to userId
        )

        FirebaseFirestore.getInstance().collection("transactions")
            .add(transactionData)
            .addOnSuccessListener {
                Toast.makeText(context, "Pembayaran Berhasil!", Toast.LENGTH_LONG).show()
                // Kembali ke Home agar saldo/tagihan terupdate
                findNavController().popBackStack(R.id.homeFragment, false)
            }
            .addOnFailureListener {
                binding.btnFinishPayment.isEnabled = true
                Toast.makeText(context, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}