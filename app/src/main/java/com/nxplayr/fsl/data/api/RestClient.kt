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

//        var base = "http://13.235.206.122/fsl/"
//        var url = "http://13.235.206.122/fsl/backend/web/index.php/v1/"

        var base = ""
        var url = ""
        var image_base_url_users = ""
        var image_base_url_banners =  ""
        var image_base_url_flag = ""
        var image_base_url_posts =""
        var image_base_url_mediaEmp =""
        var image_base_url_mediaEdu =  ""
        var image_base_url_job = ""
        var sharingUrl = ""

        internal var REST_CLIENT: RestApi? = null


        private var restAdapter: Retrofit? = null

        var uniqueDeviceId = Settings.System.getString(MyApplication.instance.contentResolver, Settings.Secure.ANDROID_ID)

        init {

            base = BuildConfig.BASE_URL
            url = BuildConfig.JOB_URL
            sharingUrl = "${url}frontend/web/"

            if (BuildConfig.FLAVOR == "staging") {
                image_base_url_users = BuildConfig.BASE_URL + BuildConfig.BASE_ASSET_URL + "users/"
                image_base_url_banners = BuildConfig.BASE_URL + BuildConfig.BASE_ASSET_URL + "banners/"
                image_base_url_flag = BuildConfig.BASE_URL + BuildConfig.BASE_ASSET_URL + "flag/"
                image_base_url_posts = BuildConfig.BASE_URL + BuildConfig.BASE_ASSET_URL + "post/"
                image_base_url_mediaEmp = BuildConfig.BASE_URL + BuildConfig.BASE_ASSET_URL + "employment/"
                image_base_url_mediaEdu = BuildConfig.BASE_URL + BuildConfig.BASE_ASSET_URL + "education/"
                image_base_url_job = BuildConfig.BASE_URL + BuildConfig.BASE_ASSET_URL + "company/"
            } else {
                image_base_url_users = BuildConfig.BASE_ASSET_URL + "users/"
                image_base_url_banners = BuildConfig.BASE_ASSET_URL + "banners/"
                image_base_url_flag = BuildConfig.BASE_ASSET_URL + "flag/"
                image_base_url_posts = BuildConfig.BASE_ASSET_URL + "post/"
                image_base_url_mediaEmp = BuildConfig.BASE_ASSET_URL + "employment/"
                image_base_url_mediaEdu = BuildConfig.BASE_ASSET_URL + "education/"
                image_base_url_job = BuildConfig.BASE_ASSET_URL + "company/"
            }

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


