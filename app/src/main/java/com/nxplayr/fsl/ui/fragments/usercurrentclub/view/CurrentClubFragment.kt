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
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_current_club.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar2.toolbar
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class CurrentClubFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: Activity? = null
    var club_list: ArrayList<ClubListData>? = ArrayList()
    var addClubList: ArrayList<ClubListData>? = ArrayList()
    var clubAdapter: CurrentClubAdapter? = null
    var addClubListAdapter: AddClubListAdapter? = null
    var addClubListData: ArrayList<ClubData>? = ArrayList()
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var club: ClubListData? = null
    var clubListAdapter: ClubListAdapter? = null
    var infaltorScheduleMode: LayoutInflater? = null
    var clubID = ""
    var dif = ""
    var deleteList: ArrayList<ClubData>? = ArrayList()
    var fromProfile=""
    var userId=""
    var otherUserData: SignupData?=null

    private lateinit var  clubListModel: ClubListModel
    private lateinit var  addClubModel: AddClubModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                     savedInstanceState: Bundle?): View? {

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

        if (arguments != null)
        {
            fromProfile = arguments!!.getString("fromProfile")!!
            userId=arguments!!.getString("userId","")
            if(!userId.equals(userData?.userID,false))
            {
                otherUserData = arguments!!.getSerializable("otherUserData") as SignupData?
            }
        }
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle.visibility = View.VISIBLE
        tvToolbarTitle.setText(R.string.current_club)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }

        if(!userId.equals(userData?.userID,false))
        {
            btn_save_current_club.visibility=View.GONE

            if (!otherUserData!!.clubs.isNullOrEmpty()) {
                setClubData(otherUserData!!.clubs)
            }
        }
        else{
            btn_save_current_club.visibility=View.VISIBLE

            if (!userData!!.clubs.isNullOrEmpty()) {
                setClubData(userData!!.clubs)
            }

        }

        btn_save_current_club.setOnClickListener(this)

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
                suggestedClubList(p0.toString().trim())
            }

        })

        btnRetry.setOnClickListener (this)

    }

    private fun setupViewModel() {
         clubListModel = ViewModelProvider(this@CurrentClubFragment).get(ClubListModel::class.java)
         addClubModel = ViewModelProvider(this@CurrentClubFragment).get(AddClubModel::class.java)
    }

    fun suggstedlist() {
        clubAdapter = CurrentClubAdapter(activity as MainActivity, club_list!!, object : CurrentClubAdapter.OnItemClick {

            override fun onClicled(clubData: ClubListData?, position: Int) {
                addClubList(clubData!!)

                club_list?.remove(clubData)
                edit_searchClub.text.clear()
                clubAdapter?.notifyDataSetChanged()

                if (addClubList!!.size > 0) {
                    btn_save_current_club.strokeColor = (resources.getColor(R.color.colorPrimary))
                    btn_save_current_club.backgroundTint = (resources.getColor(R.color.colorPrimary))
                    btn_save_current_club.textColor = resources.getColor(R.color.black)
                }
            }

        },userId)
        recyclerview.layoutManager = LinearLayoutManager(mActivity)
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = clubAdapter
        suggestedClubList("")
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
                            btn_save_current_club.strokeColor = (resources.getColor(R.color.grayborder))
                            btn_save_current_club.backgroundTint = (resources.getColor(R.color.transperent1))
                            btn_save_current_club.textColor = resources.getColor(R.color.colorPrimary)
                        }
                    }
                }
            }
        },userId)

        clubID = club!!.clubID
        if(!addClubList.isNullOrEmpty())
        {
            club_list?.add(addClubList!![0])
            clubAdapter?.notifyDataSetChanged()
            addClubList!!.removeAt(0)
        }
        addClubList!!.add(club)
        RV_selectedClubList.layoutManager = LinearLayoutManager(mActivity)
        RV_selectedClubList.setHasFixedSize(true)
        RV_selectedClubList.adapter = addClubListAdapter
        addClubListAdapter?.notifyDataSetChanged()


    }

    private fun setClubData(clubs: java.util.ArrayList<ClubData>) {

        clubListAdapter = ClubListAdapter(activity as MainActivity, addClubListData!!, object : ClubListAdapter.OnItemClick {

            override fun onClicled(position: Int, from: String) {
                deleteClub(addClubListData!![position].userclubID, position)

            }
        },userId)

        RV_addedClubList.layoutManager = LinearLayoutManager(mActivity)
        RV_addedClubList.setHasFixedSize(true)
        RV_addedClubList.adapter = clubListAdapter
        clubListAdapter?.notifyDataSetChanged()
        clubListApi()

    }

    private fun suggestedClubList(searchWord:String) {
        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("searchWord", searchWord)

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
                                if(!userId.equals(userData?.userID,false))
                                {
                                    if (!otherUserData!!.clubs.isNullOrEmpty()) {
                                    val firstListIds = otherUserData!!.clubs?.map { it.clubID }
                                    val new = clubListpojo[0].data?.filter { it.clubID !in firstListIds!! }
                                    club_list?.addAll((new))
                                    clubAdapter?.notifyDataSetChanged()
                                } else {
                                    club_list?.addAll(clubListpojo[0].data)
                                }
                                }
                                else
                                {
                                    if (!userData!!.clubs.isNullOrEmpty()) {
                                        val firstListIds = userData!!.clubs?.map { it.clubID }
                                        val new = clubListpojo[0].data?.filter { it.clubID !in firstListIds!! }
                                        club_list?.addAll((new))
                                        clubAdapter?.notifyDataSetChanged()
                                    } else {
                                        club_list?.addAll(clubListpojo[0].data)
                                    }
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
                            btn_save_current_club.backgroundTint = (resources.getColor(R.color.transperent1))
                            btn_save_current_club.textColor = resources.getColor(R.color.colorPrimary)
                            btn_save_current_club.strokeColor = (resources.getColor(R.color.grayborder))
                            ErrorUtil.errorView(mActivity!!,ll_mainClubDetails)

                        }
                    })

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

            if (!addClubList.isNullOrEmpty()) {

                for (i in 0 until addClubList!!.size) {
                    val clubDetailPojo = JSONObject()
                    clubDetailPojo.put("clubName", addClubList!![i].clubName)
                    clubDetailPojo.put("clubID", addClubList!![i].clubID)
                    jsonArrayclubData.put(clubDetailPojo)
                }
            }
            jsonObject.put("clubdetails", jsonArrayclubData)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)

         addClubModel.getClubList(mActivity!!, false, jsonArray.toString(), "Add")
                .observe(this@CurrentClubFragment!!,
                    { clubListPojo ->
                        if (clubListPojo != null) {
                            btn_save_current_club.endAnimation()
                            if (clubListPojo.get(0).status.equals("true", false)) {
                                try {
                                    userData?.clubs?.clear()
                                    userData?.clubs?.addAll(clubListPojo.get(0).data)
                                    StoreSessionManager(userData)
                                    Handler().postDelayed({
                                        (activity as MainActivity).onBackPressed()
                                    }, 1000)
                                    MyUtils.showSnackbar(mActivity!!, clubListPojo.get(0).message, ll_mainClubDetails)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            } else {
                                MyUtils.showSnackbar(mActivity!!, clubListPojo.get(0).message, ll_mainClubDetails)
                            }

                        } else {
                            btn_save_current_club.endAnimation()
                            ErrorUtil.errorMethod(ll_mainClubDetails)
                        }
                    })


    }

    private fun clubListApi() {

//        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
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
                .observe(viewLifecycleOwner,
                        { clubListPojo ->
                            if (clubListPojo != null && clubListPojo.isNotEmpty()) {

                                if (clubListPojo[0].status.equals("true", false)) {
//                                    MyUtils.dismissProgressDialog()
                                    addClubListData!!.clear()
                                    addClubListData!!.addAll(clubListPojo[0].data)
                                    clubListAdapter?.notifyDataSetChanged()

                                } else {
//                                    MyUtils.dismissProgressDialog()
                                    MyUtils.showSnackbar(mActivity!!, clubListPojo[0].message, ll_mainClubDetails)
                                }

                            } else {
//                                MyUtils.dismissProgressDialog()
                                ErrorUtil.errorView(mActivity!!,ll_mainClubDetails)
                            }
                        })

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
                .observe(this@CurrentClubFragment!!, { clubListpojo ->
                            MyUtils.dismissProgressDialog()
                            if (clubListpojo != null && clubListpojo.isNotEmpty()) {

                                if (clubListpojo[0].status.equals("true", false)) {

//                                    val userData = sessionManager!!.userData
                                    if (userData!!.clubs!!.size > 0) {
                                        deleteList?.add(addClubListData!![position])
                                        Log.e("deletelist", deleteList.toString())
                                        for (i in 0 until userData!!.clubs!!.size) {
                                            if (addClubListData!![position]!!.userclubID.equals(userData!!.clubs!![i]!!.userclubID)) {
                                                userData!!.clubs!!.removeAt(i)
                                                sessionManager!!.userData = userData
                                                addClubListData!!.removeAt(position)
                                                break
                                            }
                                        }
                                        clubListAdapter!!.notifyDataSetChanged()

                                        suggestedClubList("")
                                        clubAdapter?.notifyDataSetChanged()

                                    }


                                } else {
                                    if (activity != null && activity is MainActivity)
                                        MyUtils.showSnackbar(mActivity!!, clubListpojo[0].message, ll_mainClubDetails)
                                }

                            } else {
                                ErrorUtil.errorMethod(ll_mainClubDetails)

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

    interface ClubListUpdateListener {
        fun onclubListUpdate()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_save_current_club->{
                MyUtils.hideKeyboard1(mActivity!!)
                if (!addClubList!!.isEmpty() || !addClubListData!!.isEmpty()) {
                    addClubListApi()
                } else {
                    MyUtils.showSnackbar(mActivity!!, "Please add Clubs", ll_mainClubDetails)
                }

            }
            R.id.btnRetry->{
                suggestedClubList("")
                clubListApi()
            }
        }
    }

}
