package com.nxplayr.fsl.ui.fragments.feed.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.feed.adapter.LanguageListAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userlanguage.viewmodel.LanguagesModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.data.model.LanguageList
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_select_content_language.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SelectContentLanguage : Fragment(), View.OnClickListener {

    private var v: View? = null
    var mActivity: Activity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var languageList: ArrayList<LanguageList?>? = null
    var pageNo = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var y: Int = 0
    private lateinit var linearLayoutManager: LinearLayoutManager
    var langugelistAdapter: LanguageListAdapter? = null
    var selectedIds = ""
    var lastCheckedPosition = ""
    private lateinit var  languagesModel: LanguagesModel
    private lateinit var  loginModel: SignupModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_select_content_language, container, false)
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
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle.setText(R.string.content_language)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }
        linearLayoutManager = LinearLayoutManager(mActivity)
        if (languageList == null) {
            languageList = ArrayList()

            langugelistAdapter = LanguageListAdapter(
                mActivity!!,
                languageList,
                object : LanguageListAdapter.OnItemClick {
                    override fun onClicled(position: Int, from: String) {
                        for (i in 0 until languageList?.size!!) {
                            if (position == i && from.equals("Selected", false)!!) {
                                languageList?.get(position)?.status = true
                                selectedIds = languageList?.get(position)?.languageID!!
                            } else if (position == i && from.equals("deSelected", false)!!) {
                                languageList?.get(position)?.status = false
                                selectedIds = ""
                            } else {
                                languageList?.get(i)?.status = false
                            }
                        }
                    }

                },
                lastCheckedPosition
            )
            recyclerview.setHasFixedSize(true)
            recyclerview.layoutManager = linearLayoutManager
            recyclerview.adapter = langugelistAdapter

            languageListApi()
        }


        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                y = dy
                visibleItemCount = linearLayoutManager!!.childCount
                totalItemCount = linearLayoutManager!!.itemCount
                firstVisibleItemPosition = linearLayoutManager!!.findFirstVisibleItemPosition()
                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 10
                    ) {

                        isLoading = true
                        languageListApi()
                    }
                }
            }
        })

        btnRetry.setOnClickListener(this)

        btn_update_language.setOnClickListener(this)

    }

    private fun setupViewModel() {
        languagesModel = ViewModelProvider(this@SelectContentLanguage).get(LanguagesModel::class.java)
        loginModel = ViewModelProvider(this@SelectContentLanguage).get(SignupModel::class.java)
    }

    private fun languageListApi() {
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            languageList!!.clear()
            langugelistAdapter!!.notifyDataSetChanged()

        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
            languageList!!.add(null)
            langugelistAdapter!!.notifyItemInserted(languageList!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        languagesModel.getLanguageist(mActivity!!, false, jsonArray.toString())
            .observe(viewLifecycleOwner,
                { languageListpojo ->
                    if (languageListpojo != null && languageListpojo.isNotEmpty()) {
                        isLoading = false
                        ll_no_data_found.visibility = View.GONE
                        nointernetMainRelativelayout.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE

                        if (pageNo > 0) {
                            languageList!!.removeAt(languageList!!.size - 1)
                            langugelistAdapter!!.notifyItemRemoved(languageList!!.size)
                        }
                        if (languageListpojo[0].status.equals("true", false)) {
                            recyclerview.visibility = View.VISIBLE

                            if (pageNo == 0) {
                                languageList?.clear()
                            }
                            languageList!!.clear()
                            if (!userData?.userContentLanguageID.isNullOrEmpty()) {
                                for (i in 0 until languageListpojo?.get(0).data.size!!) {
                                    if (languageListpojo?.get(0).data?.get(i)?.languageID.equals(
                                            userData?.userContentLanguageID
                                        )
                                    ) {
                                        lastCheckedPosition =
                                            languageListpojo?.get(0).data?.get(i)?.languageName
                                        selectedIds =
                                            languageListpojo?.get(0).data?.get(i)?.languageID
                                        break
                                    }
                                }
                            }
                            languageList!!.addAll(languageListpojo[0].data)
                            langugelistAdapter?.notifyDataSetChanged()
                            pageNo += 1

                            if (languageListpojo[0].data.size < 10) {
                                isLastpage = true
                            }

                            if (languageListpojo[0].data.isNullOrEmpty()) {
                                ll_no_data_found.visibility = View.VISIBLE
                                recyclerview.visibility = View.GONE
                            } else {
                                ll_no_data_found.visibility = View.GONE
                                recyclerview.visibility = View.VISIBLE
                            }


                        } else {
                            relativeprogressBar.visibility = View.GONE

                            if (languageListpojo[0].data.isNullOrEmpty()) {
                                ll_no_data_found.visibility = View.VISIBLE
                                recyclerview.visibility = View.GONE
                            } else {
                                ll_no_data_found.visibility = View.GONE
                                recyclerview.visibility = View.VISIBLE
                            }
                        }

                    } else {
                        relativeprogressBar.visibility = View.GONE
                        ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                    }
                })
    }

    private fun changeLanguage() {

        btn_update_language.startAnimation()

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("userContentLanguageID", selectedIds)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)


         loginModel.userRegistration(
            mActivity!!,
            false,
            jsonArray.toString(),
            "changeContentLanguage"
        )
            .observe(viewLifecycleOwner,
                { loginPojo ->
                    btn_update_language.endAnimation()
                    if (loginPojo != null) {
                        if (loginPojo.get(0).status.equals("true", true)) {
                            try {
                                StoreSessionManager(loginPojo[0].data[0])
                                langugelistAdapter?.notifyDataSetChanged()
                                Handler().postDelayed({
                                    (activity as MainActivity).onBackPressed()
                                }, 1000)
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    loginPojo.get(0).message,
                                    ll_sub_content_language
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                loginPojo.get(0).message!!,
                                ll_sub_content_language
                            )
                        }

                    } else {
                        btn_update_language.endAnimation()
                        ErrorUtil.errorMethod(ll_sub_content_language)
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
        langugelistAdapter?.notifyDataSetChanged()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRetry -> {
                pageNo = 0
                languageListApi()
            }
            R.id.btn_update_language -> {
                if (!selectedIds.isNullOrEmpty()) {
                    changeLanguage()
                } else {
                    MyUtils.showSnackbar(
                        mActivity!!,
                        getString(R.string.please_select_language),
                        ll_sub_content_language
                    )
                }
            }

        }
    }

}