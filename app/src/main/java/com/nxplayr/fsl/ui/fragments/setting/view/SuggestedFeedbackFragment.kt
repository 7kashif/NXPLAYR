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
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_delete_account.*
import kotlinx.android.synthetic.main.fragment_suggested_feedback.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SuggestedFeedbackFragment : Fragment(),View.OnClickListener {


    private var v: View? = null
    var mActivity: Activity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    private lateinit var  commonStatusModel: CommonStatusModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if(v==null){
            v=inflater.inflate(R.layout.fragment_suggested_feedback, container, false)
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngSuggestFeature.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngSuggestFeature
            if (!sessionManager?.LanguageLabel?.lngSuggestAFeaturePlaceholder.isNullOrEmpty())
                edittext_suggestion_feature.hint = sessionManager?.LanguageLabel?.lngSuggestAFeaturePlaceholder
            if (!sessionManager?.LanguageLabel?.lngSave.isNullOrEmpty())
                btn_save.progressText = sessionManager?.LanguageLabel?.lngSave
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
        tvToolbarTitle.setText(R.string.suggested_feature)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }
        btn_save.setOnClickListener(this)
    }

    private fun setupViewModel() {
        commonStatusModel = ViewModelProvider(this@SuggestedFeedbackFragment).get(CommonStatusModel::class.java)

    }


    fun saveSuggestion(){
        btn_save.startAnimation()
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("suggestionName",  userData?.userFirstName+" "+userData?.userLastName)
            jsonObject.put("suggestionEmail",  userData?.userEmail)
            jsonObject.put("suggestionMobile",  userData?.userMobile)
            jsonObject.put("suggesstionDetails", edittext_suggestion_feature?.text.toString().trim())
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        commonStatusModel.getCommonStatus(mActivity!!, true,jsonArray?.toString(),"suggestionsFeature")
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
                    MyUtils.showSnackbar(mActivity!!,"Please enter your suggestion",edittext_suggestion_feature)
                }else{
                    saveSuggestion()
                }
            }

        }    }

}