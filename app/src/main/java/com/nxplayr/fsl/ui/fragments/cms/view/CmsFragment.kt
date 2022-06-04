package com.nxplayr.fsl.ui.fragments.cms.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.ui.activity.main.view.MainActivity

import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.cms.viewmodel.CmsPageModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_cms.*
import kotlinx.android.synthetic.main.fragment_cms.logo
import kotlinx.android.synthetic.main.fragment_webview.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.Dispatchers


class CmsFragment : Fragment() {

    var activity: AppCompatActivity? = null
    private var v: View? = null
    var cmsCode: String = "About us"
    var cmsTitle: String = ""
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    private lateinit var cmsPageModel: CmsPageModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_cms, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(requireActivity())
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        if (arguments != null) {
            cmsTitle = arguments!!.getString("title", "")
            cmsCode = arguments!!.getString("cmsparameter", "")
            if (cmsCode == "About Us") {
                logo.visibility = View.VISIBLE
            }
        }
        setupViewModel()
        setupUI()
        setupObserver()
    }

    private fun setupViewModel() {
        cmsPageModel = ViewModelProvider(this).get(CmsPageModel::class.java)
    }

    private fun setupObserver() {
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        webView.visibility = View.GONE
        relativeprogressBar.visibility = View.VISIBLE

        cmsPageModel?.getCmsPage(requireActivity(), userData!!.userID, cmsCode)
            .observe(requireActivity()) { it ->
                if (it != null && it.isNotEmpty() && it[0].status.equals("true")) {

                    ll_no_data_found.visibility = View.GONE
                    nointernetMainRelativelayout.visibility = View.GONE
                    relativeprogressBar.visibility = View.GONE

                    webView.visibility = View.VISIBLE
                    webView.loadUrl(it[0].data[0].url)
                    webView.setBackgroundColor(0)

                } else {


                    if (this != null) {
                        relativeprogressBar.visibility = View.GONE

                        try {
                            nointernetMainRelativelayout.visibility = View.VISIBLE
                            if (MyUtils.isInternetAvailable(requireActivity())) {
                                nointernetImageview.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.ic_warning_black_24dp
                                    )
                                )
                                nointernettextview.text =
                                    (this!!.getString(R.string.error_crash_error_message))
                            } else {
                                nointernetImageview.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.ic_signal_wifi_off_black_24dp
                                    )
                                )

                                nointernettextview.text =
                                    (this!!.getString(R.string.error_common_network))
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                }
            }

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
