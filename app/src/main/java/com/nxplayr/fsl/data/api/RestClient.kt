package com.nxplayr.fsl.data.api


import android.provider.Settings
import com.nxplayr.fsl.BuildConfig
import com.nxplayr.fsl.application.MyApplication
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RestClient {


    companion object {
        val apiType = "Android"
        const val apiVersion = "1.0"

        // Base server url
        /*var base = "http://betaapplication.com/fsl/"
        var url = "http://betaapplication.com/fsl/backend/web/index.php/v1/"*/

        var base = "http://13.235.206.122/fsl/"
        var url = "http://13.235.206.122/fsl/backend/web/index.php/v1/"

        var image_base_url_users = base + "backend/web/uploads/users/"
        var image_base_url_banners = base + "backend/web/uploads/banners/"
        var image_base_url_flag = base + "backend/web/uploads/flag/"
        var image_base_url_posts = base + "backend/web/uploads/post/"
        var image_base_url_mediaEmp = base + "backend/web/uploads/employment/"
        var image_base_url_mediaEdu = base + "backend/web/uploads/education/"
        const val image_base_url_job ="http://13.235.206.122/fsl/backend/web/uploads/company/"
        var sharingUrl = "${url}frontend/web/"


        internal var REST_CLIENT: RestApi? = null


        private var restAdapter: Retrofit? = null

        var uniqueDeviceId = Settings.System.getString(MyApplication.instance.contentResolver, Settings.Secure.ANDROID_ID)

        init {
            setupRestClient()

        }

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

        fun setupRestClient() {

            val gson = GsonBuilder().setLenient().create()
            restAdapter = Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(
                            setOkHttpClientBuilder()
                                    .build()
                    )
                    .build()
        }


        fun get(): RestApi? {
            if (REST_CLIENT == null) {
                REST_CLIENT = restAdapter!!.create(
                        RestApi::class.java
                )

            }
            return REST_CLIENT
        }


    }
}


