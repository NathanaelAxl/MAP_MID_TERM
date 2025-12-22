package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map_mid_term.adapters.TransactionAdapter
import com.example.map_mid_term.data.model.Transaction
import com.example.map_mid_term.databinding.FragmentHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadTransactionHistory()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(arrayListOf())

        // ID ini sekarang SUDAH COCOK dengan XML
        binding.rvTransactionHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }

    private fun loadTransactionHistory() {
        val userId = auth.currentUser?.uid ?: return

        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        // Query berdasarkan 'date'
        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE
                val transactionList = ArrayList<Transaction>()

                for (doc in documents) {
                    try {
                        val trx = doc.toObject(Transaction::class.java)
                        trx.id = doc.id
                        transactionList.add(trx)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                transactionAdapter.updateData(transactionList)

                // Logic Tampilkan/Sembunyikan pesan kosong
                if (transactionList.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvTransactionHistory.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvTransactionHistory.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "Gagal memuat: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}