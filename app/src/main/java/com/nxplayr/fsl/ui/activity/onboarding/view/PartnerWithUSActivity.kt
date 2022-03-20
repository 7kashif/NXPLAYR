package com.nxplayr.fsl.ui.activity.onboarding.view

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.SignupPojo
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.activity_partner_with_us.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject

class PartnerWithUSActivity : AppCompatActivity(), View.OnClickListener {

    var sessionManager:SessionManager?=null
    var useData: SignupData?=null
    private lateinit var signupModel: SignupModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner_with_us)

        if(sessionManager?.get_Authenticate_User()!=null){
            useData=sessionManager?.get_Authenticate_User()
            if(useData!=null)
            {
                setData()
            }
        }

        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        toolbar.title=resources.getString(R.string.partner_invest_with_us)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        sessionManager= SessionManager(this@PartnerWithUSActivity)
        btnSend.setOnClickListener(this)
    }

    private fun setupViewModel() {
        signupModel= ViewModelProvider(this).get(SignupModel::class.java)

    }

    private fun setData() {
        firstName_edit_text.setText(useData?.userFirstName!!+" "+useData?.userLastName!!)
        emailAddress_edit_text.setText(useData?.userEmail!!)
        mobileNumber_edit_text.setText(useData?.userMobile!!)
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btnSend->{
                chekValidation()
            }

        }
    }

    private fun chekValidation() {
        MyUtils.hideKeyboard1(this@PartnerWithUSActivity)
        when {
            firstName_edit_text!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(this@PartnerWithUSActivity, getString(R.string.please_enter_first_name), ll_main_partner)
            }
            emailAddress_edit_text!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(this@PartnerWithUSActivity, getString(R.string.please_enter_email_id), ll_main_partner)

            }
            (!MyUtils.isValidEmail(emailAddress_edit_text.text.toString())) -> {
                MyUtils.showSnackbar(this@PartnerWithUSActivity, resources.getString(R.string.valid_email_address), ll_main_partner)
            }
            mobileNumber_edit_text!!.text.toString().isEmpty() -> {

                MyUtils.showSnackbar(this@PartnerWithUSActivity, getString(R.string.please_enter_mobile_number), ll_main_partner)
            }
            mobileNumber_edit_text.text.toString().trim().length < 8 || mobileNumber_edit_text.text.toString().trim().length > 16 -> {

                MyUtils.showSnackbar(this@PartnerWithUSActivity, getString(R.string.please_enter_valid_mobile_number), ll_main_partner)
            }
            edittext_message!!.text.toString().isEmpty() -> {

                MyUtils.showSnackbar(this@PartnerWithUSActivity, getString(R.string.please_enter_message), ll_main_partner)
            }else->{

                 sendData()
          }
        }


    }

    private fun sendData() {
        btnSend.startAnimation()
        MyUtils.setViewAndChildrenEnabled(ll_main_partner, false)

        var jsonArray=JSONArray()
          var jsonObject= JSONObject()

          try {
              jsonObject.put("apiVersion",RestClient.apiVersion)
              jsonObject.put("apiType",RestClient.apiType)
              jsonObject.put("partnerwusName",firstName_edit_text.text.toString().trim())
              jsonObject.put("partnerwusEmail",emailAddress_edit_text.text.toString().trim())
              jsonObject.put("languageID","1")
              jsonObject.put("partnerwusMobile",mobileNumber_edit_text.text.toString().trim())
              jsonObject.put("partnerwusAddress","")
              jsonObject.put("loginuserID",useData?.userID)
              jsonObject.put("partnerwusMessage",edittext_message.text.toString().trim())
          }catch (e:Exception)
          {
              e.printStackTrace()
          }
          jsonArray.put(jsonObject)
          signupModel?.userRegistration(this@PartnerWithUSActivity,false,jsonArray.toString(),"partnerWithUs")?.observe(this@PartnerWithUSActivity,object : Observer<List<SignupPojo>>{
              override fun onChanged(t: List<SignupPojo>?) {
                  if(!t.isNullOrEmpty())
                  {
                      btnSend.endAnimation()
                      MyUtils.setViewAndChildrenEnabled(ll_main_partner, true)
                      if(t[0].status.equals("true"))
                      {
                          MyUtils.showSnackbar(this@PartnerWithUSActivity,t[0].message,ll_main_partner)

                          Handler().postDelayed({
                            onBackPressed()
                          }, 1000)

                      }
                      else
                      {
                          MyUtils.showSnackbar(this@PartnerWithUSActivity,t[0].message,ll_main_partner)
                      }
                  }else{
                      btnSend.endAnimation()
                      MyUtils.setViewAndChildrenEnabled(ll_main_partner, true)
                      ErrorUtil.errorMethod(ll_main_partner)
                  }
              }

          })

    }

    override fun onBackPressed() {
        MyUtils.finishActivity(this@PartnerWithUSActivity,true)
    }

}