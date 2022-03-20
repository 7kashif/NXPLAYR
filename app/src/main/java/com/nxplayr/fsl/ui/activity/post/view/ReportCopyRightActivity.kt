package com.nxplayr.fsl.ui.activity.post.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.ui.fragments.postcomment.viewmodel.ReportReasonsModel
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.data.model.ReportReasonData
import com.nxplayr.fsl.data.model.ReportReasonPojo
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.activity_report_copy_right.*
import kotlinx.android.synthetic.main.fragment_repost_reason.*
import kotlinx.android.synthetic.main.item_select_reason.view.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ReportCopyRightActivity : AppCompatActivity(),View.OnClickListener {

    var reasonlistData: ArrayList<ReportReasonData>? = ArrayList()
    var selectPosition = -1
    internal var infaltor: LayoutInflater? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var resionId = ""
    var reasonText = ""
    var PostId = ""
    var pos: Int = 0
    var postData: ArrayList<CreatePostData?>? = ArrayList()
    var postListData: CreatePostData? = null
    private lateinit var  repostReasonsModel: ReportReasonsModel
    private lateinit var  reportPostModel: CommonStatusModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_copy_right)

        sessionManager = SessionManager(this!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        if (intent != null) {
            pos = intent?.getIntExtra("position", 0)!!
            PostId = intent?.getStringExtra("PostId")!!
            postData = intent?.getSerializableExtra("postList") as ArrayList<CreatePostData?>?
            postListData = intent?.getSerializableExtra("postListData") as CreatePostData?

        }
        setupViewModel()
        setupUI()


    }

    private fun setupUI() {
        closeIconReport.setOnClickListener (this)

        showRepostingReason()
        reportReasonListApi()

        btn_submitReportCopyRight.setOnClickListener(this)
    }

    private fun setupViewModel() {
         repostReasonsModel = ViewModelProvider(this@ReportCopyRightActivity).get(ReportReasonsModel::class.java)
         reportPostModel = ViewModelProvider(this@ReportCopyRightActivity).get(CommonStatusModel::class.java)

    }

    private fun showRepostingReason() {
        reportcopyrightInflateLayout.removeAllViews()
        for (i in 0 until reasonlistData!!.size) {
            infaltor = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
            var convertView = infaltor?.inflate(R.layout.item_select_reason, null)

            convertView!!.rootView.ll_itemView.tag = i
            convertView!!.rootView.ll_itemView.setOnClickListener {
                selectPosition = it.tag as Int
            }


            var radiobutton = convertView.findViewById<AppCompatRadioButton>(R.id.reason_select_item)
            radiobutton.setText(reasonlistData!![i].postreportreasonName)
            if (selectPosition == i) {
                radiobutton.isChecked = true
            }
            radiobutton?.tag = i
            radiobutton?.setOnCheckedChangeListener { buttonView, isChecked ->
                selectPosition = radiobutton.tag as Int
                resionId = ""
                reasonText = ""
                resionId = reasonlistData!![selectPosition].postreportreasonID
                reasonText = reasonlistData!![selectPosition].postreportreasonName
                showRepostingReason()
            }

            reportcopyrightInflateLayout.addView(convertView)


        }

    }

    private fun reportReasonListApi() {
        MyUtils.showProgressDialog(this!!, "Please wait")
        repostingInflateLayout?.visibility = View.GONE
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)

          repostReasonsModel.getReportReasonsList(this@ReportCopyRightActivity!!, false, jsonArray.toString(), "ReportList")
                .observe(this@ReportCopyRightActivity,
                    { reportListPojo ->

                        MyUtils.dismissProgressDialog()

                        if (reportListPojo != null && reportListPojo.isNotEmpty()) {
                            if (reportListPojo[0]!!.status.equals("true", true)) {
                                repostingInflateLayout?.visibility = View.VISIBLE
                                reasonlistData?.clear()
                                reasonlistData?.addAll(reportListPojo!![0]!!.data!!)
                                showRepostingReason()

                            } else {
                                repostingInflateLayout?.visibility = View.GONE
                                MyUtils.dismissProgressDialog()
                                MyUtils.showSnackbar(this!!, reportListPojo[0]!!.message, ll_mainReportCopyRight)

                            }
                        } else {
                            repostingInflateLayout?.visibility = View.GONE
                            MyUtils.dismissProgressDialog()
                            ErrorUtil.errorView(this!!, nointernetMainRelativelayout)
                        }
                    })
    }

    private fun getReportPost(position: String, reasonText: String) {

        MyUtils.showProgressDialog(this!!, "Wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("postreportreasonID", position)
            jsonObject.put("postID", PostId)
            jsonObject.put("userpostreportreasonText", reasonText)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        reportPostModel.getCommonStatus(this!!, false, jsonArray.toString(), "user_Report_Reason")
                .observe(this@ReportCopyRightActivity, Observer { it ->

                    if (it != null && it.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()

                        if (it[0].status.equals("true", true)) {
                            if (it[0].message.isNullOrEmpty()) {

                                MyUtils.showSnackbar(this!!, "You have reported post", ll_mainReportCopyRight)
                            } else {
                                MyUtils.showSnackbar(this!!, it[0].message!!, ll_mainReportCopyRight)
                            }
                            onBackPressed()
                        } else {

                            MyUtils.showSnackbar(this!!, it[0].message!!, ll_mainReportCopyRight)
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        errorMethod()
                    }
                })

    }

    private fun errorMethod() {
        MyUtils.dismissProgressDialog()
        try {
            nointernetMainRelativelayout.visibility = View.VISIBLE
            if (MyUtils.isInternetAvailable(this@ReportCopyRightActivity)) {
                nointernetImageview.setImageDrawable(this@ReportCopyRightActivity.getDrawable(R.drawable.ic_warning_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_crash_error_message))
            } else {
                nointernetImageview.setImageDrawable(this@ReportCopyRightActivity.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_common_network))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.closeIconReport->{
                onBackPressed()
            }
            R.id.btn_submitReportCopyRight->{
                if (reasonText.isNotEmpty() && resionId.isNotEmpty()) {
                    getReportPost(resionId, reasonText)
                } else {
                    MyUtils.showSnackbar(this!!, "Please select reason for report", ll_mainReportCopyRight)
                }
            }
        }
    }
}