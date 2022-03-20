package com.nxplayr.fsl.ui.activity.onboarding.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_reset_pass.*
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.activity_signup_selection.*
import kotlinx.android.synthetic.main.layout_introduce_yourself.*
import org.json.JSONArray
import org.json.JSONObject

class ResetPassActivity : AppCompatActivity(),View.OnClickListener {

    var sessionManager: SessionManager? = null
    var loginuserID: String = ""

    private lateinit var  resetPasswordModel: SignupModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass)

        sessionManager = SessionManager(this@ResetPassActivity)

        if (intent != null && intent.hasExtra("userID")) {
            loginuserID = intent.getStringExtra("userID")!!
        }

        setupViewModel()
        setupUI()

    }

    private fun setupUI() {
        btn_resetPass.setOnClickListener (this)

        new_password_textInput.isHintAnimationEnabled = false
        retypenew_password_textInput.isHintAnimationEnabled = false
        new_password_edit_text.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= new_password_edit_text.getRight() - new_password_edit_text.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) {
                        // your action here
                        new_password_edit_text.tooglePassWord()

                        return true
                    }
                }
                return false
            }
        })
        reenter_password_edit_text.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= reenter_password_edit_text.getRight() - reenter_password_edit_text.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) {
                        // your action here
                        reenter_password_edit_text.tooglePassWordReenter()

                        return true
                    }
                }
                return false
            }
        })

    }

    private fun setupViewModel() {
        resetPasswordModel=ViewModelProvider(this@ResetPassActivity).get(SignupModel::class.java)

    }

    fun EditText.tooglePassWord() {
        this.tag = !((this.tag ?: false) as Boolean)
        this.inputType = if (this.tag as Boolean)
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        else
            (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)

        if (this.tag as Boolean) {
            new_password_edit_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_icon_login, 0)
        } else {
            new_password_edit_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_icon_login, 0)
        }

        this.setSelection(this.length())
    }

    fun EditText.tooglePassWordReenter() {
        this.tag = !((this.tag ?: false) as Boolean)
        this.inputType = if (this.tag as Boolean)
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        else
            (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)

        if (this.tag as Boolean) {
            reenter_password_edit_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_icon_login, 0)
        } else {
            reenter_password_edit_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_icon_login, 0)
        }

        this.setSelection(this.length())
    }


    private fun resetPassValidation() {

        MyUtils.hideKeyboard1(this!!)
        if (TextUtils.isEmpty(new_password_edit_text.text.toString())) {
            MyUtils.showSnackbar(this@ResetPassActivity, getString(R.string.psswordnewmsg), ll_main_resetPass)

        } else if (!MyUtils.isValidPassword(new_password_edit_text.text.toString().trim())) {
            MyUtils.showSnackbar(this@ResetPassActivity, getString(R.string.passwordValidation), ll_main_resetPass)

        } else if (TextUtils.isEmpty(reenter_password_edit_text.text.toString())) {
            MyUtils.showSnackbar(this@ResetPassActivity, getString(R.string.psswordresetpemsg), ll_main_resetPass)

        } else if (!MyUtils.isValidPassword(reenter_password_edit_text.text.toString().trim())) {
            MyUtils.showSnackbar(this@ResetPassActivity, getString(R.string.passwordValidation), ll_main_resetPass)

        } else if (!new_password_edit_text.text.toString().trim().equals(reenter_password_edit_text.text.toString().trim())) {
            MyUtils.showSnackbar(this@ResetPassActivity, getString(R.string.resetpassword_does_not_match), ll_main_resetPass)

        } else {
            resetPassApi()
        }
    }

    private fun resetPassApi() {

        btn_resetPass.startAnimation()
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        jsonObject.put("loginuserID", loginuserID)
        jsonObject.put("userNewPassword", new_password_edit_text.text.toString().trim())
        jsonObject.put("languageID", "1")
        jsonObject.put("apiType", RestClient.apiType)
        jsonObject.put("apiVersion", RestClient.apiVersion)


        jsonArray.put(jsonObject)
        resetPasswordModel.userRegistration(this!!, false, "" + jsonArray.toString(), "resetPassword")
                .observe(this, androidx.lifecycle.Observer { loginPojo ->

                    if (loginPojo != null && loginPojo.isNotEmpty()) {

                        if (loginPojo[0].status.equals("true", true)) {
                            btn_resetPass.endAnimation()

                            MyUtils.showSnackbar(
                                    this@ResetPassActivity,
                                    loginPojo[0].message!!,
                                    ll_main_resetPass
                            )

                            val mThread = Thread(Runnable {
                                try {
                                    Thread.sleep(1000)
                                    val i = Intent(this, SignInActivity::class.java)
                                    startActivity(i)
                                    finish()
                                    overridePendingTransition(
                                            R.anim.slide_in_right,
                                            R.anim.slide_out_left
                                    )

                                } catch (e: InterruptedException) {
                                }
                            })
                            mThread.start()

                        } else {
                            btn_resetPass.endAnimation()
                            MyUtils.showSnackbar(
                                    this@ResetPassActivity,
                                    loginPojo[0].message!!,
                                    ll_main_resetPass
                            )
                        }


                    } else {
                        btn_resetPass.endAnimation()
                        ErrorUtil.errorMethod(ll_main_resetPass)

                    }


                })

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_resetPass->{
                resetPassValidation()


            }
        }
    }


}
