package com.nxplayr.fsl.ui.activity.onboarding.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.PopupMenu
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CountryListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.CountryListModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import kotlinx.android.synthetic.main.activity_otp_verification.*
import kotlinx.android.synthetic.main.activity_parent_info.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ParentInfoActivity : AppCompatActivity(), View.OnClickListener {


    var countryListData: ArrayList<CountryListData>? = ArrayList()
    var countrylist: ArrayList<String>? = ArrayList()
    var colorId: Int? = null
    var selectModeType: Int = 0
    var loginuserID: String = ""
    var countryCode: String = ""
    var sessionManager: SessionManager? = null
    private lateinit var countryListModel: CountryListModel
    private lateinit var signup: SignupModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_info)
        sessionManager = SessionManager(this@ParentInfoActivity)

        if (intent != null && intent.hasExtra("selectModeType"))
            selectModeType = intent.getIntExtra("selectModeType", 0)

        if (intent != null && intent.hasExtra("loginuserID"))
            loginuserID = intent.getStringExtra("loginuserID")!!

        if (intent != null && intent.hasExtra("countryCode"))
            countryCode = intent.getStringExtra("countryCode")!!

        setupViewModel()
        setupUI()


    }

    private fun setupUI() {
        tv_countryCode.text = countryCode
        setUI()
        MyUtils.setSelectedModeTypeViewColor(
            this,
            arrayListOf(
                introduce_your_parents,
                textinput_parent_info_email,
                emailAddress_edit_text,
                mobile_textInputLayout,
                mobile_edit_text
            ) as ArrayList<View>,
            colorId!!
        )

        getCounrtyList()
        tv_countryCode.setOnClickListener(this)
        mobile_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var loginType = TextUtils.isDigitsOnly(mobile_edit_text?.text.toString())
                if (loginType) {
                    tv_countryCode.visibility = View.VISIBLE
                    mobile_edit_text.setPadding(
                        resources.getDimensionPixelOffset(R.dimen._60sdp),
                        resources.getDimensionPixelOffset(R.dimen._25sdp),
                        resources.getDimensionPixelOffset(R.dimen._1sdp),
                        resources.getDimensionPixelOffset(R.dimen._10sdp)
                    )
                    tv_countryCode.setPadding(
                        resources.getDimensionPixelOffset(R.dimen._10sdp),
                        resources.getDimensionPixelOffset(R.dimen._25sdp),
                        resources.getDimensionPixelOffset(R.dimen._80sdp),
                        resources.getDimensionPixelOffset(R.dimen._10sdp)
                    )
                    tv_countryCode.requestFocus()
                } else {
                    tv_countryCode.visibility = View.GONE
                    mobile_edit_text.setPadding(
                        resources.getDimensionPixelOffset(R.dimen._10sdp),
                        resources.getDimensionPixelOffset(R.dimen._25sdp),
                        resources.getDimensionPixelOffset(R.dimen._1sdp),
                        resources.getDimensionPixelOffset(R.dimen._10sdp)
                    )
                    mobile_edit_text.requestFocus()
                }
                if (p0!!.isEmpty()) {
                    tv_countryCode.visibility = View.GONE
                    mobile_edit_text.setPadding(
                        resources.getDimensionPixelOffset(R.dimen._10sdp),
                        resources.getDimensionPixelOffset(R.dimen._25sdp),
                        resources.getDimensionPixelOffset(R.dimen._1sdp),
                        resources.getDimensionPixelOffset(R.dimen._10sdp)
                    )
                }
                nextButtonEnable()
            }
        })
        btnSubmit.setOnClickListener(this)
    }

    private fun setupViewModel() {
        countryListModel =
            ViewModelProvider(this@ParentInfoActivity).get(CountryListModel::class.java)
        signup = ViewModelProvider(this@ParentInfoActivity).get(SignupModel::class.java)


    }

    fun nextButtonEnable() {
        if (validateSignInInput()) {
            when (selectModeType) {
                0 -> {
                    btnSubmit.backgroundTint = (resources.getColor(R.color.colorPrimary))
                    btnSubmit.textColor = resources.getColor(R.color.black)
                }
                1 -> {
                    btnSubmit.backgroundTint = (resources.getColor(R.color.yellow))
                    btnSubmit.textColor = resources.getColor(R.color.black)
                }
            }

        } else {
            when (selectModeType) {
                0 -> {
                    btnSubmit.strokeColor = (resources.getColor(R.color.colorPrimary))
                    btnSubmit.backgroundTint = (resources.getColor(R.color.transperent1))
                    btnSubmit.textColor = resources.getColor(R.color.colorPrimary)
                }
                1 -> {
                    btnSubmit.strokeColor = (resources.getColor(R.color.yellow))
                    btnSubmit.backgroundTint = (resources.getColor(R.color.transperent1))
                    btnSubmit.textColor = resources.getColor(R.color.yellow)
                }
            }

        }
    }

    fun validateSignInInput(): Boolean {
        var valid: Boolean = false
        when {
            mobile_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            emailAddress_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            else -> {
                valid = true
            }
        }
        return valid
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
        countryListModel.getCountryList(this, false, jsonArray.toString())
            .observe(
                this@ParentInfoActivity,
                { countryListPojo ->
                    if (countryListPojo != null) {
                        if (countryListPojo.get(0).status.equals("true", false)) {
                            countryListData?.addAll(countryListPojo.get(0).data)
                            countrylist = java.util.ArrayList()
                            countrylist!!.clear()
                            for (i in 0 until countryListData!!.size) {
                                countrylist!!.add(countryListData!![i].countryDialCode)
                            }
                            // popupMenu()
                        } else {
                            MyUtils.showSnackbar(this@ParentInfoActivity, "", ll_main_parents)
                        }

                    } else {
                        ErrorUtil.errorMethod(ll_main_parents)

                    }
                })
    }

    private fun StoreSessionManager(uesedata: SignupData?) {

        val gson = Gson()

        val json = gson.toJson(uesedata)
        sessionManager!!.create_login_session(
            json,
            uesedata!!.userMobile,
            "",
            true,
            sessionManager!!.isEmailLogin()
        )

    }

    fun popupMenu() {
        PopupMenu(this@ParentInfoActivity, tv_countryCode, countrylist!!).showPopUp(object :
            PopupMenu.OnMenuSelectItemClickListener {
            override fun onItemClick(item: String, pos: Int) {
                tv_countryCode.text = item.toString()
            }
        })
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btnSubmit -> {
                btnSubmit.textColor = (resources.getColor(R.color.black))
                btnSubmit.backgroundTint = (resources.getColor(colorId!!))
                chekValidation()
            }
            R.id.tv_countryCode -> {
                if (countrylist.isNullOrEmpty()) {
                    getCounrtyList()
                } else {
                    countrylist = java.util.ArrayList()
                    countrylist!!.clear()
                    for (i in 0 until countryListData!!.size) {
                        countrylist!!.add(countryListData!![i].countryDialCode)
                    }
                    popupMenu()
                }

            }
        }

    }

    private fun chekValidation() {
        MyUtils.hideKeyboard1(this@ParentInfoActivity)
        when {
            mobile_edit_text!!.text.toString().trim().isEmpty() -> {

                MyUtils.showSnackbar(
                    this@ParentInfoActivity,
                    getString(R.string.please_enter_mobile_number),
                    ll_main_parents
                )
            }
            mobile_edit_text.text.toString().trim().length < 8 || mobile_edit_text.text.toString()
                .trim().length > 16 -> {

                MyUtils.showSnackbar(
                    this@ParentInfoActivity,
                    getString(R.string.please_enter_valid_mobile_number),
                    ll_main_parents
                )
            }
            emailAddress_edit_text!!.text.toString().trim().isEmpty() -> {
                MyUtils.showSnackbar(
                    this@ParentInfoActivity,
                    getString(R.string.please_enter_email_id),
                    ll_main_parents
                )
            }
            (!MyUtils.isValidEmail(emailAddress_edit_text.text.toString().trim())) -> {
                MyUtils.showSnackbar(
                    this@ParentInfoActivity,
                    resources.getString(R.string.valid_email_address),
                    ll_main_parents
                )
            }
            else -> {
                signUp()
            }
        }
    }

    private fun signUp() {
        btnSubmit.startAnimation()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                btnSubmit.endAnimation()
                MyUtils.setViewAndChildrenEnabled(ll_main_parents, true)
                ErrorUtil.errorMethod(ll_main_parents)
                return@OnCompleteListener
            }
            val token = task.result
            val jsonObject = JSONObject()
            val jsonArray = JSONArray()
            try {
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)
                jsonObject.put("userDeviceType", RestClient.apiType)
                jsonObject.put("userDeviceID", token)
                jsonObject.put("userParentEmail", emailAddress_edit_text.text.toString())
                jsonObject.put("userParentMobile", mobile_edit_text.text.toString())
                jsonObject.put("loginuserID", loginuserID)
            } catch (e: JSONException) {
                e.printStackTrace()
            }


            jsonArray.put(jsonObject)
            signup.userRegistration(this, false, jsonArray.toString(), "parents_Profile")
                .observe(this@ParentInfoActivity,
                    Observer { loginPojo ->
                        if (loginPojo != null) {
                            btnSubmit.endAnimation()
                            if (loginPojo.get(0).status.equals("true", false)) {
                                try {
                                    StoreSessionManager(loginPojo.get(0).data.get(0))
                                    Handler().postDelayed({
                                        var intent = Intent(
                                            this@ParentInfoActivity,
                                            ParentsOtpVerificationActivity::class.java
                                        )
                                        intent.putExtra("selectModeType", selectModeType)
                                        intent.putExtra("from", "LoginByOtpVerification")
                                        Log.e("select2", selectModeType.toString())
                                        startActivity(intent)
                                        finishAffinity()
                                    }, 1000)
                                    MyUtils.showSnackbar(
                                        this,
                                        loginPojo.get(0).message, ll_main_parents
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            } else {
                                MyUtils.showSnackbar(
                                    this,
                                    loginPojo.get(0).message, ll_main_parents
                                )
                            }

                        } else {
                            btnSubmit.endAnimation()
                            ErrorUtil.errorMethod(ll_main_parents)
                        }
                    })
        })
    }

    private fun setUI() {
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngVerificationTitle.isNullOrEmpty())
                introduce_your_parents.text = sessionManager?.LanguageLabel?.lngVerificationTitle
            if (!sessionManager?.LanguageLabel?.lngVerificationTitle.isNullOrEmpty())
                introduce_your_parents_details.text = sessionManager?.LanguageLabel?.lngParentPermissionDetail
            if (!sessionManager?.LanguageLabel?.lngEmailAddress.isNullOrEmpty())
                textinput_parent_info_email.hint = sessionManager?.LanguageLabel?.lngEmailAddress
            if (!sessionManager?.LanguageLabel?.lngMobileNo.isNullOrEmpty())
                mobile_textInputLayout.hint = sessionManager?.LanguageLabel?.lngMobileNo
            if (!sessionManager?.LanguageLabel?.lngSubmit.isNullOrEmpty())
                btnSubmit.progressText = sessionManager?.LanguageLabel?.lngSubmit
        }

        when (selectModeType) {
            0 -> {
                colorId = R.color.colorPrimary
            }
            1 -> {
                colorId = R.color.yellow
            }
            2 -> {
                colorId = R.color.colorAccent
            }

        }
    }
}

