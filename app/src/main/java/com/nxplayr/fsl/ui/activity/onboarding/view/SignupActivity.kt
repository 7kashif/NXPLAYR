package com.nxplayr.fsl.ui.activity.onboarding.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.gson.JsonParseException
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.LanguageLabelPojo
import com.nxplayr.fsl.data.model.LanguageListData
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModelV2
import com.nxplayr.fsl.ui.fragments.dialogs.LanguageDialog
import com.nxplayr.fsl.ui.fragments.setting.viewmodel.LanguageIntefaceListModel
import com.nxplayr.fsl.ui.fragments.setting.viewmodel.LanguageLabelModelV2
import com.nxplayr.fsl.util.LinkedinActivity
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.util.interfaces.LanguageSelection
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class SignupActivity : AppCompatActivity(), View.OnClickListener {


    var fbID = ""
    var isSocial = false
    var user_Name = ""
    var user_Email = ""
    private lateinit var callbackManager: CallbackManager
    private lateinit var signup: SignupModelV2
    private lateinit var languageModel: LanguageIntefaceListModel
    private lateinit var languageLabelModel: LanguageLabelModelV2
    var sessionManager: SessionManager? = null
    var languageList: ArrayList<LanguageListData>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        initData()
        setupViewModel()
        setupUI()
    }

    private fun initData() {
        sessionManager = SessionManager(this@SignupActivity)
        callbackManager = CallbackManager.Factory.create()
    }

    private fun setupTermsPolicyText(langLabel: LanguageLabelPojo.LanguageLabelData?) {

        var bodyData = getString(R.string.by_creating_your_account_you_agree_with_our)
        var terms = getString(R.string.terms_of_use)
        var privacyPolicy = getString(R.string.privacy_policy)
        var communityG = getString(R.string.community_guidelines)

        if (!langLabel?.lngAcceptTC.isNullOrEmpty()) {
            bodyData = langLabel?.lngAcceptTC.toString()
        }

        if (!langLabel?.lngTC.isNullOrEmpty()) {
            terms = langLabel?.lngTC.toString()
        }

        if (!langLabel?.lngPrivacyPolicy.isNullOrEmpty()) {
            privacyPolicy = langLabel?.lngPrivacyPolicy.toString()
        }

        if (!langLabel?.lngCommunityGuidelines.isNullOrEmpty()) {
            communityG = langLabel?.lngCommunityGuidelines.toString()
        }

        val builder = StringBuilder()

        builder.append(bodyData)
        builder.append(" ")
        builder.append(terms)
        builder.append(", ")
        builder.append(privacyPolicy)
        builder.append(" & ")
        builder.append(communityG)

        val spannableString = SpannableStringBuilder(builder)

        val termsStart = bodyData.length + 1
        val termsEnd = termsStart + terms.length

        val policyStart = termsEnd + 2
        val policyEnd = policyStart + privacyPolicy.length

        val communityStart = policyEnd + 3
        val communityEnd = communityStart + communityG.length

        spannableString.setSpan(UnderlineSpan(), termsStart, termsEnd, 0)
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(v: View) {
                MyUtils.startActivity(
                    "1",
                    terms,
                    this@SignupActivity,
                    TermsActivity::class.java,
                    false
                )
            }
        }, termsStart, termsEnd, 0)
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
            termsStart, termsEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(UnderlineSpan(), policyStart, policyEnd, 0)
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(v: View) {
                MyUtils.startActivity(
                    "2",
                    privacyPolicy,
                    this@SignupActivity,
                    TermsActivity::class.java,
                    false
                )
            }
        }, policyStart, policyEnd, 0)
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
            policyStart, policyEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(UnderlineSpan(), communityStart, communityEnd, 0)
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(v: View) {
                MyUtils.startActivity(
                    "3",
                    communityG,
                    this@SignupActivity,
                    TermsActivity::class.java,
                    false
                )
            }
        }, communityStart, communityEnd, 0)
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
            communityStart, communityEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        termsConditionsTv.text = spannableString
        termsConditionsTv.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupUI() {
        setOnClickListeners()
        setupContent()
        buttonFacebookLoginScreen?.setReadPermissions("public_profile", "email")
        buttonFacebookLoginScreen?.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                LoginManager.getInstance().logOut()
                val request =
                    GraphRequest.newMeRequest(loginResult.accessToken) { jsonObj, response ->
                        try {
                            user_Email = jsonObj.opt("email").toString()
                            fbID = jsonObj.opt("id").toString()
                            user_Name = jsonObj.opt("name").toString()
                            if (!fbID.isNullOrEmpty() && !user_Email.isNullOrEmpty()) {
                                isSocial = true
                                setSocialData("fb")
                                /*FirebaseInstanceId.getInstance().instanceId
                                        .addOnSuccessListener(object : OnSuccessListener<InstanceIdResult> {
                                            override fun onSuccess(instanceIdResult: InstanceIdResult) {
                                                val instanceId = instanceIdResult.id
                                                var newtoken = instanceIdResult.token
                                                checkForDuplication(user_Email, fbID, user_Name, 1, newtoken)
                                            }
                                        })*/
                            } else {
                                isSocial = true
                                MyUtils.showMessageOK(
                                    this@SignupActivity,
                                    "your privacy setting in facebook is not allowing us to acccess your data, please try another account or change your privacy settings for CamFire.",
                                    object : DialogInterface.OnClickListener {
                                        override fun onClick(dialog: DialogInterface?, which: Int) {
                                            dialog?.dismiss()
                                        }
                                    })

                            }

                            Log.d("FBLOGIN_JSON_RES", jsonObj.toString())
                            if (jsonObj.has("id")) {
//                            handleSignInResultFacebook(jsonObj)
                                Log.e("FBLOGIN_Success", jsonObj.toString())
                            } else {
                                Log.e("FBLOGIN_FAILD", jsonObj.toString())
                                isSocial = false
                                // setSocialData()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
//                        dismissDialogLogin()
                            isSocial = false
                            // setSocialData()
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
                isSocial = false
                // setSocialData()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(
                    baseContext, "facebook:onError.",
                    Toast.LENGTH_SHORT
                ).show()
                isSocial = false
                //setSocialData()

            }
        })
    }

    private fun setupContent() {
        if (sessionManager?.LanguageLabel != null) {
            // static data from preference
            val langLabel = sessionManager?.LanguageLabel

            if (!langLabel?.lngletGetStart.isNullOrEmpty()) {
                signupTitle.text = langLabel?.lngletGetStart
            }

            if (!langLabel?.lngSignupLinkedIn.isNullOrEmpty()) {
                signupWithLinkedInTv.text = langLabel?.lngSignupLinkedIn
            }

            if (!langLabel?.lngSignupFB.isNullOrEmpty()) {
                signupWithFaceBookTv.text = langLabel?.lngSignupFB
            }

            if (!langLabel?.lngSignupInsta.isNullOrEmpty()) {
                signupWithInstagramTv.text = langLabel?.lngSignupInsta
            }

            if (!langLabel?.lngCreateAc.isNullOrEmpty()) {
                signupCreateAccountTv.text = langLabel?.lngCreateAc
            }

            setupTermsPolicyText(langLabel)
        }
    }

    private fun setupViewModel() {
        languageModel =
            ViewModelProvider(this@SignupActivity).get(LanguageIntefaceListModel::class.java)
        languageLabelModel =
            ViewModelProvider(this@SignupActivity).get(LanguageLabelModelV2::class.java)
        signup = ViewModelProvider(this@SignupActivity).get(SignupModelV2::class.java)
        languagesApi()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun setSocialData(s: String) {
        if (!user_Email.isNullOrEmpty()) {
            getCheckUser(user_Email, user_Name, s)
        } else {
            MyUtils.showSnackbar(this@SignupActivity, "Email address invalid", mainLL)
        }
    }

    private fun getCheckUser(userEmail: String, userName: String, s: String) {
        MyUtils.showProgressDialog(this@SignupActivity, "Wait..")
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("languageID", "1")
            jsonObject.put("userEmail", userEmail)
            jsonObject.put("userMobile", "")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        signup.checkDublication(jsonArray.toString())
        signup.checkDublication.observe(this@SignupActivity) { response ->
            if (!response.isNullOrEmpty()) {
                MyUtils.dismissProgressDialog()
                when (s) {
                    "fb" -> {
                        MyUtils.fbID = fbID
                    }
                    "linkedIN" -> {
                        MyUtils.linkedID = fbID
                    }
                }
                MyUtils.user_Name = userName
                MyUtils.user_Email = userEmail
                if (response[0]?.status.equals("true", true)) {
                    Handler().postDelayed({
                        val i = Intent(this, SelectModeActivity::class.java)
                        i.putExtra("userName", userName)
                        i.putExtra("userEmail", userEmail)
                        i.putExtra("socialID", fbID)
                        startActivity(i)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        finishAffinity()

                    }, 500)

                } else {
                    //No data and no internet
                    isSocial = true
                    MyUtils.showSnackbar(
                        this@SignupActivity, response?.get(0)?.message!!,
                        mainLL
                    )
                }
            } else {
                MyUtils.dismissProgressDialog()
                //No internet and somting went rong
                if (MyUtils.isInternetAvailable(this@SignupActivity)) {
                    MyUtils.showSnackbar(
                        this@SignupActivity,
                        resources.getString(R.string.error_crash_error_message),
                        mainLL
                    )
                } else {
                    MyUtils.showSnackbar(
                        this@SignupActivity,
                        resources.getString(R.string.error_common_network),
                        mainLL
                    )
                }
            }
        }
    }

    fun setOnClickListeners() {
        signupWithLinkedInLL.setOnClickListener(this)
        signupWithInstagramLL.setOnClickListener(this)
        signupWithFacebookLL.setOnClickListener(this)
        signupCreateAccountTv.setOnClickListener(this)
        closeIconSignupIv.setOnClickListener(this)
        tv_termsConditions.setOnClickListener(this)
        language.setOnClickListener(this)
    }

    fun setLinkedinSelected() {
        LinkedinActivity(this, object : LinkedinActivity.LinkedinData {
            override fun LinkedinSuccess(
                email: String?,
                first_name: String?,
                last_name: String?,
                id: String?
            ) {
                Log.e("name", email + " " + id)
                user_Email = email!!
                fbID = id!!
                user_Name = first_name + " " + last_name!!
                setSocialData("linkedIN")
            }

            override fun linkedCancel() {

            }
        }).showLinkedin()

    }

    fun setFaceBookSelected() {
        MyUtils.hideKeyboardFrom(this@SignupActivity, mainLL)

        buttonFacebookLoginScreen?.performClick()
    }

    fun setInstagramSelected() {
        /* signupWithLinkedInLL.background = resources.getDrawable(R.drawable.rounded_edittext)
         signupWithFacebookLL.background = resources.getDrawable(R.drawable.rounded_edittext)
         signupWithInstagramLL.background = resources.getDrawable(R.drawable.rounded_corner_selected)
         signupCreateAccountTv.background = resources.getDrawable(R.drawable.rounded_edittext)
         signupWithLinkedInTv.setTextColor(resources.getColor(R.color.white))
         signupWithFaceBookTv.setTextColor(resources.getColor(R.color.white))
         signupWithInstagramTv.setTextColor(resources.getColor(R.color.black))
         signupCreateAccountTv.setTextColor(resources.getColor(R.color.white))*/
    }

    fun setCreateAccountSelected() {
        signupWithLinkedInLL.background = resources.getDrawable(R.drawable.rounded_edittext)
        signupWithFacebookLL.background = resources.getDrawable(R.drawable.rounded_edittext)
        signupWithInstagramLL.background = resources.getDrawable(R.drawable.rounded_edittext)
        signupCreateAccountTv.background = resources.getDrawable(R.drawable.rounded_corner_selected)
        signupWithLinkedInTv.setTextColor(resources.getColor(R.color.white))
        signupWithFaceBookTv.setTextColor(resources.getColor(R.color.white))
        signupWithInstagramTv.setTextColor(resources.getColor(R.color.white))
        signupCreateAccountTv.setTextColor(resources.getColor(R.color.black))

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.signupWithLinkedInLL -> {
                setLinkedinSelected()
            }
            R.id.signupWithFacebookLL -> {
                setFaceBookSelected()
            }
            R.id.signupWithInstagramLL -> {
                setInstagramSelected()
            }
            R.id.signupCreateAccountTv -> {
                setCreateAccountSelected()
                val i = Intent(this, SelectModeActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.closeIconSignupIv -> {
                MyUtils.startActivity(this, SignInActivity::class.java, false, true)
            }
            R.id.tv_termsConditions -> {
                MyUtils.startActivity(this@SignupActivity, TermsActivity::class.java, false)
            }
            R.id.language -> {
                val dialog =
                    LanguageDialog(this@SignupActivity, languageList, object : LanguageSelection {
                        override fun onLanguageSelect(data: LanguageListData) {
                            if (data.languageName.equals("Automatic", true)) {
                                data.languageCode = Locale.getDefault().language
                                sessionManager!!.setSelectedLanguage(data)
                            }
                            updateLanguage()
                            getLanguageLabelList(data.languageID)
                        }
                    })
                dialog.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateLanguage()
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

    override fun onBackPressed() {
        MyUtils.finishActivity(this, true)
    }

    private fun getLanguageLabelList(languageId: String) {
        MyUtils.showProgressDialog(this@SignupActivity, "Please wait...")
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
        languageLabelModel.getLabels(jsonArray.toString())
        languageLabelModel.usersSuccessLiveData
            .observe(
                this@SignupActivity
            ) { languageLabelPojo: List<LanguageLabelPojo>? ->
                if (!languageLabelPojo.isNullOrEmpty()) {
                    if (languageLabelPojo[0].status == true && !languageLabelPojo[0].data.isNullOrEmpty()) {
                        MyUtils.dismissProgressDialog()
                        sessionManager?.LanguageLabel = languageLabelPojo[0].data?.get(0)!!
                        setupContent()
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

        languageModel.getLanguageList(this@SignupActivity, false, jsonArray.toString())
            .observe(this@SignupActivity) { languageListPojo ->
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
