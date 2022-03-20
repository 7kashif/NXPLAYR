package com.nxplayr.fsl.ui.fragments.notification.view


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.notification.adapter.NotificationAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.ui.fragments.notification.viewmodel.NotificationListModel
import com.nxplayr.fsl.data.model.NotificationData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.ThreedotsBottomPojo
import com.nxplayr.fsl.ui.fragments.bottomsheet.ThreeDotsBottomSheetFragment
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_languages.*
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class NotificationFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var notificationListData: ArrayList<NotificationData?>? = null
    var notificationAdapter: NotificationAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var y: Int = 0
    var pageNo = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var list_bottomsheet: ArrayList<ThreedotsBottomPojo>? = ArrayList()
    private lateinit var notificationlistModel: NotificationListModel
    private lateinit var  commonStatusModel: CommonStatusModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null)
        {
            v = inflater.inflate(R.layout.fragment_notification, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(activity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        setupViewModel()
        setupUI()


    }

    private fun setupObserver() {
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            notificationListData?.clear()
            notificationAdapter?.notifyDataSetChanged()
        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
            notificationListData!!.add(null)
            notificationAdapter?.notifyItemInserted(notificationListData!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        notificationlistModel.getNotificationList(mActivity!!, jsonArray.toString())
            .observe(viewLifecycleOwner, { notificationlistPojo ->
                if (notificationlistPojo != null && notificationlistPojo.isNotEmpty())
                {
                    isLoading = false
                    ll_no_data_found.visibility = View.GONE
                    nointernetMainRelativelayout.visibility = View.GONE
                    relativeprogressBar.visibility = View.GONE

                    if (pageNo > 0) {
                        notificationListData?.removeAt(notificationListData!!.size - 1)
                        notificationAdapter?.notifyItemRemoved(notificationListData!!.size)
                    }
                    if (notificationlistPojo[0].status.equals("true", true)) {
                        recyclerview.visibility = View.VISIBLE
                        if (pageNo == 0) {
                            notificationListData?.clear()
                        }
                        notificationListData!!.addAll(notificationlistPojo[0].data)
                        notificationAdapter?.notifyDataSetChanged()
                        pageNo += 1
                        if (notificationlistPojo[0].data.size < 10) {
                            isLastpage = true
                        }

                        if (!notificationlistPojo[0].data.isNullOrEmpty()) {
                            if (notificationlistPojo[0].data.isNullOrEmpty()) {
                                ll_no_data_found.visibility = View.VISIBLE
                                recyclerview.visibility = View.GONE
                            } else {
                                ll_no_data_found.visibility = View.GONE
                                recyclerview.visibility = View.VISIBLE
                            }


                        } else {
                            ll_no_data_found.visibility = View.VISIBLE
                            recyclerview.visibility = View.GONE
                        }

                    } else {
                        relativeprogressBar.visibility = View.GONE

                        if (notificationListData!!.isNullOrEmpty()) {
                            ll_no_data_found.visibility = View.VISIBLE
                            recyclerview.visibility = View.GONE

                        } else {
                            ll_no_data_found.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE

                        }
                    }

                }
                else
                {

                    relativeprogressBar.visibility = View.GONE
                    if (activity != null) {
                        ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                    }

                }
            })
    }

    private fun setupViewModel() {
        notificationlistModel = ViewModelProvider(this@NotificationFragment).get(
            NotificationListModel::class.java)
        commonStatusModel = ViewModelProvider(this@NotificationFragment).get(CommonStatusModel::class.java)

    }

    private fun setupUI() {
        tvToolbarTitle?.text = getString(R.string.notification)
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        btnRetry.setOnClickListener(this)
        linearLayoutManager = LinearLayoutManager(mActivity)
        if (notificationListData == null) {
            notificationListData = ArrayList()
            notificationAdapter = NotificationAdapter(activity as MainActivity, notificationListData, object : NotificationAdapter.OnItemClick {
                override fun onClicled(position: Int, from: String, v: View) {

                    when (from) {
                        "delete_notiication" -> {
                            list_bottomsheet = java.util.ArrayList()
                            list_bottomsheet!!.clear()
                            list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_delete_icon, resources.getString(R.string.delete)))
                            openBottomSheet(list_bottomsheet!!, position, notificationListData!![position]!!.notificationID)

                        }
                    }

                }
            })

            recyclerview.layoutManager = linearLayoutManager
            recyclerview.adapter = notificationAdapter
            setupObserver()

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
                        setupObserver()
                    }
                }
            }
        })
    }

    private fun openBottomSheet(data: ArrayList<ThreedotsBottomPojo>, position: Int, notificationID: String) {

        val bottomSheet = ThreeDotsBottomSheetFragment()
        val bundle = Bundle()
        bundle.putSerializable("data", data)
        bundle.putString("from", "MenuList")
        bundle.putString("userId",notificationID)
        bottomSheet.arguments = bundle
        bottomSheet.show(fragmentManager!!, "List")
        bottomSheet.setOnclickLisner(object : ThreeDotsBottomSheetFragment.SelectList {
            override fun onOptionSelect(value: Int, from: String) {
                bottomSheet.dismiss()
                when (from) {
                    resources.getString(R.string.delete) -> {
                       deleteNotification(notificationID,position)
                    }
                }
            }

        })

    }

    protected fun deleteNotification(notificationID: String, position: Int) {

        MyUtils.showProgressDialog(mActivity!!, "Please wait..")

        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID",  userData?.userID)
            jsonObject.put("languageID", "1")
            jsonObject.put("notificationID", notificationID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val jsonArray = JSONArray()
        jsonArray.put(jsonObject)

        commonStatusModel.getCommonStatus(mActivity!!, false, jsonArray.toString(), "user_delete_Notification")
                .observe(mActivity!!,
                        androidx.lifecycle.Observer { notificationlistPojo ->
                            MyUtils.dismissProgressDialog()
                            if (notificationlistPojo != null && notificationlistPojo.isNotEmpty()) {
                                if (notificationlistPojo[0].status.equals("true", false)) {
                                    notificationListData!!.removeAt(position)
                                    if (notificationListData!!.size == 0) {
                                        ll_no_data_found.visibility = View.VISIBLE
                                        recyclerview.visibility = View.GONE

                                    }
                                    notificationAdapter!!.notifyDataSetChanged()

                                } else {
                                    MyUtils.showSnackbar(mActivity!!, notificationlistPojo[0].message, ll_mainNotificationList)


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
            R.id.btnRetry->{
                setupObserver()
            }
        }
    }

}
