package com.nxplayr.fsl.ui.activity.onboarding.view

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.cms.viewmodel.CmsPageModel
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_cms.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*


class TermsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var cmsModel: CmsPageModel
    var mode : String = ""
    var title : String = ""
    var sessionManager: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_cms)
        setupViewModel()
        setupUI()
    }

    private fun setupViewModel() {
        sessionManager = SessionManager(this)
        cmsModel = ViewModelProvider(this).get(CmsPageModel::class.java)
    }

    private fun setupUI() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        mode = intent.getStringExtra("mode").toString()
        title = intent.getStringExtra("title").toString()
        tvToolbarTitle.text = title
        btnRetry.setOnClickListener(this)
        getCmsContent()
    }

    private fun getCmsContent() {
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        webView.visibility = View.GONE
        relativeprogressBar.visibility = View.VISIBLE

        var webpage = ""
        when (mode) {
            "1" -> {
                webpage = "Terms and Conditions"
            }
            "2" -> {
                webpage = "Privacy Policy"
            }
            "3" -> {
                webpage = "Community Guidelines"
            }
        }

        cmsModel.getCmsPage(this, "0", webpage + " ")
            .observe(this) {
                if (it != null && it.isNotEmpty() && it[0].status == "true") {

                    ll_no_data_found.visibility = View.GONE
                    nointernetMainRelativelayout.visibility = View.GONE
                    relativeprogressBar.visibility = View.GONE

                    webView.visibility = View.VISIBLE
                    webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN;

                    var data = ""
                    if (sessionManager?.getSelectedLanguage()!=null && sessionManager?.getSelectedLanguage()?.languageID.equals("1", true))
                        data = it[0].data[0].cmspageContents
                    else
                        data = it[0].data[0].cmspageFrenchContents

                    var text = ("<html><head>"
                            + "<link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\">"
                            + "</head>"
                            + "<body>"
                            + data
                            + "</body></html>")
                    val baseUrl = "file:///android_asset/"
                    text = "<font color='white'>" + text + "</font>";
                    webView.loadDataWithBaseURL(baseUrl, text, "text/html", "UTF-8", null);
                    webView.setBackgroundColor(0);
                } else {

                    relativeprogressBar.visibility = View.GONE

                    try {
                        nointernetMainRelativelayout.visibility = View.VISIBLE
                        if (MyUtils.isInternetAvailable(this)) {
                            nointernetImageview.setImageDrawable(this.getDrawable(R.drawable.ic_warning_black_24dp))
                            nointernettextview.text =
                                (this.getString(R.string.error_crash_error_message))
                        } else {
                            nointernetImageview.setImageDrawable(this.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp))

                            nointernettextview.text =
                                (this.getString(R.string.error_common_network))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRetry -> {

                getCmsContent()
            }
        }
    }


}
