package com.nxplayr.fsl.ui.activity.onboarding.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.CountryListModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.ui.fragments.dialogs.LanguageDialog
import com.nxplayr.fsl.ui.fragments.setting.viewmodel.LanguageIntefaceListModel
import com.nxplayr.fsl.ui.fragments.setting.viewmodel.LanguageLabelModel
import com.nxplayr.fsl.util.*
import com.nxplayr.fsl.util.interfaces.LanguageSelection
import kotlinx.android.synthetic.main.activity_signin.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class SignInActivity : AppCompatActivity(), View.OnClickListener {

    var sessionManager: SessionManager? = null
    var mobile = false
    var countryListData: ArrayList<CountryListData>? = ArrayList()
    var countrylist: ArrayList<String>? = ArrayList()
    var user_Name = ""
    var last_Name = ""
    var user_Email = ""
    var fbID = ""
    var languageList: ArrayList<LanguageListData>? = ArrayList()
    private lateinit var callbackManager: CallbackManager
    private lateinit var countryListModel: CountryListModel
    private lateinit var signup: SignupModel
    private lateinit var languageModel: LanguageIntefaceListModel
    private lateinit var languageLabelModel: LanguageLabelModel

    override fun onResume() {
        super.onResume()
        updateLanguage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        callbackManager = CallbackManager.Factory.create()
        sessionManager = SessionManager(this@SignInActivity)
        setupViewModel()
        setupUI()

    }

    private fun setupViewModel() {
        countryListModel = ViewModelProvider(this@SignInActivity).get(CountryListModel::class.java)
        signup = ViewModelProvider(this@SignInActivity).get(SignupModel::class.java)
        languageModel =
            ViewModelProvider(this@SignInActivity).get(LanguageIntefaceListModel::class.java)
        languageLabelModel =
            ViewModelProvider(this@SignInActivity).get(LanguageLabelModel::class.java)
        languagesApi()
    }

    private fun updateLanguage() {
        if (sessionManager != null && sessionManager?.getSelectedLanguage() != null) {
            language.visibility = View.VISIBLE
            flag.setImageURI(RestClient.image_base_url_flag + sessionManager?.getSelectedLanguage()!!.languageFlag)
            language_name.text = sessionManager?.getSelectedLanguage()!!.languageName
        } else {
            language.visibility = View.INVISIBLE
        }
    }

    private fun setupUI() {
        initView()
        getCounrtyList()
        updateContent()
    }

    private fun updateContent() {
        if (sessionManager?.LanguageLabel != null) {
            // static data from preference
            val langLabel = sessionManager?.LanguageLabel

            if (!langLabel?.lngSignin.isNullOrEmpty()) {
                titleSignIn.text = langLabel?.lngSignin
            }
            if (!langLabel?.lngWelcomeBack.isNullOrEmpty()) {
                welcomeBack.text = langLabel?.lngWelcomeBack
            }
            if (!langLabel?.lngEmailMobile.isNullOrEmpty()) {
                email_textInputLayout.hint = langLabel?.lngEmailMobile
            }
            if (!langLabel?.lngPassword.isNullOrEmpty()) {
                password_textInput.hint = langLabel?.lngPassword
            }
            if (!langLabel?.lngForgotPassword.isNullOrEmpty()) {
                forgotPasswordtv.text = langLabel?.lngForgotPassword
            }
            if (!langLabel?.lngSignin.isNullOrEmpty()) {
                btn_signIn.progressText = langLabel?.lngSignin
            }
            if (!langLabel?.lngNewUser.isNullOrEmpty()) {
                newUser.text = langLabel?.lngNewUser
            }
            if (!langLabel?.lngCreateAc.isNullOrEmpty()) {
                createAccountText.text = langLabel?.lngCreateAc
            }
        }
    }

    private fun initView() {
        btn_signIn.setOnClickListener(this)
        tv_countryCode.setOnClickListener(this)
        loginBackButtonIv.setOnClickListener(this)
        signupWithLL.setOnClickListener(this)
        forgotPasswordtv.setOnClickListener(this)
        fbImage.setOnClickListener(this)
        imgLinkedIn.setOnClickListener(this)
        language.setOnClickListener(this)

        buttonFacebookLoginScreen?.setPermissions("public_profile", "email")
        buttonFacebookLoginScreen.authType = "rerequest";
        buttonFacebookLoginScreen?.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                LoginManager.getInstance().logOut()

                val request =
                    GraphRequest.newMeRequest(loginResult.accessToken) { jsonObj, response ->
                        try {
                            //here is the data that you want
                            user_Email = jsonObj.opt("email").toString()
                            fbID = jsonObj.opt("id").toString()
                            user_Name = jsonObj.opt("name").toString()
                            MyUtils.showSnackbar(
                                this@SignInActivity,
                                "" + user_Name,
                                ll_main_login!!
                            )
                            if (!fbID.isNullOrEmpty() && !user_Email.isNullOrEmpty()) {
                                checkForDuplication(
                                    user_Email,
                                    fbID,
                                    user_Name, "", ""
                                )

                            } else {
                                MyUtils.showMessageOK(
                                    this@SignInActivity,
                                    "your privacy setting in facebook is not allowing us to acccess your data, please try another account or change your privacy settings for CamFire."
                                ) { dialog, which -> dialog?.dismiss() }

                            }
                            Log.d("FBLOGIN_JSON_RES", jsonObj.toString())
                            if (jsonObj.has("id")) {
//                            handleSignInResultFacebook(jsonObj)
                                Log.e("FBLOGIN_Success", jsonObj.toString())
                            } else {
                                Log.e("FBLOGIN_FAILD", jsonObj.toString())
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
//                        dismissDialogLogin()
                        }
                    }

                val parameters = Bundle()
                parameters.putString("fields", "name,email,id,picture.type(large)")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                Toast.makeText(
                    baseContext, "facebook:onCancel.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(
                    baseContext, "facebook:onError.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        emailId_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var loginType = TextUtils.isDigitsOnly(emailId_edit_text?.text.toString())
                if (loginType) {
                    tv_countryCode.visibility = View.VISIBLE
                    setPaddingTextview(tv_countryCode, "Show")
                    setPaddingEditText(emailId_edit_text, "Show")
                } else {
                    tv_countryCode.visibility = View.GONE
                    setPaddingEditText(emailId_edit_text, "hide")
                }
                if (p0!!.isEmpty()) {
                    tv_countryCode.visibility = View.GONE
                    setPaddingEditText(emailId_edit_text, "hide")
                }
                nextButtonEnable()
            }
        })

        password_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }
        })

        password_edit_text.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= password_edit_text.right - password_edit_text.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                        // your action here
                        password_edit_text.tooglePassWord()

                        return true
                    }
                }
                return false
            }
        })
    }

    private fun nextButtonEnable() {
        if (validateSignInInput()) {
            btn_signIn.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_signIn.textColor = resources.getColor(R.color.black)
        } else {
            btn_signIn.strokeColor = (resources.getColor(R.color.colorPrimary))
            btn_signIn.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_signIn.textColor = resources.getColor(R.color.colorPrimary)
        }
    }

    private fun validateSignInInput(): Boolean {
        var valid = false
        when {
            emailId_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            password_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            else -> {
                valid = true
            }
        }
        return valid
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun EditText.tooglePassWord() {
        this.tag = !((this.tag ?: false) as Boolean)
        this.inputType = if (this.tag as Boolean)
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        else
            (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)

        if (this.tag as Boolean) {
            password_edit_text.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.hide_icon_login,
                0
            )
        } else {
            password_edit_text.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.show_icon_login,
                0
            )
        }

        this.setSelection(this.length())
    }


    override fun onBackPressed() {
        MyUtils.finishActivity(this@SignInActivity, true)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_signIn -> {
                chekValidation()
            }
            R.id.tv_countryCode -> {
                PopupMenu(this@SignInActivity, v, countrylist!!).showPopUp(object :
                    PopupMenu.OnMenuSelectItemClickListener {
                    override fun onItemClick(item: String, pos: Int) {
                        tv_countryCode.text = item.toString()
                    }
                })
            }
            R.id.loginBackButtonIv -> {
                onBackPressed()
            }
            R.id.signupWithLL -> {
                val i = Intent(this, SignupActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.forgotPasswordtv -> {
                val i = Intent(this, ForgotPasswordActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.fbImage -> {
                MyUtils.hideKeyboardFrom(this@SignInActivity, ll_main_login!!)
                buttonFacebookLoginScreen?.performClick()
            }
            R.id.imgLinkedIn -> {
                LinkedinActivity(this, object : LinkedinActivity.LinkedinData {
                    override fun LinkedinSuccess(
                        email: String?,
                        first_name: String?,
                        last_name: String?,
                        id: String?
                    ) {
                        var menLinkedInId = id.toString()
                        user_Name = first_name!!
                        last_Name = last_name!!
                        user_Email = email!!
                        if (!menLinkedInId.isNullOrEmpty() && !user_Email.isNullOrEmpty()) {
                            checkForDuplication(
                                user_Email,
                                "",
                                user_Name, menLinkedInId, ""
                            )

                        }
                        /*FirebaseInstanceId.getInstance().instanceId
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        userLogin(it.result!!.token)
                                    } else {
                                        errorMethod()
                                    }
                                }*/
                    }

                    override fun linkedCancel() {

                    }
                }).showLinkedin()
            }
            R.id.language -> {
                val dialog = LanguageDialog(this@SignInActivity, languageList, object : LanguageSelection {
                    override fun onLanguageSelect(data: LanguageListData) {
                        updateLanguage()
                        getLanguageLabelList(data.languageID)
                    }
                })
                dialog.show()
            }
        }
    }


    private fun chekValidation(): Boolean {
        MyUtils.hideKeyboard1(this@SignInActivity)
        var checkFlag = true
        var loginType = TextUtils.isDigitsOnly(emailId_edit_text?.text.toString().trim())

        when {
            emailId_edit_text!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(
                    this@SignInActivity,
                    getString(R.string.enter_email_mobile_msg),
                    ll_main_login
                )
                checkFlag = false
            }
            (loginType && (emailId_edit_text.text.toString().length < 8 || emailId_edit_text.text.toString().length > 16)) -> {
                MyUtils.showSnackbar(
                    this@SignInActivity,
                    getString(R.string.valid_mobile),
                    ll_main_login
                )
                checkFlag = false
            }
            (loginType && tv_countryCode.text.toString().isEmpty()) -> {
                MyUtils.showSnackbar(
                    this@SignInActivity,
                    getString(R.string.please_enter_country_code),
                    ll_main_login
                )
                checkFlag = false
            }
            (!loginType && !MyUtils.isValidEmail(emailId_edit_text.text.toString())) -> {
                MyUtils.showSnackbar(
                    this@SignInActivity,
                    getString(R.string.valid_email),
                    ll_main_login
                )
                checkFlag = false
            }
            password_edit_text!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(
                    this@SignInActivity,
                    getString(R.string.please_enter_password),
                    ll_main_login
                )
            }
            password_edit_text!!.text.toString().trim().length < 6 -> {
                MyUtils.showSnackbar(
                    this@SignInActivity,
                    getString(R.string.please_enter_valid_password),
                    ll_main_login
                )
            }
            else -> {
                if (TextUtils.isDigitsOnly(emailId_edit_text.text.toString()))
                    mobile = true
                signIn()
            }

        }
        return checkFlag
    }

    private fun getCounrtyList() {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("blankCountryCode", "No")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        countryListModel.getCountryList(this, false, jsonArray.toString())
            .observe(this@SignInActivity,
                androidx.lifecycle.Observer { countryListPojo ->
                    if (countryListPojo != null) {
                        if (countryListPojo.get(0).status.equals("true", false)) {
                            countryListData?.addAll(countryListPojo.get(0).data)
                            countrylist = java.util.ArrayList()
                            countrylist!!.clear()
                            for (i in 0 until countryListData!!.size) {
                                countrylist!!.add(countryListData!![i].countryDialCode)
                            }
                        }
                    }
                })
    }

    private fun checkForDuplication(
        userEmail: String,
        fbID: String,
        userName: String,
        userLinkedinID: String,
        userInstaID: String
    ) {
        btn_signIn.startAnimation()
        MyUtils.setViewAndChildrenEnabled(ll_main_login, false)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                btn_signIn.endAnimation()
                MyUtils.setViewAndChildrenEnabled(ll_main_login, true)
                ErrorUtil.errorMethod(ll_main_login)
                return@OnCompleteListener
            }
            val token = task.result

            val jsonObject = JSONObject()
            val jsonArray = JSONArray()

            try {
                jsonObject.put("languageID", "1")
                jsonObject.put("userFirstName", userName)
                jsonObject.put("userLastName", "")
                jsonObject.put("userCountryCode", "")
                jsonObject.put("userEmail", userEmail)
                jsonObject.put("userMobile", "")
                jsonObject.put("userDeviceType", RestClient.apiType)
                jsonObject.put("userFBID", fbID)
                jsonObject.put("userInstaID", userInstaID)
                jsonObject.put("userLinkedinID", userLinkedinID)
                jsonObject.put("userDeviceID", token)
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)
                jsonArray.put(jsonObject)

            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: JsonParseException) {
                e.printStackTrace()
            }
            signup.userRegistration(this@SignInActivity, false, jsonArray.toString(), "socialLogin")
                .observe(this@SignInActivity,
                    Observer<List<SignupPojo?>?> { response ->
                        if (!response.isNullOrEmpty()) {
                            btn_signIn.endAnimation()
                            MyUtils.setViewAndChildrenEnabled(ll_main_login, true)
                            MyUtils.fbID = fbID
                            MyUtils.user_Name = userName
                            MyUtils.user_Email = userEmail
                            if (response[0]?.status.equals("true", true)) {
                                try {
                                    sessionManager?.clear_login_session()
                                    storeSessionManager(response.get(0)?.data?.get(0))
                                    Handler().postDelayed({
                                        if (response[0]?.data!![0].userOVerified.equals(
                                                "Yes",
                                                true
                                            )
                                        ) {
                                            MyUtils.startActivity(
                                                this@SignInActivity,
                                                MainActivity::class.java,
                                                true
                                            )
                                            finishAffinity()

                                        } else {
                                            var i = Intent(
                                                this@SignInActivity,
                                                OtpVerificationActivity::class.java
                                            )
                                            i.putExtra("from", "LoginByOtpVerification")
                                            startActivity(i)
                                            overridePendingTransition(
                                                R.anim.slide_in_right,
                                                R.anim.slide_out_left
                                            )
                                        }
                                    }, 1000)


                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            } else {
                                //No data and no internet
                                MyUtils.showSnackbar(
                                    this@SignInActivity, response.get(0)?.message!!,
                                    ll_main_login
                                )
                            }
                        } else {
                            btn_signIn.endAnimation()
                            MyUtils.setViewAndChildrenEnabled(
                                ll_main_login,
                                true
                            )                                            //No internet and somting went rong
                            if (MyUtils.isInternetAvailable(this@SignInActivity)) {
                                MyUtils.showSnackbar(
                                    this@SignInActivity,
                                    resources.getString(R.string.error_crash_error_message),
                                    ll_main_login
                                )
                            } else {
                                MyUtils.showSnackbar(
                                    this@SignInActivity,
                                    resources.getString(R.string.error_common_network),
                                    ll_main_login
                                )
                            }
                        }
                    })


        })
    }

    private fun signIn() {
        btn_signIn.startAnimation()
        MyUtils.setViewAndChildrenEnabled(ll_main_login, false)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                btn_signIn.endAnimation()
                MyUtils.setViewAndChildrenEnabled(ll_main_login, true)
                ErrorUtil.errorMethod(ll_main_login)
                return@OnCompleteListener
            }
            val token = task.result
            val jsonObject = JSONObject()
            try {
                jsonObject.put("languageID", "1")
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)
                jsonObject.put("userDeviceID", token)
                if (mobile) {
                    jsonObject.put("userMobile", emailId_edit_text.text.toString())
                } else {
                    jsonObject.put("userMobile", emailId_edit_text.text.toString())
                }
                jsonObject.put("userPassword", password_edit_text.text.toString())
                jsonObject.put("userCountryCode", tv_countryCode.text.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            var jsonArray = JSONArray()
            jsonArray.put(jsonObject)
            signup.userRegistration(this, false, jsonArray.toString(), "login")
                .observe(this@SignInActivity
                ) { loginPojo ->
                    if (loginPojo != null) {
                        btn_signIn.endAnimation()
                        MyUtils.setViewAndChildrenEnabled(ll_main_login, true)

                        if (loginPojo.get(0).status.equals("true", false)) {
                            try {
                                sessionManager?.login_pass(
                                    password_edit_text.text.toString().trim()
                                )
                                storeSessionManager(loginPojo.get(0).data.get(0))
                                Handler(Looper.getMainLooper()).postDelayed({

                                    if (loginPojo[0].data[0].userOVerified.equals(
                                            "Yes",
                                            true
                                        )
                                    ) {
                                        MyUtils.startActivity(
                                            this@SignInActivity,
                                            MainActivity::class.java,
                                            true
                                        )
                                        finishAffinity()
                                    } else {
                                        var i = Intent(
                                            this@SignInActivity,
                                            OtpVerificationActivity::class.java
                                        )
                                        i.putExtra("from", "LoginByOtpVerification")
                                        startActivity(i)
                                        overridePendingTransition(
                                            R.anim.slide_in_right,
                                            R.anim.slide_out_left
                                        )
                                    }
                                }, 1000)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        } else {
                            MyUtils.showSnackbar(this, loginPojo.get(0).message, ll_main_login)
                        }

                    } else {
                        btn_signIn.endAnimation()
                        MyUtils.setViewAndChildrenEnabled(ll_main_login, true)
                        ErrorUtil.errorMethod(ll_main_login)
                    }
                }

        })

    }

    private fun storeSessionManager(uesedata: SignupData?) {

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

    private fun setPaddingTextview(tv_countryCode: TextView, from: String) {
        when (from) {
            "Show" -> {
                tv_countryCode.setPadding(
                    resources.getDimensionPixelOffset(R.dimen._10sdp),
                    resources.getDimensionPixelOffset(R.dimen._25sdp),
                    resources.getDimensionPixelOffset(R.dimen._80sdp),
                    resources.getDimensionPixelOffset(R.dimen._10sdp)
                )
            }
            else -> {
                tv_countryCode.visibility = View.GONE
            }
        }

    }

    private fun setPaddingEditText(emailId_edit_text: TextInputEditText, from: String) {
        when (from) {
            "Show" -> {
                emailId_edit_text.setPadding(
                    resources.getDimensionPixelOffset(R.dimen._60sdp),
                    resources.getDimensionPixelOffset(R.dimen._25sdp),
                    resources.getDimensionPixelOffset(R.dimen._1sdp),
                    resources.getDimensionPixelOffset(R.dimen._10sdp)
                )
            }
            "hide" -> {
                emailId_edit_text.setPadding(
                    resources.getDimensionPixelOffset(R.dimen._10sdp),
                    resources.getDimensionPixelOffset(R.dimen._25sdp),
                    resources.getDimensionPixelOffset(R.dimen._1sdp),
                    resources.getDimensionPixelOffset(R.dimen._10sdp)
                )
            }
        }
    }

    private fun getLanguageLabelList(languageId: String) {
        MyUtils.showProgressDialog(this@SignInActivity, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("languageID", languageId)
            jsonObject.put("langLabelStatus", "")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        languageLabelModel.getLanguageList(this@SignInActivity, false, jsonArray.toString())
            .observe(
                this@SignInActivity
            ) { languageLabelPojo: List<LanguageLabelPojo>? ->
                if (!languageLabelPojo.isNullOrEmpty()) {
                    if (languageLabelPojo[0].status == true && !languageLabelPojo[0].data.isNullOrEmpty()) {
                        MyUtils.dismissProgressDialog()
                        sessionManager?.LanguageLabel = languageLabelPojo[0].data?.get(0)!!
                        updateContent()
                    } else {
                        MyUtils.dismissProgressDialog()
                    }

                } else {
                    MyUtils.dismissProgressDialog()
                }
            }
    }

    private fun languagesApi() {

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        languageModel.getLanguageList(this@SignInActivity, false, jsonArray.toString())
            .observe(this@SignInActivity) { languageListPojo ->
                if (languageListPojo != null && languageListPojo.isNotEmpty()) {
                    if (languageListPojo[0].status == true) {
                        languageList?.clear()
                        languageList?.addAll(languageListPojo[0].data)
                    }
                } else {
//                    ErrorUtil.errorView(this@SignupActivity, nointernetMainRelativelayout)
                }
            }

    }

}