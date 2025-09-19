package io.appwrite.starterkit

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// -------------------- Alpha Vantage (Stocks) --------------------
interface AlphaVantageApi {
    @GET("query")
    suspend fun getTimeSeries(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String = "SPY", // S&P500 ETF proxy
        @Query("apikey") apiKey: String
    ): AlphaResponse
}

data class AlphaResponse(
    @SerializedName("Time Series (Daily)")
    val timeSeries: Map<String, DailyPrice>?
)

data class DailyPrice(
    @SerializedName("4. close") val close: String
)

// -------------------- GoldAPI (Gold) --------------------
interface GoldApi {
    // REMOVED: Headers are now added dynamically by the Interceptor
    @GET("XAU/USD")
    suspend fun getLatestGold(): GoldResponse

    @GET("XAU/USD/{date}")
    suspend fun getHistoricalGold(@Path("date") date: String): GoldResponse
}

data class GoldResponse(
    val price: Double
)

// -------------------- HomeActivity --------------------
class HomeActivity : BaseBottomNavActivity() {

    private lateinit var recessionScoreText: TextView
    private lateinit var statusText: TextView
    private lateinit var stocksText: TextView
    private lateinit var goldText: TextView
    private lateinit var btcText: TextView

    override val currentNavId = R.id.nav_home

    // Retrofit service for Alpha Vantage
    private val alphaService: AlphaVantageApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.alphavantage.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlphaVantageApi::class.java)
    }

    // CORRECTED: Retrofit service for GoldAPI using an Interceptor
    private val goldService: GoldApi by lazy {
        // 1. Create an Interceptor to add the API key header securely
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                // Use the key from BuildConfig (which reads from local.properties)
                .header("x-access-token",  io.appwrite.starterkit.BuildConfig.GOLD_API_KEY)
                .header("Content-Type", "application/json")
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        // 2. Build an OkHttpClient with our interceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        // 3. Build Retrofit using the new secure client
        Retrofit.Builder()
            .baseUrl("https://www.goldapi.io/api/")
            .client(okHttpClient) // Use the custom client
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoldApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Bind UI
        recessionScoreText = findViewById(R.id.recessionScoreText)
        statusText = findViewById(R.id.statusText)
        stocksText = findViewById(R.id.stocksText)
        goldText = findViewById(R.id.goldText)
        btcText = findViewById(R.id.btcText)

        // Fetch data
        fetchStockData()
        fetchGoldData()
    }

    // -------------------- Fetch Stocks --------------------
    private fun fetchStockData() {
        lifecycleScope.launch {
            try {
                // IMPORTANT: This Alpha Vantage key is likely expired.
                // You should also move it to your local.properties and BuildConfig for best practice.
                val response = alphaService.getTimeSeries(apiKey = "UFKR3Y1G0POBXME3")
                val timeSeries = response.timeSeries

                if (timeSeries != null && timeSeries.isNotEmpty()) {
                    val sortedDates = timeSeries.keys.sortedDescending()
                    if (sortedDates.size > 30) {
                        val latest = timeSeries[sortedDates[0]]!!.close.toDouble()
                        val past30 = timeSeries[sortedDates[30]]!!.close.toDouble()
                        val sp500Change = ((latest - past30) / past30) * 100

                        val (score, status) = calculateRecessionScore(sp500Change)

                        runOnUiThread {
                            updateRecessionScore(score, status)
                            stocksText.text = "Stocks ${formatChange(sp500Change)}"
                        }
                    }
                } else {
                    Log.e("HomeActivity", "Empty Alpha Vantage response")
                }

            } catch (e: Exception) {
                Log.e("HomeActivity", "Error fetching Alpha Vantage data", e)
                runOnUiThread {
                    Toast.makeText(
                        this@HomeActivity,
                        "Failed to load stock data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // -------------------- Fetch Gold --------------------
    private fun fetchGoldData() {
        lifecycleScope.launch {
            try {
                val latest = goldService.getLatestGold().price

                val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                val calendar = java.util.Calendar.getInstance()
                calendar.add(java.util.Calendar.DAY_OF_YEAR, -30)
                val pastDate = format.format(calendar.time)

                val past = goldService.getHistoricalGold(pastDate).price

                val goldChangePct = ((latest - past) / past) * 100

                runOnUiThread {
                    goldText.text = "Gold ${formatChange(goldChangePct)}"
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error fetching gold data", e)
                runOnUiThread {
                    Toast.makeText(
                        this@HomeActivity,
                        "Failed to load gold data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // -------------------- Helpers --------------------
    private fun calculateRecessionScore(sp500: Double): Pair<Int, String> {
        var score = 0
        if (sp500 < -5) score += 2

        val status = when {
            score >= 4 -> "Warning"
            score >= 2 -> "Risky"
            else -> "Stable"
        }
        return score to status
    }

    private fun updateRecessionScore(score: Int, status: String) {
        recessionScoreText.text = score.toString()
        statusText.text = when (status) {
            "Stable" -> "Stable ✅"
            "Risky" -> "Risky ⚠️"
            "Warning" -> "Recession Warning 🚨"
            else -> status
        }

        val color = when (status) {
            "Stable" -> R.color.stable_green
            "Risky" -> R.color.risky_yellow
            "Warning" -> R.color.warning_red
            else -> R.color.white
        }
        statusText.setTextColor(ContextCompat.getColor(this, color))
    }

    private fun formatChange(value: Double): String {
        val arrow = if (value >= 0) "↑" else "↓"
        return "$arrow${String.format("%.1f", kotlin.math.abs(value))}%"
    }
}
