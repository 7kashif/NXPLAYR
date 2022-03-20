package com.nxplayr.fsl.ui.activity.onboarding.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.data.model.SignupPojo
import com.nxplayr.fsl.util.LinkedinActivity
import com.nxplayr.fsl.util.MyUtils
import com.google.gson.JsonParseException
import com.nxplayr.fsl.R
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONArray
import org.json.JSONObject


class SignupActivity : AppCompatActivity(), View.OnClickListener {


    var fbID = ""
    var isSocial = false
    var user_Name = ""
    var user_Email = ""
    private lateinit var callbackManager: CallbackManager
    private lateinit var  signup: SignupModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        callbackManager = CallbackManager.Factory.create()
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        setOnClickListeners()

        buttonFacebookLoginScreen?.setReadPermissions("public_profile","email")
        buttonFacebookLoginScreen?.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                LoginManager.getInstance().logOut()
                val request = GraphRequest.newMeRequest(loginResult.accessToken) { jsonObj, response ->
                    try {
                        user_Email = jsonObj.opt("email").toString()
                        fbID = jsonObj.opt("id").toString()
                        user_Name = jsonObj.opt("name").toString()
                        if(!fbID.isNullOrEmpty() && !user_Email.isNullOrEmpty()){
                            isSocial=true
                            setSocialData("fb")
                            /*FirebaseInstanceId.getInstance().instanceId
                                    .addOnSuccessListener(object : OnSuccessListener<InstanceIdResult> {
                                        override fun onSuccess(instanceIdResult: InstanceIdResult) {
                                            val instanceId = instanceIdResult.id
                                            var newtoken = instanceIdResult.token
                                            checkForDuplication(user_Email, fbID, user_Name, 1, newtoken)
                                        }
                                    })*/
                        }else {
                            isSocial=true
                            MyUtils.showMessageOK(this@SignupActivity, "your privacy setting in facebook is not allowing us to acccess your data, please try another account or change your privacy settings for CamFire.", object : DialogInterface.OnClickListener{
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
                            isSocial=false
                            // setSocialData()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
//                        dismissDialogLogin()
                        isSocial=false
                        // setSocialData()
                    }
                }

                val parameters = Bundle()
                parameters.putString("fields", "name,email,id,picture.type(large)")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                Toast.makeText(baseContext, "facebook:onCancel.",
                    Toast.LENGTH_SHORT).show()
                isSocial=false
                // setSocialData()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(baseContext, "facebook:onError.",
                    Toast.LENGTH_SHORT).show()
                isSocial=false
                //setSocialData()

            }
        })
    }

    private fun setupViewModel() {
       signup = ViewModelProvider(this@SignupActivity).get(SignupModel::class.java)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun setSocialData(s: String) {
         if(!user_Email.isNullOrEmpty())
         {
            getCheckUser(user_Email,user_Name,s)
         }
        else{
             MyUtils.showSnackbar(this@SignupActivity, "Email address invalid", mainLL)

         }

    }

    private fun getCheckUser(userEmail: String, userName: String, s: String) {
       MyUtils.showProgressDialog(this@SignupActivity,"Wait..")
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

        signup.userRegistration(this@SignupActivity,false, jsonArray.toString(), "checkDublication")
                .observe(this@SignupActivity,
                    { response ->
                        if (!response.isNullOrEmpty()) {
                            MyUtils.dismissProgressDialog()
                            when(s)
                            {
                                "fb"->{
                                    MyUtils.fbID=fbID
                                }
                                "linkedIN"->{
                                    MyUtils.linkedID=fbID
                                }
                            }
                            MyUtils.user_Name=userName
                            MyUtils.user_Email=userEmail
                            if (response[0]?.status.equals("true", true)) {
                                Handler().postDelayed({
                                    val i = Intent(this, SelectModeActivity::class.java)
                                    i.putExtra("userName",userName)
                                    i.putExtra("userEmail",userEmail)
                                    i.putExtra("socialID",fbID)
                                    startActivity(i)
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                                    finishAffinity()

                                }, 500)

                           } else {
                                //No data and no internet
                                isSocial = true
                                MyUtils.showSnackbar(
                                        this@SignupActivity,response?.get(0)?.message!!,
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
                    })
    }

    fun setOnClickListeners() {
        signupWithLinkedInLL.setOnClickListener(this)
        signupWithInstagramLL.setOnClickListener(this)
        signupWithFacebookLL.setOnClickListener(this)
        signupCreateAccountTv.setOnClickListener(this)
        closeIconSignupIv.setOnClickListener(this)
        tv_termsConditions.setOnClickListener (this)

    }

    fun setLinkedinSelected() {
        LinkedinActivity(this, object : LinkedinActivity.LinkedinData {
            override fun LinkedinSuccess(email: String?, first_name: String?, last_name: String?, id: String?) {
                Log.e("name",email+" "+id)
                user_Email = email!!
                fbID = id!!
                user_Name = first_name+" "+last_name!!
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
        }
    }

    override fun onBackPressed() {
        MyUtils.finishActivity(this, true)
    }
}
