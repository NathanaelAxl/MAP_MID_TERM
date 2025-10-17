package com.example.map_mid_term.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.R
import com.example.map_mid_term.model.DummyData
import com.google.android.material.appbar.MaterialToolbar
import java.text.NumberFormat
import java.util.*

class AdminReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_report)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.title = "Laporan Keuangan"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val localeID = Locale("in", "ID")
        val formatter = NumberFormat.getCurrencyInstance(localeID).apply { maximumFractionDigits = 0 }

        val totalSimpanan = DummyData.adminTransactions.filter { it.type == "Simpanan" }.sumOf { it.amount }
        val totalPinjaman = DummyData.adminTransactions.filter { it.type == "Pinjaman" }.sumOf { it.amount }
        val saldoKas = totalSimpanan - totalPinjaman
        val labaBersih = saldoKas * 0.1

        findViewById<TextView>(R.id.tvTotalSimpananReport).text = formatter.format(totalSimpanan)
        findViewById<TextView>(R.id.tvTotalPinjamanReport).text = formatter.format(totalPinjaman)
        findViewById<TextView>(R.id.tvSaldoKasReport).text = formatter.format(saldoKas)
        findViewById<TextView>(R.id.tvLabaBersihReport).text = formatter.format(labaBersih)

        findViewById<Button>(R.id.btnExportReport).setOnClickListener {
            Toast.makeText(this, "Laporan berhasil diekspor (simulasi)", Toast.LENGTH_SHORT).show()
        }
    }
}
