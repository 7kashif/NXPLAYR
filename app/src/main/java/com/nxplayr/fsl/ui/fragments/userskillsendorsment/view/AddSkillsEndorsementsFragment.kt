package com.nxplayr.fsl.ui.fragments.userskillsendorsment.view


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
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.UsersSkils
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.userskillsendorsment.adapter.AddSkillsAdapter
import com.nxplayr.fsl.ui.fragments.userskillsendorsment.adapter.SkillsAdapter
import com.nxplayr.fsl.ui.fragments.userskillsendorsment.adapter.SkillsListAdapter
import com.nxplayr.fsl.ui.fragments.userskillsendorsment.viewmodel.SkillsEndorsementsModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_add_skills_endorsement.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class AddSkillsEndorsementsFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var skill_list: ArrayList<UsersSkils>? = ArrayList()
    var skillList: ArrayList<UsersSkils?>? = ArrayList()
    var addskillList: ArrayList<UsersSkils>? = ArrayList()
    var skillsAdapter: SkillsAdapter? = null
    var skillListAdapter: SkillsListAdapter? = null
    var addskillsAdapter: AddSkillsAdapter? = null
    internal var infaltor: LayoutInflater? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var skillId = ""
    var skillName = ""
    private var skillsUpdateListener: SkillsUpdateListener? = null
    private lateinit var  skillsEndorsementsModel: SkillsEndorsementsModel

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (v == null)
            v = inflater.inflate(R.layout.fragment_add_skills_endorsement, container, false)

        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        try {
            skillsUpdateListener = context as SkillsUpdateListener
        } catch (e: Exception) {
              e.printStackTrace()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
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
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("searchWord", "")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        skillsEndorsementsModel.getSkillsList(mActivity!!, false, jsonArray.toString(), "List")
            .observe(
                viewLifecycleOwner
            ) { skillsListpojo ->

                relativeprogressBar.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE

                if (skillsListpojo != null && skillsListpojo.isNotEmpty()) {

                    if (skillsListpojo[0].status.equals("true", false)) {

                        skill_list?.clear()
                        if (!userData!!.skills.isNullOrEmpty()) {
                            val firstListIds = userData!!.skills.map { it.skillID }
                            val new = skillsListpojo[0].businessskills.filter { it.skillID !in firstListIds }
                            skill_list?.addAll((new))
                            skillsAdapter?.notifyDataSetChanged()
                        } else {
                            skill_list?.addAll(skillsListpojo[0].businessskills)
                        }
                        skillsAdapter?.notifyDataSetChanged()


                    } else {

                        if (skill_list!!.size == 0) {
                            ll_no_data_found.visibility = View.VISIBLE
                            recyclerview.visibility = View.GONE

                        } else {
                            ll_no_data_found.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE

                        }
                    }

                } else {
                    ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                }
            }
    }

    private fun setupUI() {
        tvToolbarTitle.text = getString(R.string.add_skills_endorsements)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }


        search_skills.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                skillsAdapter!!.filter.filter(p0.toString())
                skillsAdapter?.notifyDataSetChanged()
            }

        })

        btn_add_skills.setOnClickListener(this)

        ll_mainsearchSkillsList.visibility = View.VISIBLE

        if (!userData?.skills.isNullOrEmpty()) {
            addedSkillList()
        }

        skillsAdapter = SkillsAdapter(mActivity!!, skill_list, object : SkillsAdapter.OnItemClick {

            override fun onClicled(skillData: UsersSkils, from: String) {
                when (from) {
                    "add_skills" -> {
                        addtoskillList(skillData)
                        skill_list?.remove(skillData)
                        search_skills.text.clear()
                        if (addskillList!!.size > 0) {
                            btn_add_skills.backgroundTint = (resources.getColor(R.color.colorPrimary))
                            btn_add_skills.textColor = resources.getColor(R.color.black)
                            btn_add_skills.strokeColor = (resources.getColor(R.color.colorPrimary))

                            if (addskillList!!.size > 0 || userData!!.skills.size > 0) {
                                if (!userData?.skills.isNullOrEmpty()) {
                                    var count = userData!!.skills.size + addskillList!!.size
                                    tv_skill_count.text = "Skills Added " + "(" + count + ")"
                                } else {
                                    tv_skill_count.text = "Skills Added " + "(" + addskillList?.size + ")"
                                }
                            } else if (addskillList!!.size <= 0 || userData!!.skills.size <= 0) {
                                tv_skill_count.text = "Skills Added "
                            }
                        }
                        skillsAdapter?.notifyDataSetChanged()

                    }
                }
            }
        })
        recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = skillsAdapter
        setupObserver()

        if (userData!!.skills.size > 0) {
            btn_add_skills.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_add_skills.textColor = resources.getColor(R.color.black)
            btn_add_skills.strokeColor = (resources.getColor(R.color.colorPrimary))
        } else {
            btn_add_skills.strokeColor = (resources.getColor(R.color.grayborder))
            btn_add_skills.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_add_skills.textColor = resources.getColor(R.color.colorPrimary)
        }

        btnRetry.setOnClickListener(this)
    }

    private fun setupViewModel() {
         skillsEndorsementsModel = ViewModelProvider(this@AddSkillsEndorsementsFragment).get(
             SkillsEndorsementsModel::class.java)
    }


    private fun addtoskillList(skills: UsersSkils) {

        addskillsAdapter = AddSkillsAdapter(mActivity!!, addskillList, object : AddSkillsAdapter.OnItemClick {
            override fun onClicled(pos: Int, from: String) {
                when (from) {
                    "remove_skill" -> {

                        if (!addskillList.isNullOrEmpty()) {
                            skill_list?.add(addskillList!![pos])
                            skillsAdapter?.notifyDataSetChanged()
                            addskillList?.removeAt(pos)
                        }
                        if (addskillList!!.size > 0 || userData!!.skills.size > 0) {
                            if (!userData?.skills.isNullOrEmpty()) {
                                var count = userData!!.skills.size + addskillList!!.size
                                tv_skill_count.text = "Skills Added " + "(" + count + ")"
                            } else {
                                tv_skill_count.text = "Skills Added " + "(" + addskillList?.size + ")"
                            }
                        } else if (addskillList!!.size <= 0 || userData!!.skills.size <= 0) {
                            tv_skill_count.text = "Skills Added "
                        }
                        addskillsAdapter?.notifyDataSetChanged()

                        if (addskillList!!.size == 0) {
                            btn_add_skills.strokeColor = (resources.getColor(R.color.grayborder))
                            btn_add_skills.backgroundTint = (resources.getColor(R.color.transperent1))
                            btn_add_skills.textColor = resources.getColor(R.color.colorPrimary)
                        }
                    }
                }
            }
        })

        skillId = skills.skillID
        skillName = skills.skillName
        addskillList!!.add(skills)
        Log.e("add", addskillList!!.toString())
        RV_suggestedSkills.layoutManager = LinearLayoutManager(activity)
        RV_suggestedSkills.setHasFixedSize(true)
        RV_suggestedSkills.adapter = addskillsAdapter
        addskillsAdapter?.notifyDataSetChanged()

    }


    private fun addedSkillList() {
        skillListAdapter = SkillsListAdapter(activity as MainActivity, skillList!!, object : SkillsListAdapter.OnItemClick {

            override fun onClicled(position: Int, from: String, skillData: UsersSkils) {
                when (from) {
                    "delete_skill" -> {
                        deleteSkillsListApi(skillData.userskillID, position, skillData)
                    }
                }
            }
        },userData?.userID!!,userData?.userID!!)
        RV_addedSkillsList.layoutManager = LinearLayoutManager(activity)
        RV_addedSkillsList.setHasFixedSize(true)
        RV_addedSkillsList.adapter = skillListAdapter
        skillListAdapter?.notifyDataSetChanged()
        addedskillsListApi()
    }

    private fun addSkills() {

        btn_add_skills.startAnimation()

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        val jsonArrayskills = JSONArray()

        try {

            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)

            if (!addskillList.isNullOrEmpty()) {

                for (i in 0 until addskillList!!.size) {
                    val skillsDetailPojo = JSONObject()
                    skillsDetailPojo.put("skillName", addskillList!![i].skillName)
                    skillsDetailPojo.put("skillID", addskillList!![i].skillID)
                    jsonArrayskills.put(skillsDetailPojo)
                }

            }
            jsonObject.put("skilldetails", jsonArrayskills)

            Log.e("addSkills", "addSkills() ${Gson().toJson(jsonObject)}")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        skillsEndorsementsModel.getSkillsList(mActivity!!, false, jsonArray.toString(), "Add")
                .observe(
                    this@AddSkillsEndorsementsFragment,
                        androidx.lifecycle.Observer { loginPojo ->
                            if (loginPojo != null) {
                                btn_add_skills.endAnimation()
                                if (loginPojo.get(0).status.equals("true", false)) {
                                    try {

                                        userData?.skills?.clear()
                                        userData?.skills?.addAll(loginPojo.get(0).businessskills)
                                        if (skillsUpdateListener != null)
                                            skillsUpdateListener!!.onSkillsUpdate()

                                        Handler().postDelayed({
                                            (activity as MainActivity).onBackPressed()
                                        }, 2000)
                                        StoreSessionManager(userData)

                                        MyUtils.showSnackbar(mActivity!!, loginPojo.get(0).message, ll_mainSkillEndorsement)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                } else {
                                    MyUtils.showSnackbar(mActivity!!, loginPojo.get(0).message, ll_mainSkillEndorsement)
                                }

                            } else {
                                btn_add_skills.endAnimation()
                                ErrorUtil.errorMethod(ll_mainSkillEndorsement)
                            }
                        })
    }

    private fun addedskillsListApi() {

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
      skillsEndorsementsModel.getSkillsList(mActivity!!, false, jsonArray.toString(), "List")
                .observe(
                    viewLifecycleOwner
                ) { skillsListPojo ->

                    RV_addedSkillsList.visibility = View.VISIBLE

                    if (skillsListPojo != null && skillsListPojo.isNotEmpty()) {
                        if (skillsListPojo[0].status.equals("true", true)) {
                            skillList?.clear()
                            skillList?.addAll(skillsListPojo[0].businessskills)
                            if (skillsListPojo[0].businessskills.size > 0) {
                                if (addskillList!!.size >= 0 || userData!!.skills.size >= 0) {
                                    if (!userData?.skills.isNullOrEmpty()) {
                                        var count =
                                            skillsListPojo[0].businessskills.size + addskillList!!.size
                                        tv_skill_count.text = "Skills Added " + "(" + count + ")"
                                        skillListAdapter?.notifyDataSetChanged()
                                        addskillsAdapter?.notifyDataSetChanged()
                                    } else {
                                        tv_skill_count.text =
                                            "Skills Added " + "(" + addskillList!!.size + ")"
                                    }
                                } else if (addskillList!!.size <= 0) {
                                    tv_skill_count.text = "Skills Added "
                                }

                            }
                            StoreSessionManager(userData!!)
                            skillListAdapter?.notifyDataSetChanged()


                        } else {

                            if (skillList!!.size == 0) {
                                RV_addedSkillsList.visibility = View.GONE
                            } else {
                                RV_addedSkillsList.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }
                }

    }

    private fun deleteSkillsListApi(userskillID: String, position: Int, skillData: UsersSkils) {

        MyUtils.showProgressDialog(mActivity!!, "Please wait...")

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {

            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("userskillID", userskillID)


        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        skillsEndorsementsModel.getSkillsList(mActivity!!, false, jsonArray.toString(), "Delete")
                .observe(this@AddSkillsEndorsementsFragment
                ) { deleteSkillsPojo ->
                    MyUtils.dismissProgressDialog()

                    if (deleteSkillsPojo != null) {

                        if (deleteSkillsPojo.get(0).status.equals("true", false)) {
                            try {

                                if (userData!!.skills.size > 0) {

                                    for (i in 0 until userData!!.skills.size) {
                                        if (skillData.userskillID.equals(
                                                userData!!.skills[i].userskillID,
                                                false
                                            )
                                        ) {
                                            userData!!.skills.remove(skillData)
                                            sessionManager!!.userData = userData
                                            skillList?.remove(skillData)
                                            break
                                        }
                                    }
                                    skillListAdapter?.notifyDataSetChanged()

                                    setupObserver()
                                    skillsAdapter?.notifyDataSetChanged()

                                    if (addskillList!!.size >= 0 || userData!!.skills.size >= 0) {

                                        var count =
                                            deleteSkillsPojo[0].businessskills.size + addskillList!!.size
                                        tv_skill_count.text = "Skills Added " + "(" + count + ")"

                                    } else if (addskillList!!.size < 0 || deleteSkillsPojo[0].businessskills.size < 0) {
                                        tv_skill_count.text = "Skills Added "
                                    }


                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        } else {
                            MyUtils.dismissProgressDialog()
                            MyUtils.showSnackbar(
                                mActivity!!,
                                deleteSkillsPojo.get(0).message,
                                ll_mainSkillEndorsement
                            )
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(ll_mainSkillEndorsement)
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

    interface SkillsUpdateListener {
        fun onSkillsUpdate()
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btnRetry->{
                setupObserver()
                addedskillsListApi()
            }
            R.id.btn_add_skills->{
                MyUtils.hideKeyboard1(mActivity!!)
                if (!addskillList!!.isEmpty()) {
                    addSkills()
                } else {
                    MyUtils.showSnackbar(mActivity!!, "Please add Skills", ll_mainSkillEndorsement)
                }
            }
        }
    }


}
