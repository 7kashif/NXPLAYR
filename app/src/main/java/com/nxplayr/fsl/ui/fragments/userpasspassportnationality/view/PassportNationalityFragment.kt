package com.nxplayr.fsl.ui.fragments.userpasspassportnationality.view


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userpasspassportnationality.adapter.NationalityAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userpasspassportnationality.viewmodel.AddPassportNationalityCallsViewModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.CountryListModel
import com.nxplayr.fsl.ui.fragments.userpasspassportnationality.viewmodel.DeletePasportNationality
import com.nxplayr.fsl.ui.fragments.userpasspassportnationality.viewmodel.PassportNationalityModel
import com.nxplayr.fsl.data.model.CountryListData
import com.nxplayr.fsl.data.model.PassportNationality
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_comment_main.*
import kotlinx.android.synthetic.main.fragment_passport_nationality.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class PassportNationalityFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: Activity? = null
    var nationalityAdapter: NationalityAdapter? = null
    var nationalityDataList: ArrayList<CountryListData>? = null
    var addnationalityDataList: ArrayList<PassportNationality>? = ArrayList()
    private lateinit var linearLayoutManager: LinearLayoutManager
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var pageNo = 0
    var countryList: ArrayList<CountryListData>? = ArrayList()
    var pageSize = 20
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var firstVisibleItemPosition: Int = 0
    var y: Int = 0
    var countryId: String? = null
    var countryName: String? = null
    var data = 0
    var fromProfile = ""
    var userId = ""
    var otherUserData: SignupData? = null
    private lateinit var   countryListModel : CountryListModel
    private lateinit var   addpassportNationalityModel : AddPassportNationalityCallsViewModel
    private lateinit var   deletePasportNationality : DeletePasportNationality
    private lateinit var   passportNationalityModel : PassportNationalityModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_passport_nationality, container, false)
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
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (arguments != null)
        {
            fromProfile = arguments!!.getString("fromProfile", "")
            userId = arguments!!.getString("userId", "")
            if (!userId.equals(userData?.userID, false)) {
                otherUserData = arguments!!.getSerializable("otherUserData") as SignupData?
            }
        }

        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle.text = resources.getString(R.string.passport_nationality)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }
        if (userId.equals(userData?.userID, false))
        {
            btn_addNationality.visibility = View.VISIBLE
        } else {
            btn_addNationality.visibility = View.GONE
        }

        search_nationality.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                nationalityList(search_nationality.text.toString().trim())

            }

        })

        btn_addNationality.setOnClickListener(this)

        linearLayoutManager = LinearLayoutManager(mActivity!!)
        if (nationalityDataList == null) {
            nationalityDataList = ArrayList()
            nationalityAdapter =
                NationalityAdapter(mActivity!!, nationalityDataList, object : NationalityAdapter.OnItemClick {
                    override fun onClicled(position: Int, from: String) {
                        if (userId.equals(userData?.userID, false)) {
                            for (i in 0 until nationalityDataList!!.size) {
                                if (i == position) {
                                    nationalityDataList!![i].checked = !(nationalityDataList!![i].checked)
                                    btn_addNationality.backgroundTint = (resources.getColor(R.color.colorPrimary))
                                    btn_addNationality.textColor = resources.getColor(R.color.black)
                                    btn_addNationality.strokeColor = resources.getColor(R.color.colorPrimary)
                                }
                            }
                            if (nationalityDataList!![position].checked) {
                                countryName = nationalityDataList!![position].countryName
                                countryId = nationalityDataList!![position].countryID
                                data++
                            } else {
                                countryName = nationalityDataList!![position].countryName
                                countryId = nationalityDataList!![position].countryID
                                if (!userData?.passport.isNullOrEmpty()) {
                                    deletePassortNationaityApi(countryName!!, countryId!!)
                                }
                            }
                            nationalityAdapter?.notifyDataSetChanged()
                        }

                    }

                })
            recyclerview.layoutManager = linearLayoutManager
            recyclerview.adapter = nationalityAdapter
            nationalityList(search_nationality.text.toString().trim())

        }

        btnRetry.setOnClickListener (this)

    }

    private fun setupViewModel() {
         countryListModel = ViewModelProvider(this@PassportNationalityFragment).get(CountryListModel::class.java)
         addpassportNationalityModel = ViewModelProvider(this@PassportNationalityFragment).get(
             AddPassportNationalityCallsViewModel::class.java)
         deletePasportNationality = ViewModelProvider(this@PassportNationalityFragment).get(
             DeletePasportNationality::class.java)
         passportNationalityModel = ViewModelProvider(this@PassportNationalityFragment).get(
             PassportNationalityModel::class.java)

    }

    private fun nationalityList(searchWord: String) {

        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("blankCountryCode","No")

            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("searchWord", searchWord)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        var jsonArray = JSONArray()
        jsonArray.put(jsonObject)
        countryListModel.getCountryList(mActivity!!, false, jsonArray.toString())
                .observe(viewLifecycleOwner,
                    { passportListPojo ->
                        relativeprogressBar.visibility = View.GONE
                        recyclerview.visibility = View.VISIBLE
                        if (passportListPojo != null) {
                            if (passportListPojo.get(0).status.equals("true", false)) {
                                nationalityDataList?.clear()
                                if (userId.equals(userData?.userID, false)) {
                                    if (userData != null) {
                                        for (i in 0 until userData?.passport!!.size) {
                                            for (j in passportListPojo?.get(0).data.indices) {
                                                if (userData?.passport!![i].countryID.equals(passportListPojo?.get(0).data[j].countryID, false)) {
                                                    passportListPojo?.get(0).data[j].checked = true
                                                }
                                            }
                                        }
                                    }

                                } else {
                                    if (otherUserData != null) {
                                        for (i in 0 until otherUserData?.passport!!.size) {
                                            for (j in passportListPojo?.get(0).data.indices) {
                                                if (otherUserData?.passport!![i].countryID.equals(passportListPojo?.get(0).data[j].countryID, false)) {
                                                    passportListPojo?.get(0).data[j].checked = true
                                                }
                                            }
                                        }
                                    }

                                }
                                nationalityDataList?.addAll(passportListPojo.get(0).data)
                                nationalityAdapter?.notifyDataSetChanged()
                            } else {

                                if (nationalityDataList!!.size == 0) {
                                    ll_no_data_found.visibility = View.VISIBLE
                                    recyclerview.visibility = View.GONE

                                } else {
                                    ll_no_data_found.visibility = View.GONE
                                    recyclerview.visibility = View.VISIBLE

                                }
                            }

                        } else {
                            ErrorUtil.errorMethod(ll_mainPassNationality)
                        }
                    })

    }

    private fun addpassportNationaityApi(s: String) {

        btn_addNationality.startAnimation()
        addpassportNationalityModel.getPassport(mActivity!!, s, countryList, userData?.userID!!,userData?.passport)
                .observe(viewLifecycleOwner,
                    { countryListPojo ->
                        if (countryListPojo != null) {
                            if (countryListPojo.get(0).status.equals("true", false)) {
                                try {
                                    passpoertNationalityList()
                                    Handler().postDelayed({
                                        MyUtils.showSnackbar(mActivity!!, countryListPojo.get(0).message, ll_mainPassNationality)
                                    }, 2000)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            } else {
                                btn_addNationality.endAnimation()
                                MyUtils.showSnackbar(mActivity!!, countryListPojo.get(0).message, ll_mainPassNationality)
                            }

                        } else {
                            btn_addNationality.endAnimation()
                            ErrorUtil.errorMethod(ll_mainPassNationality)
                        }
                    })
    }

    private fun editpassportNationaityApi(s: String, passport: java.util.ArrayList<PassportNationality>?) {

        btn_addNationality.startAnimation()
        addpassportNationalityModel.getPassport(mActivity!!, s, countryList, userData?.userID!!,passport)
                .observe(viewLifecycleOwner,
                    { countryListPojo ->
                        if (countryListPojo != null) {
                            if (countryListPojo.get(0).status.equals("true", false)) {
                                try {
                                    passpoertNationalityList()

                                    Handler().postDelayed({
                                        MyUtils.showSnackbar(mActivity!!, countryListPojo.get(0).message, ll_mainPassNationality)
                                    }, 2000)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            } else {
                                btn_addNationality.endAnimation()
                                MyUtils.showSnackbar(mActivity!!, countryListPojo.get(0).message, ll_mainPassNationality)
                            }

                        } else {
                            btn_addNationality.endAnimation()
                            ErrorUtil.errorMethod(ll_mainPassNationality)
                        }
                    })
    }

    private fun deletePassortNationaityApi(countryName: String, countryID: String) {

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userId)
            jsonObject.put("countryName", countryName)
            jsonObject.put("countryID", countryID)
            for (i in 0 until userData?.passport?.size!!) {
                if (countryID.equals(userData?.passport?.get(i)?.countryID, false)) {
                    jsonObject.put("userpassport", userData?.passport?.get(i)?.userpassport)
                    break
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        deletePasportNationality.getDeleteCollecation(mActivity!!, jsonArray.toString())
                .observe(viewLifecycleOwner,
                    { countryListPojo ->
                        if (countryListPojo != null) {
                            if (countryListPojo.get(0).status.equals("true", false)) {
                                try {
                                    for (i in 0 until userData?.passport?.size!!) {
                                        if (countryID.equals(userData?.passport?.get(i)?.countryID, false)) {
                                            userData?.passport?.removeAt(i)
                                            break
                                        }
                                    }
                                    StoreSessionManager(userData)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }

                        } else {
                            ErrorUtil.errorMethod(ll_mainPassNationality)
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

    private fun passpoertNationalityList() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        var jsonArray = JSONArray()
        jsonArray.put(jsonObject)
        passportNationalityModel.getNationalityList(mActivity!!, false, jsonArray.toString(), "List")
                .observe(viewLifecycleOwner,
                    { passportListPojo ->
                        btn_addNationality.endAnimation()

                        if (passportListPojo != null) {
                            if (passportListPojo[0].status.equals("true", false)) {
                                userData?.passport?.clear()
                                userData?.passport?.addAll(passportListPojo.get(0).data)
                                StoreSessionManager(userData)
                                Handler().postDelayed({
                                    (activity as MainActivity).onBackPressed()
                                }, 2000)
                            } else {


                            }

                        } else {
                            ErrorUtil.errorMethod(ll_mainPassNationality)

                        }
                    })

    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btn_addNationality->{
                MyUtils.hideKeyboard1(mActivity!!)
                if (!userData?.passport.isNullOrEmpty()) {
                    for (i in 0 until userData?.passport?.size!!) {
                        for (k in 0 until nationalityDataList?.size!!) {
                            if (userData?.passport?.get(i)?.countryID?.equals(nationalityDataList?.get(k)?.countryID!!)!!) {
                                nationalityDataList?.get(k)?.isServerChecked = true
                            }
                        }
                    }
                }
                countryList?.clear()
                var isAdd=false
                nationalityDataList?.forEach {

                    if (it?.checked) {

                        if (!it?.isServerChecked) {
                            isAdd=true
                            countryList?.add(it)
                        }
                    }
                }
                if(countryList.isNullOrEmpty())
                {
                    nationalityDataList?.forEach {
                        if (it.checked)
                        {
                            countryList?.add(it)
                        }
                    }
                }
                if (countryList?.isNullOrEmpty()!!) {
                    MyUtils.showSnackbar(mActivity!!, "Please select country", ll_mainPassNationality)
                } else {
                    if (!userData?.passport.isNullOrEmpty() && !isAdd) {

                        editpassportNationaityApi("Edit",userData?.passport)
                    } else {
                        addpassportNationaityApi("Add")
                    }

                }
            }
            R.id.btnRetry->{
                nationalityList(search_nationality.text.toString().trim())

            }


        }
    }

}

