package com.nxplayr.fsl.ui.fragments.postcomment.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.ui.fragments.postcomment.viewmodel.ReportReasonsModel
import com.nxplayr.fsl.data.model.ReportReasonData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_repost_reason.*
import kotlinx.android.synthetic.main.item_select_reason.view.*
import kotlinx.android.synthetic.main.item_work_experince_adapter_list.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RepostReasonFragment : androidx.fragment.app.Fragment(),View.OnClickListener {

    var reasonlistData: ArrayList<ReportReasonData>? = ArrayList()
    var v: View? = null
    var activity: AppCompatActivity? = null
    var selectPosition = -1
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    internal var infaltor: LayoutInflater? = null
    var resionId = ""
    var reasonText = ""
    var fromReport = ""
    var PostId = ""
    var commentPostId = ""
    var userFriendId = ""
    var commentID = ""
    private lateinit var  repostReasonsModel: ReportReasonsModel
    private lateinit var  reportFriendModel: CommonStatusModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null)
            v = inflater.inflate(R.layout.fragment_repost_reason, container, false)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowCustomEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        sessionManager = SessionManager(activity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        fromReport = arguments?.getString("fromReport")!!
        PostId = arguments?.getString("PostId", "")!!
        commentPostId = arguments?.getString("postID", "")!!
        userFriendId = arguments?.getString("userfriendId", "")!!
        commentID = arguments?.getString("commentID", "")!!

        setupViewModel()
        setupUI()

    }

    private fun setupUI() {
        toolbar.setNavigationIcon(R.drawable.navigation_empty_icon)
        toolbar.setNavigationIcon(R.drawable.close_icon_white_signup)
        tvToolbarTitle.visibility = View.GONE
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()

        }

        showRepostingReason()
        reportReasonListApi()

        btn_submit_repostReason.setOnClickListener(this)
    }

    private fun setupViewModel() {
         repostReasonsModel = ViewModelProvider(this@RepostReasonFragment).get(ReportReasonsModel::class.java)
         reportFriendModel = ViewModelProvider(this@RepostReasonFragment).get(CommonStatusModel::class.java)
    }

    private fun showRepostingReason() {
        repostingInflateLayout.removeAllViews()
        for (i in 0 until reasonlistData!!.size) {
            infaltor = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
            var convertView = infaltor?.inflate(R.layout.item_select_reason, null)

            convertView!!.rootView.ll_itemView.tag = i
            convertView!!.rootView.ll_itemView.setOnClickListener {
                selectPosition = it.tag as Int
            }
            var radiobutton =
                    convertView?.findViewById<AppCompatRadioButton>(R.id.reason_select_item)
            radiobutton.setText(reasonlistData!![i].postreportreasonName)
            if (selectPosition == i) {

                radiobutton?.isChecked = true
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

            repostingInflateLayout.addView(convertView)
        }

    }


    private fun reportReasonListApi() {
        MyUtils.showProgressDialog(activity!!, "Please wait")
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
        repostReasonsModel.getReportReasonsList(activity!!, false, jsonArray.toString(), "ReportList")
                .observe(viewLifecycleOwner,
                    { reportListPojo ->
                        MyUtils.dismissProgressDialog()
                        if (reportListPojo != null && reportListPojo.isNotEmpty()) {
                            if (reportListPojo[0].status.equals("true", true)) {
                                repostingInflateLayout?.visibility = View.VISIBLE
                                reasonlistData?.clear()
                                reasonlistData?.addAll(reportListPojo!![0]!!.data!!)
                                showRepostingReason()
                            } else {
                                repostingInflateLayout?.visibility = View.GONE
                                MyUtils.dismissProgressDialog()
                                MyUtils.showSnackbar(activity!!, reportListPojo[0].message, ll_mainReportReason)
                            }
                        } else {
                            repostingInflateLayout?.visibility = View.GONE
                            MyUtils.dismissProgressDialog()
                            ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                        }
                    })

    }

    private fun reportFriendApi(resionId: String, reasonText: String) {

        MyUtils.showProgressDialog(activity!!, "Wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("reasonID", resionId)
            jsonObject.put("userfriendID", userFriendId)
            jsonObject.put("usercontactreportReason", edit_text_wrongwihPost.text.toString().trim())
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        reportFriendModel.getCommonStatus(activity!!, false, jsonArray.toString(), "user_friend_report")
                .observe(this@RepostReasonFragment, Observer { it ->

                    if (it != null && it.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()

                        if (it[0].status.equals("true", true)) {
                            if (it[0].message.isNullOrEmpty()) {
                                MyUtils.showSnackbar(activity!!, "You have reported post", ll_mainReportReason)
                            } else {
                                MyUtils.showSnackbar(activity!!, it[0].message!!, ll_mainReportReason)
                            }
                            (activity as MainActivity).onBackPressed()
                        } else {
                            MyUtils.showSnackbar(activity!!, it[0].message!!, ll_mainReportReason)
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        (activity as MainActivity).errorMethod()
                    }
                })

    }

    private fun reportCommentApi(resionId: String, reasonText: String) {

        MyUtils.showProgressDialog(activity!!, "Wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("postID",commentPostId)
            jsonObject.put("commentID",commentID)
            jsonObject.put("postreportreasonID",resionId)
            jsonObject.put("userpostreportreasonText",edit_text_wrongwihPost.text.toString().trim())
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        reportFriendModel.getCommonStatus(activity!!, false, jsonArray.toString(), "user_comment_report")
                .observe(this@RepostReasonFragment, Observer { it ->

                    if (it != null && it.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()

                        if (it[0].status.equals("true", true)) {
                            if (it[0].message.isNullOrEmpty()) {
                                MyUtils.showSnackbar(activity!!, "You have reported post", ll_mainReportReason)
                            } else {
                                MyUtils.showSnackbar(activity!!, it[0].message!!, ll_mainReportReason)
                            }
                            (activity as MainActivity).onBackPressed()
                        } else {
                            MyUtils.showSnackbar(activity!!, it[0].message!!, ll_mainReportReason)
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        (activity as MainActivity).errorMethod()
                    }
                })

    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btn_submit_repostReason->{
                if (fromReport.equals("friendReport")) {
                    if (reasonText.isNotEmpty() && resionId.isNotEmpty()) {
                        reportFriendApi(resionId, reasonText)
                    } else {
                        MyUtils.showSnackbar(activity!!, "Please select reason for report", ll_mainReportReason)
                    }
                }
                if (fromReport.equals("commentReport")) {
                    if (reasonText.isNotEmpty() && resionId.isNotEmpty()) {
                        reportCommentApi(resionId, reasonText)
                    } else {
                        MyUtils.showSnackbar(activity!!, "Please select reason for report", ll_mainReportReason)
                    }
                }

            }

        }

    }


}