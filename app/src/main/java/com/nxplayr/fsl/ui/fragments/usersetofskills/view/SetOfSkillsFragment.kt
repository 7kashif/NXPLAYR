package com.nxplayr.fsl.ui.fragments.usersetofskills.view

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
import com.nxplayr.fsl.ui.fragments.usersetofskills.adapter.AddSkillListAdapter
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.adapter.*
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.viewmodel.*
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import com.nxplayr.fsl.data.model.ClubListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.SkillsPojo
import com.nxplayr.fsl.data.model.UsersSkils
import com.nxplayr.fsl.ui.fragments.dialogs.MessageDialog
import com.nxplayr.fsl.ui.fragments.dialogs.PasswordInfoDialog
import com.nxplayr.fsl.ui.fragments.usersetofskills.adapter.SetOfSkillAdapter
import com.nxplayr.fsl.ui.fragments.usersetofskills.adapter.SkillListAdapter
import com.nxplayr.fsl.ui.fragments.usersetofskills.viewmodel.SetOfSkillsListModel
import com.nxplayr.fsl.ui.fragments.userskillsendorsment.viewmodel.SkillsEndorsementsModel
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_current_club.*
import kotlinx.android.synthetic.main.fragment_preferre_outfitters.*
import kotlinx.android.synthetic.main.fragment_set_of_skills.*
import kotlinx.android.synthetic.main.fragment_set_of_skills.RV_addedClubList
import kotlinx.android.synthetic.main.fragment_set_of_skills.RV_selectedClubList
import kotlinx.android.synthetic.main.fragment_set_of_skills.btn_save_current_club
import kotlinx.android.synthetic.main.fragment_set_of_skills.edit_searchClub
import kotlinx.android.synthetic.main.fragment_set_of_skills.ll_mainClubDetails
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SetOfSkillsFragment : Fragment(), View.OnClickListener {

    private lateinit var select_skill: String
    private var max_skill: String? = "You can select maximum 7 Skills."
    private var v: View? = null
    var mActivity: AppCompatActivity? = null

    var club_list: ArrayList<UsersSkils>? = ArrayList()
    var addSaveClubList: ArrayList<UsersSkils>? = ArrayList()
    var clubAdapter: SetOfSkillAdapter? = null
//    var addClubListAdapter: AddSkillListAdapter? = null

    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var clubListAdapter: SkillListAdapter? = null
    var clubID = ""
    var deleteList: ArrayList<UsersSkils>? = ArrayList()
    var fromProfile = ""
    var userId = ""
    var otherUserData: SignupData? = null
    private lateinit var clubListModel: SetOfSkillsListModel
    private lateinit var addClubModel: SkillsEndorsementsModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_set_of_skills, container, false)
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        select_skill = getString(R.string.skills_added)
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngCurrentSkill.isNullOrEmpty())
                tvToolbarTitle1.text = sessionManager?.LanguageLabel?.lngCurrentSkill
            if (!sessionManager?.LanguageLabel?.lngMaxSkillsMessage.isNullOrEmpty())
                max_skill = sessionManager?.LanguageLabel?.lngMaxSkillsMessage
            if (!sessionManager?.LanguageLabel?.lngSkillAdded.isNullOrEmpty())
                select_skill = sessionManager?.LanguageLabel?.lngSkillAdded.toString()
            if (!sessionManager?.LanguageLabel?.lngSuggestedSkillBased.isNullOrEmpty())
                tv_skill_added.text = sessionManager?.LanguageLabel?.lngSuggestedSkillBased
            if (!sessionManager?.LanguageLabel?.lngSearchSkills.isNullOrEmpty())
                edit_searchClub.hint = sessionManager?.LanguageLabel?.lngSearchSkills
            if (!sessionManager?.LanguageLabel?.lngSave.isNullOrEmpty())
                btn_save_current_club.progressText = sessionManager?.LanguageLabel?.lngSave
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tvToolbarTitle1.text = getString(R.string.set_of_skill)
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

    private fun setupUI() {
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()!!
            if (!userId.equals(userData?.userID, false)) {
                btn_save_current_club.visibility = View.GONE
                add_icon_connection.visibility = View.GONE
                if (!otherUserData?.skills.isNullOrEmpty()) {
                    addSaveClubList?.clear()
                    addSaveClubList?.addAll(otherUserData?.skills!!)
                    setClubData()
                }
            } else {
                btn_save_current_club.visibility = View.VISIBLE
                add_icon_connection.visibility = View.GONE
                if (!userData?.skills.isNullOrEmpty()) {
                    addSaveClubList?.clear()
                    addSaveClubList?.addAll(userData?.skills!!)
                    setClubData()
                }
            }

        }

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        btn_save_current_club.setOnClickListener(this)

        suggstedlist()

        if (!userData!!.skills.isNullOrEmpty()) {
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
                clubAdapter!!.filter?.filter(p0.toString())
                clubAdapter?.notifyDataSetChanged()
            }

        })

        btnRetry.setOnClickListener(this)
        info_skills.setOnClickListener(this)
    }

    private fun setupViewModel() {
        clubListModel =
            ViewModelProvider(this@SetOfSkillsFragment).get(SetOfSkillsListModel::class.java)
        addClubModel =
            ViewModelProvider(this@SetOfSkillsFragment).get(SkillsEndorsementsModel::class.java)

    }

    fun updateText() {
        selected_skills.text =
            select_skill + " (" + addSaveClubList?.size!! + ") "
    }

    fun suggstedlist() {
        clubAdapter = SetOfSkillAdapter(
            activity as MainActivity,
            club_list!!,
            object : SetOfSkillAdapter.OnItemClick {

                override fun onClicled(clubData: UsersSkils?, position: Int) {
                    if (userId.equals(userData?.userID, false)) {

                        if (addSaveClubList?.size!! < 7) {
                            clubID = clubData!!.skillID
                            addSaveClubList!!.add(clubData)
                            clubListAdapter?.notifyDataSetChanged()

                            club_list?.remove(clubData)
                            edit_searchClub.text.clear()
                            clubAdapter?.notifyDataSetChanged()

                            updateText()
                        } else {
                            val dialog = MessageDialog(0, activity!!,"You can select maximum 7 Skills.")
                            dialog.show()
                        }

                        if (addSaveClubList!!.size > 0) {
                            btn_save_current_club.strokeColor =
                                (resources.getColor(R.color.colorPrimary))
                            btn_save_current_club.backgroundTint =
                                (resources.getColor(R.color.colorPrimary))
                            btn_save_current_club.textColor = resources.getColor(R.color.black)
                        }
                    }

                }

            },
            userId
        )
        recyclerview.layoutManager = LinearLayoutManager(mActivity)
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = clubAdapter
        suggestedClubList()
        clubAdapter?.notifyDataSetChanged()
    }

    fun addClubList(club: UsersSkils) {
//        ll_mainAddClubSetSkill.visibility = View.GONE
//        addClubListAdapter = AddSkillListAdapter(activity as MainActivity, addSaveClubList, object : AddSkillListAdapter.OnItemClick {
//            override fun onClicled(position: Int, from: String) {
//                when (from) {
//                    "removefromList" -> {
////                        deleteClub(addSaveClubList!![position].userskillID, position)
//
//                        if (!addSaveClubList.isNullOrEmpty()) {
//                            club_list?.add(addSaveClubList!![position])
//                            clubAdapter?.notifyDataSetChanged()
//                            addSaveClubList!!.removeAt(position)
//                        }
//                        addClubListAdapter?.notifyDataSetChanged()
//                        if (addSaveClubList!!.size == 0) {
//                            btn_save_current_club.strokeColor = (resources.getColor(R.color.grayborder))
//                            btn_save_current_club.backgroundTint = (resources.getColor(R.color.transperent1))
//                            btn_save_current_club.textColor = resources.getColor(R.color.colorPrimary)
//                        }
//                    }
//                }
//            }
//        })
//
//        clubID = club!!.skillID
//        addSaveClubList!!.add(club)
//        Log.e("add", addSaveClubList!!.toString())
//        RV_selectedClubList.layoutManager = LinearLayoutManager(mActivity)
//        RV_selectedClubList.setHasFixedSize(true)
//        RV_selectedClubList.adapter = addClubListAdapter
//        addClubListAdapter?.notifyDataSetChanged()


    }

    private fun setClubData() {

        clubListAdapter =
            SkillListAdapter(mActivity!!, addSaveClubList!!, object : SkillListAdapter.OnItemClick {
                override fun onClicled(position: Int, from: String) {
                    if (userId.equals(userData?.userID, false)) {
                        deleteClub(addSaveClubList!![position].userskillID, position)

                    }
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
        clubListModel.getSkillsList(mActivity!!, false, jsonArray.toString())
            .observe(
                viewLifecycleOwner
            ) { clubListpojo ->
                relativeprogressBar.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE
                if (clubListpojo != null && clubListpojo.isNotEmpty()) {

                    if (clubListpojo[0].status.equals("true", false)) {

                        club_list?.clear()
                        if (!userId.equals(userData?.userID, false)) {
                            if (!otherUserData!!.skills.isNullOrEmpty()) {
                                val firstListIds = otherUserData!!.skills?.map { it.skillID }
                                val new =
                                    clubListpojo[0].data?.filter { it.skillID !in firstListIds!! }
                                club_list?.addAll((new))
                                clubAdapter?.notifyDataSetChanged()
                            } else {
                                club_list?.addAll(clubListpojo[0].data)
                            }
                        } else {
                            if (!userData!!.skills.isNullOrEmpty()) {
                                val firstListIds = userData!!.skills?.map { it.skillID }
                                val new =
                                    clubListpojo[0].data?.filter { it.skillID !in firstListIds!! }
                                club_list?.addAll((new))
                                clubAdapter?.notifyDataSetChanged()
                            } else {
                                club_list?.addAll(clubListpojo[0].data)
                            }
                        }

                        clubAdapter?.notifyDataSetChanged()
                        updateText()
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
                    errorMethod()
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

            if (!addSaveClubList.isNullOrEmpty()) {

                for (i in 0 until addSaveClubList!!.size) {
                    val clubDetailPojo = JSONObject()
                    clubDetailPojo.put("skillName", addSaveClubList!![i].skillName)
                    clubDetailPojo.put("skillID", addSaveClubList!![i].skillID)
                    jsonArrayclubData.put(clubDetailPojo)
                }
            }
            jsonObject.put("skilldetails", jsonArrayclubData)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        addClubModel.getSkillsList(mActivity!!, false, jsonArray.toString(), "Add")
            .observe(viewLifecycleOwner,
                androidx.lifecycle.Observer { clubListPojo ->
                    if (clubListPojo != null) {
                        btn_save_current_club.endAnimation()
                        if (clubListPojo.get(0).status.equals("true", false)) {
                            try {
                                userData?.skills?.clear()
                                userData?.skills?.addAll(clubListPojo.get(0).data)
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
        addClubModel.getSkillsList(mActivity!!, false, jsonArray.toString(), "List")
            .observe(viewLifecycleOwner,
                androidx.lifecycle.Observer { clubListPojo ->
                    if (clubListPojo != null && clubListPojo.isNotEmpty()) {

                        if (clubListPojo[0].status.equals("true", false)) {
//                                    MyUtils.dismissProgressDialog()
                            addSaveClubList!!.clear()
                            addSaveClubList!!.addAll(clubListPojo[0].data)
                            clubListAdapter?.notifyDataSetChanged()

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
                        errorMethod()
                    }
                })

    }

    private fun deleteClub(userclubID: String, position: Int) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("languageID", "1")
            jsonObject.put("userskillID", userclubID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

            Log.e("deleteClub", "deleteClub() = ${Gson().toJson(jsonObject)}")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)


        if (userclubID.isNullOrEmpty()) {
            addSaveClubList!!.removeAt(position)
            clubListAdapter!!.notifyDataSetChanged()
            suggestedClubList()
            clubAdapter?.notifyDataSetChanged()
            return
        }
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")

        addClubModel.getSkillsList(mActivity!!, false, jsonArray.toString(), "Delete")
            .observe(this@SetOfSkillsFragment!!,
                androidx.lifecycle.Observer<List<SkillsPojo>> { clubListpojo ->
                    MyUtils.dismissProgressDialog()
                    if (clubListpojo != null && clubListpojo.isNotEmpty()) {

                        if (clubListpojo[0].status.equals("true", false)) {

//                                    val userData = sessionManager!!.userData
                            if (userData!!.skills!!.size > 0) {
                                deleteList?.add(addSaveClubList!![position])
                                Log.e("deletelist", deleteList.toString())
                                for (i in 0 until userData!!.skills!!.size) {
                                    if (addSaveClubList!![position]!!.userskillID.equals(userData!!.skills!![i]!!.userskillID)) {
                                        userData!!.skills!!.removeAt(i)
                                        sessionManager!!.userData = userData
                                        addSaveClubList!!.removeAt(position)
                                        break
                                    }
                                }
                                clubListAdapter!!.notifyDataSetChanged()
                                suggestedClubList()
                                clubAdapter?.notifyDataSetChanged()

                            }


                        } else {
                            if (activity != null && activity is MainActivity)
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    clubListpojo[0].message,
                                    ll_mainClubDetails
                                )
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

    interface ClubListUpdateListener {
        fun onclubListUpdate()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_save_current_club -> {
                MyUtils.hideKeyboard1(mActivity!!)

                if (!addSaveClubList?.isNullOrEmpty()!!) {
                    addClubListApi()
                } else {
                    MyUtils.showSnackbar(mActivity!!, "Please add skills", ll_mainClubDetails)
                }
            }
            R.id.btnRetry -> {
                suggestedClubList()
                clubListApi()
            }
            R.id.info_skills -> {
                val dialog = MessageDialog(0, activity!!, max_skill!!)
                dialog.show()
            }
        }
    }

}