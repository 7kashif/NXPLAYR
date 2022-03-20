package com.nxplayr.fsl.ui.fragments.userwebsite.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nxplayr.fsl.ui.activity.addstaticwebsite.view.AddStaticWebsiteActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userwebsite.adapter.StaticWebsiteAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userwebsite.viewmodel.DeleteWebsiteModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.SiteList
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.webstire_option_sheet.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class WebsiteBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {

    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var URL_ID: String? = null
    var URL_POSTION: Int? = null
    var URL_NAME: String? = null
    var URL_LINK: String? = null

    var website_list: ArrayList<SiteList?>? = ArrayList()
    var websiteListAdapter: StaticWebsiteAdapter? = null
    var bottomSheetListener: BottomSheetListener?=null
    private lateinit var  getLanguageModel: DeleteWebsiteModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme1);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.webstire_option_sheet, container, false)

        return v
    }

    fun setOnclickLisner(mListener: BottomSheetListener) {
        this.bottomSheetListener = mListener
    }
    interface BottomSheetListener {
        fun onOptionClick(text: String)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(context!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        lyl_edit_site.setOnClickListener(this)
        lyl_site_delete.setOnClickListener(this)

        if (arguments != null) {
            URL_ID = arguments?.getString("urlID", "")!!
            URL_POSTION = arguments?.getInt("position")!!
            URL_NAME = arguments?.getString("urlTitle")!!
            URL_LINK = arguments?.getString("urlLink")!!
        }
    }

    override fun onClick(p0: View?) {

        when (p0!!.id) {

            R.id.lyl_edit_site -> {

                val addWebsite = Intent(context, AddStaticWebsiteActivity::class.java)
                addWebsite.putExtra("urlID", URL_ID)
                addWebsite.putExtra("urlTitle", URL_NAME!!)
                addWebsite.putExtra("urlLink", URL_LINK!!)
                addWebsite.putExtra("frmString", "Edit")
                startActivity(addWebsite)
                dismiss()
            }

            R.id.lyl_site_delete -> {

                getDeleteWebsite("1", userData?.userID!!, URL_ID, RestClient.apiType, RestClient.apiVersion,lyl_site_delete)
            }

        }
    }

    private fun getDeleteWebsite(languageID: String, loginuserID: String, urlId: String?, apiType: String, apiVersion: String, lyl_site_delete: LinearLayout) {

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("languageID", languageID)
            jsonObject.put("loginuserID", loginuserID)
            jsonObject.put("userurlID", urlId)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        getLanguageModel.apiDeleteSubAlbum((context as Activity?)!!, false, jsonArray.toString())
                .observe(this@WebsiteBottomSheet!!,
                        Observer { websiteListPojo ->

                            if (websiteListPojo != null && websiteListPojo.isNotEmpty()) {
                                if (websiteListPojo[0].status.equals("true", true)) {
                                    bottomSheetListener?.onOptionClick("Delete")
                                    dismiss()
                                } else {
                                    dismiss()
                                    MyUtils.showSnackbar(context!!,websiteListPojo[0].message!!, lyl_site_delete)
                                }

                            } else {
                                dismiss()
                                ErrorUtil.errorMethod(lyl_site_delete)
                            }

                        })
    }

}