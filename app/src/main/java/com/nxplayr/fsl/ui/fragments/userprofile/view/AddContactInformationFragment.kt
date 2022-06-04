package com.nxplayr.fsl.ui.fragments.userprofile.view


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CountryListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.CountryListModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModelV2
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.PopupMenu
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_add_contact_information.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class AddContactInformationFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var countryListData: ArrayList<CountryListData>? = ArrayList()
    var countrylist: ArrayList<String>? = ArrayList()
    private lateinit var loginModel: SignupModelV2
    private lateinit var countryListModel: CountryListModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (v == null) {
            v = inflater.inflate(R.layout.fragment_add_contact_information, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)

        tvToolbarTitle.text = getString(R.string.add_contact_information)
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngAddContactInformation.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngAddContactInformation
            if (!sessionManager?.LanguageLabel?.lngMobileNo.isNullOrEmpty())
                tv_mobile_number_edit_text.hint = sessionManager?.LanguageLabel?.lngMobileNo
            if (!sessionManager?.LanguageLabel?.lngEmailAddress.isNullOrEmpty())
                tv_emailId_edit_text.hint = sessionManager?.LanguageLabel?.lngEmailAddress
            if (!sessionManager?.LanguageLabel?.lngAlternativeEmailAddress.isNullOrEmpty())
                tv_alternative_email_edit_text.hint =
                    sessionManager?.LanguageLabel?.lngAlternativeEmailAddress
            if (!sessionManager?.LanguageLabel?.lngWebsite.isNullOrEmpty())
                tv_website_edit_text.hint = sessionManager?.LanguageLabel?.lngWebsite
            if (!sessionManager?.LanguageLabel?.lngCurrentCity.isNullOrEmpty())
                current_city_text.hint = sessionManager?.LanguageLabel?.lngCurrentCity
            if (!sessionManager?.LanguageLabel?.lngCurrentCountry.isNullOrEmpty())
                tv_current_country_edit_text.hint = sessionManager?.LanguageLabel?.lngCurrentCountry
            if (!sessionManager?.LanguageLabel?.lngSave.isNullOrEmpty())
                btn_save_contact.progressText = sessionManager?.LanguageLabel?.lngSave
        }
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
            updateContactInfo()
        }

        setupViewModel()
        setupUI()
    }

    private fun setupObserver() {
        MyUtils.showProgressDialog(mActivity!!, "Please wait..")
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                MyUtils.dismissProgressDialog()
                ErrorUtil.errorMethod(ll_main_contact)
                return@OnCompleteListener
            }
            val token = task.result
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            try {
                jsonObject.put("loginuserID", userData?.userID)
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)
                jsonObject.put("languageID", "1")
                jsonObject.put("userDeviceID", token)
                jsonObject.put("userDeviceType", RestClient.apiType)
                jsonObject.put("userEmail", userData?.userEmail)
                jsonObject.put("userFirstName", userData!!.userFirstName)
                jsonObject.put("userLastName", userData!!.userLastName)
                jsonObject.put("userGender", userData!!.userGender)
                jsonObject.put("userMobile", userData!!.userMobile)
                jsonObject.put("userPassword", userData!!.userPassword)
                jsonObject.put("userCountryCode", userData!!.userCountryCode)
                jsonObject.put("userProfilePicture", userData!!.userProfilePicture)
                jsonObject.put("userReferKey", "")
                if (!userData?.apputypeID.isNullOrEmpty()) {
                    jsonObject.put("apputypeID", userData?.apputypeID)
                } else {
                    jsonObject.put("apputypeID", "0")

                }
                if (!userData?.footbltypeID.isNullOrEmpty()) {
                    jsonObject.put("footbltypeID", userData?.footbltypeID)
                } else {
                    jsonObject.put("footbltypeID", "0")

                }
                if (!userData?.agegroupID.isNullOrEmpty()) {
                    jsonObject.put("agegroupID", userData?.agegroupID)
                } else {
                    jsonObject.put("agegroupID", "0")

                }
                if (!userData?.appuroleID.isNullOrEmpty()) {
                    jsonObject.put("appuroleID", userData?.appuroleID)
                } else {
                    jsonObject.put("appuroleID", "0")

                }
                if (!userData?.appuroleID.isNullOrEmpty()) {
                    jsonObject.put("appuroleID", userData?.appuroleID)
                } else {
                    jsonObject.put("appuroleID", "0")

                }
                if (!userData?.specialityID.isNullOrEmpty()) {
                    jsonObject.put("specialityID", userData?.specialityID)
                } else {
                    jsonObject.put("specialityID", "0")

                }
                if (!userData?.userBestFoot.isNullOrEmpty()) {
                    jsonObject.put("userBestFoot", userData?.userBestFoot)
                } else {
                    jsonObject.put("userBestFoot", "0")

                }
                if (!userData?.userDOB.isNullOrEmpty()) {
                    jsonObject.put("userDOB", userData?.userDOB)
                } else {
                    jsonObject.put("userDOB", "")

                }
                if (!userData?.footballagecatID.isNullOrEmpty()) {
                    jsonObject.put("footballagecatID", userData?.footballagecatID)
                }
                jsonObject.put("userFullname", userData?.userFullname)
                if (!userData?.userBio.isNullOrEmpty()) {
                    jsonObject.put("userBio", userData?.userBio)
                } else {
                    jsonObject.put("userBio", "")

                }
                if (!userData?.userHeight.isNullOrEmpty()) {
                    jsonObject.put("userHeight", userData?.userHeight)
                } else {
                    jsonObject.put("userHeight", "")

                }
                if (!userData?.userWeight.isNullOrEmpty()) {
                    jsonObject.put("userWeight", userData?.userHeight)
                } else {
                    jsonObject.put("userWeight", "")

                }
                jsonObject.put("userCoverPhoto", userData!!.userCoverPhoto)
                jsonObject.put("userAlternateMobile", userData!!.userAlternateMobile)
                jsonObject.put(
                    "userAlternateEmail",
                    alternative_email_edit_text.text.toString().trim()
                )
                jsonObject.put("userWebsite", website_edit_text.text.toString().trim())
                jsonObject.put("userNickname", userData?.userNickname)
                jsonObject.put("clubID", userData?.clubID)
                if (!userData?.footbllevelID.isNullOrEmpty()) {
                    jsonObject.put("footbllevelID", userData?.footbllevelID)
                }
                jsonObject.put("userHomeCountryID", userData?.userHomeCountryID)
                jsonObject.put(
                    "userHomeCountryName",
                    current_country_edit_text.text.toString().trim()
                )
                jsonObject.put("userHomeCityName", current_city_edit_text.text.toString().trim())
                jsonObject.put("userHomeCityID", userData?.userHomeCityID)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            jsonArray.put(jsonObject)
            loginModel.userUpdateProfile(jsonArray.toString())
            loginModel.userUpdateProfile
                .observe(
                    mActivity!!
                ) { loginPojo ->
                    if (loginPojo != null) {
                        if (loginPojo.get(0).status.equals("true", true)) {
                            MyUtils.dismissProgressDialog()
                            try {
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    loginPojo.get(0).message, ll_main_contact
                                )
                                StoreSessionManager(loginPojo[0].data[0])
                                Handler().postDelayed({
                                    (activity as MainActivity).onBackPressed()
                                }, 1000)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            MyUtils.dismissProgressDialog()
                            MyUtils.showSnackbar(
                                mActivity!!,
                                loginPojo.get(0).message, ll_main_contact
                            )
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(ll_main_contact)
                    }
                }
        })
    }

    private fun setupUI() {
        btn_save_contact.setOnClickListener(this)

        textWatchers()
        getCounrtyList()

        Tv_countryCode.setOnClickListener(this)

        if (!mobile_number_edit_text.text.isNullOrEmpty()) {
            var loginType = TextUtils.isDigitsOnly(mobile_number_edit_text?.text.toString())
            if (loginType) {
                Tv_countryCode.visibility = View.VISIBLE
                mobile_number_edit_text.setPadding(
                    resources.getDimensionPixelOffset(R.dimen._60sdp),
                    resources.getDimensionPixelOffset(R.dimen._25sdp),
                    resources.getDimensionPixelOffset(R.dimen._1sdp),
                    resources.getDimensionPixelOffset(R.dimen._10sdp)
                )
                Tv_countryCode.setPadding(
                    resources.getDimensionPixelOffset(R.dimen._14sdp),
                    resources.getDimensionPixelOffset(R.dimen._25sdp),
                    resources.getDimensionPixelOffset(R.dimen._80sdp),
                    resources.getDimensionPixelOffset(R.dimen._10sdp)
                )
                Tv_countryCode.requestFocus()
            } else {
                Tv_countryCode.visibility = View.GONE
                mobile_number_edit_text.setPadding(
                    resources.getDimensionPixelOffset(R.dimen._10sdp),
                    resources.getDimensionPixelOffset(R.dimen._25sdp),
                    resources.getDimensionPixelOffset(R.dimen._1sdp),
                    resources.getDimensionPixelOffset(R.dimen._10sdp)
                )
                mobile_number_edit_text.requestFocus()
            }
        }
        mobile_number_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var loginType = TextUtils.isDigitsOnly(mobile_number_edit_text?.text.toString())
                if (loginType) {
                    Tv_countryCode.visibility = View.VISIBLE
                    mobile_number_edit_text.setPadding(
                        resources.getDimensionPixelOffset(R.dimen._60sdp),
                        resources.getDimensionPixelOffset(R.dimen._25sdp),
                        resources.getDimensionPixelOffset(R.dimen._1sdp),
                        resources.getDimensionPixelOffset(R.dimen._10sdp)
                    )
                    Tv_countryCode.setPadding(
                        resources.getDimensionPixelOffset(R.dimen._14sdp),
                        resources.getDimensionPixelOffset(R.dimen._25sdp),
                        resources.getDimensionPixelOffset(R.dimen._80sdp),
                        resources.getDimensionPixelOffset(R.dimen._10sdp)
                    )
                    Tv_countryCode.requestFocus()
                } else {
                    Tv_countryCode.visibility = View.GONE
                    mobile_number_edit_text.setPadding(
                        resources.getDimensionPixelOffset(R.dimen._10sdp),
                        resources.getDimensionPixelOffset(R.dimen._25sdp),
                        resources.getDimensionPixelOffset(R.dimen._1sdp),
                        resources.getDimensionPixelOffset(R.dimen._10sdp)
                    )
                    mobile_number_edit_text.requestFocus()
                }
                if (p0!!.isEmpty()) {
                    Tv_countryCode.visibility = View.GONE
                    mobile_number_edit_text.setPadding(
                        resources.getDimensionPixelOffset(R.dimen._10sdp),
                        resources.getDimensionPixelOffset(R.dimen._25sdp),
                        resources.getDimensionPixelOffset(R.dimen._1sdp),
                        resources.getDimensionPixelOffset(R.dimen._10sdp)
                    )
                }
                nextButtonEnable()
            }

        })

        var mDrawable = resources.getDrawable(R.drawable.drop_down_arrow_public)
        mDrawable.setColorFilter(
            ContextCompat.getColor(mActivity!!, R.color.colorPrimary),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        Tv_countryCode.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, mDrawable, null)

    }

    private fun setupViewModel() {
        loginModel =
            ViewModelProvider(this@AddContactInformationFragment).get(SignupModelV2::class.java)
        countryListModel = ViewModelProvider(this@AddContactInformationFragment).get(
            CountryListModel::class.java
        )
    }


    private fun updateContactInfo() {
        if (userData != null) {
            Tv_countryCode.text = userData!!.userCountryCode
            mobile_number_edit_text.setText(userData!!.userMobile)
            emailId_edit_text.setText(userData!!.userEmail)
            alternative_email_edit_text.setText(userData!!.userAlternateEmail)
            website_edit_text.setText(userData!!.userWebsite)
            current_city_edit_text.setText(userData!!.userHomeCityName)
            current_country_edit_text.setText(userData!!.userHomeCountryName)
        }
    }

    private fun textWatchers() {
        mobile_number_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }

        })
        emailId_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }

        })
        alternative_email_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }

        })
        website_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }

        })
        current_city_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }

        })
        current_country_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }

        })
    }

    private fun nextButtonEnable() {
        if (validateContactInfo()) {
            btn_save_contact.strokeColor = (resources.getColor(R.color.colorPrimary))
            btn_save_contact.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_save_contact.textColor = resources.getColor(R.color.black)
        } else {
            btn_save_contact.strokeColor = (resources.getColor(R.color.grayborder))
            btn_save_contact.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_save_contact.textColor = resources.getColor(R.color.colorPrimary)
        }
    }

    private fun validateContactInfo(): Boolean {
        var valid: Boolean = false
        when {
            mobile_number_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            emailId_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            alternative_email_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            website_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            current_city_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            current_country_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            else -> {
                valid = true
            }
        }
        return valid
    }

    private fun checkValidation() {

        if (TextUtils.isEmpty(mobile_number_edit_text.text.toString().trim())) {
            context?.let { MyUtils.showSnackbar(it, "Please Enter Mobile Number", ll_main_contact) }
            mobile_number_edit_text.requestFocus()
        } else if (TextUtils.isEmpty(emailId_edit_text.text.toString().trim())) {
            context?.let { MyUtils.showSnackbar(it, "Please Enter Email Address", ll_main_contact) }
            emailId_edit_text.requestFocus()
        } else if (TextUtils.isEmpty(alternative_email_edit_text.text.toString().trim())) {
            context?.let {
                MyUtils.showSnackbar(
                    it,
                    "Please Enter Alternative Email Address",
                    ll_main_contact
                )
            }
            alternative_email_edit_text.requestFocus()
        } else if (TextUtils.isEmpty(website_edit_text.text.toString().trim())) {
            context?.let { MyUtils.showSnackbar(it, "Please Enter Website", ll_main_contact) }
            website_edit_text.requestFocus()

        } else if (TextUtils.isEmpty(current_city_edit_text.text.toString().trim())) {
            context?.let { MyUtils.showSnackbar(it, "Please Enter Current City", ll_main_contact) }
            current_city_edit_text.requestFocus()
        } else if (TextUtils.isEmpty(current_country_edit_text.text.toString().trim())) {
            context?.let {
                MyUtils.showSnackbar(
                    it,
                    "Please Enter Current Country",
                    ll_main_contact
                )
            }
            current_country_edit_text.requestFocus()
        } else {
            setupObserver()
        }

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
        countryListModel.getCountryList(mActivity!!, false, jsonArray.toString())
            .observe(
                viewLifecycleOwner,
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
                    } else {
                        ErrorUtil.errorMethod(ll_main_contact)
                    }
                })


    }


    private fun StoreSessionManager(uesedata: SignupData?) {

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


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_save_contact -> {
                checkValidation()
            }
            R.id.Tv_countryCode -> {
                PopupMenu(mActivity!!, v, countrylist!!).showPopUp(object :
                    PopupMenu.OnMenuSelectItemClickListener {
                    override fun onItemClick(item: String, pos: Int) {
                        Tv_countryCode.text = item
                    }
                })
            }

        }
    }

}

