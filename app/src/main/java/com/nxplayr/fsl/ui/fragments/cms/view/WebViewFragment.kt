package com.nxplayr.fsl.ui.fragments.cms.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_cms.logo
import kotlinx.android.synthetic.main.fragment_webview.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*


class WebViewFragment : Fragment() {

    var activity: AppCompatActivity? = null
    private var v: View? = null
    var html: String = "FAQ"
    var cmsTitle: String = ""
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_webview, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(activity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        if (arguments != null) {
            cmsTitle = arguments!!.getString("title", "")
            html = arguments!!.getString("html", "")
            if (cmsTitle.contains("about us")) {
                logo.visibility = View.VISIBLE
            }
        }

        setupUI()
        setupObserver()
    }

    private fun setupObserver() {
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        relativeprogressBar.visibility = View.GONE

        webViewFaq.visibility = View.VISIBLE

        var text = ("<html><head>"
                + "<link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\">"
                + "</head>"
                + "<body>"
                + html
                + "</body></html>")
        val baseUrl = "file:///android_asset/"
        webViewFaq.loadDataWithBaseURL(baseUrl, text, "text/html", "UTF-8", null);
        webViewFaq.setBackgroundColor(0)
    }

    private fun setupUI() {
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        tvToolbarTitle.text = cmsTitle
        btnRetry.setOnClickListener {
            setupObserver()
        }
    }

}
