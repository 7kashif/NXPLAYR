package com.nxplayr.fsl.ui.fragments.userprofilesummary.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.fragment_profile_summary.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ProfileSummaryFragment : Fragment(),View.OnClickListener{

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager:SessionManager?=null
    var userData: SignupData?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_profile_summary, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager= SessionManager(mActivity!!)
        if(sessionManager?.get_Authenticate_User()!=null)
        {
            userData=sessionManager?.get_Authenticate_User()
        }
        setupUI()
    }

    private fun setupUI() {
        if(userData!=null)
        {
            setData()
        }
        tvToolbarTitle.text = resources.getString(R.string.profile_summary)

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        btnUpdateBasicDetail.setOnClickListener(this)
    }

    private fun setData() {
        profile_title_edittext.setText(userData?.userTitle)
        select_position_edittext.setText(userData?.userPosition)
        organization_ediitext.setText(userData?.userOrganisation)
        who_amI_edittext.setText(userData?.userWhoAmI)
    }

    private fun checkValidation() {
        MyUtils.hideKeyboard1(mActivity!!)

        when {
            profile_title_edittext!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(mActivity!!, "Please enter profile title", ll_profile_summary)
            }
            select_position_edittext!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(mActivity!!, "Please enter your position", ll_profile_summary)
            }
            organization_ediitext!!.text.toString().trim().isEmpty() -> {
                MyUtils.showSnackbar(mActivity!!, "Please enter your organization", ll_profile_summary)
            }
            who_amI_edittext!!.text.toString().trim().isEmpty() -> {
                MyUtils.showSnackbar(mActivity!!, "Please enter who i m", ll_profile_summary)
            }
            else -> {
                profileSummary()
            }

        }
    }

    private fun profileSummary() {
        btnUpdateBasicDetail.startAnimation()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                btnUpdateBasicDetail.endAnimation()
                MyUtils.setViewAndChildrenEnabled(ll_profile_summary, true)
                ErrorUtil.errorMethod(ll_profile_summary)
                return@OnCompleteListener
            }
            val token = task.result
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")
            jsonObject.put("userDeviceID",token)
            jsonObject.put("userDeviceType", RestClient.apiType)
            jsonObject.put("userEmail", userData?.userEmail)
            jsonObject.put("userFirstName", userData!!.userFirstName)
            jsonObject.put("userLastName", userData!!.userLastName)
            jsonObject.put("userGender", userData!!.userGender)
            jsonObject.put("userMobile", userData!!.userMobile)
            jsonObject.put("userPassword", userData!!.userPassword)
            jsonObject.put("userCountryCode", userData!!.userCountryCode)
            jsonObject.put("userProfilePicture", userData!!.userProfilePicture)
            jsonObject.put("apputypeID", userData!!.apputypeID)
            jsonObject.put("footbltypeID", userData!!.footbltypeID)
            jsonObject.put("agegroupID", userData!!.agegroupID)
            jsonObject.put("appuroleID", userData!!.appuroleID)
            jsonObject.put("specialityID", userData!!.specialityID)
            jsonObject.put("userBestFoot", userData!!.userBestFoot)
            jsonObject.put("userDOB", userData!!.userDOB)
            jsonObject.put("footballagecatID", userData!!.footballagecatID)
            jsonObject.put("userFullname", userData!!.userFullname)
            jsonObject.put("userBio", userData!!.userBio)
            jsonObject.put("userCoverPhoto", userData!!.userCoverPhoto)
            jsonObject.put("userHeight", userData!!.userHeight)
            jsonObject.put("userWeight", userData!!.userWeight)
            jsonObject.put("userAlternateMobile", userData!!.userAlternateMobile)
            jsonObject.put("userWebsite", userData!!.userWebsite)
            jsonObject.put("userAlternateEmail", userData!!.userAlternateEmail)
            jsonObject.put("userNickname", userData!!.userNickname)
            jsonObject.put("clubID", userData!!.clubID)
            jsonObject.put("footbllevelID", userData!!.footbllevelID)
            jsonObject.put("userHomeCountryID", userData!!.userHomeCountryID)
            jsonObject.put("userHomeCountryName", userData!!.userHomeCountryName)
            jsonObject.put("userHomeCityName", userData!!.userHomeCityName)
            jsonObject.put("userHomeCityID", userData!!.userHomeCityID)
            jsonObject.put("userTitle", profile_title_edittext?.text.toString().trim())
            jsonObject.put("userPosition", select_position_edittext?.text.toString().trim())
            jsonObject.put("userOrganisation", organization_ediitext?.text.toString().trim())
            jsonObject.put("userWhoAmI", who_amI_edittext?.text.toString().trim())

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        var signup = ViewModelProvider(this@ProfileSummaryFragment).get(SignupModel::class.java)
        signup.userRegistration(mActivity!!, false, jsonArray.toString(), "update_profile")
            .observe(viewLifecycleOwner,
                { loginPojo ->
                    if (loginPojo != null) {
                        btnUpdateBasicDetail.endAnimation()
                        if (loginPojo[0].status.equals("true", true)) {
                            try {
                                if (loginPojo.get(0).message.isNullOrEmpty()) {
                                    MyUtils.showSnackbar(
                                        mActivity!!,
                                        "Profile summary updated",
                                        ll_profile_summary
                                    )

                                } else {
                                    MyUtils.showSnackbar(
                                        mActivity!!,
                                        loginPojo.get(0).message!!,
                                        ll_profile_summary
                                    )

                                }
                                StoreSessionManager(loginPojo[0].data[0])
                                Handler().postDelayed({
                                    (activity as MainActivity).onBackPressed()
                                }, 1000)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                loginPojo.get(0).message!!,
                                ll_profile_summary
                            )
                        }
                    } else {
                        btnUpdateBasicDetail.endAnimation()
                        ErrorUtil.errorMethod(ll_profile_summary)
                    }
                })
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

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btnUpdateBasicDetail->{
                checkValidation()
            }
        }
    }
}
