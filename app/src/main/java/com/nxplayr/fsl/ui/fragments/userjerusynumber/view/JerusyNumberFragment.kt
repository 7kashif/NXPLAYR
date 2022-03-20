package com.nxplayr.fsl.ui.fragments.userjerusynumber.view

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
import com.nxplayr.fsl.ui.fragments.usergeographical.viewmodel.UpdateResumeCallsViewModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_jerusy_number.*
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JerusyNumberFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var userJersyNumber:String=""
    private lateinit var  passportNationalityModel: UpdateResumeCallsViewModel

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if(v==null){
            v=inflater.inflate(R.layout.fragment_jerusy_number, container, false)
        }
        return v
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity=context as AppCompatActivity

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
            if (!userData?.userJersyNumber.isNullOrEmpty()) {
                userJersyNumber = userData?.userJersyNumber!!
                edit_jersey.setText(userData?.userJersyNumber)
            }else{
                ""
            }
        }
        setupViewModel()
        setupUI()

    }

    private fun setupUI() {
        tvToolbarTitle1.setText(getString(R.string.jersey_number))

        add_icon_connection.visibility = View.GONE
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        btnUpdateBasicDetail.setOnClickListener(this)
    }

    private fun setupViewModel() {
         passportNationalityModel =
            ViewModelProvider(this@JerusyNumberFragment).get(UpdateResumeCallsViewModel::class.java)


    }

    private fun getUpadte(s: String) {
        btnUpdateBasicDetail.startAnimation()
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("contractsituationID",  "0")
            jsonObject.put("geomobilityID", "1")
            jsonObject.put("userJersyNumber", userJersyNumber)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("userNationalCap", "")
            jsonObject.put("usertrophies", "")
            jsonObject.put("userPreviousClubID", "FSL")
            jsonObject.put("userNationalCountryID","0")
            jsonObject.put("useNationalGoals","0")
            jsonObject.put("leagueID", "0")
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("userContractExpiryDate", "")
            jsonObject.put("outfitterIDs", "")


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        Log.e("JERSY_NUMBER",jsonArray.toString())
        passportNationalityModel.getUpdateResume(mActivity!!, s, jsonArray?.toString())
                .observe(this@JerusyNumberFragment!!,
                    { countryListPojo ->
                        if (countryListPojo != null) {
                            btnUpdateBasicDetail.endAnimation()
                            if (countryListPojo.get(0).status.equals("true", false)) {
                                try {
                                    StoreSessionManager(countryListPojo.get(0).data[0])
                                    Handler().postDelayed({
                                        (activity as MainActivity).onBackPressed()
                                    }, 1000)
                                    MyUtils.showSnackbar(mActivity!!, countryListPojo.get(0).message, ll_nationteam)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            } else {
                                MyUtils.showSnackbar(mActivity!!, countryListPojo.get(0).message, ll_nationteam)
                            }

                        } else {
                            btnUpdateBasicDetail.endAnimation()
                            ErrorUtil.errorMethod(ll_nationteam)
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
        when(v?.id)
        {
            R.id.btnUpdateBasicDetail->{
                userJersyNumber = edit_jersey.text.toString().trim()
                if (userJersyNumber.equals("") ||
                    userJersyNumber.equals("0")){

                    MyUtils.showSnackbar(mActivity!!, "Please enter jersey number", ll_nationteam)
                } else{
                    getUpadte("Add")
                }

            }
        }
    }

}