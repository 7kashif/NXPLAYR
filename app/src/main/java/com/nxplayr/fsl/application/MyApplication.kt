package com.nxplayr.fsl.application

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Util
import com.nxplayr.fsl.R
import com.nxplayr.fsl.util.SessionManager
import com.quickblox.auth.session.QBSettings
import java.io.File


const val USER_DEFAULT_PASSWORD = "quickblox"
const val CHAT_PORT = 5223
const val SOCKET_TIMEOUT = 300

//Chat credentials range
private const val MAX_PORT_VALUE = 65535
private const val MIN_PORT_VALUE = 1000
private const val MIN_SOCKET_TIMEOUT = 300
private const val MAX_SOCKET_TIMEOUT = 60000

//App credentials
private const val APPLICATION_ID = "88286"
private const val AUTH_KEY = "x5dvS6eFrZ8AxSR"
private const val AUTH_SECRET = "rvgS4bLVDfMsEvW"
private const val ACCOUNT_KEY = "dsptYjqVx68ReL1yHyWS"

const val KEEP_ALIVE: Boolean = true
const val USE_TLS: Boolean = true
const val AUTO_JOIN: Boolean = false
const val AUTO_MARK_DELIVERED: Boolean = true
const val RECONNECTION_ALLOWED: Boolean = true
const val ALLOW_LISTEN_NETWORK: Boolean = true

class MyApplication : Application() {


    companion object {
        lateinit var instance: MyApplication
            private set

        @kotlin.jvm.JvmField
        var simpleCache: SimpleCache? = null
        protected var userAgent: String? = null
        var sessionManager: SessionManager? = null
        fun languageId(context: Context? = null): String {

            if (sessionManager == null && context != null)
                sessionManager = SessionManager(context!!)
            else if (sessionManager == null && instance != null)
                sessionManager = SessionManager(instance)
            return if (sessionManager?.getSelectedLanguage() == null)
                "1" else
                sessionManager?.getSelectedLanguage()?.languageID!!
        }

        fun langugageName(context: Context): String {
            return if (languageId(context) == "2")
                "fr"
            else
                "en"
        }

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Fresco.initialize(this)
        MultiDex.install(this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        val evictor = LeastRecentlyUsedCacheEvictor(1024 * 1024 * 10)
        val databaseProvider: DatabaseProvider = ExoDatabaseProvider(this)
        val cacheFolder = File(this?.cacheDir, "exoCache")
        checkAppCredentials()
        checkChatSettings()
        initCredentials()
        userAgent = Util.getUserAgent(this, "ExoPlayerDemo")
        simpleCache = SimpleCache(cacheFolder, evictor, databaseProvider)


    }

    private fun checkAppCredentials() {
        if (APPLICATION_ID.isEmpty() || AUTH_KEY.isEmpty() || AUTH_SECRET.isEmpty() || ACCOUNT_KEY.isEmpty()) {
            throw AssertionError(getString(R.string.error_qb_credentials_empty))
        }
    }

    private fun checkChatSettings() {
        if (USER_DEFAULT_PASSWORD.isEmpty() || CHAT_PORT !in MIN_PORT_VALUE..MAX_PORT_VALUE
            || SOCKET_TIMEOUT !in MIN_SOCKET_TIMEOUT..MAX_SOCKET_TIMEOUT
        ) {
            throw AssertionError(getString(R.string.error_chat_credentails_empty))
        }
    }

    private fun initCredentials() {
        QBSettings.getInstance().init(applicationContext, APPLICATION_ID, AUTH_KEY, AUTH_SECRET)
        QBSettings.getInstance().accountKey = ACCOUNT_KEY

        // Uncomment and put your Api and Chat servers endpoints if you want to point the sample
        // against your own server.
        //
        // QBSettings.getInstance().setEndpoints("https://your_api_endpoint.com", "your_chat_endpoint", ServiceZone.PRODUCTION);
        // QBSettings.getInstance().zone = ServiceZone.PRODUCTION
    }

    /*fun useExtensionRenderers(): Boolean {
       return "withExtensions" == BuildConfig.FLAVOR
    }*/


    /** Returns a [HttpDataSource.Factory].  */
    fun buildHttpDataSourceFactory(): HttpDataSource.Factory? {
        return DefaultHttpDataSourceFactory(userAgent)

    }

    private fun buildReadOnlyCacheDataSource(
        upstreamFactory: DefaultDataSourceFactory,
        cache: Cache
    ): CacheDataSourceFactory? {
        return CacheDataSourceFactory(
            cache,
            upstreamFactory,
            FileDataSourceFactory(),  /* cacheWriteDataSinkFactory= */
            null,
            CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,  /* eventListener= */
            null
        )
    }

}