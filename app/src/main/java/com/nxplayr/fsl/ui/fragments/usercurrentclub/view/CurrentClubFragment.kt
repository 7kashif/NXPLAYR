package com.nxplayr.fsl.ui.fragments.usercurrentclub.view


import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.usercurrentclub.adapter.AddClubListAdapter
import com.nxplayr.fsl.ui.fragments.usercurrentclub.adapter.ClubListAdapter
import com.nxplayr.fsl.ui.fragments.usercurrentclub.adapter.CurrentClubAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.usercurrentclub.viewmodel.AddClubModel
import com.nxplayr.fsl.ui.fragments.usercurrentclub.viewmodel.ClubListModel
import com.nxplayr.fsl.data.model.ClubData
import com.nxplayr.fsl.data.model.ClubListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import com.nxplayr.fsl.ui.fragments.usercurrentclub.adapter.SuggestedClubAdapter
import com.nxplayr.fsl.util.PaginationScrollListener
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_current_club.*
import kotlinx.android.synthetic.main.fragment_current_club.edit_searchClub
import kotlinx.android.synthetic.main.fragment_previous.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar2.toolbar
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class CurrentClubFragment : Fragment(), View.OnClickListener {

    private lateinit var linearLayout: LinearLayoutManager
    private var v: View? = null
    private var mIsLastPage = false
    private var mIsLoading = false
    private var mPage: Int = 1
    private var searchWord: String = ""
    var mActivity: Activity? = null
    var club_list: ArrayList<ClubListData>? = ArrayList()
    var myclub_list: ArrayList<ClubListData>? = ArrayList()
    var deleteClub_list: ArrayList<ClubListData>? = ArrayList()
    var clubAdapter: SuggestedClubAdapter? = null
    var myclubAdapter: CurrentClubAdapter? = null

    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var clubID = ""
    var dif = ""
    var fromProfile = ""
    var userId = ""
    var otherUserData: SignupData? = null
    var first: Boolean = true

    private lateinit var clubListModel: ClubListModel
    private lateinit var addClubModel: AddClubModel

    private val handler = Handler()
    private val input_finish_checker = Runnable {
        mPage = 1
        suggestedClubList(searchWord)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (v == null) {
            v = inflater.inflate(R.layout.fragment_current_club, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity

    }


    @RequiresApi(Build.VERSION_CODES.N)
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

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngCurrentClub.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngCurrentClub
            if (!sessionManager?.LanguageLabel?.lngSelectedClub.isNullOrEmpty())
                selected_club.text = sessionManager?.LanguageLabel?.lngSelectedClub
            if (!sessionManager?.LanguageLabel?.lngSuggestions.isNullOrEmpty())
                tv_suggestions.text = sessionManager?.LanguageLabel?.lngSuggestions
            if (!sessionManager?.LanguageLabel?.lngSearch.isNullOrEmpty())
                edit_searchClub.hint = sessionManager?.LanguageLabel?.lngSearch
            if (!sessionManager?.LanguageLabel?.lngSave.isNullOrEmpty())
                btn_save_current_club.progressText = sessionManager?.LanguageLabel?.lngSave
        }
    }

    private fun setupUI() {
        tvToolbarTitle.visibility = View.VISIBLE
        tvToolbarTitle.setText(R.string.current_club)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }

        if (!userId.equals(userData?.userID, false)) {
            btn_save_current_club.visibility = View.GONE
        } else {
            btn_save_current_club.visibility = View.VISIBLE
        }


        btn_save_current_club.setOnClickListener(this)

        clubListApi()
        suggstedlist()

        if (!userData!!.clubs.isNullOrEmpty()) {
            btn_save_current_club.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_save_current_club.textColor = resources.getColor(R.color.black)
            btn_save_current_club.strokeColor = (resources.getColor(R.color.colorPrimary))
        } else {
            btn_save_current_club.strokeColor = (resources.getColor(R.color.grayborder))
            btn_save_current_club.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_save_current_club.textColor = resources.getColor(R.color.colorPrimary)
        }

        edit_searchClub.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                suggestedClubList(p0.toString().trim())
                handler.removeCallbacks(input_finish_checker)
                searchWord = p0.toString()
                handler.postDelayed(
                    input_finish_checker,
                    500
                )
            }

        })

        btnRetry.setOnClickListener(this)

    }

    private fun setupViewModel() {
        clubListModel = ViewModelProvider(this@CurrentClubFragment).get(ClubListModel::class.java)
        addClubModel = ViewModelProvider(this@CurrentClubFragment).get(AddClubModel::class.java)
    }

    fun suggstedlist() {
        clubAdapter = SuggestedClubAdapter(
            activity as MainActivity,
            club_list!!,
            object : SuggestedClubAdapter.OnItemClick {

                override fun onClicled(clubData: ClubListData?, position: Int) {

                    if (first && myclub_list!!.size > 0) {
                        deleteClub_list!!.addAll(myclub_list!!)
                        first = false
                    }
                    myclub_list!!.clear()
                    myclub_list!!.add(clubData!!)
                    myclubAdapter!!.notifyDataSetChanged()

                    if ((myclub_list!!.size > 0)) {
                        btn_save_current_club.backgroundTint =
                            (resources.getColor(R.color.colorPrimary))
                        btn_save_current_club.textColor = resources.getColor(R.color.black)
                        btn_save_current_club.strokeColor =
                            (resources.getColor(R.color.colorPrimary))
                    } else {
                        btn_save_current_club.strokeColor = (resources.getColor(R.color.grayborder))
                        btn_save_current_club.backgroundTint =
                            (resources.getColor(R.color.transperent1))
                        btn_save_current_club.textColor = resources.getColor(R.color.colorPrimary)
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
        suggestedClubList(searchWord)
        clubAdapter?.notifyDataSetChanged()

        scrollViewCurrent.viewTreeObserver
            .addOnScrollChangedListener {
                if (scrollViewCurrent != null) {
                    if (scrollViewCurrent.getChildAt(0).bottom
                        <= scrollViewCurrent.height + scrollViewCurrent.scrollY
                    ) {
                        //scroll view is at bottom
                        if (!mIsLoading && !mIsLastPage) {
                            this@CurrentClubFragment.mIsLoading = true
                            Log.e("TAG", "PAGE : $mPage")
                            suggestedClubList(searchWord)
                        }
                    }
                }
            }
    }

    private fun suggestedClubList(searchWord: String) {
        if (mPage == 1) {
            relativeprogressBar.visibility = View.VISIBLE
        } else {
            paginationCurrent.visibility = View.VISIBLE
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
                recyclerview.visibility = View.VISIBLE
                paginationCurrent.visibility = View.GONE

                mIsLoading = false
                if (clubListpojo != null) {

                    if (clubListpojo.status) {

                        if (mPage == 1) {
                            club_list?.clear()
                        }

                        if (!userId.equals(userData?.userID, false)) {
                            if (!otherUserData!!.clubs.isNullOrEmpty()) {
                                val firstListIds = otherUserData!!.clubs?.map { it.clubID }
                                val new =
                                    clubListpojo.data?.filter { it.clubID !in firstListIds!! }
                                club_list?.addAll((new))
                                clubAdapter?.notifyDataSetChanged()
                            } else {
                                club_list?.addAll(clubListpojo.data)
                            }
                        } else {
                            if (!userData!!.clubs.isNullOrEmpty()) {
                                val firstListIds = userData!!.clubs?.map { it.clubID }
                                val new =
                                    clubListpojo.data?.filter { it.clubID !in firstListIds!! }

                                club_list?.addAll((new))
                                clubAdapter?.notifyDataSetChanged()
                            } else {
                                club_list?.addAll(clubListpojo.data)
                            }
                        }
                        mIsLastPage = mPage == clubListpojo.totalPages
                        if (!mIsLastPage) {
                            mPage++
                        }
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
                    btn_save_current_club.backgroundTint =
                        (resources.getColor(R.color.transperent1))
                    btn_save_current_club.textColor = resources.getColor(R.color.colorPrimary)
                    btn_save_current_club.strokeColor = (resources.getColor(R.color.grayborder))
                    ErrorUtil.errorView(mActivity!!, ll_mainClubDetails)
                }
            }
    }

    private fun addClubListApi() {

        btn_save_current_club.startAnimation()

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        val jsonArrayclubData = JSONArray()

        try {

            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)

            if (!myclub_list.isNullOrEmpty()) {

                for (i in 0 until myclub_list!!.size) {
                    val clubDetailPojo = JSONObject()
                    clubDetailPojo.put("clubName", myclub_list!![i].clubName)
                    clubDetailPojo.put("clubID", myclub_list!![i].clubID)
                    jsonArrayclubData.put(clubDetailPojo)
                }
            }
            jsonObject.put("clubdetails", jsonArrayclubData)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)

        addClubModel.getClubList(mActivity!!, false, jsonArray.toString(), "Add")
            .observe(
                this@CurrentClubFragment!!
            ) { clubListPojo ->
                if (clubListPojo != null) {
                    btn_save_current_club.endAnimation()
                    if (clubListPojo.get(0).status) {
                        try {
                            userData?.clubs?.clear()
                            userData?.clubs?.addAll(clubListPojo.get(0).data)
                            StoreSessionManager(userData)
                            Handler().postDelayed({
                                (activity as MainActivity).onBackPressed()
                            }, 1000)
                            MyUtils.showSnackbar(
                                mActivity!!,
                                clubListPojo.get(0).message,
                                ll_mainClubDetails
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            clubListPojo.get(0).message,
                            ll_mainClubDetails
                        )
                    }

                } else {
                    btn_save_current_club.endAnimation()
                    ErrorUtil.errorMethod(ll_mainClubDetails)
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
                        deleteClub(clubData.userclubID, position, true)
                    }

//                    if (!deleteClub_list!!.contains(clubData!!))
//                        deleteClub_list!!.add(clubData)

                    myclub_list!!.clear()
                    myclubAdapter!!.notifyDataSetChanged()

                    if ((myclub_list!!.size > 0)) {
                        btn_save_current_club.backgroundTint =
                            (resources.getColor(R.color.colorPrimary))
                        btn_save_current_club.textColor = resources.getColor(R.color.black)
                        btn_save_current_club.strokeColor =
                            (resources.getColor(R.color.colorPrimary))
                    } else {
                        btn_save_current_club.strokeColor = (resources.getColor(R.color.grayborder))
                        btn_save_current_club.backgroundTint =
                            (resources.getColor(R.color.transperent1))
                        btn_save_current_club.textColor = resources.getColor(R.color.colorPrimary)
                    }
                }
            },
            userId
        )
        RV_addedClubList.layoutManager = LinearLayoutManager(mActivity)
        RV_addedClubList.setHasFixedSize(true)
        RV_addedClubList.adapter = myclubAdapter
        myclubAdapter?.notifyDataSetChanged()

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        addClubModel.getClubList(mActivity!!, false, jsonArray.toString(), "List")
            .observe(
                viewLifecycleOwner
            ) { clubListPojo ->
                if (clubListPojo != null && clubListPojo.isNotEmpty()) {

                    if (clubListPojo[0].status) {
//                                    MyUtils.dismissProgressDialog()
                        myclub_list!!.clear()
                        for (i in 0 until clubListPojo[0].data.size) {
                            clubListPojo[0].data[i].selected = true
                            myclub_list!!.add(clubListPojo[0].data[i])
                        }
                        myclubAdapter?.notifyDataSetChanged()

                    } else {
//                                    MyUtils.dismissProgressDialog()
                        MyUtils.showSnackbar(
                            mActivity!!,
                            clubListPojo[0].message,
                            ll_mainClubDetails
                        )
                    }

                } else {
//                                MyUtils.dismissProgressDialog()
                    ErrorUtil.errorView(mActivity!!, ll_mainClubDetails)
                }
            }

    }

    private fun deleteClub(userclubID: String, position: Int, progress: Boolean) {
        if (progress)
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
            .observe(this@CurrentClubFragment) {

                if (!progress) {
                    deleteClub_list!!.removeAt(position)
                }
                MyUtils.dismissProgressDialog()
//                if (clubListpojo != null && clubListpojo.isNotEmpty()) {
//
////                    if (clubListpojo[0].status.equals("true", false)) {
////
//////                                    val userData = sessionManager!!.userData
//////                        if (userData!!.clubs!!.size > 0) {
//////                            deleteList?.add(addClubListData!![position])
//////                            Log.e("deletelist", deleteList.toString())
//////                            for (i in 0 until userData!!.clubs!!.size) {
//////                                if (addClubListData!![position]!!.userclubID.equals(userData!!.clubs!![i]!!.userclubID)) {
//////                                    userData!!.clubs!!.removeAt(i)
//////                                    sessionManager!!.userData = userData
//////                                    addClubListData!!.removeAt(position)
//////                                    break
//////                                }
//////                            }
//////                            clubListAdapter!!.notifyDataSetChanged()
////
//////                            suggestedClubList("")
//////                            clubAdapter?.notifyDataSetChanged()
//////
//////                        }
////
////
////                    } else {
////                        if (activity != null && activity is MainActivity)
////                            MyUtils.showSnackbar(
////                                mActivity!!,
////                                clubListpojo[0].message,
////                                ll_mainClubDetails
////                            )
////                    }
//
//                } else {
//                    ErrorUtil.errorMethod(ll_mainClubDetails)
//
//                }
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

    interface ClubListUpdateListener {
        fun onclubListUpdate()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_save_current_club -> {
                MyUtils.hideKeyboard1(mActivity!!)

                if (deleteClub_list!!.isNotEmpty()) {
                    for (i in 0 until deleteClub_list!!.size) {
                        deleteClub(deleteClub_list!![i].userclubID, i, false)
                    }
                }

                if (myclub_list!!.isNotEmpty()) {
                    addClubListApi()
                } else {
                    MyUtils.showSnackbar(mActivity!!, "Please add Clubs", ll_mainClubDetails)
                }

            }
            R.id.btnRetry -> {
                mPage = 1
                suggestedClubList(searchWord)
                clubListApi()
            }
        }
    }

}
