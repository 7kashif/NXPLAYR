package com.nxplayr.fsl.ui.fragments.usercurrentclub.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ClubListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.usercurrentclub.adapter.CurrentClubAdapter
import com.nxplayr.fsl.ui.fragments.usercurrentclub.adapter.SuggestedClubAdapter
import com.nxplayr.fsl.ui.fragments.usercurrentclub.viewmodel.AddClubModel
import com.nxplayr.fsl.ui.fragments.usercurrentclub.viewmodel.ClubListModel
import com.nxplayr.fsl.ui.fragments.usergeographical.viewmodel.UpdateResumeCallsViewModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_current_club.*
import kotlinx.android.synthetic.main.fragment_previous.*
import kotlinx.android.synthetic.main.fragment_previous.edit_searchClub
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class PreviousFragment : Fragment() {

    private var v: View? = null
    var mActivity: Activity? = null
    var club_list: ArrayList<ClubListData>? = ArrayList()
    var myclub_list: ArrayList<ClubListData>? = ArrayList()
    var deleteClub_list: ArrayList<ClubListData>? = ArrayList()
    var clubAdapter: SuggestedClubAdapter? = null
    var myclubAdapter: CurrentClubAdapter? = null
    var linearLayout: LinearLayoutManager? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var clubID = ""
    var dif = ""
    var fromProfile = ""
    var userId = ""
    var otherUserData: SignupData? = null
    private var mIsLastPage = false
    private var mIsLoading = false
    private var mPage: Int = 1
    private var searchWord: String = ""
    private lateinit var clubListModel: ClubListModel
    private lateinit var addClubModel: AddClubModel
    private lateinit var updateResumeCallsViewModel: UpdateResumeCallsViewModel
    private val handler = Handler()
    private val input_finish_checker = Runnable {
        mPage = 1
        suggestedClubList(searchWord)
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngPreviousClub.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngPreviousClub
            if (!sessionManager?.LanguageLabel?.lngSelectedClub.isNullOrEmpty())
                tv_selected_club_prev.text = sessionManager?.LanguageLabel?.lngSelectedClub
            if (!sessionManager?.LanguageLabel?.lngClubsSuggestions.isNullOrEmpty())
                tv_suggestions_prev.text = sessionManager?.LanguageLabel?.lngClubsSuggestions
            if (!sessionManager?.LanguageLabel?.lngSearch.isNullOrEmpty())
                edit_searchClub.hint = sessionManager?.LanguageLabel?.lngSearch
            if (!sessionManager?.LanguageLabel?.lngSave.isNullOrEmpty())
                btn_previous_club.progressText = sessionManager?.LanguageLabel?.lngSave
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        } else {
            btn_previous_club.visibility = View.VISIBLE
        }
        btn_previous_club.setOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            if (club_list!!.isNotEmpty()) {
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
//                clubAdapter!!.filter?.filter(p0.toString())
//                clubAdapter?.notifyDataSetChanged()
                handler.removeCallbacks(input_finish_checker)
                searchWord = p0.toString()
                handler.postDelayed(
                    input_finish_checker,
                    500
                )
            }

        })

        btnRetry.setOnClickListener {
            mPage = 1
            suggestedClubList(searchWord)
            clubListApi()
        }
    }

    private fun setupViewModel() {
        clubListModel = ViewModelProvider(this@PreviousFragment).get(ClubListModel::class.java)
        addClubModel = ViewModelProvider(this@PreviousFragment).get(AddClubModel::class.java)
        updateResumeCallsViewModel =
            ViewModelProvider(this@PreviousFragment).get(UpdateResumeCallsViewModel::class.java)

    }

    fun suggstedlist() {
        clubAdapter = SuggestedClubAdapter(
            activity as MainActivity,
            club_list!!,
            object : SuggestedClubAdapter.OnItemClick {

                override fun onClicled(clubData: ClubListData?, position: Int) {
//                    myclub_list!!.clear()
                    if (!myclub_list!!.contains(clubData!!)) {
                        myclub_list!!.add(clubData)
                        myclubAdapter!!.notifyDataSetChanged()
                        club_list!!.removeAt(position)
                        clubAdapter!!.notifyDataSetChanged()
                    }

                    if ((myclub_list!!.size > 0)) {
                        btn_previous_club.backgroundTint =
                            (resources.getColor(R.color.colorPrimary))
                        btn_previous_club.textColor = resources.getColor(R.color.black)
                        btn_previous_club.strokeColor =
                            (resources.getColor(R.color.colorPrimary))
                    } else {
                        btn_previous_club.strokeColor = (resources.getColor(R.color.grayborder))
                        btn_previous_club.backgroundTint =
                            (resources.getColor(R.color.transperent1))
                        btn_previous_club.textColor = resources.getColor(R.color.colorPrimary)
                    }
                }

            },
            userId
        )
        linearLayout = LinearLayoutManager(mActivity)
        recyclerview.layoutManager = linearLayout
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = clubAdapter
        searchWord = ""
        mPage = 1
        suggestedClubList(searchWord)
        clubAdapter?.notifyDataSetChanged()

        scrollView.viewTreeObserver
            .addOnScrollChangedListener {
                if (scrollView != null) {
                    if (scrollView.getChildAt(0).bottom
                        <= scrollView.height + scrollView.scrollY
                    ) {
                        //scroll view is at bottom
                        if (!mIsLoading && !mIsLastPage) {
                            this@PreviousFragment.mIsLoading = true
                            Log.e("TAG", "PAGE : $mPage")
                            suggestedClubList(searchWord)
                        }
                    }
                }
            }

    }

    private fun suggestedClubList(searchWord: String) {
        RestClient.cancelAll()
        if (mPage == 1) {
            relativeprogressBar.visibility = View.VISIBLE
        } else {
            pagination.visibility = View.VISIBLE
        }
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("searchWord", searchWord)
            jsonObject.put("page", mPage)
            jsonObject.put("pageSize", 15)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        clubListModel.getClubList(mActivity!!, false, jsonArray.toString())
            .observe(
                viewLifecycleOwner
            ) { clubListpojo ->

                relativeprogressBar.visibility = View.GONE
                pagination.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE

                mIsLoading = false
                if (clubListpojo != null) {
                    if (clubListpojo.status) {
                        if (mPage == 1) {
                            club_list?.clear()
                        }
                        if (!userId.equals(userData?.userID, false)) {
                            if (!otherUserData!!.clubs.isNullOrEmpty()) {
                                val firstListIds = otherUserData!!.clubs.map { it.clubID }
                                val new =
                                    clubListpojo.data.filter { it.clubID !in firstListIds!! }
                                club_list?.addAll((new))
                                clubAdapter?.notifyDataSetChanged()
                            } else {
                                club_list?.addAll(clubListpojo.data)
                            }
                        } else {
                            if (!userData!!.clubs.isNullOrEmpty()) {
                                val firstListIds = userData!!.clubs.map { it.clubID }
                                val new =
                                    clubListpojo.data.filter { it.clubID !in firstListIds!! }
                                club_list?.addAll((new))
                                clubAdapter?.notifyDataSetChanged()
                            } else {
                                club_list?.addAll(clubListpojo.data)
                            }
                        }
                        if (mPage == 1) {
                            clubListApi()
                        }
                        Log.e("mIsLastPage", "page = $mPage")
                        Log.e("mIsLastPage", "total page = ${clubListpojo.totalPages}")
                        mIsLastPage = mPage == clubListpojo.totalPages
                        Log.e("mIsLastPage", "mIsLastPage = $mIsLastPage")
                        Log.d("mIsLastPage", "===========================================")
                        clubAdapter?.notifyDataSetChanged()
                        if (!mIsLastPage) {
                            mPage++
                        }
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
                    btn_previous_club.backgroundTint =
                        (resources.getColor(R.color.transperent1))
                    btn_previous_club.textColor = resources.getColor(R.color.colorPrimary)
                    btn_previous_club.strokeColor = (resources.getColor(R.color.grayborder))
                    errorMethod()
                }
            }
    }

    private fun clubListApi() {

        myclubAdapter = CurrentClubAdapter(
            activity as MainActivity,
            myclub_list!!,
            object : CurrentClubAdapter.OnItemClick {

                override fun onClicled(clubData: ClubListData?, position: Int) {

                    if (clubData != null) {
//                        deleteClub(clubData.userclubID, position)
                        myclub_list!!.removeAt(position)
                        myclubAdapter!!.notifyDataSetChanged()
                    }

                    if ((myclub_list!!.size > 0)) {
                        btn_previous_club.backgroundTint =
                            (resources.getColor(R.color.colorPrimary))
                        btn_previous_club.textColor = resources.getColor(R.color.black)
                        btn_previous_club.strokeColor =
                            (resources.getColor(R.color.colorPrimary))
                    } else {
                        btn_previous_club.strokeColor = (resources.getColor(R.color.grayborder))
                        btn_previous_club.backgroundTint =
                            (resources.getColor(R.color.transperent1))
                        btn_previous_club.textColor = resources.getColor(R.color.colorPrimary)
                    }
                }
            },
            userId
        )
        RV_prevClubList.layoutManager = LinearLayoutManager(mActivity)
        RV_prevClubList.setHasFixedSize(true)
        RV_prevClubList.adapter = myclubAdapter
        myclubAdapter?.notifyDataSetChanged()

        var userPreiviosClub = userData?.previousclubName?.split(",")
        myclub_list!!.clear()
        if (club_list?.size!! > 0) {
            if (!userPreiviosClub.isNullOrEmpty()) {
                for (j in userPreiviosClub.indices) {
                    club_list!![j].selected = true
                    myclub_list?.add(club_list!![j])
                }
            }
        }
        myclubAdapter?.notifyDataSetChanged()

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
            .observe(
                this@PreviousFragment!!
            ) { clubListpojo ->
                MyUtils.dismissProgressDialog()
                if (clubListpojo != null && clubListpojo.isNotEmpty()) {

                    if (clubListpojo[0].status) {

//                                    val userData = sessionManager!!.userData
//                        if (userData!!.clubs!!.size > 0) {
//                            deleteList?.add(addClubListData!![position])
//                            Log.e("deletelist", deleteList.toString())
//                            for (i in 0 until userData!!.clubs!!.size) {
//                                if (addClubListData!![position]!!.clubID.equals(userData!!.clubs!![i]!!.userclubID)) {
//                                    userData!!.clubs!!.removeAt(i)
//                                    sessionManager!!.userData = userData
//                                    addClubListData!!.removeAt(position)
//                                    break
//                                }
//                            }
//                            clubListAdapter!!.notifyDataSetChanged()
//                            suggestedClubList()
//                            clubAdapter?.notifyDataSetChanged()
//
//                        }


                    } else {
                        if (activity != null && activity is MainActivity)
                            MyUtils.showSnackbar(
                                mActivity!!,
                                clubListpojo[0].message,
                                ll_main_previousclub
                            )
                    }

                } else {
                    ErrorUtil.errorMethod(ll_main_previousclub)

                }
            }


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
            jsonObject.put("userPreviousClubID", myclub_list?.joinToString { it.clubID })
            jsonObject.put("previousclubName", myclub_list?.joinToString { it.clubName })
            jsonObject.put("userJersyNumber", userData?.userJersyNumber)
            jsonObject.put("usertrophies", userData?.usertrophies)
            jsonObject.put("outfitterIDs", userData?.outfitterIDs)
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

        updateResumeCallsViewModel.getUpdateResume(mActivity!!, "Add", jsonArray?.toString())
            .observe(
                viewLifecycleOwner
            ) { countryListPojo ->
                if (countryListPojo != null) {
                    btn_previous_club.endAnimation()
                    if (countryListPojo.get(0).status.equals("true", false)) {
                        try {
                            Log.e("", countryListPojo.get(0).data[0].userPreviousClubID)
                            StoreSessionManager(countryListPojo.get(0).data[0])
                            Handler().postDelayed({
                                (activity as MainActivity).onBackPressed()
                            }, 1000)
                            MyUtils.showSnackbar(
                                mActivity!!,
                                countryListPojo.get(0).message,
                                ll_main_previousclub
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            countryListPojo.get(0).message,
                            ll_main_previousclub
                        )
                    }

                } else {
                    btn_previous_club.endAnimation()
                    ErrorUtil.errorMethod(ll_main_previousclub)
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
}