package com.nxplayr.fsl.ui.activity.chat.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.nxplayr.fsl.BuildConfig
import com.nxplayr.fsl.R
import com.quickblox.auth.session.QBSettings



class AppInfoActivity : BaseActivity() {
    private lateinit var appVersionTextView: TextView
    private lateinit var sdkVersionTextView: TextView
    private lateinit var appIDTextView: TextView
    private lateinit var authKeyTextView: TextView
    private lateinit var authSecretTextView: TextView
    private lateinit var accountKeyTextView: TextView
    private lateinit var apiDomainTextView: TextView
    private lateinit var chatDomainTextView: TextView
    private lateinit var appQAVersionTextView: TextView

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, AppInfoActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appinfo)
        initUI()
        fillUI()
    }

    private fun initUI() {
        appVersionTextView = findViewById(R.id.tv_app_version)
        sdkVersionTextView = findViewById(R.id.tv_sdk_version)
        appIDTextView = findViewById(R.id.tv_app_id)
        authKeyTextView = findViewById(R.id.tv_auth_key)
        authSecretTextView = findViewById(R.id.tv_auth_secret)
        accountKeyTextView = findViewById(R.id.tv_account_key)
        apiDomainTextView = findViewById(R.id.tv_api_domain)
        chatDomainTextView = findViewById(R.id.tv_chat_domain)
        appQAVersionTextView = findViewById(R.id.tv_qa_version)
    }

    private fun fillUI() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.appinfo_title)
        appVersionTextView.text = BuildConfig.VERSION_NAME
        sdkVersionTextView.text = com.quickblox.BuildConfig.VERSION_NAME
        appIDTextView.text = QBSettings.getInstance().applicationId
        authKeyTextView.text = QBSettings.getInstance().authorizationKey
        authSecretTextView.text = QBSettings.getInstance().authorizationSecret
        accountKeyTextView.text = QBSettings.getInstance().accountKey
        apiDomainTextView.text = QBSettings.getInstance().serverApiDomain
        chatDomainTextView.text = QBSettings.getInstance().chatEndpoint
    }
}