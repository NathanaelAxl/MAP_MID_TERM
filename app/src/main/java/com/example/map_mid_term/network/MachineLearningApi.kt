package com.example.map_mid_term.network

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit // Perlu import ini untuk mengatur waktu

// 1. Request (Tetap sama)
data class HealthRequest(
    val age: Float,
    val BMI: Float,
    val sleep_hours: Float,
    val sex: String,
    val physical_activity: String
)

// 2. Response & Result
data class HealthResponse(
    val status: String,
    val data: HealthResult?
)

data class HealthResult(
    // ✅ UBAH JADI DOUBLE: Agar aman kalau Python kirim "1.0" atau "0.0"
    // Kalau pakai Int, nanti bisa error "NumberFormatException"
    val diabetes: Double,
    val heart_attack: Double,
    val stroke: Double,
    val depression: Double,
    val cancer: Double
)

// 3. Interface API (Tetap sama)
interface ApiService {
    @POST("predict")
    fun predictHealth(@Body request: HealthRequest): Call<HealthResponse>
}

// 4. Retrofit Client (BAGIAN INI YANG KRUSIAL)
object RetrofitClient {
    private const val BASE_URL = "http://10.53.11.136:5000/"

    // ✅ Settingan Timeout: Kita paksa aplikasi menunggu sampai 60 detik
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Waktu maksimal buat nyambung
        .readTimeout(60, TimeUnit.SECONDS)    // Waktu maksimal nunggu jawaban server
        .writeTimeout(60, TimeUnit.SECONDS)   // Waktu maksimal kirim data
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // ✅ Masukkan settingan client di sini
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}