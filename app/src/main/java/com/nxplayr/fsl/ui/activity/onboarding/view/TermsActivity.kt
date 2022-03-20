package com.nxplayr.fsl.ui.activity.onboarding.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.ui.fragments.cms.viewmodel.CmsPageModel
import com.nxplayr.fsl.util.MyUtils
import kotlinx.android.synthetic.main.fragment_cms.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*


class TermsActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var  cmsModel: CmsPageModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_cms)
        setupViewModel()
        setupUI()
    }

    private fun setupViewModel() {
         cmsModel = ViewModelProvider(this).get(CmsPageModel::class.java)
    }


    private fun setupUI() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        tvToolbarTitle.text = resources.getString(R.string.terms_condition)

        btnRetry.setOnClickListener(this)
        getCmsContent()
    }

    private fun getCmsContent() {
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        webView.visibility = View.GONE
        relativeprogressBar.visibility = View.VISIBLE


        cmsModel?.getCmsPage(this!!, "0", "Terms and Conditions" +
                " ")
                ?.observe(this, {

                    if (it != null && it.isNotEmpty() && it[0].status.equals("true")) {

                        ll_no_data_found.visibility = View.GONE
                        nointernetMainRelativelayout.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE

                        webView.visibility = View.VISIBLE

                        var text = ("<html><head>"
                                + "<style type=\"text/css\">body{color: #FFFFFF}"
                                + "</style></head>"
                                + "<body>"
                                + it[0].data!![0]!!.cmspageContents
                                + "</body></html>")
                        text = "<font color='white'>" + text + "</font>";
                        webView.loadDataWithBaseURL(null, text, "text/html", "UTF-8", null);
                        webView.setBackgroundColor(0)
                    } else {


                        if (this != null) {
                            relativeprogressBar.visibility = View.GONE

                            try {
                                nointernetMainRelativelayout.visibility = View.VISIBLE
                                if (MyUtils.isInternetAvailable(this!!)) {
                                    nointernetImageview.setImageDrawable(this!!.getDrawable(R.drawable.ic_warning_black_24dp))
                                    nointernettextview.text = (this!!.getString(R.string.error_crash_error_message))
                                } else {
                                    nointernetImageview.setImageDrawable(this!!.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp))

                                    nointernettextview.text =
                                            (this!!.getString(R.string.error_common_network))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }

                    }
                })

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnRetry->{

                getCmsContent()
            }
        }
    }


}
