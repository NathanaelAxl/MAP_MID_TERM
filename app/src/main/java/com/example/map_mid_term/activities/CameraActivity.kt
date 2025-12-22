package com.example.map_mid_term.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.map_mid_term.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraActivity : AppCompatActivity() {

    private lateinit var imgPreview: ImageView
    private lateinit var tvLocation: TextView
    private lateinit var btnTakePhoto: Button
    private lateinit var btnSave: Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentPhotoPath: String? = null
    private var currentLocationString: String = "Lokasi tidak ditemukan"

    // Launcher Kamera
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Foto berhasil diambil dan disimpan di currentPhotoPath
            displayImage()
            getCurrentLocation() // Ambil lokasi setelah foto diambil
        } else {
            Toast.makeText(this, "Batal mengambil foto", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher Izin Lokasi & Kamera
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true &&
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        ) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(this, "Izin Kamera & Lokasi diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        imgPreview = findViewById(R.id.imgPreview)
        tvLocation = findViewById(R.id.tvLocation)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnSave = findViewById(R.id.btnSaveAndReturn)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnTakePhoto.setOnClickListener {
            checkPermissionsAndOpen()
        }

        btnSave.setOnClickListener {
            // Kirim data kembali ke Activity sebelumnya
            val resultIntent = Intent()
            resultIntent.putExtra("photo_path", currentPhotoPath)
            resultIntent.putExtra("location_result", currentLocationString)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun checkPermissionsAndOpen() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(this, "Gagal membuat file gambar", Toast.LENGTH_SHORT).show()
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "${applicationContext.packageName}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun displayImage() {
        currentPhotoPath?.let {
            val bitmap = BitmapFactory.decodeFile(it)
            imgPreview.setImageBitmap(bitmap)
            // Aktifkan tombol simpan
            btnSave.isEnabled = true
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        tvLocation.text = "Sedang mencari lokasi..."
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val long = location.longitude
                currentLocationString = "$lat, $long"
                tvLocation.text = "Lokasi: $lat, $long"
            } else {
                tvLocation.text = "Lokasi tidak terdeteksi (Pastikan GPS aktif)"
                currentLocationString = "Lokasi Tidak Diketahui"
            }
        }.addOnFailureListener {
            tvLocation.text = "Gagal mengambil lokasi"
        }
    }
}