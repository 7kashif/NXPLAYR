package com.nxplayr.fsl.ui.activity.onboarding.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CountryListData
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.CountryListModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModelV2
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.PopupMenu
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {

    var mobile = true
    var countryListData: ArrayList<CountryListData>? = ArrayList()
    var countrylist: ArrayList<String>? = ArrayList()
    private lateinit var countryListModel: CountryListModel
    private lateinit var signup: SignupModelV2
    var sessionManager: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        sessionManager = SessionManager(this@ForgotPasswordActivity)

        setupViewModel()
        setupUI()


    }

    private fun setupUI() {
        if (sessionManager?.LanguageLabel != null) {
            // static data from preference
            val langLabel = sessionManager?.LanguageLabel

            if (!langLabel?.lngForgotPassword.isNullOrEmpty()) {
                forgotPasswordTitle.text = langLabel?.lngForgotPassword
            }
            if (!langLabel?.lngForgotDetail.isNullOrEmpty()) {
                forgotPasswordDetails.text = langLabel?.lngForgotDetail
            }
            if (!langLabel?.lngEmailMobile.isNullOrEmpty()) {
                textInput_mobile_textInputLayout.hint = langLabel?.lngEmailMobile
            }
            if (!langLabel?.lngContinue.isNullOrEmpty()) {
                forgotContinueButton.progressText = langLabel?.lngContinue
            }
        }
        edit_mobile_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var loginType = TextUtils.isDigitsOnly(edit_mobile_text?.text.toString().trim())
                if (loginType) {
                    tv_countryCodeForgotPass.visibility = View.VISIBLE
                    edit_mobile_text.setPadding(
                        resources.getDimensionPixelOffset(R.dimen._60sdp),
                        resources.getDimensionPixelOffset(R.dimen._25sdp),
                        resources.getDimensionPixelOffset(R.dimen._1sdp),
                        resources.getDimensionPixelOffset(R.dimen._10sdp)
                    )
                    tv_countryCodeForgotPass.setPadding(
                        resources.getDimensionPixelOffset(R.dimen._10sdp),
                        resources.getDimensionPixelOffset(R.dimen._25sdp),
                        resources.getDimensionPixelOffset(R.dimen._80sdp),
                        resources.getDimensionPixelOffset(R.dimen._10sdp)
                    )
                    tv_countryCodeForgotPass.requestFocus()
                } else {
                    tv_countryCodeForgotPass.visibility = View.GONE
                    edit_mobile_text.setPadding(
                        resources.getDimensionPixelOffset(R.dimen._10sdp),
                        resources.getDimensionPixelOffset(R.dimen._25sdp),
                        resources.getDimensionPixelOffset(R.dimen._1sdp),
                        resources.getDimensionPixelOffset(R.dimen._10sdp)
                    )
                    edit_mobile_text.requestFocus()
                }
                forgotContinueButton.strokeColor = (resources.getColor(R.color.colorPrimary))
                forgotContinueButton.backgroundTint = (resources.getColor(R.color.colorPrimary))
                forgotContinueButton.textColor = resources.getColor(R.color.black)

                if (p0!!.isEmpty()) {
                    tv_countryCodeForgotPass.visibility = View.GONE
                    edit_mobile_text.setPadding(
                        resources.getDimensionPixelOffset(R.dimen._10sdp),
                        resources.getDimensionPixelOffset(R.dimen._25sdp),
                        resources.getDimensionPixelOffset(R.dimen._1sdp),
                        resources.getDimensionPixelOffset(R.dimen._10sdp)
                    )

                    forgotContinueButton.strokeColor = (resources.getColor(R.color.colorPrimary))
                    forgotContinueButton.backgroundTint = (resources.getColor(R.color.transperent1))
                    forgotContinueButton.textColor = resources.getColor(R.color.colorPrimary)
                }
            }

        })

        var mDrawable = resources.getDrawable(R.drawable.dropdown_icon)
        mDrawable.setColorFilter(
            ContextCompat.getColor(this, R.color.colorPrimary),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        tv_countryCodeForgotPass.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            mDrawable,
            null
        )

        getCounrtyList()
        tv_countryCodeForgotPass.setOnClickListener(this)
        forgotContinueButton.setOnClickListener(this)
        forgotPasswordBackButtonIv.setOnClickListener(this)
    }

    private fun setupViewModel() {
        countryListModel =
            ViewModelProvider(this@ForgotPasswordActivity).get(CountryListModel::class.java)
        signup = ViewModelProvider(this@ForgotPasswordActivity).get(SignupModelV2::class.java)

    }


    private fun chekValidation(): Boolean {
        MyUtils.hideKeyboard1(this@ForgotPasswordActivity)
        var checkFlag = true
        var loginType = TextUtils.isDigitsOnly(edit_mobile_text?.text.toString().trim())


        if (TextUtils.isEmpty(edit_mobile_text.text.toString())) {
            MyUtils.showSnackbar(
                this@ForgotPasswordActivity,
                getString(R.string.enter_email_mobile_msg),
                ll_main_forgotPass
            )
            checkFlag = false
        } else if (loginType && (edit_mobile_text.text.toString().length < 8 || edit_mobile_text.text.toString().length > 16)
        ) {
            MyUtils.showSnackbar(
                this@ForgotPasswordActivity,
                getString(R.string.please_enter_valid_mobile_number),
                ll_main_forgotPass
            )
            checkFlag = false
        } else if ((TextUtils.isDigitsOnly(edit_mobile_text.text.toString()) && TextUtils.isEmpty(
                tv_countryCodeForgotPass.text.toString()
            ))
        ) {
            MyUtils.showSnackbar(
                this@ForgotPasswordActivity,
                getString(R.string.please_enter_country_code),
                ll_main_forgotPass
            )
            checkFlag = false
        } else if (!loginType && !MyUtils.isValidEmail(edit_mobile_text.text.toString())
        ) {
            MyUtils.showSnackbar(
                this@ForgotPasswordActivity,
                getString(R.string.valid_email),
                ll_main_forgotPass
            )
            checkFlag = false
        } else {
            if (TextUtils.isDigitsOnly(edit_mobile_text.text.toString()))
                mobile = true
            forgotPass()
        }
        return checkFlag

    }

    private fun getCounrtyList() {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("blankCountryCode", "No")


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        countryListModel.getCountryList(this!!, false, jsonArray.toString())
            .observe(this@ForgotPasswordActivity!!,
                { countryListPojo ->
                    if (countryListPojo != null) {
                        if (countryListPojo.get(0).status.equals("true", false)) {
                            countryListData?.addAll(countryListPojo.get(0).data!!)
                            countrylist = java.util.ArrayList()
                            countrylist!!.clear()
                            for (i in 0 until countryListData!!.size) {
                                countrylist!!.add(countryListData!![i].countryDialCode!!)
                            }
                        }
                    }
                })


    }


    private fun forgotPass() {
        forgotContinueButton.startAnimation()
        var loginType = TextUtils.isDigitsOnly(edit_mobile_text?.text.toString().trim())
        val jsonObject = JSONObject()
        try {

            jsonObject.put("languageID", "1")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("userCountryCode", tv_countryCodeForgotPass.text.toString())
            if (loginType) {
                jsonObject.put("userMobile", edit_mobile_text.text.toString())
                jsonObject.put("userEmail", "")
            } else {
                jsonObject.put("userMobile", "")
                jsonObject.put("userEmail", edit_mobile_text.text.toString())
            }


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        var jsonArray = JSONArray()
        jsonArray.put(jsonObject)

        signup.userForgetPass(jsonArray.toString())
        signup.userForgetPass.observe(this@ForgotPasswordActivity) { loginPojo ->
                if (loginPojo != null) {
                    forgotContinueButton.endAnimation()
                    if (loginPojo.get(0).status.equals("true")) {

                        MyUtils.showSnackbar(
                            this,
                            loginPojo.get(0).message!!,
                            ll_main_forgotPass
                        )
                        val mThread = Thread(Runnable {
                            try {
                                Thread.sleep(1000)
                                var i = Intent(
                                    this@ForgotPasswordActivity,
                                    OtpVerificationActivity::class.java
                                )
                                i.putExtra("from", "ForgotPassword")
                                i.putExtra("userData", loginPojo[0].data!![0].userID!!)
                                i.putExtra("userMobile", loginPojo[0].data!![0].userMobile!!)
                                startActivity(i)
                                overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                                )

                            } catch (e: InterruptedException) {
                            }
                        })
                        mThread.start()

                    } else {
                        forgotContinueButton.endAnimation()
                        MyUtils.showSnackbar(
                            this,
                            loginPojo.get(0).message!!,
                            ll_main_forgotPass
                        )
                    }
                } else {
                    forgotContinueButton.endAnimation()
                    ErrorUtil.errorMethod(ll_main_forgotPass)
                }
            }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        MyUtils.finishActivity(this, true)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_countryCodeForgotPass -> {
                PopupMenu(this@ForgotPasswordActivity!!, v!!, countrylist!!).showPopUp(object :
                    PopupMenu.OnMenuSelectItemClickListener {
                    override fun onItemClick(item: String, pos: Int) {
                        tv_countryCodeForgotPass.setText(item.toString())
                    }
                })
            }
            R.id.forgotContinueButton -> {
                chekValidation()
            }
            R.id.forgotPasswordBackButtonIv -> {
                onBackPressed()
            }

        }
    }
}