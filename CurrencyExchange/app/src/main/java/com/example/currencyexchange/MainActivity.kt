package com.example.currencyexchange

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {

    private lateinit var amountEditText: EditText
    private lateinit var baseCurrencyEditText: EditText
    private lateinit var resultTextView: TextView
    private lateinit var convertButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        amountEditText = findViewById(R.id.amountEditText)
        baseCurrencyEditText = findViewById(R.id.baseCurrencyEditText)
        resultTextView = findViewById(R.id.resultTextView)
        convertButton = findViewById(R.id.convertButton)

        convertButton.setOnClickListener {
            performConversion()
        }
    }

    private fun performConversion() {
        val amount = amountEditText.text.toString().toDoubleOrNull()
        val baseCurrency = baseCurrencyEditText.text.toString()

        if (amount == null || baseCurrency.isBlank()) {
            resultTextView.text = "Invalid input. Please check your entries."
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.exchangerate-api.com/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CurrencyApiService::class.java)
        val call = service.getExchangeRate(baseCurrency, "e6077320ae8fcc74f950cdfe")

        call.enqueue(object : Callback<CurrencyResponse> {
            override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val rates = it.rates
                        val targetCurrency = "EUR" // Set your target currency
                        val rate = rates[targetCurrency]
                        if (rate != null) {
                            val result = amount * rate
                            resultTextView.text = "%.2f $baseCurrency = %.2f $targetCurrency".format(amount, result)
                        } else {
                            resultTextView.text = "Error: Could not find rate for $targetCurrency"
                        }
                    }
                } else {
                    resultTextView.text = "Error: ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                resultTextView.text = "Failure: ${t.message}"
            }
        })
    }

    interface CurrencyApiService {
        @GET("latest")
        fun getExchangeRate(
            @Query("base") baseCurrency: String,
            @Query("apikey") apiKey: String
        ): Call<CurrencyResponse>
    }
}

data class CurrencyResponse(val rates: Map<String, Double>)
