package com.nxplayr.fsl.ui.fragments.userskillsendorsment.view


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity

import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userskillsendorsment.adapter.SkillsListAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userskillsendorsment.viewmodel.SkillsEndorsementsModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.UsersSkils
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_skills_endorsements.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class SkillsEndorsementsFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var skill_list: ArrayList<UsersSkils?>? = ArrayList()
    var skillsAdapter: SkillsListAdapter? = null
    var mActivity: Activity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var linearLayoutManager: LinearLayoutManager? = null
    private var y: Int = 0
    var pageNo = 0
    var userId = ""
    private lateinit var  skillsEndorsementsModel: SkillsEndorsementsModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_skills_endorsements, container, false)
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
            userId = arguments?.getString("userId", "")!!
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
            if (userId.equals(userData?.userID)) {
                jsonObject.put("loginuserID", userData?.userID)
            } else {
                jsonObject.put("loginuserID", userId)
            }
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        skillsEndorsementsModel.getSkillsList(mActivity!!, false, jsonArray.toString(), "List")
            .observe(viewLifecycleOwner
            ) { skillsListPojo ->

                relativeprogressBar.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE

                if (skillsListPojo != null && skillsListPojo.isNotEmpty()) {
                    if (skillsListPojo[0].status.equals("true", true)) {
                        skill_list?.clear()
                        skill_list?.addAll(skillsListPojo!![0]!!.data!!)
                        if (skillsListPojo[0].data.size > 0) {
                            tv_skill_added.text =
                                "Skills Added " + "(" + skillsListPojo[0].data.size + ")"
                        }
                        if (userId.equals(userData?.userID)) {

                            StoreSessionManager(userData!!)
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

                    ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                }
            }

    }

    private fun setupViewModel() {
        skillsEndorsementsModel = ViewModelProvider(this@SkillsEndorsementsFragment).get(
            SkillsEndorsementsModel::class.java)
    }

    private fun setupUI() {
        tvToolbarTitle1.setText(R.string.skills_endorsements)

        if (userId.equals(userData?.userID)) {
            add_icon_connection.visibility = View.VISIBLE
        } else {
            add_icon_connection.visibility = View.GONE

        }

        search_skill_endoresements.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                skillsAdapter!!.filter?.filter(p0.toString())
                skillsAdapter?.notifyDataSetChanged()
            }

        })

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }

        add_icon_connection.setOnClickListener(this)

        skillsAdapter = SkillsListAdapter(
            mActivity!!,
            skill_list,
            object : SkillsListAdapter.OnItemClick {

                override fun onClicled(position: Int, from: String, skillData: UsersSkils) {
                    when (from) {
                        "delete_skill" -> {
                            search_skill_endoresements.text.clear()
                            deleteSkillsListApi(skillData.userskillID, position, skillData)
                        }
                    }
                }
            },
            userId,
            userData?.userID
        )
        recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = skillsAdapter
        skillsAdapter?.notifyDataSetChanged()
        setupObserver()

        btnRetry!!.setOnClickListener(this)
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
            .observe(this@SkillsEndorsementsFragment,
                { deleteSkillsPojo ->
                    MyUtils.dismissProgressDialog()

                    if (deleteSkillsPojo != null) {

                        if (deleteSkillsPojo.get(0).status.equals("true", false)) {
                            try {

//                                        val data = sessionManager?.userData
                                if (userData!!.skills.size > 0) {

                                    for (i in 0 until userData!!.skills.size) {

                                        if (skillData!!.userskillID.equals(userData!!.skills[i].userskillID)) {
                                            userData!!.skills.remove(skillData)
                                            tv_skill_added.text =
                                                "Skills Added " + "(" + userData!!.skills!!.size + ")"
                                            StoreSessionManager(userData)
                                            skill_list?.remove(skillData)
                                            break
                                        }
                                    }

                                    skillsAdapter?.notifyDataSetChanged()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            pageNo = 0
                            setupObserver()

                        } else {
                            MyUtils.dismissProgressDialog()
                            MyUtils.showSnackbar(
                                mActivity!!,
                                deleteSkillsPojo.get(0).message,
                                ll_mainSkillsList
                            )
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(ll_mainSkillsList)
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
        when (v?.id) {
            R.id.add_icon_connection -> {
                (activity as MainActivity).navigateTo(
                    AddSkillsEndorsementsFragment(),
                    AddSkillsEndorsementsFragment::class.java.name,
                    true
                )
            }
            R.id.btnRetry -> {
                pageNo = 0
                setupObserver()
            }
        }
    }


}
