package com.nxplayr.fsl.ui.activity.onboarding.view

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModelV2
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.activity_partner_with_us.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject

class PartnerWithUSActivity : AppCompatActivity(), View.OnClickListener {

    var sessionManager: SessionManager? = null
    var useData: SignupData? = null
    private lateinit var signupModel: SignupModelV2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner_with_us)
        setupViewModel()
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngPartnerWithUs.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngPartnerWithUs
            if (!sessionManager?.LanguageLabel?.lngFullName.isNullOrEmpty())
                firstName_textInputLayout.hint = sessionManager?.LanguageLabel?.lngFullName
            if (!sessionManager?.LanguageLabel?.lngEmail.isNullOrEmpty())
                emailAddress_textInput.hint = sessionManager?.LanguageLabel?.lngEmail
            if (!sessionManager?.LanguageLabel?.lngMobileNo.isNullOrEmpty())
                mobileNumber_textInput.hint = sessionManager?.LanguageLabel?.lngMobileNo
            if (!sessionManager?.LanguageLabel?.lngWriteMessage.isNullOrEmpty())
                edittext_message.hint = sessionManager?.LanguageLabel?.lngWriteMessage
            if (!sessionManager?.LanguageLabel?.lngSend.isNullOrEmpty())
                btnSend.progressText = sessionManager?.LanguageLabel?.lngSend
        }
    }

    private fun setupUI() {
        tvToolbarTitle.text = resources.getString(R.string.partner_invest_with_us)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        sessionManager = SessionManager(this@PartnerWithUSActivity)
        btnSend.setOnClickListener(this)
        if (sessionManager?.get_Authenticate_User() != null) {
            useData = sessionManager?.get_Authenticate_User()
            if (useData != null) {
                setData()
            }
        }


        btnSend.strokeColor = (resources.getColor(R.color.grayborder1))
        btnSend.backgroundTint = (resources.getColor(R.color.transperent1))
        btnSend.textColor = (resources.getColor(R.color.colorPrimary))
        edittext_message.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                btnSend.backgroundTint = (resources.getColor(R.color.colorPrimary))
                btnSend.textColor = (resources.getColor(R.color.black))
                btnSend.strokeColor = (resources.getColor(R.color.colorPrimary))

                if (p0!!.isEmpty()) {
                    btnSend.strokeColor = (resources.getColor(R.color.grayborder1))
                    btnSend.backgroundTint = (resources.getColor(R.color.transperent1))
                    btnSend.textColor = (resources.getColor(R.color.colorPrimary))
                }
            }

        })

    }

    private fun setupViewModel() {
        signupModel = ViewModelProvider(this).get(SignupModelV2::class.java)
    }

    private fun setData() {
        firstName_edit_text.setText(useData?.userFirstName!! + " " + useData?.userLastName!!)
        emailAddress_edit_text.setText(useData?.userEmail!!)
        mobileNumber_edit_text.setText(useData?.userCountryCode!! + " " + useData?.userMobile!!)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSend -> {
                chekValidation()
            }
        }
    }

    private fun chekValidation() {
        MyUtils.hideKeyboard1(this@PartnerWithUSActivity)
        when {
            firstName_edit_text!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(
                    this@PartnerWithUSActivity,
                    getString(R.string.please_enter_first_name),
                    ll_main_partner
                )
            }
            emailAddress_edit_text!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(
                    this@PartnerWithUSActivity,
                    getString(R.string.please_enter_email_id),
                    ll_main_partner
                )

            }
            (!MyUtils.isValidEmail(emailAddress_edit_text.text.toString())) -> {
                MyUtils.showSnackbar(
                    this@PartnerWithUSActivity,
                    resources.getString(R.string.valid_email_address),
                    ll_main_partner
                )
            }
            mobileNumber_edit_text!!.text.toString().isEmpty() -> {

                MyUtils.showSnackbar(
                    this@PartnerWithUSActivity,
                    getString(R.string.please_enter_mobile_number),
                    ll_main_partner
                )
            }
            mobileNumber_edit_text.text.toString()
                .trim().length < 8 || mobileNumber_edit_text.text.toString().trim().length > 16 -> {

                MyUtils.showSnackbar(
                    this@PartnerWithUSActivity,
                    getString(R.string.please_enter_valid_mobile_number),
                    ll_main_partner
                )
            }
            edittext_message!!.text.toString().isEmpty() -> {

                MyUtils.showSnackbar(
                    this@PartnerWithUSActivity,
                    getString(R.string.please_enter_message),
                    ll_main_partner
                )
            }
            else -> {
                sendData()
            }
        }


    }

    private fun sendData() {
        btnSend.startAnimation()
        MyUtils.setViewAndChildrenEnabled(ll_main_partner, false)

        var jsonArray = JSONArray()
        var jsonObject = JSONObject()

        try {
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("partnerwusName", firstName_edit_text.text.toString().trim())
            jsonObject.put("partnerwusEmail", emailAddress_edit_text.text.toString().trim())
            jsonObject.put("languageID", "1")
            jsonObject.put("partnerwusMobile", mobileNumber_edit_text.text.toString().trim())
            jsonObject.put("partnerwusAddress", "")
            jsonObject.put("loginuserID", useData?.userID)
            jsonObject.put("partnerwusMessage", edittext_message.text.toString().trim())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        signupModel.getPartnerWithUs(jsonArray.toString())
        signupModel.getPartnerWithUs.observe(this@PartnerWithUSActivity) { t ->
                if (!t.isNullOrEmpty()) {
                    btnSend.endAnimation()
                    MyUtils.setViewAndChildrenEnabled(ll_main_partner, true)
                    if (t[0].status.equals("true")) {
                        MyUtils.showSnackbar(
                            this@PartnerWithUSActivity,
                            t[0].message,
                            ll_main_partner
                        )

                        Handler().postDelayed({
                            onBackPressed()
                        }, 1000)

                    } else {
                        MyUtils.showSnackbar(
                            this@PartnerWithUSActivity,
                            t[0].message,
                            ll_main_partner
                        )
                    }
                } else {
                    btnSend.endAnimation()
                    MyUtils.setViewAndChildrenEnabled(ll_main_partner, true)
                    ErrorUtil.errorMethod(ll_main_partner)
                }
            }

    }

    override fun onBackPressed() {
        MyUtils.finishActivity(this@PartnerWithUSActivity, true)
    }

}