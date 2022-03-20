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
import com.nxplayr.fsl.data.model.ApplyJobData
import com.nxplayr.fsl.data.model.ApplyJoblist
import com.nxplayr.fsl.data.model.JobListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.databinding.FragmentHomeJobListBinding
import com.nxplayr.fsl.ui.fragments.job.adapter.ApplyJobListAdapter
import com.nxplayr.fsl.ui.fragments.job.adapter.JobListAdapter
import com.nxplayr.fsl.ui.fragments.job.viewmodel.ApplyJobListViewModel
import com.nxplayr.fsl.ui.fragments.job.viewmodel.JobListViewModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ApplyedJobListFragment : Fragment(),View.OnClickListener, LifecycleObserver {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    private lateinit var  jobListViewModel: ApplyJobListViewModel
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
    var joblistData : ArrayList<ApplyJobData?>? = null
    var jobListAdapter: ApplyJobListAdapter? = null

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
            jobListAdapter = ApplyJobListAdapter(mActivity!!, joblistData, object : ApplyJobListAdapter.OnItemClick {
                override fun onClicled(position: Int, from: String, v: View) {

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

    private fun setupViewModel() {
        jobListViewModel = ViewModelProvider(this).get(ApplyJobListViewModel::class.java)

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
        jobListViewModel.getNotificationList(mActivity!!, jsonArray.toString(),"Apply")
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