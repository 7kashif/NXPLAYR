package com.nxplayr.fsl.ui.fragments.setting.view


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.activity.onboarding.view.OtpVerificationActivity

import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.onboarding.view.SignInActivity
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.activity_reset_pass.*
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.fragment_add_hashtag.*
import kotlinx.android.synthetic.main.fragment_add_languages.*
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ChangePasswordFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: Activity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    private lateinit var  changePasswordModel: SignupModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_change_password, container, false)

        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        setupViewModel()
        setupUI()
        setupObserver()



    }

    private fun setupObserver() {
        btn_change_password.startAnimation()

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", "1")
            jsonObject.put("userCurrentPassword", edittext_current_pass.text.toString().trim())
            jsonObject.put("userNewPassword", edittext_new_pass.text.toString().trim())
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)

        changePasswordModel.userRegistration(mActivity!!, false, jsonArray.toString(), "changePassword")
            .observe(viewLifecycleOwner, { changePassPojo ->

                btn_change_password.endAnimation()

                if (changePassPojo != null && changePassPojo.isNotEmpty()) {
                    if (changePassPojo[0].status.equals("true", true)) {

                        MyUtils.showSnackbar(mActivity!!, changePassPojo[0].message!!, changePass_main_layout)

                        try {

                            sessionManager!!.clear_login_session()

                            Handler().postDelayed({
                                if (changePassPojo[0].data!![0]!!.userOVerified.equals("Yes", true)) {
                                    MyUtils.startActivity(mActivity!!, SignInActivity::class.java, true)
                                    finishAffinity(mActivity!!)

                                } else {
                                    var i = Intent(mActivity!!, OtpVerificationActivity::class.java)
                                    i.putExtra("from", "LoginByOtpVerification")
                                    startActivity(i)
                                }
                            }, 1000)


                        } catch (e: Exception) {

                        }

                    } else {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            changePassPojo[0].message!!,
                            changePass_main_layout
                        )
                    }


                } else {
                    ErrorUtil.errorMethod(changePass_main_layout)

                }

            })

    }

    private fun setupUI() {
        tvToolbarTitle.setText(R.string.change_password)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }

        btn_change_password.setOnClickListener (this)

        current_password_textInput.isHintAnimationEnabled = false
        newPassword_textInput.isHintAnimationEnabled = false
        confirm_password_textInput.isHintAnimationEnabled = false
        textWatcher()
        edittext_current_pass.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= edittext_current_pass.getRight() - edittext_current_pass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) {
                        // your action here
                        edittext_current_pass.tooglePassWord()

                        return true
                    }
                }
                return false
            }
        })
        edittext_new_pass.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= edittext_current_pass.getRight() - edittext_new_pass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) {
                        // your action here
                        edittext_new_pass.toogleNewPassWord()

                        return true
                    }
                }
                return false
            }
        })
        edittext_confirm_pass.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= edittext_current_pass.getRight() - edittext_confirm_pass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) {
                        // your action here
                        edittext_confirm_pass.toogleConfirmPassWord()

                        return true
                    }
                }
                return false
            }
        })
    }

    private fun setupViewModel() {
         changePasswordModel = ViewModelProvider(this@ChangePasswordFragment).get(SignupModel::class.java)

    }

    fun EditText.tooglePassWord() {
        this.tag = !((this.tag ?: false) as Boolean)
        this.inputType = if (this.tag as Boolean)
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        else
            (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)

        if (this.tag as Boolean) {
            edittext_current_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_icon_login, 0)
        } else {
            edittext_current_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_icon_login, 0)
        }

        this.setSelection(this.length())
    }

    fun EditText.toogleNewPassWord() {
        this.tag = !((this.tag ?: false) as Boolean)
        this.inputType = if (this.tag as Boolean)
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        else
            (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)

        if (this.tag as Boolean) {
            edittext_new_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_icon_login, 0)
        } else {
            edittext_new_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_icon_login, 0)
        }

        this.setSelection(this.length())
    }

    fun EditText.toogleConfirmPassWord() {
        this.tag = !((this.tag ?: false) as Boolean)
        this.inputType = if (this.tag as Boolean)
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        else
            (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)

        if (this.tag as Boolean) {
            edittext_confirm_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_icon_login, 0)
        } else {
            edittext_confirm_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_icon_login, 0)
        }

        this.setSelection(this.length())
    }

    fun textWatcher() {
        edittext_current_pass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }

        })
        edittext_new_pass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }

        })
        edittext_confirm_pass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }

        })
    }

    fun nextButtonEnable() {
        if (validateChangePass()) {
            btn_change_password.strokeColor = (resources.getColor(R.color.colorPrimary))
            btn_change_password.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_change_password.textColor = resources.getColor(R.color.black)
        } else {
            btn_change_password.strokeColor = (resources.getColor(R.color.grayborder))
            btn_change_password.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_change_password.textColor = resources.getColor(R.color.colorPrimary)
        }
    }

    fun validateChangePass(): Boolean {
        var valid: Boolean = false
        when {
            edittext_current_pass.text.toString().trim().isEmpty() -> {
                return valid
            }
            edittext_new_pass.text.toString().trim().isEmpty() -> {
                return valid
            }
            edittext_confirm_pass.text.toString().trim().isEmpty() -> {
                return valid
            }
            else -> {
                valid = true
            }
        }
        return valid
    }

    private fun changePasswordValidation() {
        MyUtils.hideKeyboard1(mActivity!!)
        if (TextUtils.isEmpty(edittext_current_pass.text.toString().trim())) {
            context?.let { MyUtils.showSnackbar(it, resources.getString(R.string.enter_current_password), changePass_main_layout) }
            edittext_current_pass.requestFocus()
        } else if (TextUtils.isEmpty(edittext_new_pass.text.toString().trim())) {
            context?.let { MyUtils.showSnackbar(it, resources.getString(R.string.enter_new_password), changePass_main_layout) }
            edittext_new_pass.requestFocus()
        } else if (edittext_new_pass.text.toString().trim().length < 6 || !MyUtils.isValidPassword(
                        edittext_new_pass.text.toString().trim())) {
            context?.let { MyUtils.showSnackbar(it, resources.getString(R.string.password_should_contain_six_character), changePass_main_layout) }
            edittext_new_pass.requestFocus()
        } else if (edittext_current_pass.text.toString().trim().equals(edittext_new_pass.text.toString().trim())) {
            context?.let { MyUtils.showSnackbar(it, resources.getString(R.string.new_password_should_not_same), changePass_main_layout) }
            edittext_new_pass.requestFocus()
        } else if (TextUtils.isEmpty(edittext_confirm_pass.text.toString().trim())) {
            context?.let { MyUtils.showSnackbar(it, getString(R.string.enter_confirm_new_pass), changePass_main_layout) }
            edittext_confirm_pass.requestFocus()

        } else if (!edittext_new_pass.text.toString().trim().equals(edittext_confirm_pass.text.toString().trim())) {
            context?.let { MyUtils.showSnackbar(it, resources.getString(R.string.new_password_retypepass_not_match), changePass_main_layout) }
            edittext_confirm_pass.requestFocus()

        } else {
            setupObserver()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btn_change_password->{
                changePasswordValidation()
            }

        }
    }


}
