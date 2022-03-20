package com.nxplayr.fsl.ui.activity.onboarding.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nxplayr.fsl.ui.activity.onboarding.adapter.SignUpSelectUserTypeAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.FootballTypeListModel
import com.nxplayr.fsl.data.model.FootballTypeListData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.R
import kotlinx.android.synthetic.main.activity_user_type.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class SignupSelectUserTypeActivity : AppCompatActivity(), View.OnClickListener {

    var signUpSelectUserTypeAdapter: SignUpSelectUserTypeAdapter? = null
    var footballTypeListData: ArrayList<FootballTypeListData>? = ArrayList()
    private lateinit var gridLayoutManager: GridLayoutManager
    var v: View? = null
    var selectModeType: Int = 0
    var sessionManager: SessionManager? = null
    var apputypeID = 0
    var appuroleID = "0"
    private lateinit var footballTypeListModel: FootballTypeListModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_type)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        if (intent != null && intent.hasExtra("selectModeType"))
            selectModeType = intent.getIntExtra("selectModeType", 0)
        if (intent != null && intent.hasExtra("apputypeID"))
            apputypeID = intent.getIntExtra("apputypeID", 0)

        setupViewModel()
        setupUI()

    }

    private fun setupUI() {
        gridLayoutManager = GridLayoutManager(this, 3)
        signUpSelectUserTypeAdapter =
            SignUpSelectUserTypeAdapter(
                this,
                object : SignUpSelectUserTypeAdapter.OnItemClick {
                    override fun onClicklisneter(pos: Int, name: String) {
                        appuroleID = footballTypeListData!![pos].appuroleID!!
                        for (i in 0 until footballTypeListData?.size!!) {
                            footballTypeListData!![i].checked = i == pos
                            btnSelectUserType.backgroundTint =
                                (resources.getColor(R.color.colorPrimary))
                            btnSelectUserType.textColor = (resources.getColor(R.color.black))
                        }
                        signUpSelectUserTypeAdapter?.notifyDataSetChanged()
                    }
                }, footballTypeListData
            )
        recyclerview.layoutManager = gridLayoutManager
        recyclerview.adapter = signUpSelectUserTypeAdapter
        getFootballTypeList()

        btnRetry.setOnClickListener (this)
        btnSelectUserType.setOnClickListener(this)


    }

    private fun setupViewModel() {
         footballTypeListModel = ViewModelProvider(this@SignupSelectUserTypeActivity).get(
             FootballTypeListModel::class.java)
    }

    fun validation() {
        if (appuroleID.toInt() != 0) {
            var intent = Intent(
                this@SignupSelectUserTypeActivity,
                SignupSelectFootballTypeActivity::class.java
            )
            intent.putExtra("appuroleID", appuroleID.toInt())
            intent.putExtra("apputypeID", apputypeID.toInt())
            startActivity(intent)

        } else {
            MyUtils.showSnackbar(
                applicationContext,
                getString(R.string.please_select_user_type),
                ll_MainUserType
            )
        }
    }


    private fun getFootballTypeList() {
        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
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
        footballTypeListModel.getFootballTypeList(this!!, false, jsonArray.toString())
            .observe(this@SignupSelectUserTypeActivity!!,
                { footballTypeListpojo ->

                    relativeprogressBar.visibility = View.GONE
                    recyclerview.visibility = View.VISIBLE

                    if (footballTypeListpojo != null) {
                        if (footballTypeListpojo.get(0).status.equals("true", false)) {

                            MyUtils.dismissProgressDialog()

                            footballTypeListData?.clear()
                            footballTypeListData?.addAll(footballTypeListpojo.get(0).data)
                            signUpSelectUserTypeAdapter?.notifyDataSetChanged()

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
                })

    }


    private fun errorMethod() {
        relativeprogressBar.visibility = View.GONE
        try {
            nointernetMainRelativelayout.visibility = View.VISIBLE
            if (MyUtils.isInternetAvailable(this@SignupSelectUserTypeActivity)) {
                nointernetImageview.setImageDrawable(this@SignupSelectUserTypeActivity.getDrawable(R.drawable.ic_warning_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_crash_error_message))
            } else {
                nointernetImageview.setImageDrawable(this@SignupSelectUserTypeActivity.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_common_network))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        MyUtils.finishActivity(this, true)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRetry -> {
                getFootballTypeList()

            }
            R.id.btnSelectUserType -> {
                validation()

            }
        }
    }
}