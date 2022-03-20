package com.nxplayr.fsl.ui.activity.post.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.activity_web_link_post.*
import kotlinx.android.synthetic.main.toolbar.*


class WebPostActivity : AppCompatActivity(),View.OnClickListener {

    companion object {
        private val TAG = WebPostActivity::class.java.simpleName
    }

    var sessionManager: SessionManager? = null
    var userData: SignupData? = null

    private val mYourBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // now you can call all your fragments method here
            MyUtils.finishActivity(this@WebPostActivity, true)
        }
    }

    var bitmapImage: Bitmap? = null
    var From = ""
    var postDes=""
    var Url=""

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@WebPostActivity)
                .unregisterReceiver(mYourBroadcastReceiver)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_link_post)
        sessionManager = SessionManager(this@WebPostActivity)
        LocalBroadcastManager.getInstance(this@WebPostActivity)
                .registerReceiver(mYourBroadcastReceiver, IntentFilter("CreatePost"))
        userData = sessionManager?.get_Authenticate_User()
        if (intent != null) {
            From = intent?.getStringExtra("From")!!
        }
        setupUI()
    }

    private fun  setupUI(){


        tvToolbarTitle.text = resources.getString(R.string.post_link)
        toolbar.setNavigationIcon(R.drawable.back_arrow_signup)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        menuToolbarItem.text = "Next"
        menuToolbarItem.isEnabled=false
        menuToolbarItem.setOnClickListener (this)
        web_et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

                val textFromCreatePostEditText = web_et_search.text.toString().trim()
                if (textFromCreatePostEditText.isNotEmpty()) {
                    menuToolbarItem.visibility = View.VISIBLE
                } else {
                    menuToolbarItem.visibility = View.INVISIBLE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        initEditTextSearch()
        initWebView()
        web_iv_cross.setOnClickListener(this)
    }

    override fun onBackPressed() {
        if (web_view.canGoBack())
            web_view.goBack()
        else
            super.onBackPressed()
    }

    private fun initEditTextSearch() {
        web_et_search.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(v.text.toString().trim())
                hideKeyboard()
            }
            false
        }

        web_et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (web_iv_cross.visibility == View.VISIBLE && s.isEmpty()) {
                    web_iv_cross.visibility = View.INVISIBLE
                } else if (web_iv_cross.visibility == View.INVISIBLE && s.isNotEmpty()) {
                    web_iv_cross.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun initWebView() {
        val webSettings: WebSettings = web_view.settings
        webSettings.javaScriptEnabled = true

        web_view.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.i(TAG, "URL: $url")
                Url=url!!
                web_et_search.setText(url)
            }


        }

        web_view.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                Log.i(TAG, "Progress: $newProgress%")

                if (web_progress_bar.visibility == View.INVISIBLE && newProgress != 100)
                    web_progress_bar.visibility = View.VISIBLE
                if (web_progress_bar.visibility == View.VISIBLE && newProgress == 100)
                    web_progress_bar.visibility = View.INVISIBLE
                web_progress_bar.progress = newProgress
            }

            override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                super.onReceivedIcon(view, icon)
                Log.i(TAG, "URL1: $icon")
                Log.i(TAG, "URL1: $icon")
                Log.i(TAG, "URL2: ${view?.title}")
                if(icon!=null && view?.title!=null)
                {

                    menuToolbarItem.isEnabled=true
                    postDes=view?.title!!
                    bitmapImage = icon
                }

            }

        }
    }

    private fun performSearch(query: String) {
       // if (query.startsWith("http://") || query.startsWith("https://"))
            web_view.loadUrl(query)
        /*else
            web_view.loadUrl("https://www.google.com/search?q=$query")*/
    }

    private fun hideKeyboard() {
        try {
            val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.web_iv_cross->{
                web_et_search.setText("")

            }
            R.id.menuToolbarItem->{
                val intent = Intent(this@WebPostActivity, CreatePostActivityTwo::class.java)
                intent.putExtra("From", From)
                if (bitmapImage != null) {
                    intent.putExtra("bitmapImage", bitmapImage)
                }
                if (postDes.isNotEmpty()) {
                    intent.putExtra("postDes", postDes)
                }
                if (Url.isNotEmpty()) {
                    intent.putExtra("url", Url)
                }
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            }

        }
    }
}