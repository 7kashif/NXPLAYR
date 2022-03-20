package com.nxplayr.fsl.data.api

import com.nxplayr.fsl.BuildConfig
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceBuilder {


    fun setOkHttpClientBuilder(): OkHttpClient.Builder {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            // development build
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            // production build
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
        }
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(loggingInterceptor)
        builder.connectTimeout(300, TimeUnit.SECONDS)
        builder.readTimeout(80, TimeUnit.SECONDS)
        builder.writeTimeout(90, TimeUnit.SECONDS)
        return builder
    }

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://google-translate1.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
            setOkHttpClientBuilder()
                    .build()
            )
            .build()


    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}