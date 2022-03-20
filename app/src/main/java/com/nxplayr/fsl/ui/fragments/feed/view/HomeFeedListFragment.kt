package com.nxplayr.fsl.ui.fragments.feed.view


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.*
import com.nxplayr.fsl.ui.fragments.feed.adapter.HomeFeedListAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.postcomment.viewmodel.CommentModel
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.ui.fragments.feed.viewmodel.CreatePostModel
import com.nxplayr.fsl.util.Constant
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nxplayr.fsl.ui.activity.post.view.CreatePostActivityTwo
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.ui.fragments.feedlikeviewlist.view.LikeViewFragment
import com.nxplayr.fsl.ui.fragments.otheruserprofile.view.OtherUserProfileMainFragment
import com.nxplayr.fsl.ui.fragments.bottomsheet.ThreeDotsBottomSheetFragment
import com.nxplayr.fsl.ui.activity.*
import com.nxplayr.fsl.ui.activity.post.view.CreatePostActivity
import com.nxplayr.fsl.ui.activity.post.view.ReportCopyRightActivity
import com.nxplayr.fsl.ui.activity.post.view.ReportPostActivity
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.ownprofile.view.ProfileMainFragment
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.custom_alert_dialog.view.*
import kotlinx.android.synthetic.main.fragment_comment_like_list.*
import kotlinx.android.synthetic.main.fragment_comment_like_list.swiperefresh
import kotlinx.android.synthetic.main.fragment_hash_tag_post_list.*
import kotlinx.android.synthetic.main.fragment_postcomment_list.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class HomeFeedListFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var postList: ArrayList<CreatePostData?>? = null
    var list_bottomsheet: ArrayList<ThreedotsBottomPojo>? = ArrayList()
    var homeFeedListAdapter: HomeFeedListAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var pageNo = 0
    var pageSize = 10
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var y: Int = 0
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var mActivity: Activity? = null
    var isIn = false
    var datumList1: ArrayList<UploadImagePojo> = ArrayList()
    var postId = ""
    var mBottomSheetDialog: BottomSheetDialog? = null
    var recentlyHidePos: Int = 0
    var recentlyHideItem: CreatePostData? = null
    var isLastPage1 = false
    var footballLevel=""
    var countryID=""
    var postType=""
    var sortby=""
    var pitchPosition=""
    var gender=""
    var footballagecatID=""
    var footballType=""
    var publicationTime=""
    var searchKeyword=""

    private lateinit var createPostModel: CreatePostModel
    private lateinit var apiCall : CommentModel
    private lateinit var reportPostModel : CommonStatusModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
      //  if (v == null) {
            v = inflater.inflate(R.layout.fragment_comment_like_list, container, false)
       // }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
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

    private fun setupViewModel() {
        createPostModel = ViewModelProvider(this@HomeFeedListFragment).get(CreatePostModel::class.java)
        apiCall = ViewModelProvider(this@HomeFeedListFragment).get(CommentModel::class.java)
        reportPostModel = ViewModelProvider(this@HomeFeedListFragment).get(CommonStatusModel::class.java)

    }

     fun setupObserver() {
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            postList!!.clear()
            isLastpage = false
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
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("page", pageNo)
            jsonObject.put("publicationTime", publicationTime)
            jsonObject.put("gender", gender)
            jsonObject.put("footballLevel", footballLevel)
            jsonObject.put("footballType", footballType)
            jsonObject.put("pitchPosition", pitchPosition)
            jsonObject.put("tag", "")
            jsonObject.put("countryID", countryID)
            jsonObject.put("footballagecatID", footballagecatID)
            jsonObject.put("sortby", sortby)
            jsonObject.put("pagesize", "10")
            jsonObject.put("postType", "Social")
            when(postType)
            {
                "All"->{
                    jsonObject.put("postMediaType", "")

                }else->{
                jsonObject.put("postMediaType", postType)
              }
            }
            jsonObject.put("tabname", "trending")
            jsonObject.put("searchKeyword", searchKeyword)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        }
        catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        createPostModel.apiFunction(mActivity!!, jsonArray.toString(), "getPostList")
            .observe(viewLifecycleOwner, { postListPojo ->
                if (postListPojo != null && postListPojo.isNotEmpty())
                {
                    isLoading = false
                    ll_no_data_found.visibility = View.GONE
                    nointernetMainRelativelayout.visibility = View.GONE
                    relativeprogressBar.visibility = View.GONE
                    if (pageNo > 0) {
                        postList!!.removeAt(postList!!.size - 1)
                        homeFeedListAdapter!!.notifyItemRemoved(postList!!.size)
                    }
                    if (postListPojo[0].status.equals("true",false))
                    {

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

                    }
                    else
                    {
                        relativeprogressBar.visibility = View.GONE

                        if (postList!!.isNullOrEmpty()) {
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
                    if (mActivity != null)
                    {
                        relativeprogressBar.visibility = View.GONE
                        recyclerview.visibility = View.GONE
                        ErrorUtil.errorView(mActivity!!,nointernetMainRelativelayout)
                    }
                }
            })

    }


    private fun setupUI() {
        btnRetry?.setOnClickListener(this)
        floating_btn_createpost?.setOnClickListener(this)
        floating_btn_scroll?.setOnClickListener(this)

       swiperefresh.setOnRefreshListener {
            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            refreshItems()
        }
        mBottomSheetDialog = BottomSheetDialog(mActivity!!)

        linearLayoutManager = LinearLayoutManager(mActivity!!)
        if (postList == null) {
            postList = ArrayList()
            homeFeedListAdapter = HomeFeedListAdapter(mActivity!!, postList!!, object : HomeFeedListAdapter.OnItemClick {
                override fun onClicled(position: Int, from: String, createData: CreatePostData, sendComment: String, view1: View) {

                    when (from) {
                        "like_unlike" -> {
                            //postLikeApi("user_Like")
                            (activity as MainActivity).navigateTo(LikeViewFragment(), LikeViewFragment::class.java.name, true)
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

                            openBottomSheet(list_bottomsheet!!, position, postList!![position]!!.postID)

                        }
                        "list_share" -> {
                            openBottomSheetForShare(position)
                        }
                        "OtherUserProfile" -> {
                            if (!userData?.userID?.equals(postList?.get(position)?.userID)!!) {
                                var bundle = Bundle()
                                if (postList?.get(position)!!.postShared == "1") {
                                    bundle.putString("userId", postList?.get(position)?.originaluserID)
                                } else {
                                    bundle.putString("userId", postList?.get(position)?.userID)
                                }
                                (activity as MainActivity).navigateTo(OtherUserProfileMainFragment(), bundle, OtherUserProfileMainFragment::class.java.name, true)

                            } else {
                                var bundle = Bundle()
                                if (postList?.get(position)!!.postShared == "1") {
                                    bundle.putString("userId", postList?.get(position)?.originaluserID)
                                } else {
                                    bundle.putString("userId", postList?.get(position)?.userID)
                                }
                                (activity as MainActivity).navigateTo(ProfileMainFragment(), bundle, ProfileMainFragment::class.java.name, true)

                            }

                        }
                        "other_user_profile" -> {
                            if (!userData?.userID?.equals(postList?.get(position)?.userID)!!) {
                                var bundle = Bundle()
                                if (postList?.get(position)!!.postShared == "1") {
                                    bundle.putString("userId", postList?.get(position)?.originaluserID)
                                } else {
                                    bundle.putString("userId", postList?.get(position)?.userID)
                                }
                                (activity as MainActivity).navigateTo(OtherUserProfileMainFragment(), bundle, OtherUserProfileMainFragment::class.java.name, true)

                            } else {
                                var bundle = Bundle()
                                if (postList?.get(position)!!.postShared == "1") {
                                    bundle.putString("userId", postList?.get(position)?.originaluserID)
                                } else {
                                    bundle.putString("userId", postList?.get(position)?.userID)
                                }
                                bundle.putSerializable("postData", postList?.get(position))
                                (activity as MainActivity).navigateTo(ProfileMainFragment(), bundle, ProfileMainFragment::class.java.name, true)

                            }
                        }
                        "sendComment" -> {
                            MyUtils.hideKeyboard1(mActivity!!)
                            if((view1 as EditText).text.toString().trim().isNullOrEmpty())
                            {
                                MyUtils.showSnackbar(mActivity!!,"Please enter your comment",mainRootRv)
                            }
                            else{
                                sendPostComment(postList!![position]!!.postID, postList!![position]!!.postMediaType, sendComment, position, view1 as EditText)
                            }
                        }
                    }
                }
            })
            recyclerview.layoutManager = linearLayoutManager
            recyclerview.isNestedScrollingEnabled=true
            recyclerview.adapter = homeFeedListAdapter
            setupObserver()

        }
        else
        {
            homeFeedListAdapter = HomeFeedListAdapter(mActivity!!, postList!!, object : HomeFeedListAdapter.OnItemClick {
                override fun onClicled(position: Int, from: String, createData: CreatePostData, sendComment: String, view1: View) {

                    when (from) {
                        "like_unlike" -> {
                            //postLikeApi("user_Like")
                            (activity as MainActivity).navigateTo(LikeViewFragment(), LikeViewFragment::class.java.name, true)
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

                            openBottomSheet(list_bottomsheet!!, position, postList!![position]!!.postID)

                        }
                        "list_share" -> {
                            openBottomSheetForShare(position)
                        }
                        "OtherUserProfile" -> {
                            if (!userData?.userID?.equals(postList?.get(position)?.userID)!!) {
                                var bundle = Bundle()
                                if (postList?.get(position)!!.postShared == "1") {
                                    bundle.putString("userId", postList?.get(position)?.originaluserID)
                                } else {
                                    bundle.putString("userId", postList?.get(position)?.userID)
                                }
                                (activity as MainActivity).navigateTo(OtherUserProfileMainFragment(), bundle, OtherUserProfileMainFragment::class.java.name, true)

                            } else {
                                var bundle = Bundle()
                                if (postList?.get(position)!!.postShared == "1") {
                                    bundle.putString("userId", postList?.get(position)?.originaluserID)
                                } else {
                                    bundle.putString("userId", postList?.get(position)?.userID)
                                }
                                (activity as MainActivity).navigateTo(ProfileMainFragment(), bundle, ProfileMainFragment::class.java.name, true)

                            }

                        }
                        "other_user_profile" -> {
                            if (!userData?.userID?.equals(postList?.get(position)?.userID)!!) {
                                var bundle = Bundle()
                                if (postList?.get(position)!!.postShared == "1") {
                                    bundle.putString("userId", postList?.get(position)?.originaluserID)
                                } else {
                                    bundle.putString("userId", postList?.get(position)?.userID)
                                }
                                (activity as MainActivity).navigateTo(OtherUserProfileMainFragment(), bundle, OtherUserProfileMainFragment::class.java.name, true)

                            } else {
                                var bundle = Bundle()
                                if (postList?.get(position)!!.postShared == "1") {
                                    bundle.putString("userId", postList?.get(position)?.originaluserID)
                                } else {
                                    bundle.putString("userId", postList?.get(position)?.userID)
                                }
                                bundle.putSerializable("postData", postList?.get(position))
                                (activity as MainActivity).navigateTo(ProfileMainFragment(), bundle, ProfileMainFragment::class.java.name, true)

                            }
                        }
                        "sendComment" -> {
                            MyUtils.hideKeyboard1(mActivity!!)
                            if((view1 as EditText).text.toString().trim().isNullOrEmpty())
                            {
                                MyUtils.showSnackbar(mActivity!!,"Please enter your comment",mainRootRv)
                            }
                            else{
                                sendPostComment(postList!![position]!!.postID, postList!![position]!!.postMediaType, sendComment, position, view1 as EditText)
                            }
                        }
                    }
                }
            })
            recyclerview.layoutManager = linearLayoutManager
            recyclerview.isNestedScrollingEnabled=true
            recyclerview.adapter = homeFeedListAdapter

        }

        recyclerview.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
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
                        floating_btn_scroll.visibility=View.VISIBLE
                        homeFeedListAdapter?.playAvailableVideos(newState, recyclerview)
                        val firstPos = linearLayoutManager!!.findFirstVisibleItemPosition()
                        val lastPos = linearLayoutManager!!.findLastVisibleItemPosition()
                        if (homeFeedListAdapter != null && postList?.size!! > 0)
                            homeFeedListAdapter?.setAdapterPosition(firstPos, lastPos)
                    }
                }
            })
    }

    private fun sendPostComment(postID: String, postMediaType: String, commentComment: String, position: Int, view1: EditText) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("postID", postID)
            jsonObject.put("action", "Add")
            if(postMediaType.equals("Photo"))
            {
                jsonObject.put("commentMediaType", "Image")
            }
            else
            {
                jsonObject.put("commentMediaType", postMediaType)
            }
            jsonObject.put("commentType", "Upload")
            jsonObject.put("commentComment", Constant.encode(commentComment))
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiCall.apiFunction(mActivity!!, true, jsonArray.toString(), 1)
            .observe(this@HomeFeedListFragment,
                { response ->

                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true")) {
                            if (!response[0].data.isNullOrEmpty()) {
                                postList!![position]!!.postCommentList?.add(0, response[0].data[0])
                                postList!![position]!!.postComment = (postList!![position]!!.postComment.toInt() + 1).toString()
                            }
                            if(!postList!![position]!!.postMediaType.equals("video",false))
                            {
                                homeFeedListAdapter?.notifyItemChanged(position)
                            }
                            view1.text.clear()
                        } else {
                            //data not find
                            MyUtils.showSnackbar(
                                mActivity!!,
                                response[0].message,
                                mainRootRv!!
                            )
                        }
                    } else {
                        ErrorUtil.errorMethod(mainRootRv)
                    }
                })
    }

    private fun openBottomSheet(data: ArrayList<ThreedotsBottomPojo>, position: Int, postID: String) {

        val bottomSheet = ThreeDotsBottomSheetFragment()
        val bundle = Bundle()
        bundle.putSerializable("data", data)
        bundle.putString("from", "MenuList")
        bundle.putString("userId", postList!![position]!!.userID)
        bottomSheet.arguments = bundle
        bottomSheet.show(childFragmentManager, "List")
        bottomSheet.setOnclickLisner(object : ThreeDotsBottomSheetFragment.SelectList {
            override fun onOptionSelect(value: Int, from: String) {
                when (from) {
                    resources.getString(R.string.share_via) -> {
                        var uri =
                                Uri.parse("https://play.google.com/store/apps/details?id=" + mActivity?.getPackageName());
                        val shareIntent = Intent()
                        shareIntent.action = Intent.ACTION_SEND
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Download the app now - " + uri + "\n\n");
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)))
                    }
                    resources.getString(R.string.hide_this_post) -> {

                        getHidePost(position, postID)


                    }
                    resources.getString(R.string.unfollow) -> {
                        getUnfollow(position)

                    }
                    resources.getString(R.string.follow) -> {
                        getUnfollow(position)

                    }
                    resources.getString(R.string.report_this_Post_ad) -> {
//                        var repostReasonFragment = RepostReasonFragment()
//                        Bundle().apply {
//                            putString("fromReport", "postReport")
//                            putString("PostId", postList?.get(position)?.postID)
//                            repostReasonFragment.arguments = this
//                        }
//                        (activity as MainActivity).navigateTo(repostReasonFragment, repostReasonFragment::class.java.name, true)

                        val intent = Intent(mActivity!!, ReportPostActivity::class.java)
                        intent.putExtra("position", position)
                        intent.putExtra("PostId", postList?.get(position)?.postID)
                        intent.putExtra("postList", postList)
                        startActivity(intent)
                        mActivity!!.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    }
                    resources.getString(R.string.report_copyright_infringement) -> {
                        val intent = Intent(mActivity!!, ReportCopyRightActivity::class.java)
                        intent.putExtra("position", position)
                        intent.putExtra("PostId", postList?.get(position)?.postID)
                        intent.putExtra("postList", postList)
                        intent.putExtra("postListData", postList?.get(position))
                        startActivity(intent)
                        mActivity!!.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    }
                    resources.getString(R.string.save_this_post) -> {
                        getSavePost(position)
                    }
                    resources.getString(R.string.edit) -> {
                        Intent(mActivity!!, CreatePostActivityTwo::class.java).apply {
                            putExtra("postData", postList?.get(position))
                            putExtra("fromType", "editPost")
                            startActivity(this)
                        }
                    }
                    "Delete" -> {
                        getDeletePost(position)
                    }
                }
                bottomSheet.dismiss()

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
                .observe(this@HomeFeedListFragment, Observer { it ->
                    if (it != null && it.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()
                        if (it[0].status.equals("true", true)) {
                            if (it[0].message.isNullOrEmpty()) {
                                (activity as MainActivity).showSnackBar("Post has been saved")

                            } else {
                                (activity as MainActivity).showSnackBar(it[0].message!!)

                            }
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
        reportPostModel.getCommonStatus(mActivity!!, false, jsonArray.toString(), fromUser).observe(this@HomeFeedListFragment,
                Observer
                { trendingFeedDatum ->
                    if (trendingFeedDatum != null && trendingFeedDatum.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()
                        if (trendingFeedDatum[0].status?.equals("true", false)!!) {
                            when (fromUser) {
                                "userUnfollow" -> {
                                    if (trendingFeedDatum[0].message.isNullOrEmpty()) {
                                        (activity as MainActivity).showSnackBar("Successfully Unfollow")

                                    } else {
                                        (activity as MainActivity).showSnackBar(trendingFeedDatum[0].message!!)

                                    }

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
                                    if (trendingFeedDatum[0].message.isNullOrEmpty()) {
                                        (activity as MainActivity).showSnackBar("Successfully follow")

                                    } else {
                                        (activity as MainActivity).showSnackBar(trendingFeedDatum[0].message!!)

                                    }
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

    private fun openBottomSheetDialog(postID: String?, position: Int) {

        val sheetView = activity!!.layoutInflater.inflate(com.nxplayr.fsl.R.layout.custom_alert_dialog, null)
        mBottomSheetDialog!!.setContentView(sheetView)
        mBottomSheetDialog!!.show()

        sheetView.btn_undo.setOnClickListener {

            unHidePostApi(postId!!, position)
        }
    }

    private fun getHidePost(position: Int, postID: String) {
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
                .observe(this@HomeFeedListFragment, Observer { it ->
                    //            MyUtils.closeProgress()

                    if (it != null && it.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()

                        if (it[0].status.equals("true", true)) {
                            recentlyHidePos = position
                            recentlyHideItem = postList?.get(position)
                            postId = postList!!.get(position)!!.postID

                            for (i in 0 until postList?.size!!) {
                                if (postList?.get(i)?.postID.equals(postID, false)) {
                                    postList?.removeAt(i)
                                    homeFeedListAdapter?.notifyItemRemoved(postList!!.size)
                                    homeFeedListAdapter?.notifyDataSetChanged()
                                    if (postList?.size == 0) {
                                        isLastPage1 = true
                                    }
                                    break
                                }
                            }

                            if (isLastPage1) {
                                postList?.clear()
                                pageNo = 0
                                setupObserver()
                            }

                            Handler().postDelayed({
                                openBottomSheetDialog(postId, position)
                                Handler().postDelayed({
                                    mBottomSheetDialog?.dismiss()
                                }, 5000)

                            }, 1000)


                        } else {

                            (activity as MainActivity).showSnackBar(it[0].message!!)
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(mainRootRv)
                    }
                })

    }

    private fun unHidePostApi(postID: String, position: Int) {
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

        reportPostModel.getCommonStatus(mActivity!!, false, jsonArray.toString(), "removehidePost")
                .observe(this@HomeFeedListFragment!!,
                        androidx.lifecycle.Observer { commonStatuspojo ->
                            if (commonStatuspojo != null && commonStatuspojo.isNotEmpty()) {
                                if (commonStatuspojo[0].status.equals("true", false)) {

                                    MyUtils.dismissProgressDialog()
                                    mBottomSheetDialog!!.dismiss()

                                    postList?.add(recentlyHidePos, recentlyHideItem)
                                    homeFeedListAdapter?.notifyItemInserted(recentlyHidePos)

                                } else {
                                    MyUtils.dismissProgressDialog()
                                    MyUtils.showSnackbar(mActivity!!, commonStatuspojo[0].message, mainRootRv)
                                }

                            } else {
                                MyUtils.dismissProgressDialog()
                                (activity as MainActivity).errorMethod()
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
                .observe(this@HomeFeedListFragment, Observer { it ->
                    //            MyUtils.closeProgress()

                    if (it != null && it.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()

                        if (it[0].status.equals("true", true)) {
                            (activity as MainActivity).showSnackBar(it[0].message!!)
                            postList?.removeAt(position)
                            homeFeedListAdapter?.notifyItemRemoved(postList!!.size)
                            homeFeedListAdapter?.notifyDataSetChanged()
                            /*if (postList?.size == 0) {
                                isLastPage1 = true
                            }*/

                            /* if (isLastPage1) {
                                feedList?.clear()
                                pageNo = 0
                                getPostList()
                            }*/
                        } else {

                            (activity as MainActivity).showSnackBar(it[0].message!!)
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(mainRootRv)
                    }
                })

    }

    private fun openBottomSheetForShare(position: Int) {
        list_bottomsheet = ArrayList()
        list_bottomsheet!!.clear()
        list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_share_in_post_icon, resources.getString(R.string.share_in_a_post)))
        list_bottomsheet!!.add(ThreedotsBottomPojo(R.drawable.popup_send_messages_icon, resources.getString(R.string.send_in_a_private_message)))
        val bottomSheet = ThreeDotsBottomSheetFragment()
        val bundle = Bundle()
        bundle.putSerializable("data", list_bottomsheet)
        bundle.putString("from", "ShareList")
        bottomSheet.arguments = bundle
        bottomSheet.show(fragmentManager!!, "list")
        bottomSheet.setOnclickLisner(object : ThreeDotsBottomSheetFragment.SelectList {
            override fun onOptionSelect(value: Int, from: String) {
                when (from) {
                    resources.getString(R.string.share_in_a_post) -> {
                        Intent(mActivity!!, CreatePostActivityTwo::class.java).apply {
                            putExtra("postDataShare", postList?.get(position))
                            putExtra("fromType", "postShare")
                            startActivity(this)
                        }
                        bottomSheet.dismiss()

                    }
                    resources.getString(R.string.send_in_a_private_message) -> {
                    }
                }
            }

        })

    }

    fun createPost(from: String, datumList: List<CreatePostData?>?, stringExtraDes: String, stringExtraPrivcy: String) {
        postList?.add(0, datumList!![0])
        homeFeedListAdapter?.notifyDataSetChanged()
    }

    fun isProgress(isVisible: Boolean) {
        if (isVisible) {
            ll_uploading_progress.visibility = View.VISIBLE
        } else {
            ll_uploading_progress.visibility = View.GONE
        }
    }

    fun refreshField() {
        val positionView =
                (recyclerview.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (positionView == 0) {
            (activity as MainActivity?)!!.showexit()
        } else {
            recyclerview.smoothScrollToPosition(0)
            refreshItems()
        }
    }

    fun refreshItems() {
        swiperefresh.isRefreshing = true
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("page", "0")
            jsonObject.put("pagesize", "10")
            jsonObject.put("tabname", "trending")
            jsonObject.put("postType", "Social")
            jsonObject.put("postID", "0")
            jsonObject.put("publicationTime", publicationTime)
            jsonObject.put("gender", gender)
            jsonObject.put("footballLevel", footballLevel)
            jsonObject.put("footballType", footballType)
            jsonObject.put("pitchPosition", pitchPosition)
            jsonObject.put("tag", "")
            jsonObject.put("countryID", countryID)
            jsonObject.put("footballagecatID", footballagecatID)
            jsonObject.put("sortby", sortby)
            when(postType)
            {
                "All"->{
                    jsonObject.put("postMediaType", "")

                }else->{
                jsonObject.put("postMediaType", postType)

            }
            }
            jsonObject.put("tabname", "trending")
            jsonObject.put("searchKeyword", searchKeyword)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        createPostModel.apiFunction(mActivity!!, jsonArray.toString(), "getPostList")
                .observe(viewLifecycleOwner, { trendingFeedlistpojos ->
                    isLoading = false
                    if (trendingFeedlistpojos != null && trendingFeedlistpojos.isNotEmpty()) {
                        if (trendingFeedlistpojos[0]!!.status.equals("true", false)) {
                            pageNo = 0
                            if (pageNo == 0) {
                                postList?.clear()
                            }
                            postList?.addAll(trendingFeedlistpojos[0].data!!)
                            homeFeedListAdapter?.notifyDataSetChanged()
                            recyclerview.scrollToPosition(0)
                        } else {
                        }
                        if (postList == null && postList!!.size == 0) {
                            ll_no_data_found.visibility = View.VISIBLE
                        } else {
                            ll_no_data_found.visibility = View.GONE
                        }
                    }
                    swiperefresh.isRefreshing = false
                }
                )
    }

    fun getPostListApi() {
        pageNo = 0
        setupObserver()
    }

    fun applyFilter( postType: String, sortby: String,gender: String, publicationTime: String) {

        this.postType= postType
        this. sortby = sortby
        this. gender = gender
        this. publicationTime = publicationTime
        pageNo = 0
        isLastpage = false
        isLoading = false
        setupObserver()
    }

    fun applySearch(searchKeyword: String) {
        this.searchKeyword=searchKeyword
        pageNo = 0
        isLastpage = false
        isLoading = false
        setupObserver()
    }

    override fun onStop() {
        super.onStop()
        if (homeFeedListAdapter != null) {
            homeFeedListAdapter?.stopPlayer()
            homeFeedListAdapter?.pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (homeFeedListAdapter != null) {
            homeFeedListAdapter?.stopPlayer()
            homeFeedListAdapter?.pausePlayer()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btnRetry->{
                pageNo = 0
                setupObserver()

            }
            R.id.floating_btn_createpost->{
                var intent = Intent(mActivity!!, CreatePostActivity::class.java)
                startActivity(intent)
                mActivity?.overridePendingTransition(R.anim.slide_up, R.anim.stay)
            }
            R.id.floating_btn_scroll->{
                linearLayoutManager?.scrollToPosition(0)
                floating_btn_scroll.visibility=View.GONE
            }
        }
    }

    fun notifyData(
        feedDatum: CreatePostData?,
        delete: Boolean,
        deleteComment: Boolean,
        postComment: String?,
        commentId:String?
    ) {
        for(i in 0 until postList?.size!!)
        {
            if(postList!![i]!!.postID.equals(feedDatum!!.postID,false))
            {

                    postList?.set(i,feedDatum)
                    homeFeedListAdapter?.notifyItemChanged(i)
                    break
            }
        }

    }
}





