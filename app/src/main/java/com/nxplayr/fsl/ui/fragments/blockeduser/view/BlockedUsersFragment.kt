package com.nxplayr.fsl.ui.fragments.blockeduser.view


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.ui.activity.main.view.MainActivity

import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.blockeduser.adapter.BlockUsersAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.BlockUserData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.blockeduser.viewmodel.BlockUserListModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_blocked_users.*
import kotlinx.android.synthetic.main.fragment_saved_posts.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class BlockedUsersFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var list_block_user: ArrayList<BlockUserData?>? = null
    var blockUsersAdapter: BlockUsersAdapter? = null
    var mActivity: AppCompatActivity? = null
    var pageNo = 0
    var pageSize = 10
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var y: Int = 0
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var savePostListModel: BlockUserListModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_blocked_users, container, false)

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
        setupObserver()
    }

    private fun setupObserver() {
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            list_block_user!!.clear()
            blockUsersAdapter!!.notifyDataSetChanged()

        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
            list_block_user!!.add(null)
            blockUsersAdapter!!.notifyItemInserted(list_block_user!!.size - 1)
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("action", "list")
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)

        savePostListModel.getBlockUserList(
            mActivity!!, false, jsonArray.toString())
            .observe(viewLifecycleOwner, { blockUserListPojo ->


                if (blockUserListPojo != null && blockUserListPojo.isNotEmpty()) {
                    isLoading = false
                    ll_no_data_found.visibility = View.GONE
                    nointernetMainRelativelayout.visibility = View.GONE
                    relativeprogressBar.visibility = View.GONE

                    if (pageNo > 0) {
                        list_block_user!!.removeAt(list_block_user!!.size - 1)
                        blockUsersAdapter!!.notifyItemRemoved(list_block_user!!.size)
                    }


                    if (blockUserListPojo[0].status.equals("true", true)) {
                        recyclerview.visibility = View.VISIBLE

                        if (pageNo == 0) {
                            list_block_user?.clear()
                        }

                        list_block_user?.addAll(blockUserListPojo[0].data)
                        blockUsersAdapter?.notifyDataSetChanged()
                        pageNo += 1

                        if (blockUserListPojo[0].data.size < 10) {
                            isLastpage = true
                        }



                        if (!blockUserListPojo[0].data.isNullOrEmpty()) {
                            if (blockUserListPojo[0].data.isNullOrEmpty()) {
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

                        if (list_block_user!!.isNullOrEmpty()) {
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

    private fun setupViewModel() {
         savePostListModel = ViewModelProvider(this@BlockedUsersFragment).get(BlockUserListModel::class.java)

    }

    private fun setupUI() {
        tvToolbarTitle.text = getString(R.string.blocked_users)

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        linearLayoutManager = LinearLayoutManager(mActivity)
        if (list_block_user == null) {
            list_block_user = ArrayList()
            blockUsersAdapter = BlockUsersAdapter(activity as MainActivity, list_block_user, object : BlockUsersAdapter.OnItemClick {
                override fun onClicled(position: Int, from: String) {
                    when (from) {
                        "blockUser" -> {
                            removeblockUserApi(list_block_user!![position]!!.userID, position)
                        }
                    }
                }

            })

            recyclerview.layoutManager = LinearLayoutManager(mActivity)
            recyclerview.setHasFixedSize(true)
            recyclerview.adapter = blockUsersAdapter
            val divider = DividerItemDecoration(recyclerview.context,
                DividerItemDecoration.VERTICAL)
            divider.setDrawable(
                context?.let { ContextCompat.getDrawable(it, R.drawable.line_layout) }!!
            )
            recyclerview.addItemDecoration(divider)
            blockUsersAdapter?.notifyDataSetChanged()
            pageNo = 0
            isLastpage = false
            isLoading = false
            setupObserver()
        }

        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                y = dy
                visibleItemCount = linearLayoutManager.childCount
                totalItemCount = linearLayoutManager.itemCount
                firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
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

        btnRetry.setOnClickListener (this)
    }

    private fun removeblockUserApi(userID: String, position: Int) {
        MyUtils.showProgressDialog(mActivity!!, "Wait")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("userID", userID)
            jsonObject.put("action", "remove")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        savePostListModel.getBlockUserList(mActivity!!, false, jsonArray.toString())
                .observe(
                    this@BlockedUsersFragment,
                    { blockUserpojo ->
                        if (blockUserpojo != null && blockUserpojo.isNotEmpty()) {
                            if (blockUserpojo[0].status.equals("true", false)) {
                                MyUtils.dismissProgressDialog()
                                list_block_user?.removeAt(position)
                                blockUsersAdapter?.notifyItemRemoved(position)
                                blockUsersAdapter?.notifyItemChanged(position, list_block_user?.size)
                            } else {
                                MyUtils.dismissProgressDialog()
                                MyUtils.showSnackbar(mActivity!!, blockUserpojo[0].message, ll_mainBlockUsers)
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
                pageNo = 0
                isLastpage = false
                isLoading = false
                setupObserver()

            }
        }
    }

}
