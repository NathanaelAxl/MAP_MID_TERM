package com.example.map_mid_term.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar // Import Toolbar
import com.example.map_mid_term.R // Import R
import com.example.map_mid_term.databinding.ActivityAdminReportBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class AdminReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminReportBinding
    private val db = FirebaseFirestore.getInstance()

    // Variabel penampung untuk perhitungan
    private var totalSimpanan = 0.0
    private var totalPinjamanDisalurkan = 0.0
    private var totalAngsuranMasuk = 0.0
    private var estimasiLaba = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // PERBAIKAN: Gunakan findViewById agar tipe Toolbar dikenali dengan jelas
        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener { finish() }

        // Tombol Ekspor (Fitur Dummy)
        binding.btnExportReport.setOnClickListener {
            Toast.makeText(this, "Laporan berhasil diekspor ke PDF (Demo)", Toast.LENGTH_SHORT).show()
        }

        // Mulai hitung data secara real-time
        startRealtimeMonitoring()
    }

    private fun startRealtimeMonitoring() {
        // 1. MONITOR TRANSAKSI (Simpanan & Pembayaran Angsuran)
        db.collection("transactions")
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                totalSimpanan = 0.0
                totalAngsuranMasuk = 0.0

                if (snapshots != null) {
                    for (doc in snapshots) {
                        val amount = doc.getDouble("amount") ?: 0.0
                        val type = doc.getString("type") ?: ""

                        // Hitung Simpanan
                        if (type == "credit") {
                            totalSimpanan += amount
                        }
                        // Hitung Angsuran Masuk (Pembayaran Hutang)
                        else if (type == "loan_payment") {
                            totalAngsuranMasuk += amount
                        }
                    }
                }
                updateUI()
            }

        // 2. MONITOR PINJAMAN (Uang Keluar)
        db.collection("loan_applications")
            .whereEqualTo("status", "approved")
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                totalPinjamanDisalurkan = 0.0
                estimasiLaba = 0.0

                if (snapshots != null) {
                    for (doc in snapshots) {
                        val amount = doc.getDouble("amount") ?: 0.0
                        val tenor = doc.getLong("tenor") ?: 0

                        totalPinjamanDisalurkan += amount

                        // Estimasi Laba Kasar (Misal bunga 1.5% x Tenor)
                        // Ini logika sederhana untuk demo
                        val bungaPerBulan = amount * 0.015
                        val totalBunga = bungaPerBulan * tenor
                        estimasiLaba += totalBunga
                    }
                }
                updateUI()
            }
    }

    private fun updateUI() {
        // Rumus Kas = (Uang Masuk dari Simpanan + Angsuran) - Uang Keluar Pinjaman
        // Catatan: Ini simulasi kas sederhana.
        val saldoKas = (totalSimpanan + totalAngsuranMasuk) - totalPinjamanDisalurkan

        // Format Rupiah
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0

        // Update Text Views sesuai ID di XML kamu
        binding.tvTotalSimpananReport.text = numberFormat.format(totalSimpanan)
        binding.tvTotalPinjamanReport.text = numberFormat.format(totalPinjamanDisalurkan)
        binding.tvSaldoKasReport.text = numberFormat.format(saldoKas)
        binding.tvLabaBersihReport.text = numberFormat.format(estimasiLaba)
    }
}