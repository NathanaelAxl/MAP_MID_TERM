package com.example.map_mid_term.activities

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.databinding.ActivityAdminReportBinding
import com.example.map_mid_term.data.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminReportBinding
    private val db = FirebaseFirestore.getInstance()

    // Variabel Penampung Data (Awalnya 0)
    private var totalSimpanan: Double = 0.0
    private var totalPinjaman: Double = 0.0
    private var totalAngsuranMasuk: Double = 0.0
    private var saldoKas: Double = 0.0
    private var labaBersih: Double = 0.0
    private val modalAwalKoperasi = 50000000.0 // Contoh Modal Awal (50 Juta)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        calculateFinancials() // Mulai hitung saat halaman dibuka
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnExport.setOnClickListener { exportToPDF() }
    }

    private fun calculateFinancials() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnExport.isEnabled = false // Jangan export dulu kalau belum selesai hitung

        // 1. HITUNG TOTAL SIMPANAN (Dari Transaksi 'credit')
        //    & TOTAL ANGSURAN (Nanti bisa dipisah logic-nya kalau ada field khusus)
        db.collection("transactions")
            .whereEqualTo("type", "credit") // Ambil semua uang masuk
            .get()
            .addOnSuccessListener { documents ->
                totalSimpanan = 0.0
                totalAngsuranMasuk = 0.0

                for (doc in documents) {
                    val trx = doc.toObject(Transaction::class.java)

                    // Cek apakah ini simpanan atau angsuran (berdasarkan title/desc)
                    // Jika kamu belum membedakan secara strict, kita anggap semua credit adalah Pemasukan
                    totalSimpanan += trx.amount
                }

                // Setelah Simpanan selesai, Lanjut hitung Pinjaman
                calculateLoans()
            }
            .addOnFailureListener {
                showError("Gagal mengambil data transaksi")
            }
    }

    private fun calculateLoans() {
        // 2. HITUNG TOTAL PINJAMAN KELUAR (Dari Loan Application 'approved')
        db.collection("loan_applications")
            .whereEqualTo("status", "approved")
            .get()
            .addOnSuccessListener { documents ->
                totalPinjaman = 0.0
                labaBersih = 0.0

                for (doc in documents) {
                    // Ambil field "amount" (sesuaikan nama field di database kamu, misal 'loanAmount' atau 'amount')
                    val amount = doc.getDouble("amount") ?: 0.0
                    val totalPayable = doc.getDouble("totalPayable") ?: amount

                    totalPinjaman += amount

                    // Hitung Laba (Bunga) = Total Harus Bayar - Pokok Pinjaman
                    labaBersih += (totalPayable - amount)
                }

                // 3. HITUNG SALDO AKHIR
                // Rumus: Modal Awal + Uang Masuk (Simpanan) - Uang Keluar (Pinjaman)
                saldoKas = (modalAwalKoperasi + totalSimpanan) - totalPinjaman

                updateUI()
            }
            .addOnFailureListener {
                showError("Gagal mengambil data pinjaman")
            }
    }

    private fun updateUI() {
        binding.progressBar.visibility = View.GONE
        binding.btnExport.isEnabled = true

        binding.tvTotalSimpanan.text = formatRupiah(totalSimpanan)
        binding.tvTotalPinjaman.text = formatRupiah(totalPinjaman)
        binding.tvSaldoKas.text = formatRupiah(saldoKas)
        binding.tvLabaBersih.text = formatRupiah(labaBersih)

        // Warna Saldo Kas: Merah jika minus
        if (saldoKas < 0) {
            binding.tvSaldoKas.setTextColor(Color.RED)
        } else {
            binding.tvSaldoKas.setTextColor(Color.parseColor("#1565C0")) // Biru
        }
    }

    private fun formatRupiah(amount: Double): String {
        return "Rp ${"%,.0f".format(amount)}"
    }

    private fun showError(msg: String) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // --- PDF EXPORT (Code lama kamu, sudah bagus) ---
    private fun exportToPDF() {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        paint.textSize = 24f
        paint.color = Color.BLACK
        paint.isFakeBoldText = true
        canvas.drawText("Laporan Keuangan Koperasi", 50f, 80f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        val tanggal = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Dicetak pada: $tanggal", 50f, 110f, paint)
        paint.strokeWidth = 2f
        canvas.drawLine(50f, 130f, 545f, 130f, paint)

        paint.textSize = 16f
        var yPos = 180f

        fun drawRow(label: String, value: Double, isNegativeRed: Boolean = false) {
            paint.color = Color.BLACK
            canvas.drawText(label, 50f, yPos, paint)
            val formattedValue = formatRupiah(value)
            if (isNegativeRed && value < 0) paint.color = Color.RED else paint.color = Color.parseColor("#4CAF50")
            val textWidth = paint.measureText(formattedValue)
            canvas.drawText(formattedValue, 540f - textWidth, yPos, paint)
            yPos += 40f
        }

        drawRow("Modal Awal", modalAwalKoperasi) // Tampilkan modal awal juga
        drawRow("Total Simpanan Masuk", totalSimpanan)
        drawRow("Total Pinjaman Keluar", totalPinjaman)
        drawRow("Saldo Kas Saat Ini", saldoKas, true)
        drawRow("Estimasi Laba (Bunga)", labaBersih)

        paint.color = Color.GRAY
        paint.textSize = 12f
        canvas.drawText("Laporan ini digenerate otomatis oleh Aplikasi Tabungin.", 50f, 800f, paint)

        pdfDocument.finishPage(page)
        val fileName = "Laporan_Koperasi_${System.currentTimeMillis()}.pdf"
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF Tersimpan di Downloads!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal Ekspor PDF", Toast.LENGTH_SHORT).show()
        }
        pdfDocument.close()
    }
}