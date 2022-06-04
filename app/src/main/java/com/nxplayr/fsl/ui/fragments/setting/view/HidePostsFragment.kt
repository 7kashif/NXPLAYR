package com.nxplayr.fsl.ui.fragments.setting.view


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.setting.adapter.HidePostsAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.HidePostData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.ui.fragments.setting.viewmodel.HidePostListModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_hide_posts.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class HidePostsFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var hideposts_list: ArrayList<HidePostData?>? = null
    var hidePostsAdapter: HidePostsAdapter? = null
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
    private var linearLayoutManager: LinearLayoutManager? = null
    private lateinit var hidePostListModel: HidePostListModel
    private lateinit var commonStatusModel: CommonStatusModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_hide_posts, container, false)
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngHiddenPosts.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngHiddenPosts
            if (!sessionManager?.LanguageLabel?.lngNoDataFound.isNullOrEmpty())
                nodatafoundtextview.text = sessionManager?.LanguageLabel?.lngNoDataFound
            if (!sessionManager?.LanguageLabel?.lngCheckNoInternet.isNullOrEmpty())
                nointernettextview.text = sessionManager?.LanguageLabel?.lngCheckNoInternet
            if (!sessionManager?.LanguageLabel?.lngUnhideThisPost.isNullOrEmpty())
                tv_unhidePost.text = sessionManager?.LanguageLabel?.lngUnhideThisPost
        }
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

    private fun setupObserver() {
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            hideposts_list!!.clear()
            hidePostsAdapter!!.notifyDataSetChanged()

        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
            hideposts_list!!.add(null)
            hidePostsAdapter!!.notifyItemInserted(hideposts_list!!.size - 1)
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        hidePostListModel.gethidePostList(
            activity as MainActivity, false, jsonArray.toString(), "hidePostList"
        )
            .observe(mActivity!!) { hidePostListPojo ->
                if (hidePostListPojo != null && hidePostListPojo.isNotEmpty()) {
                    isLoading = false
                    ll_no_data_found.visibility = View.GONE
                    nointernetMainRelativelayout.visibility = View.GONE
                    relativeprogressBar.visibility = View.GONE

                    if (pageNo > 0) {
                        hideposts_list!!.removeAt(hideposts_list!!.size - 1)
                        hidePostsAdapter!!.notifyItemRemoved(hideposts_list!!.size)
                    }
                    if (hidePostListPojo[0].status.equals("true", true)) {
                        recyclerview.visibility = View.VISIBLE

                        if (pageNo == 0) {
                            hideposts_list?.clear()
                        }

                        hideposts_list?.addAll(hidePostListPojo[0].data!!)
                        hidePostsAdapter?.notifyDataSetChanged()
                        pageNo += 1

                        if (hidePostListPojo[0].data!!.size < 10) {
                            isLastpage = true
                        }

                        if (hidePostListPojo[0].data!!.isNullOrEmpty()) {
                            ll_no_data_found.visibility = View.VISIBLE
                            recyclerview.visibility = View.GONE
                        } else {
                            ll_no_data_found.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE
                        }

                    } else {
                        relativeprogressBar.visibility = View.GONE

                        if (hideposts_list!!.isNullOrEmpty()) {
                            ll_no_data_found.visibility = View.VISIBLE
                            recyclerview.visibility = View.GONE

                        } else {
                            ll_no_data_found.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE
                        }
                    }
                } else {
                    if (activity != null) {
                        relativeprogressBar.visibility = View.GONE
                        ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                    }

                }
            }
    }

    private fun setupUI() {

        tvToolbarTitle.setText(R.string.hidden_posts)
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        linearLayoutManager = LinearLayoutManager(mActivity)
        if (hideposts_list == null) {
            hideposts_list = ArrayList()
            hidePostsAdapter = HidePostsAdapter(
                activity as MainActivity,
                hideposts_list,
                object : HidePostsAdapter.OnItemClick {
                    override fun onClicled(position: Int, from: String) {
                        when (from) {
                            "unhidePost" -> {
                                tv_unhidePost.visibility = View.VISIBLE
                                tv_unhidePost.setOnClickListener {
                                    unHidePostApi(hideposts_list!![position]!!.postID, position)
                                }
                            }
                            else -> {
                                tv_unhidePost.visibility = View.GONE
                            }
                        }

                    }

                })
            recyclerview.setHasFixedSize(true)
            recyclerview.layoutManager = linearLayoutManager
            recyclerview.adapter = hidePostsAdapter
            pageNo = 0
            isLastpage = false
            isLoading = false
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

        btnRetry.setOnClickListener(this)

        var mDrawable = resources.getDrawable(R.drawable.post_view_icon_unselected)
        mDrawable.setColorFilter(
            ContextCompat.getColor(mActivity!!, R.color.black),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        tv_unhidePost.setCompoundDrawablesRelativeWithIntrinsicBounds(mDrawable, null, null, null)

    }

    private fun setupViewModel() {
        hidePostListModel =
            ViewModelProvider(this@HidePostsFragment).get(HidePostListModel::class.java)
        commonStatusModel =
            ViewModelProvider(this@HidePostsFragment).get(CommonStatusModel::class.java)

    }


    private fun unHidePostApi(postID: String, position: Int) {
        tv_unhidePost.visibility = View.GONE
        MyUtils.showProgressDialog(mActivity!!, "Wait")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("postID", postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        commonStatusModel.getCommonStatus(
            mActivity!!,
            false,
            jsonArray.toString(),
            "removehidePost"
        )
            .observe(
                this@HidePostsFragment!!
            ) { commonStatuspojo ->
                if (commonStatuspojo != null && commonStatuspojo.isNotEmpty()) {
                    if (commonStatuspojo[0].status.equals("true", false)) {
                        MyUtils.dismissProgressDialog()
                        hideposts_list?.removeAt(position);
                        hidePostsAdapter?.notifyItemRemoved(position);
                        hidePostsAdapter?.notifyItemChanged(position, hideposts_list?.size);
                    } else {
                        MyUtils.dismissProgressDialog()
                        MyUtils.showSnackbar(
                            mActivity!!,
                            commonStatuspojo[0].message,
                            ll_mainHidePost
                        )
                    }

                } else {
                    MyUtils.dismissProgressDialog()
                    (activity as MainActivity).errorMethod()
                }
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRetry -> {
                pageNo = 0
                isLastpage = false
                isLoading = false
                setupObserver()
            }
        }
    }

}
