package com.nxplayr.fsl.ui.activity.onboarding.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.onboarding.adapter.SignupSelectFootballTypeAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FootballLevelListData
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.FootballLevelModel

import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.activity_select_mode.*
import kotlinx.android.synthetic.main.activity_user_type.*
import kotlinx.android.synthetic.main.activity_user_type.tv_you_are
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class SignupSelectFootballTypeActivity : AppCompatActivity(), View.OnClickListener {

    var signupSelectFootballTypeAdapter: SignupSelectFootballTypeAdapter? = null
    var footballTypeListData: ArrayList<FootballLevelListData>? = ArrayList()
    private lateinit var gridLayoutManager: GridLayoutManager
    var sessionManager: SessionManager? = null
    var apputypeID = 0
    var footbltypeID = "0"
    var userName = ""
    var userEmail = ""
    var socialID = ""
    private lateinit var footballTypeListModel: FootballLevelModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_type)
        supportActionBar?.setDisplayShowCustomEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (intent != null) {
            if (intent.hasExtra("userName")) {
                userName = intent?.getStringExtra("userName")!!
            }
            if (intent.hasExtra("userEmail")) {
                userName = intent?.getStringExtra("userEmail")!!
            }
            if (intent.hasExtra("socialID")) {
                socialID = intent?.getStringExtra("socialID")!!
            }
            if (intent != null && intent.hasExtra("apputypeID"))
                apputypeID = intent.getIntExtra("apputypeID", 0)
        }

        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        sessionManager = SessionManager(this@SignupSelectFootballTypeActivity)

        tvToolbarTitle.visibility = View.GONE

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngFootBallQue.isNullOrEmpty())
                tv_you_are.text = sessionManager?.LanguageLabel?.lngFootBallQue
            if (!sessionManager?.LanguageLabel?.lngFootBallQueDetail.isNullOrEmpty())
                tv_select_gender.text = sessionManager?.LanguageLabel?.lngFootBallQueDetail
            if (!sessionManager?.LanguageLabel?.lngNext.isNullOrEmpty())
                btnSelectUserType.progressText = sessionManager?.LanguageLabel?.lngNext
        }

        getFootballTypeList1()

        btnSelectUserType.strokeColor = resources.getColor(R.color.grayborder)

        btnRetry.setOnClickListener(this)

        btnSelectUserType.setOnClickListener(this)

    }

    private fun setupViewModel() {
        footballTypeListModel = ViewModelProvider(this@SignupSelectFootballTypeActivity).get(
            FootballLevelModel::class.java
        )

    }

    fun validation() {
        if (footbltypeID.toInt() != 0) {
            var intent =
                Intent(this@SignupSelectFootballTypeActivity, SignUpSelectionActivity::class.java)
            intent.putExtra("apputypeID", apputypeID)
            intent.putExtra("footbltypeID", footbltypeID.toInt())
            startActivity(intent)
        } else {
            MyUtils.showSnackbar(
                this@SignupSelectFootballTypeActivity,
                getString(R.string.please_select_football_type),
                ll_MainUserType
            )
        }
    }

    private fun getFootballTypeList1() {

        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("languageID", "1")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        footballTypeListModel.getFootballLevelList(this!!, false, jsonArray.toString())
            .observe(
                this@SignupSelectFootballTypeActivity!!
            ) { footballLevelListPojo ->

                relativeprogressBar.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE

                if (footballLevelListPojo != null) {

                    if (footballLevelListPojo.get(0).status.equals("true", false)) {
                        footballTypeListData?.clear()
                        footballTypeListData?.addAll(footballLevelListPojo.get(0).data)

                        signupSelectFootballTypeAdapter =
                            SignupSelectFootballTypeAdapter(
                                this,
                                object : SignupSelectFootballTypeAdapter.OnItemClick {
                                    override fun onClicklisneter(pos: Int, name: String) {

                                        footbltypeID = footballTypeListData!![pos].footbltypeID!!
                                        for (i in 0 until footballTypeListData!!.size) {
                                            footballTypeListData!![i].checked = i == pos

                                            btnSelectUserType.backgroundTint =
                                                (resources.getColor(R.color.colorPrimary))
                                            btnSelectUserType.textColor = (resources.getColor(R.color.black))
                                            btnSelectUserType.strokeColor =
                                                (resources.getColor(R.color.colorPrimary))

                                        }
                                        signupSelectFootballTypeAdapter?.notifyDataSetChanged()

                                    }
                                }, footballTypeListData!!

                            )
                        gridLayoutManager = GridLayoutManager(this, 2).also {
                            if (footballTypeListData?.size == 5) {
                                it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                                    override fun getSpanSize(position: Int): Int {
                                        return if (position % 3 == 2)
                                            2
                                        else
                                            1
                                    }
                                }
                            }
                        }
                        recyclerview.layoutManager = gridLayoutManager
                        recyclerview.adapter = signupSelectFootballTypeAdapter

                    } else {
                        if (footballTypeListData!!.size == 0) {
                            ll_no_data_found.visibility = View.VISIBLE
                            recyclerview.visibility = View.GONE

                        } else {
                            ll_no_data_found.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE
                        }
                    }
                } else {

                    errorMethod()
                }
            }
    }

    private fun errorMethod() {
        relativeprogressBar.visibility = View.GONE
        try {
            nointernetMainRelativelayout.visibility = View.VISIBLE
            if (MyUtils.isInternetAvailable(this@SignupSelectFootballTypeActivity)) {
                nointernetImageview.setImageDrawable(
                    this@SignupSelectFootballTypeActivity.getDrawable(
                        R.drawable.ic_warning_black_24dp
                    )
                )
                nointernettextview.text = (this.getString(R.string.error_crash_error_message))
            } else {
                nointernetImageview.setImageDrawable(
                    this@SignupSelectFootballTypeActivity.getDrawable(
                        R.drawable.ic_signal_wifi_off_black_24dp
                    )
                )
                nointernettextview.text = (this.getString(R.string.error_common_network))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRetry -> {
                getFootballTypeList1()

            }
            R.id.btnSelectUserType -> {

                validation()
            }
        }
    }
}