package com.nxplayr.fsl.ui.activity.onboarding.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModelV2
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.activity_otp_verification.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ParentsOtpVerificationActivity : AppCompatActivity(), View.OnClickListener {

    var selectModeType: Int = 0
    var colorId: Int? = null
    var sessionManager: SessionManager? = null
    var from = ""
    var usersId = ""
    var parentMobile = ""
    var userMobile = ""
    var inte: Intent? = null

    private lateinit var signup: SignupModelV2
    private lateinit var commonStatusModel: CommonStatusModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)
        sessionManager = SessionManager(this@ParentsOtpVerificationActivity)
        if (intent != null)
            if (intent.hasExtra("selectModeType")) {
                selectModeType = intent.getIntExtra("selectModeType", 0)
            }

        if (intent != null) {
            if (intent.hasExtra("from")) {
                from = intent?.getStringExtra("from")!!

            }
            if (intent.hasExtra("userData")) {
                usersId = intent?.getStringExtra("userData")!!

            }
            if (intent.hasExtra("userMobile")) {
                userMobile = intent?.getStringExtra("userMobile")!!


            }
        }
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngVerificationTitle.isNullOrEmpty())
                tv_verificationCode.text = sessionManager?.LanguageLabel?.lngVerificationTitle
            if (!sessionManager?.LanguageLabel?.lngVerificationDetail.isNullOrEmpty())
                tv_verificationCode_Details.text =
                    sessionManager?.LanguageLabel?.lngVerificationDetail
            if (!sessionManager?.LanguageLabel?.lngContinue.isNullOrEmpty())
                btn_otpVerified.progressText = sessionManager?.LanguageLabel?.lngContinue
            if (!sessionManager?.LanguageLabel?.lngDontReceiveCode.isNullOrEmpty())
                tvVerificationDetails.text = sessionManager?.LanguageLabel?.lngDontReceiveCode
            if (!sessionManager?.LanguageLabel?.lngSendCodeAgain.isNullOrEmpty())
                tv_sendCodeAgain.text = sessionManager?.LanguageLabel?.lngSendCodeAgain
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        when (selectModeType) {
            0 -> colorId = R.color.colorPrimary
            1 -> colorId = R.color.yellow
            2 -> colorId = R.color.colorAccent
        }

        MyUtils.setSelectedModeTypeViewColor(
            this,
            arrayListOf(
                tv_verificationCode,
                et1,
                et2,
                et3,
                et4,
                tv_sendCodeAgain
            ) as ArrayList<View>,
            colorId!!
        )

        et1.backgroundTintList = ContextCompat.getColorStateList(this, colorId!!)
        et2.backgroundTintList = ContextCompat.getColorStateList(this, colorId!!)
        et3.backgroundTintList = ContextCompat.getColorStateList(this, colorId!!)
        et4.backgroundTintList = ContextCompat.getColorStateList(this, colorId!!)
        btn_otpVerified.textColor = resources.getColor(colorId!!)
        btn_otpVerified.strokeColor = resources.getColor(colorId!!)

        et1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    et2.requestFocus()
                } else if (s.isEmpty()) {
                    et1.clearFocus()
                }
            }
        })

        et2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    et3.requestFocus()
                } else if (s.isEmpty()) {
                    et1.requestFocus()
                }
            }
        })

        et3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    et4.requestFocus()
                } else if (s.isEmpty()) {
                    et2.requestFocus()
                }
            }
        })

        et4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                btn_otpVerified.backgroundTint = (resources.getColor(colorId!!))
                btn_otpVerified.textColor = resources.getColor(R.color.black)
                if (s.length == 1) {
                    et4.clearFocus()
                } else if (s.isEmpty()) {
                    et3.requestFocus()
                }
            }
        })


        btn_otpVerified.setOnClickListener(this)

        tv_sendCodeAgain.setOnClickListener(this)

    }

    private fun setupViewModel() {
        signup =
            ViewModelProvider(this@ParentsOtpVerificationActivity).get(SignupModelV2::class.java)
        commonStatusModel =
            ViewModelProvider(this@ParentsOtpVerificationActivity).get(CommonStatusModel::class.java)

    }

    fun otpValidation() {
        var otp = et1.text.toString().trim() + et2.text.toString().trim() + et3.text.toString()
            .trim() + et4.text.toString().trim()
        if (otp.isEmpty() || otp.length != 4)
            MyUtils.showSnackbar(this, "Please add otp number", ll_main_otpVerification)
        else
            verifyOtp(otp)


    }

    private fun verifyOtp(otp: String) {
        btn_otpVerified.startAnimation()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                btn_otpVerified.endAnimation()
                MyUtils.setViewAndChildrenEnabled(ll_main_otpVerification, true)
                ErrorUtil.errorMethod(ll_main_otpVerification)
                return@OnCompleteListener
            }
            val token = task.result
            val jsonObject = JSONObject()

            if (from.equals("LoginByOtpVerification")) {
                var userData = sessionManager!!.get_Authenticate_User()
                usersId = userData!!.userID
                parentMobile = userData?.userParentMobile
            }
            try {
                jsonObject.put("languageID", "1")
                jsonObject.put("loginuserID", usersId)
                jsonObject.put("userParentOTP", otp)
                jsonObject.put("userDeviceID", token)
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val jsonArray = JSONArray()
            jsonArray.put(jsonObject)
            signup.parentsVerifyOtp(jsonArray.toString())
            signup.parentsVerifyOtp
                .observe(this@ParentsOtpVerificationActivity!!,
                    Observer { loginPojo ->
                        if (loginPojo != null) {
                            btn_otpVerified.endAnimation()
                            if (loginPojo.get(0).status.equals("true")) {

                                if (from.equals("ForgotPassword")) {

                                    val intent = Intent(this, ResetPassActivity::class.java)
                                    intent.putExtra("selectModeType", selectModeType)
                                    intent.putExtra("userID", usersId)
                                    startActivity(intent)
                                    finish()
                                    overridePendingTransition(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left
                                    )
                                } else {

                                    StoreSessionManager(loginPojo[0].data[0])
                                    try {
                                        Handler().postDelayed({
                                            var intent = Intent(
                                                this@ParentsOtpVerificationActivity,
                                                OtpVerificationActivity::class.java
                                            )
                                            intent.putExtra("selectModeType", selectModeType)
                                            intent.putExtra("from", "LoginByOtpVerification")
                                            intent.putExtra("userID", usersId)
                                            startActivity(intent)
                                            finishAffinity()
                                        }, 1000)
                                        MyUtils.showSnackbar(
                                            this,
                                            loginPojo.get(0).message!!,
                                            ll_main_otpVerification
                                        )

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }


                            } else {
                                MyUtils.showSnackbar(
                                    this,
                                    loginPojo.get(0).message!!,
                                    ll_main_otpVerification
                                )
                            }
                        } else {
                            btn_otpVerified.endAnimation()
                        }
                    })
        })

    }

    private fun resendOtp() {
        val jsonObject = JSONObject()

        if (from.equals("LoginByOtpVerification")) {
            var userData = sessionManager!!.get_Authenticate_User()
            usersId = userData!!.userID
            userMobile = userData!!.userMobile
            parentMobile = userData?.userParentMobile
        }

        try {

            jsonObject.put("languageID", "1")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("loginuserID", usersId)
            jsonObject.put("userParentMobile", parentMobile)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonArray = JSONArray()
        jsonArray.put(jsonObject)
        commonStatusModel.getCommonStatus(this!!, false, jsonArray.toString(), "parents_resend_otp")
            .observe(this@ParentsOtpVerificationActivity!!,
                androidx.lifecycle.Observer { loginPojo ->
                    if (loginPojo != null) {

                        if (loginPojo.get(0).status.equals("true")) {

                            MyUtils.showSnackbar(
                                this,
                                loginPojo.get(0).message!!,
                                ll_main_otpVerification
                            )


                        } else {
                            MyUtils.showSnackbar(
                                this,
                                loginPojo.get(0).message!!,
                                ll_main_otpVerification
                            )
                        }

                    } else {

                        ErrorUtil.errorMethod(ll_main_otpVerification)
                    }
                })
    }


    override fun onBackPressed() {

        MyUtils.showMessageOKCancel(this@ParentsOtpVerificationActivity,
            "Are you sure want to exit ?",
            "Verification Code",
            DialogInterface.OnClickListener { dialogInterface, i ->
                if (sessionManager != null && sessionManager!!.isLoggedIn() && !sessionManager!!.get_Authenticate_User().userOVerified.equals(
                        "Yes",
                        true
                    )
                )
                    sessionManager!!.clear_login_session()
                MyUtils.finishActivity(
                    this@ParentsOtpVerificationActivity,
                    true
                )

            })

    }


    private fun StoreSessionManager(uesedata: SignupData?) {

        val gson = Gson()

        val json = gson.toJson(uesedata)
        sessionManager?.create_login_session(
            json,
            uesedata!!.userMobile!!,
            "",
            true,
            sessionManager!!.isEmailLogin()
        )

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_sendCodeAgain -> {
                et1.text = Editable.Factory.getInstance().newEditable("")
                et2.text = Editable.Factory.getInstance().newEditable("")
                et3.text = Editable.Factory.getInstance().newEditable("")
                et4.text = Editable.Factory.getInstance().newEditable("")
                et1.requestFocus()
                resendOtp()

            }
            R.id.btn_otpVerified -> {
                otpValidation()
            }
        }
    }


}

