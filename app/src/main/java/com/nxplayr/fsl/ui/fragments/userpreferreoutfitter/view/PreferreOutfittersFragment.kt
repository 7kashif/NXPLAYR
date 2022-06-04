package com.nxplayr.fsl.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userpreferreoutfitter.viewmodel.OutfittersListModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.OutfittersPojoData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.usergeographical.viewmodel.UpdateResumeCallsViewModel
import com.nxplayr.fsl.ui.fragments.userpreferreoutfitter.adapter.PreferreOutfittersAdapter
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_jerusy_number.*
import kotlinx.android.synthetic.main.fragment_preferre_outfitters.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PreferreOutfittersFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var contractSitiuationAdapter: PreferreOutfittersAdapter? = null

    var linearLayoutManager: LinearLayoutManager? = null
    var pageNo = 0
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var outfitterNames = ""
    var outfitterIDs = ""
    var outfitters: ArrayList<OutfittersPojoData?>? = null
    var ids: List<String>? = null
    var fromProfile=""
    var userId=""
    var otherUserData:SignupData?=null
    private lateinit var  passportNationalityModel: UpdateResumeCallsViewModel
    private lateinit var  languageModel: OutfittersListModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_preferre_outfitters, container, false)

        }
        return v
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngPreferredOutfitters.isNullOrEmpty())
                tvToolbarTitle1.text = sessionManager?.LanguageLabel?.lngPreferredOutfitters
            if (!sessionManager?.LanguageLabel?.lngSave.isNullOrEmpty())
                btn_addNationality.progressText = sessionManager?.LanguageLabel?.lngSave
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tvToolbarTitle1.text = getString(R.string.preferred_outfitters)

        sessionManager = SessionManager(mActivity!!)
        if (arguments != null) {
            fromProfile = arguments!!.getString("fromProfile","")
            userId = arguments!!.getString("userId","")
            if(!userId.equals(userData?.userID,false))
            {
                otherUserData = arguments!!.getSerializable("otherUserData") as SignupData?
            }
        }
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
            if(!userId.equals(userData?.userID,false))
            {
                btn_addNationality.visibility=View.GONE
                if (!otherUserData?.outfitterNames.isNullOrEmpty()) {
                    outfitterNames = otherUserData?.outfitterNames!!
                    outfitterIDs = otherUserData?.outfitterIDs!!
                }
            }else {
                btn_addNationality.visibility=View.VISIBLE
                if (!userData?.outfitterNames.isNullOrEmpty()) {
                    outfitterNames = userData?.outfitterNames!!
                    outfitterIDs = userData?.outfitterIDs!!
                }
            }
        }

        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        add_icon_connection.visibility = View.GONE
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        if (outfitters == null) {
            outfitters = ArrayList()
            contractSitiuationAdapter = PreferreOutfittersAdapter(mActivity!!, outfitters, object : PreferreOutfittersAdapter.OnItemClick {

                override fun onClicled(position: Int, from: String) {
                    if(userId.equals(userData?.userID,false)) {
                        when (from) {
                            "click" -> {

                            }
                            else -> {
                                for (i in 0 until outfitters!!.size) {
                                    if (i == position) {
                                        outfitters!![i]!!.checked = !(outfitters!![i]!!.checked)
                                        btn_addNationality.backgroundTint = (resources.getColor(R.color.colorPrimary))
                                        btn_addNationality.textColor = resources.getColor(R.color.black)
                                        btn_addNationality.strokeColor = resources.getColor(R.color.colorPrimary)
                                    }
                                }
                            }
                        }

                        contractSitiuationAdapter?.notifyDataSetChanged()
                    }
                }
            }, "Language")
            recyclerview.layoutManager = LinearLayoutManager(mActivity!!)
            recyclerview.setHasFixedSize(true)
            recyclerview.adapter = contractSitiuationAdapter
            contractSitiuationAdapter!!.notifyDataSetChanged()
            getoutfittersList()
        }

        btnRetry!!.setOnClickListener(this)

        btn_addNationality?.setOnClickListener(this)
    }

    private fun setupViewModel() {
         passportNationalityModel = ViewModelProvider(this@PreferreOutfittersFragment).get(UpdateResumeCallsViewModel::class.java)
         languageModel = ViewModelProvider(this@PreferreOutfittersFragment).get(OutfittersListModel::class.java)

    }

    private fun getoutfittersList() {
        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("pagesize", "100")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)


        } catch (e: Exception) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        languageModel.getOutfittersList(mActivity!!, false, jsonArray.toString(), "List")
                .observe(viewLifecycleOwner,
                        Observer { languagesPojo ->

                            relativeprogressBar.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE

                            if (languagesPojo != null && languagesPojo.isNotEmpty()) {
                                if (languagesPojo[0].status.equals("true", true)) {
                                    outfitters?.clear()
                                    if(!userId.equals(userData?.userID,false))
                                    {
                                        if (otherUserData != null) {
                                            if (!otherUserData?.outfitterIDs.isNullOrEmpty()) {
                                                ids = otherUserData?.outfitterIDs?.split(",")
                                            }
                                            languagesPojo[0].data.forEachIndexed { indext, it ->
                                                if (!ids.isNullOrEmpty()) {
                                                    for (i in 0 until ids?.size!!) {
                                                        if (ids!![i].trim().equals(languagesPojo[0].data.get(indext).outfitterID.toString().trim(), false)) {
                                                            languagesPojo[0].data[indext].checked = true
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }
                                    else
                                    {
                                        if (userData != null) {
                                            if (!userData?.outfitterIDs.isNullOrEmpty()) {
                                                ids = userData?.outfitterIDs?.split(",")
                                            }
                                            languagesPojo[0].data.forEachIndexed { indext, it ->
                                                if (!ids.isNullOrEmpty()) {
                                                    for (i in 0 until ids?.size!!) {
                                                        if (ids!![i].trim().equals(languagesPojo[0].data.get(indext).outfitterID.toString().trim(), false)) {
                                                            languagesPojo[0].data[indext].checked = true
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }
                                    outfitters?.addAll(languagesPojo[0].data)
                                    contractSitiuationAdapter?.notifyDataSetChanged()
                                } else {

                                    if (outfitters!!.size == 0) {
                                        ll_no_data_found.visibility = View.VISIBLE
                                        recyclerview.visibility = View.GONE

                                    } else {
                                        ll_no_data_found.visibility = View.GONE
                                        recyclerview.visibility = View.VISIBLE

                                    }
                                }
                            } else {
                                relativeprogressBar.visibility = View.GONE
                                ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                            }
                        })
    }

    fun getAddOutfitters(from: String) {
        btn_addNationality.startAnimation()
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("leagueID", userData?.leagueID)
            jsonObject.put("contractsituationID", userData?.contractsituationID)
            jsonObject.put("userContractExpiryDate", userData?.userContractExpiryDate)
            jsonObject.put("userPreviousClubID", userData?.userPreviousClubID)
            jsonObject.put("previousclubName", userData?.previousclubName)
            jsonObject.put("userJersyNumber", userData?.userJersyNumber)
            jsonObject.put("usertrophies", userData?.usertrophies)
            jsonObject.put("outfitterIDs", outfitterIDs)
            jsonObject.put("userAgentName", userData?.userAgentName)
            jsonObject.put("geomobilityID", userData?.geomobilityID)
            jsonObject.put("userNationalCountryID", userData?.userNationalCountryID)
            jsonObject.put("userNationalCap", userData?.userNationalCap)
            jsonObject.put("useNationalGoals", userData?.useNationalGoals)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        Log.e("json", "" + jsonArray.toString())

        passportNationalityModel.getUpdateResume(mActivity!!, "Add", jsonArray?.toString())
                .observe(viewLifecycleOwner,
                        androidx.lifecycle.Observer { countryListPojo ->
                            if (countryListPojo != null) {
                                btn_addNationality.endAnimation()
                                if (countryListPojo.get(0).status.equals("true", false)) {
                                    try {
                                        StoreSessionManager(countryListPojo.get(0).data[0])
                                        Handler().postDelayed({
                                            (activity as MainActivity).onBackPressed()
                                        }, 1000)
                                        MyUtils.showSnackbar(mActivity!!, countryListPojo.get(0).message, ll_mainPassNationality)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                } else {
                                    MyUtils.showSnackbar(mActivity!!, countryListPojo.get(0).message, ll_mainPassNationality)
                                }

                            } else {
                                btn_addNationality.endAnimation()
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

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btnRetry->{
                pageNo = 0
                getoutfittersList()
            }
            R.id.btn_addNationality->{
                var outfittersData: ArrayList<OutfittersPojoData?>? = ArrayList()
                for (i in 0 until outfitters!!.size) {
                    if (outfitters!![i]!!.checked) {
                        outfittersData?.add(outfitters!![i])
                    }
                }

                outfitterNames = outfittersData?.joinToString {
                    it?.outfitterName!!
                }.toString()

                outfitterIDs = outfittersData?.joinToString {
                    it?.outfitterID!!
                }.toString()

                if (outfitterIDs.equals("")) {
                    MyUtils.showSnackbar(mActivity!!, "Please select outfitter names", ll_mainPassNationality)
                } else {
                    if (!userData?.outfitterIDs.isNullOrEmpty()) {
                        getAddOutfitters("Add")
                    } else {
                        getAddOutfitters("Add")
                    }

                }

            }


        }
    }


}