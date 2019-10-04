package com.trending.data.remote

import com.google.gson.GsonBuilder
import com.trending.utils.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
    private val TAG = "ApiClient"

    private var retrofit: Retrofit? = null

    companion object {
        private var apiClient: ApiClient? = null
        fun getInstance(): ApiClient {
            if (apiClient == null) {
                apiClient =
                    ApiClient()
            }
            return apiClient as ApiClient
        }
    }

    private fun getClient(): Retrofit? {
        if (retrofit == null) {
            val gson = GsonBuilder().setLenient().create()
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getHttpClient())
                .build()
        }
        return retrofit
    }

    private fun getHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.addInterceptor { chain ->
            val request =
                chain.request().newBuilder().header("Content-Type", "application/json").build()
            chain.proceed(request)
        }
        return httpClientBuilder.addNetworkInterceptor(interceptor)
            .readTimeout(30000, TimeUnit.MILLISECONDS)
            .connectTimeout(60000, TimeUnit.MILLISECONDS).build()
    }

    fun getAPIService(): ApiService? {
        return getClient()?.create(ApiService::class.java)
    }

}