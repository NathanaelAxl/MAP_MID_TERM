package com.example.map_mid_term.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.map_mid_term.R

class CameraActivity : AppCompatActivity() {

    private lateinit var btnTakePhoto: Button
    private lateinit var imgPreview: ImageView

    // Launcher baru untuk menangani hasil dari Intent Kamera
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imgPreview.setImageBitmap(imageBitmap)
        } else {
            Toast.makeText(this, "Gagal mengambil foto", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        imgPreview = findViewById(R.id.imgPreview)

        btnTakePhoto.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                takePictureLauncher.launch(takePictureIntent)
            } else {
                Toast.makeText(this, "Kamera tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
