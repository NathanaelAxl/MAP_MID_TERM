package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.map_mid_term.R
import com.example.map_mid_term.network.HealthRequest
import com.example.map_mid_term.network.HealthResponse
import com.example.map_mid_term.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MachineLearningFragment : Fragment() {

    private lateinit var etAge: EditText
    private lateinit var etBMI: EditText
    private lateinit var etSleep: EditText
    private lateinit var rgSex: RadioGroup
    private lateinit var rgActivity: RadioGroup
    private lateinit var btnPredict: Button
    private lateinit var tvResult: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_machine_learning, container, false)

        // Init Views
        etAge = view.findViewById(R.id.etAge)
        etBMI = view.findViewById(R.id.etBMI)
        etSleep = view.findViewById(R.id.etSleep)
        rgSex = view.findViewById(R.id.rgSex)
        rgActivity = view.findViewById(R.id.rgActivity)
        btnPredict = view.findViewById(R.id.btnPredict)
        tvResult = view.findViewById(R.id.tvResult)

        btnPredict.setOnClickListener {
            predictHealth()
        }

        return view
    }

    private fun predictHealth() {
        // ... (Bagian ambil data input biarkan sama) ...
        val ageStr = etAge.text.toString()
        val bmiStr = etBMI.text.toString()
        val sleepStr = etSleep.text.toString()

        if (ageStr.isEmpty() || bmiStr.isEmpty() || sleepStr.isEmpty()) {
            Toast.makeText(context, "Mohon isi semua data angka", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedSexId = rgSex.checkedRadioButtonId
        val sex = if (selectedSexId == R.id.rbMale) "Male" else "Female"

        // Default Activity jika belum dipilih
        val activity = "Moderate"

        val requestData = HealthRequest(
            age = ageStr.toFloat(),
            BMI = bmiStr.toFloat(),
            sleep_hours = sleepStr.toFloat(),
            sex = sex,
            physical_activity = activity
        )

        tvResult.text = "Sedang menghubungi server..."
        btnPredict.isEnabled = false

        // --- REQUEST KE SERVER ---
        RetrofitClient.instance.predictHealth(requestData).enqueue(object : Callback<HealthResponse> {
            override fun onResponse(call: Call<HealthResponse>, response: Response<HealthResponse>) {
                btnPredict.isEnabled = true

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.data != null) {
                        val result = body.data

                        // ✅ PERBAIKAN DI SINI:
                        // Menggunakan 1.0 (Double) agar cocok dengan tipe data HealthResult
                        val hasilText = """
                            Sukses! Hasil Analisa:
                            - Diabetes: ${if (result.diabetes == 1.0) "⚠️ BERISIKO" else "✅ Aman"}
                            - Jantung: ${if (result.heart_attack == 1.0) "⚠️ BERISIKO" else "✅ Aman"}
                            - Stroke: ${if (result.stroke == 1.0) "⚠️ BERISIKO" else "✅ Aman"}
                            - Depresi: ${if (result.depression == 1.0) "⚠️ BERISIKO" else "✅ Aman"}
                            - Kanker: ${if (result.cancer == 1.0) "⚠️ BERISIKO" else "✅ Aman"}
                        """.trimIndent()

                        tvResult.text = hasilText
                    } else {
                        tvResult.text = "Server membalas, tapi datanya kosong (Null)."
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    tvResult.text = "Gagal! Kode: ${response.code()}\nPesan: $errorBody"
                }
            }

            override fun onFailure(call: Call<HealthResponse>, t: Throwable) {
                btnPredict.isEnabled = true
                tvResult.text = "ERROR APLIKASI:\n${t.message}"
                t.printStackTrace()
            }
        })
    }
}