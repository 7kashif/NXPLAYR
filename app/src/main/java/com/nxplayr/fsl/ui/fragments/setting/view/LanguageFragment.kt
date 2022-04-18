package com.nxplayr.fsl.ui.fragments.setting.view


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity

import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.setting.adapter.LanguageAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.viewmodel.*
import kotlinx.android.synthetic.main.toolbar.*
import com.google.gson.Gson
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.ui.fragments.setting.viewmodel.LanguageIntefaceListModel
import com.nxplayr.fsl.ui.fragments.setting.viewmodel.LanguageLabelModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.ui.fragments.setting.viewmodel.LanguageListModel
import com.nxplayr.fsl.util.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_add_hashtag.*
import kotlinx.android.synthetic.main.fragment_jerusy_number.*
import kotlinx.android.synthetic.main.fragment_language.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class LanguageFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var language_list: ArrayList<LanguageListPojo>? = ArrayList()
    var languageListModel: LanguageListModel? = null
    var mActivity: AppCompatActivity? = null
    var languageList: ArrayList<LanguageListData>? = ArrayList()
    var languageAdapter: LanguageAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var userData: SignupData? = null
    var sessionManager: SessionManager? = null
    var language : LanguageListData? = null
    var languageId = ""
    private lateinit var lannguageModel: LanguageIntefaceListModel
    private lateinit var loginModel: SignupModel
    private lateinit var languageLabelModel: LanguageLabelModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_language, container, false)
        }
        return v

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mActivity = context as AppCompatActivity

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (userData != null) {
            languageId = userData!!.languageID
        }
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle.text = getString(R.string.language)
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        linearLayoutManager = LinearLayoutManager(mActivity!!)

        languageAdapter =
            LanguageAdapter(mActivity!!, languageList, object : LanguageAdapter.OnItemClick {

                override fun onClicled(position: Int, from: String) {
                    if (sessionManager!!.isLoggedIn()) {

                        for (i in 0 until languageList!!.size) {
                            languageList!![i].status = i == position
                        }
                        languageId = languageList!![position].languageID
                        language = languageList!![position]
                        languageAdapter?.notifyDataSetChanged()
                    }
                }
            }, languageId)

        recyclerview.layoutManager = linearLayoutManager
        recyclerview.adapter = languageAdapter
        recyclerview.setHasFixedSize(true)
        languagesApi()

        if (!languageId.isNullOrEmpty()) {
            btn_update_language.strokeColor = (resources.getColor(R.color.colorPrimary))
            btn_update_language.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_update_language.textColor = resources.getColor(R.color.black)
        } else {
            btn_update_language.strokeColor = (resources.getColor(R.color.grayborder))
            btn_update_language.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_update_language.textColor = resources.getColor(R.color.colorPrimary)
        }

        btn_update_language.setOnClickListener(this)

        btnRetry.setOnClickListener(this)
    }

    private fun setupViewModel() {
        lannguageModel = ViewModelProvider(this@LanguageFragment).get(LanguageIntefaceListModel::class.java)
        loginModel = ViewModelProvider(this@LanguageFragment).get(SignupModel::class.java)
        languageLabelModel = ViewModelProvider(this@LanguageFragment).get(LanguageLabelModel::class.java)
    }

    private fun languagesApi() {
        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        lannguageModel.getLanguageList(mActivity!!, false, jsonArray.toString())
            .observe(viewLifecycleOwner) { languageListPojo ->

                relativeprogressBar.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE

                if (languageListPojo != null && languageListPojo.isNotEmpty()) {
                    if (languageListPojo[0].status == true) {
                        languageList?.clear()
                        languageList?.addAll(languageListPojo[0].data)
                        languageAdapter?.notifyDataSetChanged()
                    } else {

                        if (languageList!!.size == 0) {
                            ll_no_data_found.visibility = View.VISIBLE
                            recyclerview.visibility = View.GONE

                        } else {
                            ll_no_data_found.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE
                        }
                    }
                } else {
                    btn_update_language.strokeColor = (resources.getColor(R.color.grayborder))
                    btn_update_language.backgroundTint = (resources.getColor(R.color.transperent1))
                    btn_update_language.textColor = resources.getColor(R.color.colorPrimary)

                    ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                }
            }

    }

    private fun changeLanguage(
        languageId: String,
        languageLabel: LanguageLabelPojo.LanguageLabelData
    ) {

        btn_update_language.startAnimation()

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", languageId)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        loginModel.userRegistration(mActivity!!, false, jsonArray.toString(), "changeLanguage")
            .observe(
                viewLifecycleOwner
            ) { loginPojo ->
                btn_update_language.endAnimation()
                if (loginPojo != null) {
                    if (loginPojo.get(0).status.equals("true", true)) {
                        try {


                            sessionManager?.LanguageLabel = languageLabel
                            loginPojo[0].data[0].languageID = this.languageId
                            sessionManager?.setSelectedLanguage(language)
                            StoreSessionManager(loginPojo[0].data[0])
                            //setLocalLanguage()

                            languageAdapter?.notifyDataSetChanged()
                            Handler().postDelayed({
                                (activity as MainActivity).onBackPressed()
                            }, 1000)
                            MyUtils.showSnackbar(
                                mActivity!!,
                                loginPojo.get(0).message,
                                ll_mainLanguageList
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            loginPojo.get(0).message, ll_mainLanguageList
                        )
                    }

                } else {
                    btn_update_language.endAnimation()
                    ErrorUtil.errorMethod(ll_mainLanguageList)
                }
            }

    }

    private fun getLanguageLabelList(languageId: String) {
        MyUtils.showProgressDialog(activity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", languageId)
            jsonObject.put("langLabelStatus", "")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        languageLabelModel.getLanguageList(activity!!, false, jsonArray.toString())
            .observe(viewLifecycleOwner
            ) { languageLabelPojo ->
                if (!languageLabelPojo.isNullOrEmpty()) {
                    if (languageLabelPojo[0].status == true && !languageLabelPojo[0].data.isNullOrEmpty()) {
                        MyUtils.dismissProgressDialog()
                        changeLanguage(languageId, languageLabelPojo[0].data?.get(0)!!)
                    } else {
                        MyUtils.dismissProgressDialog()
                        (activity as MainActivity).showSnackBar(languageLabelPojo[0].info.toString())
                    }

                } else {
                    MyUtils.dismissProgressDialog()
                    (activity as MainActivity).errorMethod()
                }
            }
    }

    private fun setLocalLanguage() {


//        if (sessionManager?.getsetSelectedLanguage() == "2") {
//            LocaleUtils.updateConfig(activity!!, "fr")
//        } else {
//            LocaleUtils.updateConfig(activity!!, "en")
//        }
//        startMainActivity()

    }

    private fun StoreSessionManager(userdata: SignupData?) {

        val gson = Gson()

        val json = gson.toJson(userdata)
        sessionManager?.create_login_session(
            json,
            userdata!!.userMobile,
            "",
            true,
            sessionManager!!.isEmailLogin()
        )
        languageAdapter?.notifyDataSetChanged()

    }

    fun startMainActivity() {

//        Intent(activity!!, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(this)
//        }
//        activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
//        activity!!.finish()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_update_language -> {
                if (!languageId.isEmpty()) {
                    // changeLanguage()
                    getLanguageLabelList(languageId)
                } else {
                    MyUtils.showSnackbar(
                        mActivity!!,
                        getString(R.string.please_select_language),
                        ll_mainLanguageList
                    )
                }

            }
            R.id.btnRetry -> {
                languagesApi()

                if (!languageId.isNullOrEmpty()) {
                    btn_update_language.strokeColor = (resources.getColor(R.color.colorPrimary))
                    btn_update_language.backgroundTint = (resources.getColor(R.color.colorPrimary))
                    btn_update_language.textColor = resources.getColor(R.color.black)
                } else {
                    btn_update_language.strokeColor = (resources.getColor(R.color.grayborder))
                    btn_update_language.backgroundTint = (resources.getColor(R.color.transperent1))
                    btn_update_language.textColor = resources.getColor(R.color.colorPrimary)
                }

            }
        }
    }
}


