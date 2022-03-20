package com.nxplayr.fsl.ui.activity.post.view

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.util.MyUtils
import kotlinx.android.synthetic.main.activity_document.*
import kotlinx.android.synthetic.main.toolbar.*

class DocumentActivity : AppCompatActivity() {
    var file=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        if (intent != null)
        {
            if (intent.hasExtra("file"))
            {
                file = intent.getStringExtra("file")!!
            }

        }
        setupUI()


    }

    private fun setupUI() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        tvToolbarTitle.text = resources.getString(R.string.document)
        var mstr = "http://docs.google.com/gview?embedded=true&url=${file}"

        webViewTryForFree.settings.javaScriptEnabled = true
        webViewTryForFree.settings.domStorageEnabled = true
        webViewTryForFree.settings.setSupportMultipleWindows(true)
        webViewTryForFree.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {

            }
        }


        webViewTryForFree.loadUrl(mstr)
        onBackPressed()
    }

    override fun onBackPressed() {
        MyUtils.finishActivity(this@DocumentActivity, true)
    }
}