package com.nxplayr.fsl.ui.fragments.ownprofile.view


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.post.view.CreatePostActivityTwo
import com.nxplayr.fsl.ui.fragments.feed.adapter.HomeFeedListAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.ui.fragments.feed.viewmodel.CreatePostModel
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.ThreedotsBottomPojo
import com.nxplayr.fsl.ui.fragments.bottomsheet.BottomSheetFragment
import com.nxplayr.fsl.ui.fragments.feedlikeviewlist.view.LikeViewFragment
import com.nxplayr.fsl.ui.fragments.postcomment.view.RepostReasonFragment
import com.nxplayr.fsl.ui.fragments.bottomsheet.ThreeDotsBottomSheetFragment
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.ui.fragments.postcomment.viewmodel.CommentModel
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_comment_like_list.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class UserFeedListsFragment : Fragment(),View.OnClickListener  {

    private var v: View? = null
    var homeFeedListAdapter: HomeFeedListAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var mActivity: Activity? = null
    var postList: ArrayList<CreatePostData?>? = null
    var pageNo = 0
    var pageSize = 20
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var y: Int = 0
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var list_bottomsheet: ArrayList<ThreedotsBottomPojo>? = ArrayList()
    var list: ArrayList<String>? = ArrayList()
    var tabPos: Int = 0
    var from = ""
    var userId = ""

    private lateinit var createPostModel: CreatePostModel
    private lateinit var apiCall : CommentModel
    private lateinit var reportPostModel : CommonStatusModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_follow_list, container, false)

        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(activity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (arguments != null) {
            tabPos = arguments?.getInt("position", 0)!!
            from = arguments?.getString("from").toString()
            userId = arguments?.getString("userID").toString()

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
            postList!!.clear()
            homeFeedListAdapter!!.notifyDataSetChanged()

        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
            postList!!.add(null)
            homeFeedListAdapter!!.notifyItemInserted(postList!!.size - 1)

        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("postID", "0")
            if (from.equals("followersDetails")) {
                jsonObject.put("loginuserID", userData!!.userID)
            } else if (from.equals("OtherUserPro")) {
                jsonObject.put("loginuserID",userId)
            }
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            when (tabPos) {
                0 -> {
                    jsonObject.put("tabname", "my")
                    jsonObject.put("postType", "Social")
                }
                2 -> {
                    jsonObject.put("tabname", "my")
                    jsonObject.put("postType", "Social")
                    jsonObject.put("postMediaType", "Photo")
                }
                3 -> {
                    jsonObject.put("tabname", "my")
                    jsonObject.put("postType", "Social")
                    jsonObject.put("postMediaType", "Video")
                }
                4 -> {
                    jsonObject.put("tabname", "my")
                    jsonObject.put("postType", "Social")
                    jsonObject.put("postMediaType", "Document")
                }
                5 -> {
                    jsonObject.put("tabname", "my")
                    jsonObject.put("postType", "Social")
                    jsonObject.put("postMediaType", "Link")
                }
            }
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        createPostModel.apiFunction(mActivity!!, jsonArray.toString(), "getPostList")
            .observe(viewLifecycleOwner, { postListPojo ->


                if (postListPojo != null && postListPojo.isNotEmpty()) {
                    isLoading = false
                    ll_no_data_found.visibility = View.GONE
                    nointernetMainRelativelayout.visibility = View.GONE
                    relativeprogressBar.visibility = View.GONE

                    if (pageNo > 0) {
                        postList!!.removeAt(postList!!.size - 1)
                        homeFeedListAdapter!!.notifyItemRemoved(postList!!.size)
                    }


                    if (postListPojo[0].status.equals("true")) {

                        recyclerview.visibility = View.VISIBLE

                        if (pageNo == 0) {
                            postList?.clear()
                        }
                        postList?.addAll(postListPojo[0].data!!)
                        homeFeedListAdapter?.notifyDataSetChanged()
                        pageNo += 1

                        if (postListPojo[0].data!!.size < 10) {
                            isLastpage = true
                        }
                        if (postListPojo[0].data!!.isNullOrEmpty()) {
                            ll_no_data_found.visibility = View.VISIBLE
                            recyclerview.visibility = View.GONE
                        } else {
                            ll_no_data_found.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE
                        }

                    } else {
                        relativeprogressBar.visibility = View.GONE

                        if (postList!!.isNullOrEmpty()) {
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
                        try {
                            nointernetMainRelativelayout.visibility = View.VISIBLE
                            if (MyUtils.isInternetAvailable(activity!!)) {

                                nointernetImageview.setImageDrawable(activity!!.getDrawable(R.drawable.ic_warning_black_24dp))
                                nointernettextview.text =
                                    (activity!!.getString(R.string.error_crash_error_message))

                            } else {

                                nointernetImageview.setImageDrawable(activity!!.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp))
                                nointernettextview.text =
                                    (activity!!.getString(R.string.error_common_network))

                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }

                    }

                }
            })

    }

    private fun setupUI() {
        linearLayoutManager = LinearLayoutManager(mActivity!!)

        if (postList == null) {
            postList = ArrayList()
            homeFeedListAdapter = HomeFeedListAdapter(mActivity!!, postList!!, object : HomeFeedListAdapter.OnItemClick {
                override fun onClicled(position: Int, from: String, get: CreatePostData, sendComment:String, view:View) {

                    when (from) {
                        "like_unlike" -> {
                            postLikeApi("user_Like")
                            (context as MainActivity).navigateTo(LikeViewFragment(), LikeViewFragment::class.java.name, true)
                        }
                        "views" -> {
                            (context as MainActivity).navigateTo(LikeViewFragment(), LikeViewFragment::class.java.name, true)
                        }
                        "from_more_icon" -> {
                            list_bottomsheet = ArrayList()
                            list_bottomsheet?.clear()

                            list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_share_via_icon, resources.getString(R.string.share_via)))
                            list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_hide_post_icon, resources.getString(R.string.hide_this_post)))

                            if (!userData?.userID.equals(postList!![position]!!.userID)) {
                                if (postList?.get(position)?.isYouFollowing.equals("Yes", false)) {
                                    list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_unfollow_icon, resources.getString(R.string.unfollow)))

                                } else if (postList?.get(position)?.isYouFollowing.equals("No", false)) {
                                    list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_unfollow_icon, resources.getString(R.string.follow)))
                                }
                                list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_report_icon, resources.getString(R.string.report_this_Post_ad)))
                                list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_copyright_icon, resources.getString(R.string.report_copyright_infringement)))
                                list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_save_post_icon, resources.getString(R.string.save_this_post)))
                            } else {
                                list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_edit_icon, resources.getString(R.string.edit)))
                                list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_delete_icon, "Delete"))

                            }

                            openBottomSheet(list_bottomsheet!!, position)

                        }
                        "list_share" ->
                            openBottomSheetForShare(list!!)
                    }

                }
            })
            recyclerview.layoutManager = linearLayoutManager
            recyclerview.adapter = homeFeedListAdapter
            setupObserver()

        }
        if (list.isNullOrEmpty()) {
            list!!.add(resources.getString(R.string.share_in_a_post))
            list!!.add(resources.getString(R.string.send_in_a_private_message))
        }
        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                y = dy
                visibleItemCount = linearLayoutManager!!.childCount
                totalItemCount = linearLayoutManager!!.itemCount
                firstVisibleItemPosition = linearLayoutManager!!.findFirstVisibleItemPosition()
                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= 10) {
                        isLoading = true
                        setupObserver()
                    }
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (RecyclerView.SCROLL_STATE_IDLE === newState) {
                    homeFeedListAdapter?.playAvailableVideos(newState, recyclerview)
                    val firstPos = linearLayoutManager!!.findFirstVisibleItemPosition()
                    val lastPos = linearLayoutManager!!.findLastVisibleItemPosition()
                    if (homeFeedListAdapter != null && postList?.size!! > 0)
                        homeFeedListAdapter?.setAdapterPosition(firstPos, lastPos)
                }
            }
        })

        btnRetry.setOnClickListener(this)

    }

    private fun setupViewModel() {
        createPostModel = ViewModelProvider(this@UserFeedListsFragment).get(CreatePostModel::class.java)
        apiCall = ViewModelProvider(this@UserFeedListsFragment).get(CommentModel::class.java)
        reportPostModel = ViewModelProvider(this@UserFeedListsFragment).get(CommonStatusModel::class.java)

    }

    private fun openBottomSheet(data: ArrayList<ThreedotsBottomPojo>, position: Int) {
        val bottomSheet = ThreeDotsBottomSheetFragment()
        val bundle = Bundle()
        bundle.putSerializable("data", data)
        bundle.putString("from", "MenuList")
        bundle.putString("userId", postList!![position]!!.userID)
        bottomSheet.arguments = bundle
        bottomSheet.show(fragmentManager!!, "List")
        bottomSheet.setOnclickLisner(object : ThreeDotsBottomSheetFragment.SelectList {
            override fun onOptionSelect(value: Int, from: String) {
                bottomSheet.dismiss()
                when (from) {
                    resources.getString(R.string.share_via) -> {

                    }
                    resources.getString(R.string.hide_this_post) -> {
                        getHidePost(position)
                    }
                    resources.getString(R.string.unfollow) -> {
                        getUnfollow(position)

                    }
                    resources.getString(R.string.follow) -> {
                        getUnfollow(position)

                    }
                    resources.getString(R.string.report_this_Post_ad) -> {
                        var repostReasonFragment = RepostReasonFragment()
                        Bundle().apply {
                            putString("fromReport", "postReport")
                            putString("PostId", postList?.get(position)?.postID)
                            repostReasonFragment.arguments = this
                        }
                        (activity as MainActivity).navigateTo(repostReasonFragment, repostReasonFragment::class.java.name, true)

                    }
                    resources.getString(R.string.save_this_post) -> {
                        getSavePost(position)
                    }
                    resources.getString(R.string.edit) -> {
                        Intent(mActivity!!, CreatePostActivityTwo::class.java).apply {
                            putExtra("postData", postList?.get(position))
                            startActivity(this)
                        }
                    }
                    "Delete" -> {
                        getDeletePost(position)
                    }
                }
            }

        })
    }

    private fun getSavePost(position: Int) {
        MyUtils.showProgressDialog(mActivity!!, "Wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("postID", postList?.get(position)?.postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        reportPostModel.getCommonStatus(mActivity!!, false, jsonArray.toString(), "savePost")
                .observe(this@UserFeedListsFragment, Observer { it ->
                    //            MyUtils.closeProgress()

                    if (it != null && it.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()

                        if (it[0].status.equals("true", true)) {
                            (activity as MainActivity).showSnackBar(it[0].message!!)
                        } else {

                            (activity as MainActivity).showSnackBar(it[0].message!!)
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(mainRootRv)
                    }
                })


    }

    private fun getUnfollow(position: Int) {
        MyUtils.showProgressDialog(mActivity!!, "Wait..")
        var fromUser = ""
        var action = ""
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        if (postList?.get(position)?.isYouFollowing.equals("Yes", false)) {
            fromUser = "userUnfollow"
            action = "unfollow"
            jsonObject.put("userfollowerFollowingID", postList?.get(position)?.userID)
            jsonObject.put("userfollowerFollowerID", userData?.userID)


        } else if (postList?.get(position)?.isYouFollowing.equals("No", false)) {
            fromUser = "userFollow"
            action = "follow"
            jsonObject.put("userfollowerFollowingID", postList?.get(position)?.userID)
            jsonObject.put("userfollowerFollowerID", userData?.userID)

        }
        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("action", action)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        reportPostModel.getCommonStatus(mActivity!!, false, jsonArray.toString(), fromUser).observe(this@UserFeedListsFragment,
            { trendingFeedDatum ->
                if (trendingFeedDatum != null && trendingFeedDatum.isNotEmpty()) {
                    MyUtils.dismissProgressDialog()
                    if (trendingFeedDatum[0].status?.equals("true", false)!!) {
                        (activity as MainActivity).showSnackBar(trendingFeedDatum[0].message!!)
                        when (fromUser) {
                            "userUnfollow" -> {
                                postList?.get(position)?.isYouFollowing = "No"
                                list_bottomsheet = ArrayList()
                                list_bottomsheet?.clear()
                                list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_share_via_icon, resources.getString(R.string.share_via)))
                                list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_hide_post_icon, resources.getString(R.string.hide_this_post)))
                                if (!userData?.userID.equals(postList!![position]!!.userID)) {
                                    list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_unfollow_icon, resources.getString(R.string.follow)))
                                }
                                if (!userData?.userID.equals(postList!![position]!!.userID)) {
                                    list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_report_icon, resources.getString(R.string.report_this_Post_ad)))
                                    list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_copyright_icon, resources.getString(R.string.report_copyright_infringement)))

                                }
                                list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_save_post_icon, resources.getString(R.string.save_this_post)))

                            }
                            "userFollow" -> {
                                postList?.get(position)?.isYouFollowing = "Yes"
                                list_bottomsheet = ArrayList()
                                list_bottomsheet?.clear()
                                list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_share_via_icon, resources.getString(R.string.share_via)))
                                list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_hide_post_icon, resources.getString(R.string.hide_this_post)))
                                if (!userData?.userID.equals(postList!![position]!!.userID)) {
                                    list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_unfollow_icon, resources.getString(R.string.unfollow)))
                                }
                                if (!userData?.userID.equals(postList!![position]!!.userID)) {
                                    list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_report_icon, resources.getString(R.string.report_this_Post_ad)))
                                    list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_copyright_icon, resources.getString(R.string.report_copyright_infringement)))

                                }
                                list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_save_post_icon, resources.getString(R.string.save_this_post)))

                            }
                        }
                    } else {
                        (activity as MainActivity).showSnackBar(trendingFeedDatum[0].message!!)

                    }

                } else {
                    if (activity != null) {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(mainRootRv)

                    }
                }
            })
    }

    private fun getHidePost(position: Int) {
        MyUtils.showProgressDialog(mActivity!!, "Wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("postID", postList?.get(position)?.postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        reportPostModel.getCommonStatus(mActivity!!, false, jsonArray.toString(), "hidePost")
                .observe(this@UserFeedListsFragment, Observer { it ->

                    if (it != null && it.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()

                        if (it[0].status.equals("true", true)) {
                            (activity as MainActivity).showSnackBar(it[0].message!!)
                            postList?.removeAt(position)
                            homeFeedListAdapter?.notifyItemRemoved(postList!!.size)
                            homeFeedListAdapter?.notifyDataSetChanged()
                        } else {

                            (activity as MainActivity).showSnackBar(it[0].message!!)
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(mainRootRv)
                    }
                })

    }

    private fun getDeletePost(position: Int) {
        MyUtils.showProgressDialog(mActivity!!, "Wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("postID", postList?.get(position)?.postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        reportPostModel.getCommonStatus(mActivity!!, false, jsonArray.toString(), "deletePostList")
                .observe(this@UserFeedListsFragment, Observer { it ->
                    if (it != null && it.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()

                        if (it[0].status.equals("true", true)) {
                            (activity as MainActivity).showSnackBar(it[0].message!!)
                            postList?.removeAt(position)
                            homeFeedListAdapter?.notifyItemRemoved(postList!!.size)
                            homeFeedListAdapter?.notifyDataSetChanged()
                        } else {

                            (activity as MainActivity).showSnackBar(it[0].message!!)
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(mainRootRv)
                    }
                })

    }

    private fun openBottomSheetForShare(data: ArrayList<String>) {
        var bottomSheetData1 = ArrayList<String>()
        bottomSheetData1.clear()
        for (i in 0 until data.size) {
            bottomSheetData1.add(data[i])
        }
        val bottomSheet = BottomSheetFragment()
        val bundle = Bundle()
        bundle.putSerializable("data", bottomSheetData1)
        bundle.putString("from", "ShareList")
        bottomSheet.arguments = bundle
        bottomSheet.show(fragmentManager!!, "list")
    }

    private fun postLikeApi(from: String) {

        var jsonObject = JSONObject()
        var jsonArray = JSONArray()

        try {
            jsonObject.put("postID", "7")
            jsonObject.put("loginuserID", "1")
            jsonObject.put("action", "Add")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        reportPostModel.getCommonStatus(activity!!, false, jsonArray.toString(), from)
                .observe(this@UserFeedListsFragment,
                    { commonPojo ->
                        if (!commonPojo.isNullOrEmpty() && activity != null) {

                            if (commonPojo[0].status.equals("true")) {

                                (activity as MainActivity).showSnackBar(commonPojo[0].message)
                                homeFeedListAdapter?.notifyDataSetChanged()

                            } else {
                                (activity as MainActivity).showSnackBar(commonPojo[0].message)
                            }
                        } else {
                            (activity as MainActivity).errorMethod()
                        }

                    })
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btnRetry->{
                pageNo = 0
                setupObserver()
            }
        }
    }


}
