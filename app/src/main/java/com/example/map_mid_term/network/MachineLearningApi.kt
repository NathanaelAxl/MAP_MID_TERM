package com.example.map_mid_term.network

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

data class HealthRequest(
    val age: Float,
    val BMI: Float,
    val sleep_hours: Float,
    val sex: String,
    val physical_activity: String
)

data class HealthResponse(
    val status: String,
    val data: HealthResult?
)

data class HealthResult(
    // Menggunakan Double agar aman menerima angka desimal (1.0) dari Python
    val diabetes: Double,
    val heart_attack: Double,
    val stroke: Double,
    val depression: Double,
    val cancer: Double
)

// ========================
// 2. API INTERFACE
// ========================
interface ApiService {
    // Import @POST dan @Body sudah ada di atas, jadi tidak akan error NonExistentClass lagi
    @POST("predict")
    fun predictHealth(@Body request: HealthRequest): Call<HealthResponse>
}

// ========================
// 3. RETROFIT CLIENT OBJECT
// ========================
object RetrofitClient {
    // Menggunakan Server PythonAnywhere
    private const val BASE_URL = "https://acedia.pythonanywhere.com/"

    // Setting Timeout 60 Detik agar tidak mudah RTO (Request Time Out)
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}