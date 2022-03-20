package com.nxplayr.fsl.ui.fragments.usercontractsituation.view

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
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.usercontractsituation.adapter.ContractSitiuationAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.usercontractsituation.viewmodel.ContractSitiuationListModel
import com.nxplayr.fsl.ui.fragments.usergeographical.viewmodel.UpdateResumeCallsViewModel
import com.nxplayr.fsl.data.model.ContractSitiuationData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_contract_sitiuation.*
import kotlinx.android.synthetic.main.fragment_contract_sitiuation.btn_addNationality
import kotlinx.android.synthetic.main.fragment_passport_nationality.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ContractSitiuationFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var contractSitiuationAdapter: ContractSitiuationAdapter? = null

    var linearLayoutManager: LinearLayoutManager? = null
    private var y: Int = 0
    var pageNo = 0
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var contract_list: ArrayList<ContractSitiuationData?>? = null
    var contractsituationID = ""
    var contractsituationName = ""
    var userContractExpiryDate = ""
    private lateinit var  languageModel: ContractSitiuationListModel
    private lateinit var  passportNationalityModel: UpdateResumeCallsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_contract_sitiuation, container, false)
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
        if (sessionManager?.get_Authenticate_User() != null)
        {
            userData = sessionManager?.get_Authenticate_User()
            if (!userData?.contractsituationID.isNullOrEmpty() && !userData?.contractsituationName.isNullOrEmpty()) {
                contractsituationID = userData?.contractsituationID!!
                contractsituationName = userData?.contractsituationName!!
                userContractExpiryDate = if (userData?.userContractExpiryDate != null) {
                    MyUtils.formatDate(userData?.userContractExpiryDate!!, "yyyy-MM-dd", "dd/MM/yyyy")
                } else {
                    ""
                }

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
        languageModel.getContractSitiuationList(mActivity!!, false, jsonArray.toString(), "List")
            .observe(viewLifecycleOwner,
                Observer { languagesPojo ->

                    relativeprogressBar.visibility = View.GONE
                    recyclerview.visibility = View.VISIBLE

                    if (languagesPojo != null && languagesPojo.isNotEmpty()) {
                        if (languagesPojo[0].status.equals("true", true)) {
                            contract_list?.clear()
                            if (userData != null) {
                                for (i in languagesPojo[0].data.indices) {
                                    if (userData?.contractsituationID.equals(languagesPojo[0].data[i].contractsituationID, false)) {
                                        languagesPojo[0].data[i].checked = true
                                        if(!userData?.userContractExpiryDate.isNullOrEmpty())
                                        {
                                            try {
                                                languagesPojo[0].data[i].userContractExpiryDate=MyUtils.formatDate(userData?.userContractExpiryDate!!, "yyyy-MM-dd", "dd/MM/yyyy")
                                            } catch (e: Exception) {
                                            }
                                        }


                                    }
                                }
                            }
                            contract_list?.addAll(languagesPojo[0].data)
                            contractSitiuationAdapter?.notifyDataSetChanged()


                        } else {

                            if (contract_list!!.size == 0) {
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

    private fun setupUI() {
        tvToolbarTitle1.text = getString(R.string.contract_situation)
        add_icon_connection.visibility=View.GONE
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        if (contract_list == null) {
            contract_list = ArrayList()
            contractSitiuationAdapter = ContractSitiuationAdapter(mActivity!!, contract_list, object : ContractSitiuationAdapter.OnItemClick {

                override fun onClicled(position: Int, from: String) {
                    when (from) {
                        "click" -> {

                        }
                        else -> {
                            for (i in 0 until contract_list!!.size) {
                                if (i == position) {
                                    contract_list!![i]!!.checked = !(contract_list!![i]!!.checked)
                                    btn_addNationality.backgroundTint = (resources.getColor(R.color.colorPrimary))
                                    btn_addNationality.textColor = resources.getColor(R.color.black)
                                    btn_addNationality.strokeColor = resources.getColor(R.color.colorPrimary)
                                } else {
                                    contract_list!![i]!!.checked = false
                                }
                            }
                        }
                    }

                    contractSitiuationAdapter?.notifyDataSetChanged()
                }
            }, "Language")
            recyclerview.layoutManager = LinearLayoutManager(mActivity!!)
            recyclerview.setHasFixedSize(true)
            recyclerview.adapter = contractSitiuationAdapter
            contractSitiuationAdapter!!.notifyDataSetChanged()
            setupObserver()
        }

        btnRetry!!.setOnClickListener(this)
        btn_addNationality?.setOnClickListener(this)

    }

    private fun setupViewModel() {
         languageModel = ViewModelProvider(this@ContractSitiuationFragment).get(
             ContractSitiuationListModel::class.java)
         passportNationalityModel = ViewModelProvider(this@ContractSitiuationFragment).get(
             UpdateResumeCallsViewModel::class.java)

    }

    private fun getUpadteContractSitiuation(s: String) {
        btn_addNationality.startAnimation()
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("leagueID", userData?.leagueID)
            if(s.equals("Edit",false))
            {
                jsonObject.put("contractsituationID", contractsituationID)
            }
            if(!userContractExpiryDate.equals("Free",false))
            {
                try {
                    jsonObject.put("userContractExpiryDate", MyUtils.formatDate(userContractExpiryDate, "dd/MM/yyyy", "yyyy-MM-dd"))
                } catch (e: Exception) {
                }
            }

            jsonObject.put("userPreviousClubID", userData?.userPreviousClubID)
            jsonObject.put("userJersyNumber", userData?.userJersyNumber)
            jsonObject.put("usertrophies", userData?.usertrophies)
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
        passportNationalityModel.getUpdateResume(mActivity!!, s, jsonArray.toString())
                .observe(
                    this@ContractSitiuationFragment,
                    { countryListPojo ->
                        if (countryListPojo != null) {
                            btn_addNationality.endAnimation()
                            if (countryListPojo.get(0).status.equals("true", false)) {
                                try {
                                    StoreSessionManager(countryListPojo.get(0).data[0])
                                    Handler().postDelayed({
                                        (activity as MainActivity).onBackPressed()
                                    }, 1000)
                                    MyUtils.showSnackbar(mActivity!!, countryListPojo.get(0).message, llycontract)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            } else {
                                MyUtils.showSnackbar(mActivity!!, countryListPojo.get(0).message, llycontract)
                            }

                        } else {
                            btn_addNationality.endAnimation()
                            ErrorUtil.errorMethod(llycontract)
//                                Toast.makeText(context,R.string.error_common_network, Toast.LENGTH_SHORT).show()
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
                setupObserver()
            }
            R.id.btn_addNationality->{
                for (i in 0 until contract_list!!.size) {
                    if (contract_list!![i]!!.checked) {
                        contractsituationName = contract_list!![i]!!.contractsituationName
                        contractsituationID = contract_list!![i]!!.contractsituationID
                        if (!contract_list!![i]!!.userContractExpiryDate.isNullOrEmpty()) {
                            userContractExpiryDate = contract_list!![i]!!.userContractExpiryDate

                        } else {
                            userContractExpiryDate = ""

                        }
                        break
                    }
                }
                if (contractsituationID.isNullOrEmpty()) {
                    MyUtils.showSnackbar(mActivity!!, "Please select contract situation", llycontract)
                } else if (userContractExpiryDate.isNullOrEmpty()) {
                    MyUtils.showSnackbar(mActivity!!, "Please select expiry date", llycontract)
                } else {
                    if (!userData?.contractsituationID.isNullOrEmpty()) {
                        getUpadteContractSitiuation("Edit")
                    } else {
                        getUpadteContractSitiuation("Add")
                    }
                }
            }

        }
    }

}