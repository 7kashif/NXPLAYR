package com.nxplayr.fsl.ui.fragments.userwebsite.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.nxplayr.fsl.ui.activity.addstaticwebsite.view.AddStaticWebsiteActivity
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userwebsite.adapter.StaticWebsiteAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userwebsite.viewmodel.WebsiteListModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.SiteList
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_set_of_skills.*
import kotlinx.android.synthetic.main.fragment_static_website.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.tvToolbarTitle
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class StaticWebsiteFragment : Fragment(), View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    private var v: View? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var mActivity: AppCompatActivity? = null
    var swipeCount = 0
    var linearLayoutManager: LinearLayoutManager? = null
    var website_list: ArrayList<SiteList?>? = ArrayList()
    var web_links: ArrayList<String> = ArrayList()
    var websiteListAdapter: StaticWebsiteAdapter? = null
    var fromProfile = ""
    var userId = ""
    var otherUserData: SignupData? = null
    private lateinit var getLanguageModel: WebsiteListModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_static_website, container, false)

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
            fromProfile = arguments!!.getString("fromProfile", "")
            userId = arguments!!.getString("userId", "")
            if (!userId.equals(userData?.userID, false)) {
                imgAddWebsite.visibility = View.GONE
                otherUserData = arguments!!.getSerializable("otherUserData") as SignupData?
            } else {
                imgAddWebsite.visibility = View.VISIBLE
                userData = sessionManager?.get_Authenticate_User()
            }
        }
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle.text = getString(R.string.static_website)
        if (userId.equals(userData?.userID, false)) {
            imgAddWebsite.setOnClickListener(this)
        }
        imgBackWebsite.setOnClickListener(this)
        getWebsiteList()
        swipe_website_layout.setOnRefreshListener(this)
    }

    private fun setupViewModel() {
        getLanguageModel =
            ViewModelProvider(this@StaticWebsiteFragment).get(WebsiteListModel::class.java)
    }

    fun getWebsiteList() {
        linearLayoutManager = LinearLayoutManager(mActivity!!, LinearLayoutManager.VERTICAL, false)

        rc_website.layoutManager = linearLayoutManager
        websiteListAdapter =
            StaticWebsiteAdapter(mActivity!!, object : StaticWebsiteAdapter.OnItemClick {
                override fun setOnClickListener(pos: Int) {

                    val bottomSheet = WebsiteBottomSheet()
                    bottomSheet.show(fragmentManager!!, "BottomSheetExplore")
                    Bundle().apply {
                        putString("urlID", website_list!![pos]!!.userurlID)
                        putString("urlTitle", website_list!![pos]!!.userurlName)
                        putString("urlLink", website_list!![pos]!!.userurlLink)
                        putInt("position", pos)
                        bottomSheet.arguments = this
                    }
                    bottomSheet.setOnclickLisner(object : WebsiteBottomSheet.BottomSheetListener {
                        override fun onOptionClick(text: String) {
                            when (text) {
                                "Delete" -> {
                                    webSiteListApi(
                                        "1", if (userId.equals(userData?.userID, false)) {
                                            userData?.userID!!
                                        } else {
                                            otherUserData?.userID
                                        }!!, RestClient.apiType, RestClient.apiVersion
                                    )
                                }
                            }
                        }

                    })
                }

            }, website_list!!, false)

        rc_website.setHasFixedSize(true)
        rc_website.adapter = websiteListAdapter

        webSiteListApi(
            "1", if (userId.equals(userData?.userID, false)) {
                userData?.userID!!
            } else {
                otherUserData?.userID
            }!!, RestClient.apiType, RestClient.apiVersion
        )
    }

    override fun onRefresh() {
        swipeCount += 1
        if (swipeCount > 0) {
            websiteListAdapter!!.notifyDataSetChanged()
            swipe_website_layout.isRefreshing = false
            getWebsiteList()
        }
    }

    private fun webSiteListApi(
        languageID: String,
        loginuserID: String,
        apiType: String,
        apiVersion: String
    ) {

        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("languageID", languageID)
            jsonObject.put("loginuserID", loginuserID)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        getLanguageModel.getWebsiteList(mActivity!!, false, jsonArray.toString())
            .observe(viewLifecycleOwner,
                Observer { websiteListPojo ->

                    relativeprogressBar.visibility = View.GONE

                    if (websiteListPojo != null && websiteListPojo.isNotEmpty()) {

                        if (websiteListPojo[0].status.equals("true", true)) {
                            website_list?.clear()
                            website_list?.addAll(websiteListPojo[0].data!!)
                            web_links.clear()
                            for (i in 0 until websiteListPojo[0].data!!.size) {
                                web_links.add(websiteListPojo[0].data!![i].userurlName!!)
                            }
                           sessionManager?.setWebLinks(web_links.joinToString())

                            websiteListAdapter?.notifyDataSetChanged()
                            if (website_list?.size == 0) {
                                ll_no_data_found.visibility = View.VISIBLE
                            } else {
                                ll_no_data_found.visibility = View.GONE
                            }
                        } else {
                            if (website_list!!.size == 0) {
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

    override fun onResume() {
        super.onResume()
        webSiteListApi("1", userData?.userID!!, RestClient.apiType, RestClient.apiVersion)
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngLinkWebsite.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngLinkWebsite
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.imgAddWebsite -> {
                val addWebsite = Intent(context, AddStaticWebsiteActivity::class.java)
                addWebsite.putExtra("urlID", "")
                addWebsite.putExtra("frmString", "Add")
                startActivity(addWebsite)
            }

            R.id.imgBackWebsite -> {
                MyUtils.hideKeyboard1(mActivity!!)
                (activity as MainActivity).onBackPressed()
            }
        }
    }

}
