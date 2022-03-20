package com.nxplayr.fsl.ui.activity.onboarding.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import com.nxplayr.fsl.R
import kotlinx.android.synthetic.main.activity_otp_verification.*
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class OtpVerificationActivity : AppCompatActivity(),View.OnClickListener {

    var selectModeType: Int = 0
    var colorId: Int? = null
    var sessionManager: SessionManager? = null
    var from = ""
    var usersId = ""
    var userMobile = ""
    var inte: Intent? = null
    private lateinit var  signup: SignupModel
    private lateinit var  commonStatusModel: CommonStatusModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)
        sessionManager = SessionManager(this@OtpVerificationActivity)

        if (intent != null) {
            if (intent.hasExtra("selectModeType")) {
                selectModeType = intent.getIntExtra("selectModeType", 0)
            }
            if (intent.hasExtra("from")) {
                from = intent.getStringExtra("from")!!

            }
            if (intent.hasExtra("userData")) {
                usersId = intent.getStringExtra("userData")!!

            }
            if (intent.hasExtra("userMobile")) {
                userMobile = intent.getStringExtra("userMobile")!!


            }
        }
        setupViewModel()
        setupUI()

    }

    private fun setupUI() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        when (selectModeType) {
            0 -> colorId = R.color.colorPrimary
            1 -> colorId = R.color.yellow
            2 -> colorId = R.color.colorAccent
        }

        MyUtils.setSelectedModeTypeViewColor(this, arrayListOf(tv_verificationCode, et1, et2, et3, et4, tv_sendCodeAgain) as ArrayList<View>, colorId!!)

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

        btn_otpVerified.setOnClickListener (this)
        tv_sendCodeAgain.setOnClickListener (this)
    }

    private fun setupViewModel() {
         signup = ViewModelProvider(this@OtpVerificationActivity).get(SignupModel::class.java)
         commonStatusModel = ViewModelProvider(this@OtpVerificationActivity).get(CommonStatusModel::class.java)

    }

    fun otpValidation() {
        var otp = et1.text.toString().trim() + et2.text.toString().trim() + et3.text.toString().trim() + et4.text.toString().trim()
        if (otp.isEmpty() || otp.length != 4)
            MyUtils.showSnackbar(this, "Please add otp number", ll_main_otpVerification)
        else
            verifyOtp(otp)
    }

    private fun verifyOtp(otp: String) {
        btn_otpVerified.startAnimation()
        MyUtils.setViewAndChildrenEnabled(ll_main_otpVerification, false)

        if (from.equals("LoginByOtpVerification")) {
            var userData = sessionManager!!.get_Authenticate_User()
            usersId = userData!!.userID
        }
        FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener { task ->

                    if (!task.isSuccessful) {
                        Log.w("TokenError", "getInstanceId failed", task.exception)
                        btn_otpVerified.endAnimation()
                        MyUtils.setViewAndChildrenEnabled(ll_main_otpVerification, true)
                        ErrorUtil.errorMethod(ll_main_otpVerification)
                        return@OnCompleteListener
                    }
                    // Get new Instance ID token
                    val token = task.result

                    val jsonObject = JSONObject()

                    try {
                        jsonObject.put("languageID", "1")
                        jsonObject.put("loginuserID", usersId)
                        jsonObject.put("userOTP", otp)
                        jsonObject.put("userDeviceID", token)
                        jsonObject.put("apiType", RestClient.apiType)
                        jsonObject.put("apiVersion", RestClient.apiVersion)


                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    val jsonArray = JSONArray()
                    jsonArray.put(jsonObject)
                    signup.userRegistration(this!!, false, jsonArray.toString(), "otp_verify")
                            .observe(this@OtpVerificationActivity!!,
                                { loginPojo ->
                                    if (loginPojo != null) {
                                        btn_otpVerified.endAnimation()
                                        MyUtils.setViewAndChildrenEnabled(ll_main_otpVerification, true)
                                        if (loginPojo.get(0).status.equals("true", false)) {

                                            if (from.equals("ForgotPassword", false)) {
                                                val intent = Intent(this, ResetPassActivity::class.java)
                                                intent.putExtra("selectModeType", selectModeType)
                                                intent.putExtra("userID", usersId)
                                                startActivity(intent)
                                                finish()
                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                                            } else {

                                                StoreSessionManager(loginPojo[0].data[0])
                                                try {
                                                    Handler().postDelayed({
                                                        var intent = Intent(this@OtpVerificationActivity, SuccessfullyRegisteredActivity::class.java)
                                                        intent.putExtra("selectModeType", selectModeType)
                                                        startActivity(intent)
                                                        finishAffinity()
                                                    }, 500)
                                                    MyUtils.showSnackbar(this, loginPojo.get(0).message!!, ll_main_otpVerification)

                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }


                                        }
                                        else {
                                            MyUtils.showSnackbar(this, loginPojo.get(0).message!!, ll_main_otpVerification)
                                        }
                                    } else {
                                        MyUtils.setViewAndChildrenEnabled(ll_main_otpVerification, true)
                                        btn_otpVerified.endAnimation()
                                        ErrorUtil.errorMethod(ll_main_otpVerification)
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
        }

        try {

            jsonObject.put("languageID", "1")
            jsonObject.put("userMobile", userMobile)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("loginuserID", usersId)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonArray = JSONArray()
        jsonArray.put(jsonObject)
        commonStatusModel.getCommonStatus(this!!, false, jsonArray.toString(), "resend_otp")
                .observe(this@OtpVerificationActivity!!,
                    { loginPojo ->
                        if (loginPojo != null) {

                            if (loginPojo.get(0).status.equals("true")) {

                                MyUtils.showSnackbar(this, loginPojo.get(0).message!!, ll_main_otpVerification)


                            } else {
                                MyUtils.showSnackbar(this, loginPojo.get(0).message!!, ll_main_otpVerification)
                            }

                        } else {

                            ErrorUtil.errorMethod(ll_main_otpVerification)
                        }
                    })
    }


    override fun onBackPressed() {

        MyUtils.showMessageOKCancel(this@OtpVerificationActivity,
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
                            this@OtpVerificationActivity,
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
        when(v?.id){
            R.id.btn_otpVerified->{
                otpValidation()
            }
            R.id.tv_sendCodeAgain->{
                et1.text = Editable.Factory.getInstance().newEditable("")
                et2.text = Editable.Factory.getInstance().newEditable("")
                et3.text = Editable.Factory.getInstance().newEditable("")
                et4.text = Editable.Factory.getInstance().newEditable("")
                et1.requestFocus()
                resendOtp()
            }
        }
    }


}

