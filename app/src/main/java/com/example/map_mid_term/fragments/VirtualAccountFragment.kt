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

        // Terima Data
        val title = arguments?.getString("title") ?: "Pembayaran"
        val amount = arguments?.getDouble("amount") ?: 0.0

        // Update UI
        binding.tvTotalPayment.text = "Rp ${"%,.0f".format(amount)}"

        // Fitur Copy
        binding.btnCopyVa.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val vaNumber = binding.tvVaNumber.text.toString().replace(" ", "")
            val clip = ClipData.newPlainText("VA Number", vaNumber)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Nomor Virtual Account disalin!", Toast.LENGTH_SHORT).show()
        }

        // --- LOGIKA SIMPAN KE FIREBASE ---
        binding.btnFinishPayment.setOnClickListener {
            savePaymentToFirestore(title, amount)
        }
    }

    private fun savePaymentToFirestore(title: String, amount: Double) {
        binding.btnFinishPayment.isEnabled = false
        binding.btnFinishPayment.text = "Memproses..."

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(context, "Sesi habis, login ulang", Toast.LENGTH_SHORT).show()
            return
        }

        val transactionData = hashMapOf(
            "title" to title,
            "amount" to amount,
            "type" to "loan_payment", // Tipe khusus bayar hutang
            "method" to "virtual_account",
            "status" to "verified", // Langsung sukses karena VA
            "timestamp" to System.currentTimeMillis(),
            "userId" to userId
        )

        FirebaseFirestore.getInstance().collection("transactions")
            .add(transactionData)
            .addOnSuccessListener {
                Toast.makeText(context, "Pembayaran Berhasil!", Toast.LENGTH_LONG).show()
                // Kembali ke Home
                findNavController().popBackStack(R.id.homeFragment, false)
            }
            .addOnFailureListener {
                binding.btnFinishPayment.isEnabled = true
                binding.btnFinishPayment.text = "Saya Sudah Bayar"
                Toast.makeText(context, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}