package com.example.map_mid_term.activities

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.databinding.ActivityAdminReportBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminReportBinding

    // Variabel data (Anggap ini data yang sudah kamu hitung dari Firestore)
    private var totalSimpanan: Double = 1000000.0
    private var totalPinjaman: Double = 2500000.0
    private var saldoKas: Double = -1045833.0
    private var labaBersih: Double = 225000.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ... Kode fetch data Firestore kamu di sini ...
        // updateUI()
        binding.btnBack.setOnClickListener {
            finish() // Kembali ke menu sebelumnya
        }

        // TOMBOL EKSPOR
        binding.btnExport.setOnClickListener {
            exportToPDF()
        }
    }

    private fun exportToPDF() {
        val pdfDocument = PdfDocument()
        val paint = Paint()

        // 1. Buat Halaman A4
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // 2. Desain Isi PDF
        // Judul
        paint.textSize = 24f
        paint.color = Color.BLACK
        paint.isFakeBoldText = true
        canvas.drawText("Laporan Keuangan Koperasi", 50f, 80f, paint)

        // Tanggal Cetak
        paint.textSize = 14f
        paint.isFakeBoldText = false
        val tanggal = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Dicetak pada: $tanggal", 50f, 110f, paint)

        // Garis Pemisah
        paint.strokeWidth = 2f
        canvas.drawLine(50f, 130f, 545f, 130f, paint)

        // --- ISI DATA ---
        paint.textSize = 16f
        var yPos = 180f // Posisi vertikal awal

        // Fungsi helper untuk menggambar baris
        fun drawRow(label: String, value: Double, isNegativeRed: Boolean = false) {
            paint.color = Color.BLACK
            canvas.drawText(label, 50f, yPos, paint)

            val formattedValue = "Rp ${"%,.0f".format(value)}"

            // Logika Warna Merah/Hijau
            if (isNegativeRed && value < 0) paint.color = Color.RED else paint.color = Color.parseColor("#4CAF50") // Hijau

            // Gambar Nilai (Rata Kanan Manual)
            val textWidth = paint.measureText(formattedValue)
            canvas.drawText(formattedValue, 540f - textWidth, yPos, paint)

            yPos += 40f // Pindah baris ke bawah
        }

        drawRow("Total Simpanan Anggota", totalSimpanan)
        drawRow("Total Pinjaman Keluar", totalPinjaman)
        drawRow("Saldo Kas Koperasi", saldoKas, true) // True artinya kalau minus jadi merah
        drawRow("Laba Bersih (Bunga)", labaBersih)

        // Footer
        paint.color = Color.GRAY
        paint.textSize = 12f
        canvas.drawText("Laporan ini digenerate otomatis oleh Aplikasi Tabungin.", 50f, 800f, paint)

        pdfDocument.finishPage(page)

        // 3. Simpan File ke Folder Downloads
        val fileName = "Laporan_Koperasi_${System.currentTimeMillis()}.pdf"
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF Berhasil Disimpan di Downloads!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal Ekspor: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        pdfDocument.close()
    }
}