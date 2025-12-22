package com.example.map_mid_term.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map_mid_term.adapters.TransactionAdapter // Pakai yang ini!
import com.example.map_mid_term.data.model.Transaction
import com.example.map_mid_term.databinding.FragmentHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // Kita pakai TransactionAdapter, BUKAN HistoryAdapter
    private lateinit var transactionAdapter: TransactionAdapter

    private var firestoreListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupRecyclerView()
        fetchTransactionsFromFirestore()
    }

    private fun setupRecyclerView() {
        // Inisialisasi adapter
        transactionAdapter = TransactionAdapter(arrayListOf())

        binding?.rvHistory?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }

    private fun fetchTransactionsFromFirestore() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            binding?.tvNoTransactions?.text = "Silakan login kembali"
            binding?.tvNoTransactions?.visibility = View.VISIBLE
            return
        }

        binding?.progressBar?.visibility = View.VISIBLE

        val query = firestore.collection("transactions")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        firestoreListener = query.addSnapshotListener { snapshots, e ->
            if (_binding == null) return@addSnapshotListener
            binding?.progressBar?.visibility = View.GONE

            if (e != null) {
                // ⚠️ PENTING: KLIK LINK DI LOGCAT JIKA MUNCUL ERROR DISINI ⚠️
                Log.e("HistoryFragment", "Error ambil data: ${e.message}", e)
                return@addSnapshotListener
            }

            if (snapshots != null && !snapshots.isEmpty) {
                binding?.tvNoTransactions?.visibility = View.GONE

                val safeList = ArrayList<Transaction>()
                for (document in snapshots) {
                    try {
                        val trx = document.toObject(Transaction::class.java)
                        trx.id = document.id
                        // Hanya masukkan data yang punya tanggal biar gak error sort
                        if(trx.timestamp != null) {
                            safeList.add(trx)
                        }
                    } catch (error: Exception) {
                        Log.e("HistoryFragment", "Data rusak: ${document.id}")
                    }
                }
                transactionAdapter.updateData(safeList)
            } else {
                binding?.tvNoTransactions?.visibility = View.VISIBLE
                transactionAdapter.updateData(emptyList())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firestoreListener?.remove()
        _binding = null
    }
}