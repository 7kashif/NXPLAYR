package com.nxplayr.fsl.ui.activity.addstaticwebsite.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.activity.addstaticwebsite.viewmodel.AddWebSiteModel
import com.nxplayr.fsl.ui.activity.addstaticwebsite.viewmodel.EditWebsiteModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.R
import kotlinx.android.synthetic.main.fragment_add_website.*
import kotlinx.android.synthetic.main.fragment_cms.*
import kotlinx.android.synthetic.main.fragment_current_location.*
import kotlinx.android.synthetic.main.fragment_jerusy_number.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject

class AddStaticWebsiteActivity:AppCompatActivity(), View.OnClickListener {

    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var mActivity: AppCompatActivity? = null

    var TitleSite: String? = null
    var URLSite: String? = null

    var URL_ID: String? = null
    var URL_TITLE: String? = null
    var URL_LINK: String? = null
    var FRNSTR: String? = null
    private lateinit var  addWebsieModel: AddWebSiteModel
    private lateinit var  editWebsieModel: EditWebsiteModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_add_website)

        sessionManager = SessionManager(this@AddStaticWebsiteActivity)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
            if (userData?.userWebsite.isNullOrEmpty()){

            }
        }
        FRNSTR =  intent.getStringExtra("frmString")
        URL_TITLE =  intent.getStringExtra("urlTitle")
        URL_LINK =  intent.getStringExtra("urlLink")
        URL_ID =  intent.getStringExtra("urlID")

        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(this@AddStaticWebsiteActivity)
            onBackPressed()
        }

        lylSaveUrl.setOnClickListener(this)
        lylSelectedSaveUrl.setOnClickListener(this)

        if (FRNSTR.equals("Add"))
        {

            tvToolbarTitle.text = getString(R.string.static_website)

            url_edit_text.setText("https://")

            title_edit_text.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {

                    lylSaveUrl.visibility = View.GONE
                    lylSelectedSaveUrl.visibility = View.VISIBLE
                }
            })
            url_edit_text.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {

                    lylSaveUrl.visibility = View.GONE
                    lylSelectedSaveUrl.visibility = View.VISIBLE
                }
            })

        }
        else
        {

            tvToolbarTitle.text = getString(R.string.edit_website)

            title_edit_text.setText(URL_TITLE)
            url_edit_text.setText(URL_LINK)

            TitleSite = title_edit_text.text.toString().trim()
            URLSite = url_edit_text.text.toString().trim()

            lylSaveUrl.visibility = View.GONE
            lylSelectedSaveUrl.visibility = View.VISIBLE
        }
    }

    private fun setupViewModel() {
        addWebsieModel = ViewModelProvider(this@AddStaticWebsiteActivity).get(AddWebSiteModel::class.java)
        editWebsieModel = ViewModelProvider(this@AddStaticWebsiteActivity).get(EditWebsiteModel::class.java)

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.lylSaveUrl -> {
                Toast.makeText(this@AddStaticWebsiteActivity, "Enter Static Website Name", Toast.LENGTH_SHORT).show()
            }

            R.id.lylSelectedSaveUrl -> {

                if (FRNSTR.equals("Add")) {

                    TitleSite = title_edit_text.text.toString().trim()
                    URLSite = url_edit_text.text.toString().trim()

                    if (TitleSite.equals("")) {

                        title_edit_text.setFocusable(true);
                        title_edit_text.setError("Field is Required");

                    } else if (URLSite.equals("")) {

                        title_edit_text.setError(null);
                        url_edit_text.setFocusable(true);
                        url_edit_text.setError("Field is Required");

                    }  else if (!MyUtils.isValidURL(url_edit_text.text.toString())){
                        Toast.makeText(this@AddStaticWebsiteActivity, "Please Enter valid URL", Toast.LENGTH_SHORT).show()
//                         MyUtils.showSnackbar(mActivity!!, "Please Enter valid URL", rcWebsite)
                    }else {

                        addWebSiteLink("1", userData?.userID!!, TitleSite!!, URLSite!!, RestClient.apiType, RestClient.apiVersion)
                    }

                } else {

                    TitleSite = title_edit_text.text.toString().trim()
                    URLSite = url_edit_text.text.toString().trim()

                    if (TitleSite.equals("")) {

                        title_edit_text.setFocusable(true);
                        title_edit_text.setError("Field is Required");

                    } else if (URLSite.equals("")) {

                        title_edit_text.setError(null);
                        url_edit_text.setFocusable(true);
                        url_edit_text.setError("Field is Required");

                    } else if (!MyUtils.isValidURL(url_edit_text.text.toString())){
//                        MyUtils.showSnackbar(mActivity!!, "Please Enter valid URL", rcWebsite)
                         Toast.makeText(this@AddStaticWebsiteActivity, "Please Enter valid URL", Toast.LENGTH_SHORT).show()
                    }else  {

                        editWebSiteLink("1", userData?.userID!!, URL_ID, TitleSite, URLSite, RestClient.apiType, RestClient.apiVersion)
                    }
                }

            }
        }
    }

    private fun addWebSiteLink(languageID: String, userID: String?, titleSite: String, urlSite: String, apiType: String, apiVersion: String) {

        relativeprogressBar.visibility = View.VISIBLE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("languageID", languageID)
            jsonObject.put("loginuserID", userID)
            jsonObject.put("userurlName", titleSite)
            jsonObject.put("userurlLink", urlSite)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("ADD_WEBSITE_GRID", jsonObject.toString())
        jsonArray.put(jsonObject)
        addWebsieModel.apiAddSubAlbum(this@AddStaticWebsiteActivity, false, jsonArray.toString())
                .observe(this@AddStaticWebsiteActivity,
                    { websiteListPojo ->

                        relativeprogressBar.visibility = View.GONE

                        if (websiteListPojo != null && websiteListPojo.isNotEmpty()) {

                          Toast.makeText(this@AddStaticWebsiteActivity,websiteListPojo!!.get(0)!!.message!!,Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            relativeprogressBar.visibility = View.GONE
                            Toast.makeText(this@AddStaticWebsiteActivity,"It seems there is no internet connection.",Toast.LENGTH_SHORT).show()
                        }

                    })
    }

    private fun editWebSiteLink(languageID: String, userID: String, urlId: String?, urlName: String?, urlLink: String?,
                                apiType: String, apiVersion: String) {

        relativeprogressBar.visibility = View.VISIBLE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("languageID", languageID)
            jsonObject.put("loginuserID", userID)
            jsonObject.put("userurlID", urlId)
            jsonObject.put("userurlName", urlName)
            jsonObject.put("userurlLink", urlLink)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("EDIT_WEBSITE_GRID", jsonObject.toString())
        jsonArray.put(jsonObject)
        editWebsieModel.apiEditSubAlbum(this@AddStaticWebsiteActivity, false, jsonArray.toString())
                .observe(this@AddStaticWebsiteActivity,
                    { websiteListPojo ->

                        relativeprogressBar.visibility = View.GONE
                        if (websiteListPojo != null && websiteListPojo.isNotEmpty()) {
                            Toast.makeText(this@AddStaticWebsiteActivity, websiteListPojo[0].message, Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            relativeprogressBar.visibility = View.GONE
                        }
                    })
    }

    override fun onBackPressed() {
        MyUtils.finishActivity(this@AddStaticWebsiteActivity,true)
    }
}