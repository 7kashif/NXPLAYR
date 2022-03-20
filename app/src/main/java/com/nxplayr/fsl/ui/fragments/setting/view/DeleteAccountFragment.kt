package com.nxplayr.fsl.ui.fragments.setting.view


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.intro.view.SplashActivity
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_delete_account.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject


class DeleteAccountFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    private lateinit var  deleteAccModel: CommonStatusModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.fragment_delete_account, container, false)
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
    }

    private fun setupObserver() {
        btn_deleteAccount.startAnimation()

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("userCurrentPassword", edit_currentPass.text.toString().trim())
            jsonObject.put("languageID", "1")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        deleteAccModel.getCommonStatus(mActivity!!, false, jsonArray.toString(), "deleteAccount")
            .observe(viewLifecycleOwner, Observer { commonStatusPojo ->
                btn_deleteAccount.endAnimation()

                if (commonStatusPojo != null && commonStatusPojo.isNotEmpty()) {
                    if (commonStatusPojo[0].status.equals("true", true)) {

                        try {
                            sessionManager?.clear_login_session()
                            MyUtils.startActivity(activity!!, SplashActivity::class.java, true)
                        } catch (e: Exception) {
                        }

                    } else {
                        MyUtils.showSnackbar(mActivity!!, commonStatusPojo[0].message!!, ll_mainDeleteAccount)
                    }

                } else {
                    ErrorUtil.errorMethod(ll_mainDeleteAccount)

                }
            })
    }

    private fun setupUI() {
        tvToolbarTitle.setText(R.string.delete_account)
        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }
        btn_deleteAccount.strokeColor = (resources.getColor(R.color.colorPrimary))
        btn_deleteAccount.backgroundTint = (resources.getColor(R.color.colorPrimary))
        btn_deleteAccount.textColor = resources.getColor(R.color.black)
        edit_currentPass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                btn_deleteAccount.strokeColor = (resources.getColor(R.color.colorPrimary))
                btn_deleteAccount.backgroundTint = (resources.getColor(R.color.colorPrimary))
                btn_deleteAccount.textColor = resources.getColor(R.color.black)
                if (p0!!.isEmpty()) {
                    btn_deleteAccount.strokeColor = (resources.getColor(R.color.grayborder))
                    btn_deleteAccount.backgroundTint = (resources.getColor(R.color.transperent1))
                    btn_deleteAccount.textColor = resources.getColor(R.color.colorPrimary)
                }
            }

        })
        edit_currentPass.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= edit_currentPass.getRight() - edit_currentPass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) {
                        // your action here
                        edit_currentPass.tooglePassWord()

                        return true
                    }
                }
                return false
            }
        })
        btn_deleteAccount.setOnClickListener (this)
    }

    private fun setupViewModel() {
         deleteAccModel = ViewModelProvider(this@DeleteAccountFragment).get(CommonStatusModel::class.java)

    }

    fun EditText.tooglePassWord() {
        this.tag = !((this.tag ?: false) as Boolean)
        this.inputType = if (this.tag as Boolean)
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        else
            (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)

        if (this.tag as Boolean) {
            edit_currentPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_icon_login, 0)
        } else {
            edit_currentPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_icon_login, 0)
        }

        this.setSelection(this.length())
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
           R.id.btn_deleteAccount->{
               MyUtils.hideKeyboard1(mActivity!!)
               if (TextUtils.isEmpty(edit_currentPass.text.toString().trim())) {
                   context?.let { MyUtils.showSnackbar(it, resources.getString(R.string.enter_current_password), ll_mainDeleteAccount) }
                   edit_currentPass.requestFocus()
               } else if (!sessionManager!!.get_Password().isNullOrEmpty() && !sessionManager!!.get_Password().equals(edit_currentPass.text.toString().trim())) {
                   context?.let { MyUtils.showSnackbar(it, resources.getString(R.string.please_enter_valid_password), ll_mainDeleteAccount) }
                   edit_currentPass.requestFocus()
               } else {

                   MyUtils.showMessageYesNoRound(
                       activity!!,
                       "Are you sure to delete your account?",
                       "Yes",
                       "No",
                       { setupObserver() },
                       false, "Alert"
                   )

               }
           }

        }
    }


}
