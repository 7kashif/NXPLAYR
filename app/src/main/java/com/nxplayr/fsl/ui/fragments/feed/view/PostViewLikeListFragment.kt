package com.nxplayr.fsl.ui.fragments.feed.view

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
import com.nxplayr.fsl.ui.fragments.feed.adapter.PostLikeViewAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.PostLikeViewListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.ui.fragments.feedlikeviewlist.viewmodel.PostLikeListModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_post_view_like_list.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject

class PostViewLikeListFragment : Fragment() {

    var mActivity: AppCompatActivity? = null
    private var v: View? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var list_followers: ArrayList<PostLikeViewListData?>? = null
    var postLikeViewAdapter: PostLikeViewAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var pageNo = 0
    var pageSize = 20
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var y: Int = 0
    var from=""
    var postId=""

    private lateinit var postLikeListModel : PostLikeListModel
    private lateinit var commonStatusModel: CommonStatusModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if(v==null){
            v=inflater.inflate(R.layout.fragment_post_view_like_list, container, false)
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

        if(arguments!=null)
        {
            from=arguments?.getString("from","")!!
            postId=arguments?.getString("postId","")!!
        }
        setupViewModel()
        setupUI()

    }

    private fun setupUI() {
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        tvToolbarTitle.text = from+"s"
        linearLayoutManager=LinearLayoutManager(mActivity!!)
        if (list_followers == null) {
            list_followers = ArrayList()
            postLikeViewAdapter = PostLikeViewAdapter(mActivity!!, list_followers, object : PostLikeViewAdapter.OnItemClick {
                override fun onClicled(position: Int, from: String) {
                    when (from) {
                        "follow" -> {
                            followUserApi(position, list_followers!![position]!!.userID)
                        }
                        "unfollow" -> {
                            userUnFollowApi(position, list_followers!![position]!!.userID)
                        }
                    }
                }
            })

            recyclerview.layoutManager = linearLayoutManager
            recyclerview.isNestedScrollingEnabled = false
            recyclerview.adapter = postLikeViewAdapter
            recyclerview.setHasFixedSize(true)
            followersListApi()

        }
        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                y = dy
                visibleItemCount = linearLayoutManager?.childCount!!
                totalItemCount = linearLayoutManager?.itemCount!!
                firstVisibleItemPosition = linearLayoutManager?.findFirstVisibleItemPosition()!!
                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 10
                    ) {
                        isLoading = true
                        followersListApi()
                    }
                }
            }
        })
        btnRetry.setOnClickListener {
            followersListApi()
        }
    }

    private fun setupViewModel() {
         postLikeListModel =
            ViewModelProvider(this@PostViewLikeListFragment).get(PostLikeListModel::class.java)
         commonStatusModel =
            ViewModelProvider(this@PostViewLikeListFragment).get(CommonStatusModel::class.java)

    }

    private fun followersListApi() {
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            list_followers!!.clear()
            postLikeViewAdapter!!.notifyDataSetChanged()

        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
            list_followers!!.add(null)
            postLikeViewAdapter!!.notifyItemInserted(list_followers!!.size - 1)

        }
        var type=""
        when(from){
            "View"->type="postViewList"
            "Like"->type="postLikeList"
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("action", "List")
            jsonObject.put("postID",postId)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        postLikeListModel.getPostLikeViewList(mActivity!!, false, jsonArray.toString(), type)
                .observe(mActivity!!,  { followerListPojo ->

                    if (followerListPojo != null && followerListPojo.isNotEmpty()) {

                        isLoading = false
                        ll_no_data_found.visibility = View.GONE
                        nointernetMainRelativelayout.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE

                        if (pageNo > 0) {
                            list_followers!!.removeAt(list_followers!!.size - 1)
                            postLikeViewAdapter!!.notifyItemRemoved(list_followers!!.size)
                        }

                        if (followerListPojo[0].status.equals("true", true)) {
                            recyclerview.visibility = View.VISIBLE
                            if (pageNo == 0) {
                                list_followers?.clear()
                            }
                            list_followers?.addAll(followerListPojo[0].data)
                            postLikeViewAdapter?.notifyDataSetChanged()
                            pageNo += 1

                            if (followerListPojo[0].data.size < 10) {
                                isLastpage = true
                            }
                            tv_like_count.text=followerListPojo[0].data.size.toString().trim()+" "+from+"s"

                                if (followerListPojo[0].data.isNullOrEmpty()) {
                                    ll_no_data_found.visibility = View.VISIBLE
                                    recyclerview.visibility = View.GONE
                                    ll_like.visibility = View.GONE
                                } else {
                                    ll_no_data_found.visibility = View.GONE
                                    recyclerview.visibility = View.VISIBLE
                                    ll_like.visibility = View.VISIBLE
                                }
                        } else {
                            relativeprogressBar.visibility = View.GONE
                            ll_like.visibility = View.GONE

                            if (list_followers!!.isNullOrEmpty()) {
                                ll_no_data_found.visibility = View.VISIBLE
                                recyclerview.visibility = View.GONE

                            } else {
                                ll_no_data_found.visibility = View.GONE
                                recyclerview.visibility = View.VISIBLE

                            }


                        }
                    } else {
                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }
                })

    }

    private fun followUserApi(position: Int, userID: String) {

        MyUtils.showProgressDialog(mActivity!!, "Please wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("action", "follow")
            jsonObject.put("userfollowerFollowingID", userID)
            jsonObject.put("userfollowerFollowerID", userData!!.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)

         commonStatusModel.getCommonStatus(activity!!, false, jsonArray.toString(), "userFollow")
                .observe(this,{ commonStatusPojo ->

                    MyUtils.dismissProgressDialog()

                    if (commonStatusPojo != null && commonStatusPojo.isNotEmpty()) {
                        if (commonStatusPojo[0].status.equals("true", true)) {

                            list_followers?.get(position)?.isYouFollowing = "Yes"
                            postLikeViewAdapter?.notifyItemChanged(position)
                            postLikeViewAdapter?.notifyItemChanged(position, list_followers!!.size)
                            MyUtils.showSnackbar(activity!!, commonStatusPojo[0].message, ll_like)

                        } else {

                            MyUtils.showSnackbar(activity!!, commonStatusPojo[0].message, ll_like)

                        }
                    } else {
                        MyUtils.dismissProgressDialog()

                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }
                })

    }

    private fun userUnFollowApi(position: Int, userID: String) {

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("action", "unfollow")
            jsonObject.put("userfollowerFollowingID", userID)
            jsonObject.put("userfollowerFollowerID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        commonStatusModel.getCommonStatus(activity!!, false, jsonArray.toString(), "userUnfollow")
                .observe(this,{ commonStatusPojo ->

                    relativeprogressBar.visibility = View.GONE
                    recyclerview.visibility = View.VISIBLE

                    if (commonStatusPojo != null && commonStatusPojo.isNotEmpty()) {
                        if (commonStatusPojo[0].status.equals("true", true)) {

                            list_followers?.get(position)?.isYouFollowing = "No"
                            postLikeViewAdapter?.notifyItemChanged(position)
                            postLikeViewAdapter?.notifyItemChanged(position, list_followers!!.size)
                            MyUtils.showSnackbar(activity!!, commonStatusPojo[0].message, ll_like)


                        } else {
                            MyUtils.showSnackbar(activity!!, commonStatusPojo[0].message, ll_like)

                        }
                    } else {

                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }
                })

    }


}