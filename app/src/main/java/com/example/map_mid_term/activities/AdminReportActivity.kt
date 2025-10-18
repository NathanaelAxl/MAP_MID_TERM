package com.example.map_mid_term.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.R
import com.example.map_mid_term.model.DummyData
import java.text.NumberFormat
import java.util.Locale

class AdminReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_report)

        supportActionBar?.title = "Laporan Koperasi"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Format rupiah
        val localeID = Locale.forLanguageTag("in-ID")
        val formatter = NumberFormat.getCurrencyInstance(localeID)
        formatter.maximumFractionDigits = 0

        // Ambil data dari DummyData
        val totalSimpanan = DummyData.adminTransactions
            .filter { it.type == "Simpanan" }
            .sumOf { it.amount }

        val totalPinjaman = DummyData.adminTransactions
            .filter { it.type == "Pinjaman" }
            .sumOf { it.amount }

        val saldoKas = totalSimpanan - totalPinjaman
        val labaBersih = saldoKas * 0.1 // misalnya 10% dari kas

        // Set text
        findViewById<TextView>(R.id.tvTotalSimpananReport).text = formatter.format(totalSimpanan)
        findViewById<TextView>(R.id.tvTotalPinjamanReport).text = formatter.format(totalPinjaman)
        findViewById<TextView>(R.id.tvSaldoKasReport).text = formatter.format(saldoKas)
        findViewById<TextView>(R.id.tvLabaBersihReport).text = formatter.format(labaBersih)

        // Tombol Export
        findViewById<Button>(R.id.btnExportReport).setOnClickListener {
            Toast.makeText(this, "Laporan berhasil diekspor (simulasi)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
