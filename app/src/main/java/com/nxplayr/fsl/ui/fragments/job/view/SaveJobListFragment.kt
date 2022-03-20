package com.nxplayr.fsl.ui.fragments.job.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.JobListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.ThreedotsBottomPojo
import com.nxplayr.fsl.databinding.FragmentHomeJobListBinding
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.job.adapter.JobListAdapter
import com.nxplayr.fsl.ui.fragments.job.viewmodel.JobListViewModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_comment_like_list.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SaveJobListFragment : Fragment(),View.OnClickListener, LifecycleObserver {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    private lateinit var  jobListViewModel: JobListViewModel
    private var fragmentHomeJobListBinding: FragmentHomeJobListBinding? = null
    private val binding get() = fragmentHomeJobListBinding!!
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var y: Int = 0
    var pageNo = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var joblistData : ArrayList<JobListData?>? = null
    var jobListAdapter: JobListAdapter? = null
    var list_bottomsheet: ArrayList<ThreedotsBottomPojo>? = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentHomeJobListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_job_list, container, false)
        return fragmentHomeJobListBinding?.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity=context as AppCompatActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        setupViewModel()
        setupUI()

    }

    private fun setupUI() {
        linearLayoutManager = LinearLayoutManager(mActivity)
        if (joblistData == null) {
            joblistData = ArrayList()
            jobListAdapter = JobListAdapter(mActivity!!, joblistData, object : JobListAdapter.OnItemClick {
                override fun onClicled(position: Int, from: String, v: View) {
                    when (from) {
                        "threedots" -> {
                            list_bottomsheet = ArrayList()
                            list_bottomsheet?.clear()

                            if (joblistData!![position]!!.isYouSaved.equals("false",true))
                            {
                                list_bottomsheet!!.add(
                                    ThreedotsBottomPojo(
                                        R.drawable.popup_share_via_icon,
                                        resources.getString(R.string.save)
                                    )
                                )
                                list_bottomsheet!!.add(
                                    ThreedotsBottomPojo(
                                        R.drawable.popup_hide_post_icon,
                                        resources.getString(R.string.apply)
                                    )
                                )


                            }
                            else {
                                list_bottomsheet!!.add(
                                    ThreedotsBottomPojo(
                                        R.drawable.popup_share_via_icon,
                                        resources.getString(R.string.unsave)
                                    )
                                )
                                list_bottomsheet!!.add(
                                    ThreedotsBottomPojo(
                                        R.drawable.popup_hide_post_icon,
                                        resources.getString(R.string.apply)
                                    )
                                )
                            }
                            openBottomSheet(position)
                        }
                    }
                }
            })

            recyclerview.layoutManager = linearLayoutManager
            recyclerview.adapter = jobListAdapter
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
    private fun openBottomSheet(position: Int) {
        val bottomSheet = BottomSheetJobSaveApplyFragment()
        val bundle = Bundle()
        if(!list_bottomsheet.isNullOrEmpty())
        {
            bundle.putSerializable("data", list_bottomsheet)
        }
        bundle.putString("from", "List")
        bottomSheet.arguments = bundle
        bottomSheet.show(childFragmentManager, "List")
        bottomSheet.setOnclickLisner(object : BottomSheetJobSaveApplyFragment.SelectList {
            override fun onOptionSelect(value: Int, from: String) {
                when (from) {
                    resources.getString(R.string.save) -> {
                        getSaveJob(position)
                        bottomSheet.dismiss()
                    }
                    resources.getString(R.string.unsave) -> {
                        getUnSaveJob(position)
                        bottomSheet.dismiss()
                    }
                    resources.getString(R.string.apply) -> {
                        bottomSheet.dismiss()
                    }
                }
                bottomSheet.dismiss()

            }

        })
    }

    private fun getSaveJob(position: Int) {
        MyUtils.showProgressDialog(mActivity!!, "Wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("jobID", joblistData?.get(position)?.jobID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        jobListViewModel.getNotificationList(mActivity!!, jsonArray.toString(), "SaveJob")
            .observe(this@SaveJobListFragment, { it ->
                if (it != null && it.isNotEmpty()) {
                    MyUtils.dismissProgressDialog()

                    if (it[0].status.equals("true", true)) {
                        (activity as MainActivity).showSnackBar(it[0].message!!)
                        joblistData!![position]!!.isYouSaved="true"
                        jobListAdapter?.notifyItemChanged(position)
                        list_bottomsheet = ArrayList()
                        list_bottomsheet?.clear()
                        list_bottomsheet!!.add(
                            ThreedotsBottomPojo(
                                R.drawable.popup_share_via_icon,
                                resources.getString(R.string.unsave)
                            )
                        )
                        list_bottomsheet!!.add(
                            ThreedotsBottomPojo(
                                R.drawable.popup_hide_post_icon,
                                resources.getString(R.string.apply)
                            )
                        )

                    } else {

                        (activity as MainActivity).showSnackBar(it[0].message!!)
                    }

                } else {
                    MyUtils.dismissProgressDialog()
                    ErrorUtil.errorMethod(mainRootRv)
                }
            })

    }

    private fun getUnSaveJob(position: Int) {
        MyUtils.showProgressDialog(mActivity!!, "Wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("jobID", joblistData?.get(position)?.jobID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        jobListViewModel.getNotificationList(mActivity!!, jsonArray.toString(), "RemoveJob")
            .observe(this@SaveJobListFragment, { it ->
                if (it != null && it.isNotEmpty()) {
                    MyUtils.dismissProgressDialog()

                    if (it[0].status.equals("true", true)) {
                        (activity as MainActivity).showSnackBar(it[0].message!!)
                        joblistData!![position]!!.isYouSaved="false"
                        joblistData?.removeAt(position)
                        jobListAdapter?.notifyItemRemoved(joblistData!!.size)
                        jobListAdapter?.notifyDataSetChanged()

                        list_bottomsheet = ArrayList()
                        list_bottomsheet?.clear()
                        list_bottomsheet!!.add(
                            ThreedotsBottomPojo(
                                R.drawable.popup_share_via_icon,
                                resources.getString(R.string.save)
                            )
                        )
                        list_bottomsheet!!.add(
                            ThreedotsBottomPojo(
                                R.drawable.popup_hide_post_icon,
                                resources.getString(R.string.apply)
                            )
                        )

                    } else {

                        (activity as MainActivity).showSnackBar(it[0].message!!)
                    }

                } else {
                    MyUtils.dismissProgressDialog()
                    ErrorUtil.errorMethod(mainRootRv)
                }
            })

    }

    private fun setupViewModel() {
        jobListViewModel = ViewModelProvider(this).get(JobListViewModel::class.java)

    }

    private fun setupObserver() {
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            joblistData?.clear()
            jobListAdapter?.notifyDataSetChanged()
        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
            joblistData!!.add(null)
            jobListAdapter?.notifyItemInserted(joblistData!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "1")
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        jobListViewModel.getNotificationList(mActivity!!, jsonArray.toString(),"Save")
            .observe(viewLifecycleOwner, { notificationlistPojo ->
                if (notificationlistPojo != null && notificationlistPojo.isNotEmpty())
                {
                    isLoading = false
                    ll_no_data_found.visibility = View.GONE
                    nointernetMainRelativelayout.visibility = View.GONE
                    relativeprogressBar.visibility = View.GONE

                    if (pageNo > 0) {
                        joblistData?.removeAt(joblistData!!.size - 1)
                        jobListAdapter?.notifyItemRemoved(joblistData!!.size)
                    }
                    if (notificationlistPojo[0].status.equals("true", true)) {
                        recyclerview.visibility = View.VISIBLE
                        if (pageNo == 0) {
                            joblistData?.clear()
                        }
                        joblistData!!.addAll(notificationlistPojo[0].data!!)
                        jobListAdapter?.setJobList(joblistData!!)
                        pageNo += 1
                        if (notificationlistPojo[0].data?.size!! < 10) {
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

                        if (joblistData!!.isNullOrEmpty()) {
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

    override fun onClick(v: View?) {
        when(v?.id){

        }
    }

}