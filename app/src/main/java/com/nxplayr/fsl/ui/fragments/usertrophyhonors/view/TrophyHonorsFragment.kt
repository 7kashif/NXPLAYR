package com.nxplayr.fsl.ui.fragments.usertrophyhonors.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.usergeographical.viewmodel.UpdateResumeCallsViewModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_set_of_skills.*
import kotlinx.android.synthetic.main.fragment_trophy_honors.*
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class TrophyHonorsFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var usertrophies: String = ""
    var fromProfile = ""
    var userId = ""
    var otherUserData: SignupData? = null
    private lateinit var updateResumeCallsViewModel: UpdateResumeCallsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_trophy_honors, container, false)

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
        if (arguments != null) {
            fromProfile = arguments!!.getString("fromProfile", "")
            userId = arguments!!.getString("userId", "")
            if (!userId.equals(userData?.userID, false)) {
                otherUserData = arguments!!.getSerializable("otherUserData") as SignupData?
            }
        }
        setupViewModel()
        setupUI()
    }

    private fun setupViewModel() {
        updateResumeCallsViewModel =
            ViewModelProvider(this@TrophyHonorsFragment).get(UpdateResumeCallsViewModel::class.java)

    }

    private fun setupUI() {
        tvToolbarTitle1.text = "Trophy & Honors"

        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()

            if (!userId.equals(userData?.userID, false)) {
                btnUpdateBasicDetail.visibility = View.GONE
                edit_trophyHonors.isEnabled = false
                if (!otherUserData?.usertrophies.isNullOrEmpty()) {
                    usertrophies = otherUserData?.usertrophies!!
                    edit_trophyHonors.setText(otherUserData?.usertrophies)
                }
            } else {
                btnUpdateBasicDetail.visibility = View.VISIBLE
                edit_trophyHonors.isEnabled = true
                if (!userData?.usertrophies.isNullOrEmpty()) {
                    usertrophies = userData?.usertrophies!!
                    edit_trophyHonors.setText(userData?.usertrophies)
                }
            }


        }
        add_icon_connection.visibility = View.GONE
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        btnUpdateBasicDetail.setOnClickListener(this)
    }

    private fun getUpadte(s: String) {
        btnUpdateBasicDetail.startAnimation()
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
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
            if (userData?.userJersyNumber.isNullOrEmpty()) {
                jsonObject.put("userJersyNumber", "")
            } else {
                jsonObject.put("userJersyNumber", userData?.userJersyNumber)
            }
            jsonObject.put("usertrophies", edit_trophyHonors.text.toString().trim())
            if (userData?.geomobilityID.isNullOrEmpty()) {
                jsonObject.put("geomobilityID", "")
            } else {
                jsonObject.put("geomobilityID", userData?.geomobilityID)
            }
            if (userData?.userNationalCountryID.isNullOrEmpty()) {
                jsonObject.put("userNationalCountryID", "")
            } else {
                jsonObject.put("userNationalCountryID", userData?.userNationalCountryID)
            }
            if (userData?.userNationalCap.isNullOrEmpty()) {
                jsonObject.put("userNationalCap", "")
            } else {
                jsonObject.put("userNationalCap", userData?.userNationalCap)
            }
            if (userData?.useNationalGoals.isNullOrEmpty()) {
                jsonObject.put("useNationalGoals", "")
            } else {
                jsonObject.put("useNationalGoals", userData?.useNationalGoals)
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
        updateResumeCallsViewModel.getUpdateResume(mActivity!!, s, jsonArray.toString())
            .observe(viewLifecycleOwner
            ) { countryListPojo ->
                if (countryListPojo != null) {
                    btnUpdateBasicDetail.endAnimation()
                    if (countryListPojo.get(0).status.equals("true", false)) {
                        try {
                            StoreSessionManager(countryListPojo.get(0).data[0])
                            MyUtils.hideKeyboard1(mActivity!!)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnUpdateBasicDetail -> {
                if (edit_trophyHonors.text.toString().trim().equals("")) {
                    MyUtils.showSnackbar(mActivity!!, "Please add trophy & honors", ll_nationteam)
                } else {
                    getUpadte("Add")
                }

            }
        }
    }


}