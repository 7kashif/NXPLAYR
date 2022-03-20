package com.nxplayr.fsl.ui.fragments.usercurrentclub.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.usergeographical.viewmodel.UpdateResumeCallsViewModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import com.nxplayr.fsl.ui.fragments.usercurrentclub.adapter.AddClubListAdapter
import com.nxplayr.fsl.ui.fragments.usercurrentclub.adapter.CurrentClubAdapter
import com.nxplayr.fsl.ui.fragments.usercurrentclub.adapter.PreviousClubListAdapter
import com.nxplayr.fsl.ui.fragments.usercurrentclub.viewmodel.AddClubModel
import com.nxplayr.fsl.ui.fragments.usercurrentclub.viewmodel.ClubListModel
import com.nxplayr.fsl.data.model.ClubData
import com.nxplayr.fsl.data.model.ClubListData
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_previous.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class PreviousFragment : Fragment() {

    private var v: View? = null
    var mActivity: Activity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var fromProfile = ""
    var userId = ""
    var otherUserData: SignupData? = null

    var club_list: ArrayList<ClubListData>? = ArrayList()
    var addClubList: ArrayList<ClubListData>? = ArrayList()
    var clubAdapter: CurrentClubAdapter? = null
    var addClubListAdapter: AddClubListAdapter? = null
    var addClubListData: ArrayList<ClubListData>? = ArrayList()

    var clubListAdapter: PreviousClubListAdapter? = null
    var clubID = ""
    var deleteList: ArrayList<ClubListData>? = ArrayList()

    private lateinit var  clubListModel: ClubListModel
    private lateinit var  addClubModel: AddClubModel
    private lateinit var  updateResumeCallsViewModel: UpdateResumeCallsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_previous, container, false)
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

        if (arguments != null) {
            fromProfile = arguments!!.getString("fromProfile")!!
            userId = arguments!!.getString("userId", "")
            if (!userId.equals(userData?.userID, false)) {
                otherUserData = arguments!!.getSerializable("otherUserData") as SignupData?
            }
        }
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle.visibility = View.VISIBLE
        tvToolbarTitle.setText(R.string.previous_club)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }
        suggstedlist()

        if (!userId.equals(userData?.userID, false)) {
            btn_previous_club.visibility = View.GONE

            if (!otherUserData?.previousclubName.isNullOrEmpty()) {
                setClubData(otherUserData!!.clubs)
            }

        } else {
            btn_previous_club.visibility = View.VISIBLE

            if (!userData?.previousclubName.isNullOrEmpty()) {
                setClubData(userData!!.clubs)
            }


        }
        btn_previous_club.setOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            if (!addClubList!!.isEmpty() || !addClubListData!!.isEmpty()) {
                getPriousClub()
            } else {
                MyUtils.showSnackbar(mActivity!!, "Please add Clubs", ll_main_previousclub)
            }
        }



        if (!userData!!.clubs.isNullOrEmpty()) {
            btn_previous_club.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_previous_club.textColor = resources.getColor(R.color.black)
            btn_previous_club.strokeColor = (resources.getColor(R.color.colorPrimary))
        } else {
            btn_previous_club.strokeColor = (resources.getColor(R.color.grayborder))
            btn_previous_club.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_previous_club.textColor = resources.getColor(R.color.colorPrimary)
        }

        edit_searchClub.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clubAdapter!!.filter?.filter(p0.toString())
                clubAdapter?.notifyDataSetChanged()
            }

        })

        btnRetry.setOnClickListener {
            suggestedClubList()
            clubListApi()
        }
    }
    private fun setupViewModel() {
        clubListModel = ViewModelProvider(this@PreviousFragment).get(ClubListModel::class.java)
        addClubModel = ViewModelProvider(this@PreviousFragment).get(AddClubModel::class.java)
        updateResumeCallsViewModel = ViewModelProvider(this@PreviousFragment).get(UpdateResumeCallsViewModel::class.java)

    }

    fun suggstedlist() {
        clubAdapter = CurrentClubAdapter(activity as MainActivity, club_list!!, object : CurrentClubAdapter.OnItemClick {

            override fun onClicled(clubData: ClubListData?, position: Int) {
                addClubList(clubData!!)

                club_list?.remove(clubData)
                edit_searchClub.text.clear()
                clubAdapter?.notifyDataSetChanged()

                if (addClubList!!.size > 0) {
                    btn_previous_club.strokeColor = (resources.getColor(R.color.colorPrimary))
                    btn_previous_club.backgroundTint = (resources.getColor(R.color.colorPrimary))
                    btn_previous_club.textColor = resources.getColor(R.color.black)
                }
            }

        }, userId)
        recyclerview.layoutManager = LinearLayoutManager(mActivity)
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = clubAdapter
        suggestedClubList()
        clubAdapter?.notifyDataSetChanged()
    }

    fun addClubList(club: ClubListData) {
        addClubListAdapter = AddClubListAdapter(activity as MainActivity, addClubList, object : AddClubListAdapter.OnItemClick {
            override fun onClicled(position: Int, from: String) {

                when (from) {
                    "removefromList" -> {

                        if (!addClubList.isNullOrEmpty()) {
                            club_list?.add(addClubList!![position])
                            clubAdapter?.notifyDataSetChanged()
                            addClubList!!.removeAt(position)
                        }
                        addClubListAdapter?.notifyDataSetChanged()
                        if (addClubList!!.size == 0) {
                            btn_previous_club.strokeColor = (resources.getColor(R.color.grayborder))
                            btn_previous_club.backgroundTint = (resources.getColor(R.color.transperent1))
                            btn_previous_club.textColor = resources.getColor(R.color.colorPrimary)
                        }
                    }
                }
            }
        }, userId)

        clubID = club!!.clubID
        addClubList!!.add(club)
        RV_selectedClubList.layoutManager = LinearLayoutManager(mActivity)
        RV_selectedClubList.setHasFixedSize(true)
        RV_selectedClubList.adapter = addClubListAdapter
        addClubListAdapter?.notifyDataSetChanged()


    }

    private fun setClubData(clubs: java.util.ArrayList<ClubData>) {

        clubListAdapter = PreviousClubListAdapter(activity as MainActivity, addClubListData!!, object : PreviousClubListAdapter.OnItemClick {

            override fun onClicled(position: Int, from: String) {
                deleteClub(addClubListData!![position].clubID, position)

            }
        }, userId)

        RV_addedClubList.layoutManager = LinearLayoutManager(mActivity)
        RV_addedClubList.setHasFixedSize(true)
        RV_addedClubList.adapter = clubListAdapter
        clubListAdapter?.notifyDataSetChanged()
        clubListApi()

    }

    private fun suggestedClubList() {
        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

//        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("searchWord", "")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        clubListModel.getClubList(mActivity!!, false, jsonArray.toString())
                .observe(viewLifecycleOwner,
                    { clubListpojo ->

                        relativeprogressBar.visibility = View.GONE
                        recyclerview.visibility = View.VISIBLE


                        if (clubListpojo != null && clubListpojo.isNotEmpty()) {

                            if (clubListpojo[0].status.equals("true", false)) {

                                club_list?.clear()
                                if (!userId.equals(userData?.userID, false)) {
                                    if (!otherUserData!!.clubs.isNullOrEmpty()) {
                                        val firstListIds = otherUserData!!.clubs?.map { it.clubID }
                                        val new = clubListpojo[0].data?.filter { it.clubID !in firstListIds!! }
                                        club_list?.addAll((new))
                                        clubAdapter?.notifyDataSetChanged()
                                    } else {
                                        club_list?.addAll(clubListpojo[0].data)
                                    }
                                } else {
                                    if (!userData!!.clubs.isNullOrEmpty()) {
                                        val firstListIds = userData!!.clubs?.map { it.clubID }
                                        val new = clubListpojo[0].data?.filter { it.clubID !in firstListIds!! }
                                        club_list?.addAll((new))
                                        clubAdapter?.notifyDataSetChanged()
                                    } else {
                                        club_list?.addAll(clubListpojo[0].data)
                                    }
                                }
                                clubListApi()
                                clubAdapter?.notifyDataSetChanged()
                            } else {

                                if (club_list!!.size == 0) {
                                    ll_no_data_found.visibility = View.VISIBLE
                                    recyclerview.visibility = View.GONE
                                } else {
                                    ll_no_data_found.visibility = View.GONE
                                    recyclerview.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            btn_previous_club.backgroundTint = (resources.getColor(R.color.transperent1))
                            btn_previous_club.textColor = resources.getColor(R.color.colorPrimary)
                            btn_previous_club.strokeColor = (resources.getColor(R.color.grayborder))
                            errorMethod()
                        }
                    })

    }

    private fun clubListApi() {
        var userPreiviosClub = userData?.previousclubName?.split(",")
        addClubListData!!.clear()
        if(!userPreiviosClub.isNullOrEmpty())
        {
            for (j in 0 until userPreiviosClub?.size!!) {
                for (i in 0 until club_list?.size!!) {
                    if(club_list!![i].clubName.equals(userPreiviosClub[j].trim()))
                    {
                        addClubListData?.add(club_list!![i]!!)
                    }
                }
            }

        }
        clubListAdapter?.notifyDataSetChanged()


    }

    private fun deleteClub(userclubID: String, position: Int) {
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("languageID", "1")
            jsonObject.put("userclubID", userclubID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        addClubModel.getClubList(mActivity!!, false, jsonArray.toString(), "Delete")
                .observe(this@PreviousFragment!!,
                    { clubListpojo ->
                        MyUtils.dismissProgressDialog()
                        if (clubListpojo != null && clubListpojo.isNotEmpty()) {

                            if (clubListpojo[0].status.equals("true", false)) {

//                                    val userData = sessionManager!!.userData
                                if (userData!!.clubs!!.size > 0) {
                                    deleteList?.add(addClubListData!![position])
                                    Log.e("deletelist", deleteList.toString())
                                    for (i in 0 until userData!!.clubs!!.size) {
                                        if (addClubListData!![position]!!.clubID.equals(userData!!.clubs!![i]!!.userclubID)) {
                                            userData!!.clubs!!.removeAt(i)
                                            sessionManager!!.userData = userData
                                            addClubListData!!.removeAt(position)
                                            break
                                        }
                                    }
                                    clubListAdapter!!.notifyDataSetChanged()
                                    suggestedClubList()
                                    clubAdapter?.notifyDataSetChanged()

                                }


                            } else {
                                if (activity != null && activity is MainActivity)
                                    MyUtils.showSnackbar(mActivity!!, clubListpojo[0].message, ll_main_previousclub)
                            }

                        } else {
                            ErrorUtil.errorMethod(ll_main_previousclub)

                        }
                    })


    }

    private fun errorMethod() {
        relativeprogressBar.visibility = View.GONE
        try {
            nointernetMainRelativelayout.visibility = View.VISIBLE
            if (MyUtils.isInternetAvailable(mActivity!!)) {
                nointernetImageview.setImageDrawable(mActivity!!.getDrawable(R.drawable.ic_warning_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_crash_error_message))
            } else {
                nointernetImageview.setImageDrawable(mActivity!!.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_common_network))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getPriousClub() {
        btn_previous_club.startAnimation()
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("leagueID", userData?.leagueID)
            jsonObject.put("contractsituationID", userData?.contractsituationID)
            jsonObject.put("userContractExpiryDate", userData?.userContractExpiryDate)
            jsonObject.put("previousclubName", addClubList?.joinToString { it?.clubName })
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

        updateResumeCallsViewModel.getUpdateResume(mActivity!!, "Add", jsonArray?.toString())
                .observe(viewLifecycleOwner,
                    { countryListPojo ->
                        if (countryListPojo != null) {
                            btn_previous_club.endAnimation()
                            if (countryListPojo.get(0).status.equals("true", false)) {
                                try {
                                    Log.e("",countryListPojo.get(0).data[0].userPreviousClubID)
                                    StoreSessionManager(countryListPojo.get(0).data[0])
                                    Handler().postDelayed({
                                        (activity as MainActivity).onBackPressed()
                                    }, 1000)
                                    MyUtils.showSnackbar(mActivity!!, countryListPojo.get(0).message, ll_main_previousclub)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            } else {
                                MyUtils.showSnackbar(mActivity!!, countryListPojo.get(0).message, ll_main_previousclub)
                            }

                        } else {
                            btn_previous_club.endAnimation()
                            ErrorUtil.errorMethod(ll_main_previousclub)
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