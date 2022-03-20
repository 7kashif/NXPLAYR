package com.nxplayr.fsl.ui.fragments.userlanguage.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.ui.activity.main.view.MainActivity

import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.viewmodel.*
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import com.nxplayr.fsl.data.model.LanguageList
import com.nxplayr.fsl.data.model.ProficiencyData
import com.nxplayr.fsl.data.model.ProfileLanguageData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.bottomsheet.BottomSheetListFragment
import com.nxplayr.fsl.ui.fragments.userlanguage.viewmodel.LanguagesModel
import com.nxplayr.fsl.ui.fragments.userlanguage.viewmodel.ProficiencyModel
import com.nxplayr.fsl.ui.fragments.userlanguage.viewmodel.ProfileLanguageModel
import kotlinx.android.synthetic.main.fragment_add_languages.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList

class AddLanguageFragment : Fragment(), BottomSheetListFragment.SelectLanguage,View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var languageList: ArrayList<LanguageList>? = ArrayList()
    var proficiencyList: ArrayList<ProficiencyData>? = ArrayList()
    var profileLanguageData: ArrayList<ProfileLanguageData>? = ArrayList()
    var languageID: String = ""
    var proficiencyID: String = ""
    private var languageUpdateListener: LanguageUpdateListener? = null
    var position: Int = 0
    var languageData: ProfileLanguageData? = null
    var from = ""
    private lateinit var  languagesModel: LanguagesModel
    private lateinit var addLanguageModel: ProfileLanguageModel
    private lateinit var profileLanguageModel: ProficiencyModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_add_languages, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        try {
            languageUpdateListener = context as LanguageUpdateListener
        } catch (e: Exception) {

        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (arguments != null) {
            from = arguments!!.getString("from").toString()
            position = arguments!!.getInt("pos")
            languageData = arguments!!.getSerializable("userlanlID") as ProfileLanguageData
        }

        setupViewModel()
        setupUI()

    }

    private fun setupObserver(from: String, userlanguageID: String) {
        btn_add_language.startAnimation()

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {

            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageName", language_edit_text.text.toString().trim())
            jsonObject.put("profiencyID", proficiencyID)
            jsonObject.put("profiencyName", proficiency_edit_text.text.toString().trim())
            jsonObject.put("languageID", languageID)
            if (languageData != null) {
                jsonObject.put("userlanguageID", userlanguageID)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        addLanguageModel.getLanguageList(mActivity!!, false, jsonArray.toString(), from)
            .observe(this@AddLanguageFragment!!,
                androidx.lifecycle.Observer { loginPojo ->
                    if (loginPojo != null) {
                        btn_add_language.endAnimation()
                        if (loginPojo.get(0).status.equals("true", false)) {
                            try {

//                                        userData?.languages?.clear()

                                if (from.equals("Add")) {
                                    userData?.languages?.addAll(loginPojo.get(0).data)
                                    StoreSessionManager(userData)
                                } else if (from.equals("Edit")) {
                                    for (i in 0 until userData?.languages!!.size) {
                                        if (userlanguageID.equals(userData!!.languages!![i]!!.userlanguageID))
                                            userData!!.languages!![i] = loginPojo!![0].data!![0]
                                        break
                                    }
                                    StoreSessionManager(userData)
                                    if (languageUpdateListener != null)
                                        languageUpdateListener!!.onLanguageUpdate()
                                }

                                Handler().postDelayed({
                                    (activity as MainActivity).onBackPressed()
                                }, 2000)

                                MyUtils.showSnackbar(mActivity!!, loginPojo.get(0).message, main_add_languages)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        } else {
                            MyUtils.showSnackbar(mActivity!!, loginPojo.get(0).message, main_add_languages)
                        }

                    } else {
                        btn_add_language.endAnimation()
                        ErrorUtil.errorMethod(main_add_languages)
                    }
                })

    }

    private fun setupUI() {
        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()

        }
        if (from.equals("edit")) {
            tvToolbarTitle.setText(getString(R.string.edit_languages))
            btn_add_language.progressText = resources.getString(R.string.update)
        } else {
            tvToolbarTitle.setText(getString(R.string.add_languages))
            btn_add_language.progressText = resources.getString(R.string.save)
        }

        setLanguageData()

        proficiency_edit_text.setOnClickListener(this)
        language_edit_text.setOnClickListener(this)

        btn_add_language.setOnClickListener(this)

        language_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()

            }
        })
        proficiency_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }
        })

    }

    private fun setupViewModel() {
      languagesModel = ViewModelProvider(this@AddLanguageFragment).get(LanguagesModel::class.java)
      addLanguageModel = ViewModelProvider(this@AddLanguageFragment).get(ProfileLanguageModel::class.java)
      profileLanguageModel = ViewModelProvider(this@AddLanguageFragment).get(ProficiencyModel::class.java)

    }

    fun setLanguageData() {
        if (from.equals("edit") && !userData!!.languages.isNullOrEmpty()) {
            if (languageData != null) {
                language_edit_text.setText(languageData!!.languageName)
                proficiency_edit_text.setText(languageData!!.profiencyName)
                languageID = languageData!!.languageID
                proficiencyID = languageData!!.profiencyID
            }
            btn_add_language.strokeColor = (resources.getColor(R.color.colorPrimary))
            btn_add_language.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_add_language.textColor = resources.getColor(R.color.black)
        }
    }

    fun nextButtonEnable() {
        if (validateAddLanguage()) {
            btn_add_language.strokeColor = (resources.getColor(R.color.colorPrimary))
            btn_add_language.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_add_language.textColor = resources.getColor(R.color.black)
        } else {
            btn_add_language.strokeColor = (resources.getColor(R.color.grayborder))
            btn_add_language.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_add_language.textColor = resources.getColor(R.color.colorPrimary)
        }
    }

    fun validateAddLanguage(): Boolean {
        var valid: Boolean = false
        when {
            language_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            proficiency_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            else -> {
                valid = true
            }
        }
        return valid
    }

    private fun checkValidation() {
        MyUtils.hideKeyboard1(mActivity!!)
        if (TextUtils.isEmpty(language_edit_text.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Enter Language", main_add_languages)
            language_edit_text.requestFocus()
        } else if (TextUtils.isEmpty(proficiency_edit_text.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Enter Proficiency", main_add_languages)
            proficiency_edit_text.requestFocus()
        } else {
            if (languageData != null) {
                setupObserver("Edit", languageData!!.userlanguageID)
            } else {
                setupObserver("Add", "")
            }
        }
    }


    private fun languageListApi() {
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
      languagesModel.getLanguageist(mActivity!!, false, jsonArray.toString())
                .observe(this@AddLanguageFragment!!,
                    { languageListpojo ->
                        if (languageListpojo != null && languageListpojo.isNotEmpty()) {

                            if (languageListpojo[0].status.equals("true", false)) {
                                MyUtils.dismissProgressDialog()

                                languageList!!.clear()
                                languageList!!.addAll(languageListpojo[0].data)
                                openLanguageList(languageList!!)
                            } else {
                                MyUtils.dismissProgressDialog()
                                MyUtils.showSnackbar(mActivity!!, languageListpojo[0].message, main_add_languages)
                            }

                        } else {
                            MyUtils.dismissProgressDialog()
                            ErrorUtil.errorMethod(main_add_languages)
                        }
                    })


    }

    private fun proficiencyListApi() {
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("searchWord", "")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        profileLanguageModel.getProficiencyList(mActivity!!, false, jsonArray.toString())
                .observe(this@AddLanguageFragment!!,
                        androidx.lifecycle.Observer { proficiencyListpojo ->
                            if (proficiencyListpojo != null && proficiencyListpojo.isNotEmpty()) {

                                if (proficiencyListpojo[0].status.equals("true", false)) {
                                    MyUtils.dismissProgressDialog()

                                    proficiencyList!!.clear()
                                    proficiencyList!!.addAll(proficiencyListpojo[0].data)
                                    openProficiencyList(proficiencyList!!)
                                } else {
                                    MyUtils.dismissProgressDialog()
                                    MyUtils.showSnackbar(mActivity!!, proficiencyListpojo[0].message, main_add_languages)
                                }

                            } else {
                                MyUtils.dismissProgressDialog()
                                Toast.makeText(mActivity!!,R.string.error_common_network, Toast.LENGTH_SHORT).show()
                            }
                        })


    }

    private fun openLanguageList(data: ArrayList<LanguageList>) {

        var bottomSheetData = ArrayList<String>()
        bottomSheetData.clear()
        for (i in 0 until data.size) {
            bottomSheetData.add(data[i]!!.languageName!!)
        }

        val bottomSheet = BottomSheetListFragment()
        val bundle = Bundle()
        bundle.putString("from", "Language")
        bundle.putSerializable("data", bottomSheetData)
        bottomSheet.arguments = bundle
        bottomSheet.show(childFragmentManager!!, "List")
    }

    private fun openProficiencyList(data: ArrayList<ProficiencyData>) {

        var bottomSheetData = ArrayList<String>()
        bottomSheetData.clear()
        for (i in 0 until data.size) {
            bottomSheetData.add(data[i]!!.profiencyName!!)
        }

        val bottomSheet = BottomSheetListFragment()
        val bundle = Bundle()
        bundle.putString("from", "ProficiencyList")
        bundle.putSerializable("data", bottomSheetData)
        bottomSheet.arguments = bundle
        bottomSheet.show(childFragmentManager!!, "List")
    }

    override fun onLanguageSelect(value: String, from: String) {
        when (from) {
            "Language" -> {
                language_edit_text.setText(value)
                for (i in 0 until languageList!!.size) {
                    if (value.equals(languageList!![i]!!.languageName, false)) {
                        if (!languageID.equals(languageList!![i]!!.languageID))
                            languageID = languageList!![i]!!.languageID
                    }
                }
            }
            "ProficiencyList" -> {
                proficiency_edit_text.setText(value)
                for (i in 0 until proficiencyList!!.size) {
                    if (value.equals(proficiencyList!![i]!!.profiencyName, false)) {
                        if (!proficiencyID.equals(proficiencyList!![i]!!.profiencyID))
                            proficiencyID = proficiencyList!![i]!!.profiencyID
                    }
                }
            }
        }
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


    interface LanguageUpdateListener {
        fun onLanguageUpdate()
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.proficiency_edit_text->{
                proficiencyListApi()
            }
            R.id.language_edit_text->{
                languageListApi()
            }
            R.id.btn_add_language->{
                checkValidation()
            }
        }
    }
}
