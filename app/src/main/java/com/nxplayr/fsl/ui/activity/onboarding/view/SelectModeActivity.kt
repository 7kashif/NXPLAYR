package com.nxplayr.fsl.ui.activity.onboarding.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.onboarding.adapter.UserRoleListAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.UserRoleListModel
import com.nxplayr.fsl.data.model.UserRoleListData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.activity_select_mode.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.item_signup_select_football_type_adapter.view.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList


@Suppress("DEPRECATION")
class SelectModeActivity : AppCompatActivity(), View.OnClickListener {

    var selectModeType: Int = 0
    var userRoleListData: ArrayList<UserRoleListData>? = ArrayList()
    var gridLayoutManager: GridLayoutManager? = null
    var userRoleListAdapter: UserRoleListAdapter? = null
    var from: String = ""
    var sessionManager: SessionManager? = null
    var apputypeID = "0"
    var userName = ""
    var userEmail = ""
    var socialID = ""
    private lateinit var userRoleListModel: UserRoleListModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_mode)


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
        }

        setupViewModel()
        setupUI()

    }

    private fun setupUI() {
        sessionManager = SessionManager(this@SelectModeActivity)
        tvToolbarTitle.visibility = View.GONE

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngSelectModeQue.isNullOrEmpty())
                tv_you_are.text = sessionManager?.LanguageLabel?.lngSelectModeQue
            if (!sessionManager?.LanguageLabel?.lngSelectModeQueDetail.isNullOrEmpty())
                tv_selectModetext.text = sessionManager?.LanguageLabel?.lngSelectModeQueDetail
            if (!sessionManager?.LanguageLabel?.lngNext.isNullOrEmpty())
                btnNextSelectMode.progressText = sessionManager?.LanguageLabel?.lngNext
        }

        userRoleListAdapter = UserRoleListAdapter(
            this,
            object : UserRoleListAdapter.OnItemClick {
                @SuppressLint("ResourceType")
                override fun onClicklisneter(pos: Int, name: String) {

                    selectModeType = intent.getIntExtra("selectModeType", pos)
                    apputypeID = userRoleListData!![pos].apputypeID

                    for (i in 0 until userRoleListData!!.size) {
                        userRoleListData!![i].checked = i == pos
                        when (pos) {
                            0 -> {
                                btnNextSelectMode.backgroundTint =
                                    (resources.getColor(R.color.colorPrimary))
                                btnNextSelectMode.textColor = (resources.getColor(R.color.black))
                                btnNextSelectMode.strokeColor =
                                    (resources.getColor(R.color.colorPrimary))
                            }
                            1 -> {
                                btnNextSelectMode.backgroundTint =
                                    (resources.getColor(R.color.yellow_modes))
                                btnNextSelectMode.textColor = (resources.getColor(R.color.black))
                                btnNextSelectMode.strokeColor =
                                    (resources.getColor(R.color.yellow_modes))
                            }
                            2 -> {
                                btnNextSelectMode.backgroundTint =
                                    (resources.getColor(R.color.colorAccent))
                                btnNextSelectMode.textColor = (resources.getColor(R.color.black))
                                btnNextSelectMode.strokeColor =
                                    (resources.getColor(R.color.colorAccent))
                            }
                        }
                    }
                    userRoleListAdapter?.notifyDataSetChanged()
                }
            }, userRoleListData
        )
        recyclerview.setHasFixedSize(true)
        gridLayoutManager = GridLayoutManager(this, 2).also {
            it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position % 3 == 2)
                        2
                    else
                        1
                }
            }
        }

        recyclerview.layoutManager = gridLayoutManager
        recyclerview.adapter = userRoleListAdapter
        userRoleListAdapter?.notifyDataSetChanged()
        getuserRoleList()
        btnNextSelectMode.strokeColor = resources.getColor(R.color.grayborder)


        btnNextSelectMode.setOnClickListener(this)

        btnRetry.setOnClickListener(this)
    }

    private fun setupViewModel() {
        userRoleListModel =
            ViewModelProvider(this@SelectModeActivity).get(UserRoleListModel::class.java)

    }

    fun Validation() {
        if (selectModeType == 0 && apputypeID.toInt() != 0) {
            var intent =
                Intent(this@SelectModeActivity, SignupSelectFootballTypeActivity::class.java)
            intent.putExtra("selectModeType", selectModeType)
            intent.putExtra("apputypeID", apputypeID.toInt())
            intent.putExtra("userName", userName)
            intent.putExtra("userEmail", userEmail)
            intent.putExtra("socialID", socialID)
            startActivity(intent)


        } else if (selectModeType == 1 && apputypeID.toInt() != 0) {
            val intent = Intent(this@SelectModeActivity, SignUpSelectionActivity::class.java)
            intent.putExtra("selectModeType", selectModeType)
            intent.putExtra("apputypeID", apputypeID.toInt())
            intent.putExtra("userName", userName)
            intent.putExtra("userEmail", userEmail)
            intent.putExtra("socialID", socialID)
            startActivity(intent)

        } else if (selectModeType == 2 && apputypeID.toInt() != 0) {
            var intent = Intent(this@SelectModeActivity, SignUpSelectionActivity::class.java)
            intent.putExtra("selectModeType", selectModeType)
            intent.putExtra("apputypeID", apputypeID.toInt())
            intent.putExtra("userName", userName)
            intent.putExtra("userEmail", userEmail)
            intent.putExtra("socialID", socialID)
            startActivity(intent)

        } else {
            MyUtils.showSnackbar(
                applicationContext,
                getString(R.string.please_select_mode),
                ll_mainSelectMode
            )
        }
    }

    fun getuserRoleList() {
        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        var jsonArray = JSONArray()
        jsonArray.put(jsonObject)

        userRoleListModel.getUserRoleList(this, false, jsonArray.toString())
            .observe(
                this@SelectModeActivity
            ) { userRolePojo ->

                relativeprogressBar.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE
                if (userRolePojo != null) {
                    if (userRolePojo[0].status.equals("true", false)) {
                        userRoleListData?.clear()
                        userRoleListData?.addAll(userRolePojo[0].data.sortedBy { it.apputypeID })
                        userRoleListAdapter?.notifyDataSetChanged()
                    } else {
                        if (userRoleListData!!.size == 0) {
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
            if (MyUtils.isInternetAvailable(this@SelectModeActivity)) {
                nointernetImageview.setImageDrawable(this@SelectModeActivity.getDrawable(R.drawable.ic_warning_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_crash_error_message))
            } else {
                nointernetImageview.setImageDrawable(this@SelectModeActivity.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_common_network))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnNextSelectMode -> {
                Validation()
            }
            R.id.btnRetry -> {
                getuserRoleList()
            }

        }
    }
}
