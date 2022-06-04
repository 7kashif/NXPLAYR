package com.nxplayr.fsl.ui.fragments.setting.view


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.SignupPojo
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModelV2
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_privacy.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject


class PrivacyFragment : Fragment() {
    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    private lateinit var  loginModel: SignupModelV2


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (v == null) {
            v = inflater.inflate(R.layout.fragment_privacy, container, false)
        }

        return v
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngPrivacy.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngPrivacy
            if (!sessionManager?.LanguageLabel?.lngSetYourPrivacy.isNullOrEmpty())
                set_ur_privacy.text = sessionManager?.LanguageLabel?.lngSetYourPrivacy
            if (!sessionManager?.LanguageLabel?.lngAllowPeopleToFollowMe.isNullOrEmpty())
                allow_ppl_follow_me.text = sessionManager?.LanguageLabel?.lngAllowPeopleToFollowMe
            if (!sessionManager?.LanguageLabel?.lngAllowConnectRequests.isNullOrEmpty())
                allow_connect_req.text = sessionManager?.LanguageLabel?.lngAllowConnectRequests
        }
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
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle.setText(R.string.privacy)

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        if (!userData?.privacy.isNullOrEmpty()) {
            setPrivacySettings()
        }
        switch_peopleFollowMe.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            updatePrivacyApi()
        })
        switch_allowConnectRequest.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            updatePrivacyApi()
        })
    }

    private fun setupViewModel() {
         loginModel = ViewModelProvider(this@PrivacyFragment).get(SignupModelV2::class.java)

    }

    fun setPrivacySettings() {
        if (!userData?.privacy.isNullOrEmpty()) {
            switch_peopleFollowMe.isChecked = !userData!!.privacy[0].userprivacysettingsAFME.isNullOrEmpty() && userData!!.privacy[0].userprivacysettingsAFME.toString().contains("Yes")
            switch_allowConnectRequest.isChecked = !userData!!.privacy[0].userprivacysettingsACR.isNullOrEmpty() && userData!!.privacy[0].userprivacysettingsACR.toString().contains("Yes")
        }
    }


    private fun updatePrivacyApi() {
        MyUtils.showProgressDialog(mActivity!!, "Please wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)

            if (switch_peopleFollowMe.isChecked)
                jsonObject.put("userprivacysettingsAFME", "Yes")
            else
                jsonObject.put("userprivacysettingsAFME", "No")

            if (switch_allowConnectRequest.isChecked)
                jsonObject.put("userprivacysettingsACR", "Yes")
            else
                jsonObject.put("userprivacysettingsACR", "No")


            jsonObject.put("languageID", "0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        loginModel.users_updatePrivacy(jsonArray.toString())
        loginModel.users_updatePrivacy
                .observe(viewLifecycleOwner,
                        Observer<List<SignupPojo>> { loginPojo ->
                            if (loginPojo != null && loginPojo.isNotEmpty()) {
                                MyUtils.dismissProgressDialog()

                                if (loginPojo[0].status.equals("true", true)) {

                                    StoreSessionManager(loginPojo[0].data[0])

                                } else {
                                    if (activity != null && activity is MainActivity)
                                        MyUtils.showSnackbar(mActivity!!, loginPojo!![0]!!.message!!, ll_mainPrivacy)
                                }

                            } else {
                                if (activity != null && activity is MainActivity) {
                                    ErrorUtil.errorMethod(ll_mainPrivacy)
                                    MyUtils.dismissProgressDialog()

                                }

                            }
                        })

    }


    private fun StoreSessionManager(uesedata: SignupData?) {

        val gson = Gson()

        val json = gson.toJson(uesedata)
        sessionManager?.create_login_session(
                json,
                uesedata!!.userMobile,
                "",
                true,
                sessionManager!!.isEmailLogin()
        )

    }


}
