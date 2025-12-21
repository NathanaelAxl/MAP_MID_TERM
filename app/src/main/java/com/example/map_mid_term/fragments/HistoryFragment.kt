package com.example.map_mid_term.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map_mid_term.adapters.TransactionAdapter
import com.example.map_mid_term.data.model.Transaction
import com.example.map_mid_term.databinding.FragmentHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    // Menggunakan safe call untuk binding
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var transactionAdapter: TransactionAdapter

    // Inisialisasi list kosong di awal
    private var transactionList = ArrayList<Transaction>()

    // Listener disimpan agar bisa dimatikan saat onDestroy
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
        // Inisialisasi adapter dengan list kosong dulu
        transactionAdapter = TransactionAdapter(arrayListOf())

        binding?.rvHistory?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }

    private fun fetchTransactionsFromFirestore() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            binding?.tvNoTransactions?.text = "Anda harus login untuk melihat riwayat"
            binding?.tvNoTransactions?.visibility = View.VISIBLE
            return
        }

        binding?.progressBar?.visibility = View.VISIBLE

        firestoreListener = firestore.collection("transactions")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                // PENTING: Cek binding null sebelum update UI
                if (_binding == null) return@addSnapshotListener

                binding?.progressBar?.visibility = View.GONE

                if (e != null) {
                    Log.w("HistoryFragment", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    binding?.tvNoTransactions?.visibility = View.GONE

                    val safeList = ArrayList<Transaction>()

                    // --- REVISI ANTI FORCE CLOSE ---
                    // Kita looping manual satu per satu
                    for (document in snapshots) {
                        try {
                            // Coba convert dokumen ke object Transaction
                            val trx = document.toObject(Transaction::class.java)
                            // Set ID dokumen manual biar aman
                            trx.id = document.id
                            safeList.add(trx)
                        } catch (error: Exception) {
                            // Jika ada data rusak (misal timestamp error), dia masuk sini.
                            // Aplikasi TIDAK AKAN CRASH, cuma mencatat error di log.
                            Log.e("HistoryFragment", "Data rusak dilewati: ${document.id} - ${error.message}")
                        }
                    }

                    // Masukkan data yang BERHASIL diambil saja ke adapter
                    transactionAdapter.updateData(safeList)

                } else {
                    Log.d("HistoryFragment", "No transactions found")
                    binding?.tvNoTransactions?.visibility = View.VISIBLE
                    transactionAdapter.updateData(emptyList())
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Matikan listener database
        firestoreListener?.remove()
        _binding = null
    }
}