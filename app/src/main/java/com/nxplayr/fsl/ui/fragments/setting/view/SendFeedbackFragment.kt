package com.nxplayr.fsl.ui.fragments.setting.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_send_feedback.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SendFeedbackFragment : Fragment(),View.OnClickListener {


    private var v: View? = null
    var mActivity: Activity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    private lateinit var  loginModel: SignupModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if(v==null){
            v=inflater.inflate(R.layout.fragment_send_feedback, container, false)
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
        setupViewModel()
        setupUI()


    }

    private fun setupUI() {
        tvToolbarTitle.setText(R.string.send_feedback)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }
        btn_save.setOnClickListener(this)
    }

    private fun setupViewModel() {
        loginModel = ViewModelProvider(this@SendFeedbackFragment).get(SignupModel::class.java)

    }


    fun saveSuggestion(){
        btn_save.startAnimation()
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("feedbackName",  userData?.userFirstName+" "+userData?.userLastName)
            jsonObject.put("feedbackEmail",  userData?.userEmail)
            jsonObject.put("feedbackMobile",  userData?.userMobile)
            jsonObject.put("feedbackDetails", edittext_suggestion_feature?.text.toString().trim())
            jsonObject.put("feedbackRattings", edittext_suggestion_feature?.text.toString().trim())
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        loginModel.userRegistration(mActivity!!, true,jsonArray?.toString(),"sendFeedback")
                .observe(viewLifecycleOwner,
                        { countryListPojo ->

                            if (countryListPojo != null) {
                                btn_save.endAnimation()
                                if (countryListPojo.get(0).status.equals("true", false)) {
                                    try {
                                        Handler().postDelayed({
                                            (activity as MainActivity).onBackPressed()
                                        }, 1000)
                                        MyUtils.showSnackbar(mActivity!!, countryListPojo.get(0).message, ll_main_suggestion)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    MyUtils.showSnackbar(mActivity!!, countryListPojo.get(0).message, ll_main_suggestion)
                                }
                            } else {
                                btn_save.endAnimation()
                                ErrorUtil.errorMethod(ll_main_suggestion)
                            }
                        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_save->{
                if(edittext_suggestion_feature.text.toString().trim().isNullOrEmpty())
                {
                    MyUtils.showSnackbar(mActivity!!,"Please enter your feedback",edittext_suggestion_feature)
                } else if(rating.rating.toString().isNullOrEmpty())
                {
                    MyUtils.showSnackbar(mActivity!!,"Please rate us",edittext_suggestion_feature)
                }else{
                    saveSuggestion()
                }
            }

        }    }

}