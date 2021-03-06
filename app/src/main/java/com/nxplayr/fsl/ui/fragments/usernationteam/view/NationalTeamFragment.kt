package com.nxplayr.fsl.ui.fragments.usernationteam.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.CountryListModel
import com.nxplayr.fsl.ui.fragments.usergeographical.viewmodel.UpdateResumeCallsViewModel
import com.nxplayr.fsl.data.model.CountryListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import com.nxplayr.fsl.ui.fragments.bottomsheet.CountryListBottomSheetFragment
import kotlinx.android.synthetic.main.fragment_basic_detail.*
import kotlinx.android.synthetic.main.fragment_jerusy_number.*
import kotlinx.android.synthetic.main.fragment_national_team.*
import kotlinx.android.synthetic.main.fragment_national_team.btnUpdateBasicDetail
import kotlinx.android.synthetic.main.fragment_national_team.ll_nationteam
import kotlinx.android.synthetic.main.fragment_passport_nationality.*
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class NationalTeamFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var pageNo = 0
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var userNationalCountryID = ""
    var teamcountryName = ""
    var userNationalCap = ""
    var useNationalGoals = ""
    var countryListData: ArrayList<CountryListData>? = ArrayList()
    var fromProfile = ""
    var userId = ""
    var otherUserData: SignupData? = null
    private lateinit var countryListModel: CountryListModel
    private lateinit var passportNationalityModel: UpdateResumeCallsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_national_team, container, false)
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngNationalTeam.isNullOrEmpty())
                tvToolbarTitle1.text = sessionManager?.LanguageLabel?.lngNationalTeam
            if (!sessionManager?.LanguageLabel?.lngCountry.isNullOrEmpty())
                tv_edit_country.hint = sessionManager?.LanguageLabel?.lngCountry
            if (!sessionManager?.LanguageLabel?.lngCaps.isNullOrEmpty())
                tv_edit_caps.hint = sessionManager?.LanguageLabel?.lngCaps
            if (!sessionManager?.LanguageLabel?.lngGoals.isNullOrEmpty())
                tv_edit_goals.hint = sessionManager?.LanguageLabel?.lngGoals
            if (!sessionManager?.LanguageLabel?.lngSave.isNullOrEmpty())
                btnUpdateBasicDetail.progressText = sessionManager?.LanguageLabel?.lngSave
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tvToolbarTitle1.setText(getString(R.string.national_team))

        sessionManager = SessionManager(mActivity!!)
        if (arguments != null) {
            fromProfile = arguments!!.getString("fromProfile", "")
            userId = arguments!!.getString("userId", "")
            if (!userId.equals(userData?.userID, false)) {
                otherUserData = arguments!!.getSerializable("otherUserData") as SignupData?
            }
        }
        setupViewModel()
        setupUI()
        setupObserver()
    }

    private fun setupObserver() {

    }

    private fun setupUI() {
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
            if (!userId.equals(userData?.userID, false)) {
                btnUpdateBasicDetail.visibility = View.GONE
                edit_caps.isEnabled = false
                edit_goals.isEnabled = false
                if (!otherUserData?.userNationalCountryID.isNullOrEmpty() && !otherUserData?.teamcountryName.isNullOrEmpty()) {

                    userNationalCountryID = otherUserData?.userNationalCountryID!!
                    teamcountryName = otherUserData?.teamcountryName!!
                    edit_country.setText(otherUserData?.teamcountryName)
                }
                if (!otherUserData?.userNationalCap.isNullOrEmpty()) {
                    userNationalCap = otherUserData?.userNationalCap!!
                    edit_caps.setText(otherUserData?.userNationalCap)

                }
                if (!otherUserData?.useNationalGoals.isNullOrEmpty()) {
                    useNationalGoals = otherUserData?.useNationalGoals!!
                    edit_goals.setText(otherUserData?.useNationalGoals)
                }
            } else {
                btnUpdateBasicDetail.visibility = View.VISIBLE
                edit_caps.isEnabled = true
                edit_goals.isEnabled = true
                if (!userData?.userNationalCountryID.isNullOrEmpty() && !userData?.teamcountryName.isNullOrEmpty()) {

                    userNationalCountryID = userData?.userNationalCountryID!!
                    teamcountryName = userData?.teamcountryName!!
                    edit_country.setText(userData?.teamcountryName)
                }
                if (!userData?.userNationalCap.isNullOrEmpty()) {
                    userNationalCap = userData?.userNationalCap!!
                    edit_caps.setText(userData?.userNationalCap)

                }
                if (!userData?.useNationalGoals.isNullOrEmpty()) {
                    useNationalGoals = userData?.useNationalGoals!!
                    edit_goals.setText(userData?.useNationalGoals)
                }
            }

        }
        add_icon_connection.visibility = View.GONE
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        getCounrtyList("List")

        edit_country.setOnClickListener(this)
        btnUpdateBasicDetail.setOnClickListener(this)
    }

    private fun setupViewModel() {
        countryListModel =
            ViewModelProvider(this@NationalTeamFragment).get(CountryListModel::class.java)
        passportNationalityModel =
            ViewModelProvider(this@NationalTeamFragment).get(UpdateResumeCallsViewModel::class.java)
    }

    private fun getCounrtyList(s: String) {
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("blankCountryCode", "No")

            jsonObject.put("apiVersion", RestClient.apiVersion)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        countryListModel.getCountryList(mActivity!!, false, jsonArray.toString())
            .observe(viewLifecycleOwner,
                { countryListPojo ->
                    if (countryListPojo != null) {
                        MyUtils.dismissProgressDialog()
                        if (countryListPojo.get(0).status.equals("true", false)) {
                            countryListData?.clear()
                            countryListData?.addAll(countryListPojo.get(0).data!!)
                            when (s) {
                                "click" -> {
                                    openBottomSheet(countryListData!!)
                                }
                            }
                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                countryListPojo.get(0).message,
                                ll_nationteam
                            )
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        //No internet and somting went rong
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_crash_error_message),
                                ll_nationteam
                            )
                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_common_network),
                                ll_nationteam
                            )
                        }
                    }
                })


    }

    private fun getUpadte(s: String) {
        btnUpdateBasicDetail.startAnimation()
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("userNationalCountryID", userNationalCountryID)
            jsonObject.put("userNationalCap", edit_caps.text.toString().trim())
            jsonObject.put("useNationalGoals", edit_goals.text.toString().trim())

            jsonObject.put("loginuserID", userData?.userID)
            if (userData?.contractsituationID.isNullOrEmpty()) {
                jsonObject.put("contractsituationID", "")
            } else {
                jsonObject.put("contractsituationID", userData?.contractsituationID)
            }

            if (userData?.leagueID.isNullOrEmpty()) {
                jsonObject.put("leagueID", "")
            } else {
                jsonObject.put("leagueID", userData?.leagueID)
            }

            if (userData?.userContractExpiryDate.isNullOrEmpty()) {
                jsonObject.put("userContractExpiryDate", "")
            } else {
                jsonObject.put("userContractExpiryDate", userData?.userContractExpiryDate)
            }
            if (userData?.userPreviousClubID.isNullOrEmpty()) {
                jsonObject.put("userPreviousClubID", "")
            } else {
                jsonObject.put("userPreviousClubID", userData?.userPreviousClubID)
            }
            if (userData?.usertrophies.isNullOrEmpty()) {
                jsonObject.put("usertrophies", "")
            } else {
                jsonObject.put("usertrophies", userData?.usertrophies)
            }
            if (userData?.userJersyNumber.isNullOrEmpty()) {
                jsonObject.put("userJersyNumber", "")
            } else {
                jsonObject.put("userJersyNumber", userData?.userJersyNumber)
            }
            if (userData?.geomobilityID.isNullOrEmpty()) {
                jsonObject.put("geomobilityID", "")
            } else {
                jsonObject.put("geomobilityID", userData?.geomobilityID)
            }
            if (userData?.userPreviousClubID.isNullOrEmpty()) {
                jsonObject.put("userPreviousClubID", "")
            } else {
                jsonObject.put("userPreviousClubID", userData?.userPreviousClubID)
            }
            if (userData?.previousclubName.isNullOrEmpty()) {
                jsonObject.put("previousclubName", "")
            } else {
                jsonObject.put("previousclubName", userData?.previousclubName)
            }
            if (userData?.userAgentName.isNullOrEmpty()) {
                jsonObject.put("userAgentName", "")
            } else {
                jsonObject.put("userAgentName", userData?.userAgentName)
            }
            if (userData?.outfitterIDs.isNullOrEmpty()) {
                jsonObject.put("outfitterIDs", "")
            } else {
                jsonObject.put("outfitterIDs", userData?.outfitterIDs)
            }
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        Log.e("team_OBJECT", jsonObject.toString())
        passportNationalityModel.getUpdateResume(mActivity!!, s, jsonArray?.toString())
            .observe(
                this@NationalTeamFragment!!
            ) { countryListPojo ->
                if (countryListPojo != null) {
                    btnUpdateBasicDetail.endAnimation()
                    if (countryListPojo.get(0).status.equals("true", false)) {
                        try {
                            MyUtils.hideKeyboard1(mActivity!!)
                            StoreSessionManager(countryListPojo.get(0).data[0])
                            Handler().postDelayed({
                                (activity as MainActivity).onBackPressed()
                            }, 1000)
                            MyUtils.showSnackbar(
                                mActivity!!,
                                countryListPojo.get(0).message,
                                ll_nationteam
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            countryListPojo.get(0).message,
                            ll_nationteam
                        )
                    }

                } else {
                    btnUpdateBasicDetail.endAnimation()
                    ErrorUtil.errorMethod(ll_nationteam)
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

    private fun openBottomSheet(data: ArrayList<CountryListData>?) {
        val bottomSheet = CountryListBottomSheetFragment()
        val bundle = Bundle()
        bundle.putSerializable("data", data)
        bottomSheet.arguments = bundle
        bottomSheet.show(fragmentManager!!, "List")
        bottomSheet.setOnclickLisner(object : CountryListBottomSheetFragment.SelectList {
            override fun onOptionSelect(value: Int, from: String, Id: String) {
                bottomSheet.dismiss()
                userNationalCountryID = Id
                teamcountryName = from
                edit_country.setText(teamcountryName)

            }

        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edit_country -> {
                if (userId.equals(userData?.userID, false)) {
                    if (countryListData.isNullOrEmpty()) {
                        getCounrtyList("click")
                    } else {
                        openBottomSheet(countryListData)
                    }
                }
            }
            R.id.btnUpdateBasicDetail -> {
                getUpadte("Add")
            }
        }
    }

}