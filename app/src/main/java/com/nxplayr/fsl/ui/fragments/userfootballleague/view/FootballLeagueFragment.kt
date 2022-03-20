package com.nxplayr.fsl.ui.fragments.userfootballleague.view

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
import com.nxplayr.fsl.ui.fragments.userfootballleague.adapter.FootballLanguageListAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userfootballleague.viewmodel.LanguageListDataModel
import com.nxplayr.fsl.ui.fragments.userfootballleague.viewmodel.UpdateResumeModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.UserLanguageList
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_user_profile_details.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_football_language.*
import kotlinx.android.synthetic.main.fragment_football_level.*
import kotlinx.android.synthetic.main.fragment_trophy_honors.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.tvToolbarTitle
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class FootballLeagueFragment : Fragment(), View.OnClickListener {


    private var v: View? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var mActivity: AppCompatActivity? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var football_language_list: ArrayList<UserLanguageList?>? = ArrayList()
    var footballLanguageListAdapter: FootballLanguageListAdapter? = null

    var leagueID: String=""
    var leagueName: String=""
    var fromProfile=""
    var userId=""
    var otherUserData: SignupData?=null
    private lateinit var  getUpdateResumeModel: UpdateResumeModel
    private lateinit var  getLanguageModel: LanguageListDataModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_football_language, container, false)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(this.activity!!)

        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
            fromProfile = arguments!!.getString("fromProfile","")
            userId = arguments!!.getString("userId","")
            if(!userId.equals(userData?.userID,false))
            {
                otherUserData = arguments!!.getSerializable("otherUserData") as SignupData?
            }
            if(!userId.equals(userData?.userID,false))
            {
                if (!otherUserData?.leagueName.isNullOrEmpty()) {
                    leagueID =otherUserData?.leagueID!!
                    leagueName = otherUserData?.leagueName!!

                }

            }
            else{
                if (!userData?.leagueName.isNullOrEmpty()) {
                    leagueID =userData?.leagueID!!
                    leagueName = userData?.leagueName!!

                }

            }

        }
        setupViewModel()
        setupUI()
    }

    private fun setupObserver(loginID: String, apiType: String, apiVersion: String) {
        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("loginuserID", loginID)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("LANGUAGE_LIST_GRID", jsonObject.toString())
        jsonArray.put(jsonObject)
        getLanguageModel.getlanguageVList(mActivity!!, false, jsonArray.toString())
            .observe(viewLifecycleOwner,
                { languageListPojo ->

                    relativeprogressBar.visibility = View.GONE
                    if (languageListPojo != null && languageListPojo.isNotEmpty()) {

                        if (languageListPojo[0].status.equals("true", true)) {

                            football_language_list?.clear()

                            football_language_list?.addAll(languageListPojo!![0]!!.data!!)
                            footballLanguageListAdapter?.notifyDataSetChanged()

                        } else {

                            if (football_language_list!!.size == 0) {

                                ll_no_data_found.visibility = View.VISIBLE

                            } else {

                                ll_no_data_found.visibility = View.GONE
                            }

                        }

                    } else {

                        relativeprogressBar.visibility = View.GONE
                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }

                })
    }

    private fun setupUI() {
        tvToolbarTitle.text = getString(R.string.football_league)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }
        getLanguageList()

        btnRetry.setOnClickListener(this)
    }

    private fun setupViewModel() {
         getUpdateResumeModel = ViewModelProvider(this@FootballLeagueFragment).get(UpdateResumeModel::class.java)
         getLanguageModel = ViewModelProvider(this@FootballLeagueFragment).get(LanguageListDataModel::class.java)

    }

    private fun getLanguageList() {
        linearLayoutManager = LinearLayoutManager(mActivity!!)
        if (football_language_list.isNullOrEmpty()) {
            rc_language.layoutManager = linearLayoutManager
            footballLanguageListAdapter = FootballLanguageListAdapter(mActivity!!, object : FootballLanguageListAdapter.OnItemClick {
                override fun onClicklisneter(pos: Int) {

                    for (i in 0 until football_language_list!!.size) {
                        football_language_list!![i]!!.isSelect = i == pos
                    }

                    leagueID = football_language_list!!.get(pos)!!.leagueID!!
                    leagueName = football_language_list!!.get(pos)!!.leagueName!!
                    footballLanguageListAdapter?.notifyDataSetChanged()

                    btn_save_footballLeague.backgroundTint = (resources.getColor(R.color.colorPrimary))
                    btn_save_footballLeague.textColor = resources.getColor(R.color.black)
                    btn_save_footballLeague.strokeColor = (resources.getColor(R.color.colorPrimary))

                    footballLanguageListAdapter?.notifyDataSetChanged()
                }

            }, football_language_list!!, false,leagueID!!,userId,userData?.userID)
        }
        rc_language.setHasFixedSize(true)
        rc_language.adapter = footballLanguageListAdapter
        rc_language.setHasFixedSize(true)
        setupObserver("0", RestClient.apiType, RestClient.apiVersion)

        if (!(userData!!.leagueID.isNullOrEmpty()) && (!userData!!.leagueID.equals("0"))) {
            btn_save_footballLeague.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_save_footballLeague.textColor = resources.getColor(R.color.black)
            btn_save_footballLeague.strokeColor = (resources.getColor(R.color.colorPrimary))
        } else {
            btn_save_footballLeague.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_save_footballLeague.textColor = resources.getColor(R.color.colorPrimary)
            btn_save_footballLeague.strokeColor = (resources.getColor(R.color.grayborder))
        }

        btn_save_footballLeague.setOnClickListener(this)

    }

    //Click Handle
    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.btnRetry -> {

                getLanguageList()

                if (!(userData!!.footbllevelID.isNullOrEmpty()) && (!userData!!.footbllevelID.equals("0"))) {
              //  Toast.makeText(context, "Please Select Any Language", Toast.LENGTH_SHORT).show()
                    btn_save_footballLeague.textColor = resources.getColor(R.color.black)
                    btn_save_footballLeague.strokeColor = (resources.getColor(R.color.colorPrimary))
                } else {
                    btn_save_footballLeague.backgroundTint = (resources.getColor(R.color.transperent1))
                    btn_save_footballLeague.textColor = resources.getColor(R.color.colorPrimary)
                    btn_save_footballLeague.strokeColor = (resources.getColor(R.color.grayborder))
                }
                if(leagueID.isNullOrEmpty() &&leagueName.isNullOrEmpty())
                {
                    MyUtils.showSnackbar(mActivity!!,"Please Select Any Football league",nointernetMainRelativelayout)
                }
                else{
                    updateFootbalLanguage()
                }
            }
            R.id.btn_save_footballLeague->{
                if (!userData!!.leagueID.isEmpty() || !(userData!!.leagueID.isEmpty())) {
                    updateFootbalLanguage()
                } else {
                    MyUtils.showSnackbar(mActivity!!, getString(R.string.please_select_footballLeague), lly_league)
                }
            }
        }
    }

    private fun updateFootbalLanguage() {

        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion",  RestClient.apiVersion)
            jsonObject.put("loginuserID", userData?.userID!!)
            jsonObject.put("leagueID", leagueID)
            if(!userData?.contractsituationID.isNullOrEmpty())
            {
                jsonObject.put("contractsituationID",  userData?.contractsituationID)

            }
            else{
                jsonObject.put("contractsituationID", "")

            }
            if(!userData?.userContractExpiryDate.isNullOrEmpty())
            {
                jsonObject.put("userContractExpiryDate",  userData?.userContractExpiryDate)

            }
            else{
                jsonObject.put("userContractExpiryDate", "")

            }
            if(!userData?.userPreviousClubID.isNullOrEmpty())
            {
                jsonObject.put("userPreviousClubID",  userData?.userPreviousClubID)

            }
            else{
                jsonObject.put("userPreviousClubID", "")

            }
            if(!userData?.userJersyNumber.isNullOrEmpty())
            {
                jsonObject.put("userJersyNumber",  userData?.userJersyNumber)

            }
            else{
                jsonObject.put("userJersyNumber", "")

            }
            if(!userData?.usertrophies.isNullOrEmpty())
            {
                jsonObject.put("usertrophies",  userData?.usertrophies)

            }
            else{
                jsonObject.put("usertrophies", "")

            }
            if(!userData?.geomobilityID.isNullOrEmpty())
            {
                jsonObject.put("geomobilityID",  userData?.geomobilityID)

            }
            else{
                jsonObject.put("geomobilityID", "")

            }
            if(!userData?.userNationalCountryID.isNullOrEmpty())
            {
                jsonObject.put("userNationalCountryID",  userData?.userNationalCountryID)

            }
            else{
                jsonObject.put("userNationalCountryID", "")

            }
            if(!userData?.userNationalCap.isNullOrEmpty())
            {
                jsonObject.put("userNationalCap",  userData?.userNationalCap)

            }
            else{
                jsonObject.put("userNationalCap", "")

            }
            if(!userData?.useNationalGoals.isNullOrEmpty())
            {
                jsonObject.put("useNationalGoals",  userData?.useNationalGoals)

            }
            else{
                jsonObject.put("useNationalGoals", "")

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("UpdateObject", jsonObject.toString())
        jsonArray.put(jsonObject)
            getUpdateResumeModel.getUpateResumeList(mActivity!!, false, jsonArray.toString())
                .observe(this@FootballLeagueFragment!!,
                        Observer { updateResumePojo ->

                            relativeprogressBar.visibility = View.GONE

                            if (updateResumePojo != null && updateResumePojo.isNotEmpty()) {

                                if (updateResumePojo[0].status.equals("true", true)) {
                                    StoreSessionManager(updateResumePojo[0].data[0])
                                    Handler().postDelayed({
                                        (activity as MainActivity).onBackPressed()
                                    }, 1000)
                                    MyUtils.showSnackbar(mActivity!!, updateResumePojo.get(0).message, lly_footballeague)
                                } else {

                                    MyUtils.showSnackbar(mActivity!!, updateResumePojo.get(0).message, lly_footballeague)


                                }

                            } else {
                                relativeprogressBar.visibility = View.GONE
                                ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
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

}