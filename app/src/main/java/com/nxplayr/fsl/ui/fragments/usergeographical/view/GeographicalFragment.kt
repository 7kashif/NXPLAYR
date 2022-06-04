package com.nxplayr.fsl.ui.fragments.usergeographical.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.usergeographical.adapter.GeographicalAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.usergeographical.viewmodel.GeomobilitysListModel
import com.nxplayr.fsl.ui.fragments.usergeographical.viewmodel.UpdateResumeCallsViewModel
import com.nxplayr.fsl.data.model.GeomobilitysData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_contract_sitiuation.*
import kotlinx.android.synthetic.main.fragment_geographical.*
import kotlinx.android.synthetic.main.fragment_geographical.btn_addNationality
import kotlinx.android.synthetic.main.fragment_passport_nationality.*
import kotlinx.android.synthetic.main.fragment_trophy_honors.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class GeographicalFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var geomobilityName: String = ""
    var geomobilityID: String = ""

    var geographicalAdapter: GeographicalAdapter? = null

    var linearLayoutManager: LinearLayoutManager? = null

    var pageNo = 0
    var geomobilitysList: ArrayList<GeomobilitysData?>? = null
    var fromProfile = ""
    var userId = ""
    var otherUserData: SignupData? = null
    private lateinit var  languageModel: GeomobilitysListModel
    private lateinit var  passportNationalityModel: UpdateResumeCallsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_geographical, container, false)
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngGeographical.isNullOrEmpty())
                tvToolbarTitle1.text = sessionManager?.LanguageLabel?.lngGeographical
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
        tvToolbarTitle1.text = getString(R.string.geographical_mobility)
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
        languageModel.getGeomobilitysList(mActivity!!, false, jsonArray.toString(), "List")
            .observe(viewLifecycleOwner
            ) { languagesPojo ->

                relativeprogressBar.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE

                if (languagesPojo != null && languagesPojo.isNotEmpty()) {
                    if (languagesPojo[0].status.equals("true", true)) {
                        geomobilitysList?.clear()
                        if (!userId.equals(userData?.userID, false)) {
                            if (otherUserData != null) {
                                for (i in languagesPojo[0].data.indices) {
                                    if (otherUserData?.geomobilityID.equals(
                                            languagesPojo[0].data[i].geomobilityID, false
                                        )
                                    ) {
                                        languagesPojo[0].data[i].checked = true
                                    }
                                }
                            }
                        } else {
                            if (userData != null) {
                                for (i in languagesPojo[0].data.indices) {
                                    if (userData?.geomobilityID.equals(
                                            languagesPojo[0].data[i].geomobilityID,
                                            false
                                        )
                                    ) {
                                        languagesPojo[0].data[i].checked = true
                                    }
                                }
                            }

                        }
                        geomobilitysList?.addAll(languagesPojo[0].data)
                        geographicalAdapter?.notifyDataSetChanged()
                    } else {

                        if (geomobilitysList!!.size == 0) {
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
            }

    }

    private fun setupUI() {
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
            if (!userId.equals(userData?.userID, false)) {
                btn_addNationality.visibility = View.GONE
                if (!otherUserData?.geomobilityID.isNullOrEmpty() && !otherUserData?.geomobilityName.isNullOrEmpty()) {
                    geomobilityID = otherUserData?.geomobilityID!!
                    geomobilityName = otherUserData?.geomobilityName!!
                }
            } else {
                btn_addNationality.visibility = View.VISIBLE
                if (!userData?.geomobilityID.isNullOrEmpty() && !userData?.geomobilityName.isNullOrEmpty()) {
                    geomobilityID = userData?.geomobilityID!!
                    geomobilityName = userData?.geomobilityName!!

                }
            }
        }
        add_icon_connection.visibility = View.GONE

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        if (geomobilitysList == null) {
            geomobilitysList = ArrayList()
            geographicalAdapter = GeographicalAdapter(mActivity!!, geomobilitysList, object : GeographicalAdapter.OnItemClick {

                override fun onClicled(position: Int, from: String) {

                    if (userId.equals(userData?.userID, false)) {
                        geomobilityID = geomobilitysList!![position]!!.geomobilityID
                        geomobilityName = geomobilitysList!![position]!!.geomobilityName

                        for (i in 0 until geomobilitysList!!.size) {
                            geomobilitysList!![i]!!.checked = i == position
                            if (position == 0) {
                                btn_addNationality.backgroundTint = (resources.getColor(R.color.colorPrimary))
                                btn_addNationality.textColor = (resources.getColor(R.color.black))
                                btn_addNationality.strokeColor = (resources.getColor(R.color.colorPrimary))
                            } else if (position == 1) {
                                btn_addNationality.backgroundTint = (resources.getColor(R.color.colorPrimary))
                                btn_addNationality.textColor = (resources.getColor(R.color.black))
                                btn_addNationality.strokeColor = (resources.getColor(R.color.colorPrimary))
                            } else if (position == 2) {
                                btn_addNationality.backgroundTint = (resources.getColor(R.color.colorPrimary))
                                btn_addNationality.textColor = (resources.getColor(R.color.black))
                                btn_addNationality.strokeColor = (resources.getColor(R.color.colorPrimary))
                            }
                        }
                        geographicalAdapter?.notifyDataSetChanged()

                    }


                }
            }, "Language")
            recyclerview.setHasFixedSize(true)
            linearLayoutManager = GridLayoutManager(mActivity!!, 2).also {
                it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position % 3 == 2)
                            2
                        else
                            1
                    }
                }
            }
            recyclerview.layoutManager = linearLayoutManager
            recyclerview.adapter = geographicalAdapter
            setupObserver()
        }

        btnRetry!!.setOnClickListener(this)

        btn_addNationality?.setOnClickListener(this)
    }

    private fun setupViewModel() {
         languageModel = ViewModelProvider(this@GeographicalFragment).get(GeomobilitysListModel::class.java)
         passportNationalityModel =
            ViewModelProvider(this@GeographicalFragment).get(UpdateResumeCallsViewModel::class.java)


    }

    private fun getUpadteContractSitiuation(s: String) {
        btn_addNationality.startAnimation()
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("leagueID", userData?.leagueID)
            jsonObject.put("previousclubName", userData?.previousclubName)
            jsonObject.put("contractsituationID", userData?.contractsituationID)
            jsonObject.put("outfitterIDs", userData?.outfitterIDs)
            jsonObject.put("userAgentName", userData?.userAgentName)
            jsonObject.put("userContractExpiryDate", userData?.userContractExpiryDate)
            jsonObject.put("userPreviousClubID", userData?.userPreviousClubID)
            jsonObject.put("userJersyNumber", userData?.userJersyNumber)
            jsonObject.put("usertrophies", userData?.usertrophies)
            jsonObject.put("userNationalCountryID", userData?.userNationalCountryID)
            jsonObject.put("userNationalCap", userData?.userNationalCap)
            jsonObject.put("useNationalGoals", userData?.useNationalGoals)
            jsonObject.put("geomobilityID", geomobilityID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        passportNationalityModel.getUpdateResume(mActivity!!, s, jsonArray.toString())
                .observe(
                    this@GeographicalFragment
                ) { countryListPojo ->
                    if (countryListPojo != null) {
                        btn_addNationality.endAnimation()
                        if (countryListPojo.get(0).status.equals("true", false)) {
                            try {
                                StoreSessionManager(countryListPojo.get(0).data[0])
                                Handler().postDelayed({
                                    (activity as MainActivity).onBackPressed()
                                }, 1000)
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    countryListPojo.get(0).message,
                                    llyGeographical
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                countryListPojo.get(0).message,
                                llyGeographical
                            )
                        }

                    } else {
                        btn_addNationality.endAnimation()
                        ErrorUtil.errorMethod(llyGeographical)
//                                Toast.makeText(mActivity!!,"It seems there is no internet connection.",Toast.LENGTH_SHORT)
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
        when(v?.id){
            R.id.btnRetry->{
                pageNo = 0
                setupObserver()
            }
            R.id.btn_addNationality->{
                for (i in 0 until geomobilitysList!!.size) {
                    if (geomobilitysList!![i]!!.checked) {
                        geomobilityName = geomobilitysList!![i]!!.geomobilityName
                        geomobilityID = geomobilitysList!![i]!!.geomobilityID
                    }
                }
                if (geomobilityName.isNullOrEmpty() && !geomobilityID.isNullOrEmpty()) {
                    MyUtils.showSnackbar(mActivity!!, "Please select geo mobility name", llyGeographical)
                } else {
                    if (!userData?.contractsituationID.isNullOrEmpty()) {
                        getUpadteContractSitiuation("Add")
                    } else {
                        getUpadteContractSitiuation("Add")
                    }

                }
            }

        }
    }


}