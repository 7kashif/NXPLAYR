package com.nxplayr.fsl.ui.fragments.postcomment.view


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.ui.activity.main.managers.NotifyInterface
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.bottomsheet.BottomSheetListFragment
import com.nxplayr.fsl.ui.fragments.bottomsheet.PrivacyBottomSheetFragment
import com.nxplayr.fsl.ui.fragments.feedlikeviewlist.viewmodel.PostViewModel
import com.nxplayr.fsl.ui.fragments.friendrequest.viewmodel.FriendListModel
import com.nxplayr.fsl.ui.fragments.postcomment.adapter.PostCommentListAdapter
import com.nxplayr.fsl.ui.fragments.postcomment.viewmodel.CommentModel
import com.nxplayr.fsl.ui.fragments.postcomment.viewmodel.PostSubCommentLikeModel
import com.nxplayr.fsl.ui.fragments.postcomment.viewmodel.ReasonModel
import com.nxplayr.fsl.ui.fragments.postcomment.viewmodel.ReplyCommentModel
import com.nxplayr.fsl.ui.fragments.userconnection.viewmodel.ConnectionListModel
import com.nxplayr.fsl.util.*
import com.nxplayr.fsl.viewmodel.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_postcomment_list.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.text.ParseException

/**
 * A simple [Fragment] subclass.
 */
class PostCommentListFragment : Fragment(), PrivacyBottomSheetFragment.selectPrivacy,
    BottomSheetListFragment.SelectLanguage, View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var titleChangePassword = ""

    var commentListAdapter: PostCommentListAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var y: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var pageNo = 0
    var commnethint = ""

    var arrayComment: ArrayList<CommentData?>? = null
    var arrayReplyComment: ArrayList<Postcommentreply?> = ArrayList()
    var postID = ""
    var postUserID = ""
    var objReason: ReasonList? = null
    var isCommentReply = false
    var commentID: String = ""
    var mPosition: Int = -1
    var progressBar: ProgressBar? = null
    var replyCommentId = ""
    var commentReplyReply = ""
    var replyCommentPos = -1
    var feedData: CreatePostData? = null
    var notifyInterface: NotifyInterface? = null
    var conneTypeList: ArrayList<ConnectionListData>? = ArrayList()
    var conneTypeID = ""
    var userId = ""
    var arrayData: CommentData? = null
    private lateinit var apiCall: CommentModel
    private lateinit var postViewModel: PostViewModel
    private lateinit var postSubCommentLikeModel: PostSubCommentLikeModel
    private lateinit var replyApiCall: ReplyCommentModel
    private lateinit var reasonModel: ReasonModel
    private lateinit var connectionListModel: ConnectionListModel
    private lateinit var friendListModel: FriendListModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //  if (v == null) {
        v = inflater.inflate(R.layout.fragment_postcomment_list, container, false)
        //  }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager != null && sessionManager!!.isLoggedIn()) {
            userData = sessionManager?.get_Authenticate_User()
            setUserData1()
        }
        if (arguments != null) {
            postID = arguments?.getString("postId")!!
            postUserID = arguments?.getString("postUserID")!!
            feedData = arguments?.getSerializable("feedData") as CreatePostData?
        }
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle.text = "Comments"
        toolbar.setNavigationOnClickListener {
            (mActivity as MainActivity).onBackPressed()
        }
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        relativeprogressBar.visibility = View.GONE
        setReply(false, "")
        linearLayoutManager = LinearLayoutManager(mActivity!!)
        recyclerview.layoutManager = linearLayoutManager
        recyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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

                        getCommentList()
                    }
                }
            }
        })
        setCommentAdapter()
        editComment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().length > 0) {
                    send.isClickable = true
                    send.isEnabled = true
                } else {
                    send.isClickable = false
                    send.isEnabled = false
                }
            }
        })

        send.setOnClickListener(this)
        btnRetry.setOnClickListener(this)
        img_clear.setOnClickListener(this)

    }

    private fun setupViewModel() {
        apiCall = ViewModelProvider(this@PostCommentListFragment).get(CommentModel::class.java)
        postViewModel =
            ViewModelProvider(this@PostCommentListFragment).get(PostViewModel::class.java)
        postSubCommentLikeModel =
            ViewModelProvider(this@PostCommentListFragment).get(PostSubCommentLikeModel::class.java)
        replyApiCall =
            ViewModelProvider(this@PostCommentListFragment).get(ReplyCommentModel::class.java)
        connectionListModel =
            ViewModelProvider(this@PostCommentListFragment).get(ConnectionListModel::class.java)
        friendListModel =
            ViewModelProvider(this@PostCommentListFragment).get(FriendListModel::class.java)
    }

    private fun setUserData1() {
        if (userData != null) {
            var imgUrl = ""
            if (!userData?.userProfilePicture.isNullOrEmpty()) {
                imgUrl = RestClient.image_base_url_users + userData?.userProfilePicture
            }
            imv_user_dp_comment?.setImageURI(Uri.parse(imgUrl))

            var userName = ""
            if (!userData?.userFirstName.isNullOrEmpty() && !userData?.userLastName.isNullOrEmpty()) {
                userName = userData?.userFirstName + " " + userData?.userLastName
            } else if (!userData?.userFirstName.isNullOrEmpty() && userData?.userLastName.isNullOrEmpty()) {
                userName = userData?.userFirstName + ""
            } else if (userData?.userFirstName.isNullOrEmpty() && !userData?.userLastName.isNullOrEmpty()) {
                userName = userData?.userLastName + ""
            }

//            editComment.hint = "$commnethint $userName"

        }
    }

    private fun setCommentAdapter() {
        //  if (arrayComment.isNullOrEmpty()) {
        arrayComment = ArrayList<CommentData?>()
        commentListAdapter = PostCommentListAdapter(
            mActivity!!, arrayComment!!,
            object : PostCommentListAdapter.OnItemClick {
                override fun onClicklisneter(
                    pos: Int,
                    actionType: Int,
                    comentObj: CommentData,
                    v1: View,
                    commentId: String,
                    comment: String,
                    replyPos: Int
                ) {
                    mPosition = pos
                    commentID = comentObj.commentID!!
                    when (actionType) {
                        0 -> {
                            /*MyUtils.showMessageYesNo(
                                    mActivity!!,
                                    "Are you sure you want to delete comment?", "Delete Comment"
                            ) { dialog, which ->
                                dialog?.dismiss()
                                deleteComment(comentObj.commentID, pos)
                            }*/
                        }
                        1 -> {
                            if (objReason == null) {
                                getReasonList(comentObj.commentID, pos)
                            }
                        }
                        3 -> {
                            feedFavorite(v1 as AppCompatImageView, pos)
                        }
                        4 -> {
                            isCommentReply = true
                            setReply(
                                true,
                                comentObj.userFirstName + " " + comentObj.userLastName
                            )

                            getReplyCommentList(pos, arrayComment?.get(pos)!!.commentID)

                            editComment.requestFocus()
                        }
                        5 -> {
                            progressBar = v1 as ProgressBar
                            getReplyCommentList(pos, arrayComment?.get(pos)!!.commentID)
                        }
                        6 -> {
                            MyUtils.showMessageYesNo(
                                mActivity!!,
                                "Are you sure you want to delete comment?", "Delete Comment"
                            ) { dialog, which ->
                                dialog?.dismiss()
                                deleteReplyComment(commentId, replyPos, pos)
                            }

                        }
                        7 -> {
                            editComment.setText(comment)
                            editComment.setSelection(editComment.length())
                            val imm =
                                mActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(
                                editComment,
                                InputMethodManager.SHOW_IMPLICIT
                            )

                            replyCommentPos = replyPos
                            replyCommentId = commentId
                            commentReplyReply = comment
                            isCommentReply = true
                            showKeyboard()
                            setReply(true, comentObj.userFirstName + " " + comentObj.userLastName)
                            editComment.requestFocus()

                        }
                        8 -> {
                            feedSubCommentFavorite(v1 as AppCompatImageView, pos, replyPos)
                        }
                        9 -> {
                            isCommentReply = true
                            setReply(true, comment)
                            getReplyCommentList(pos, arrayComment?.get(pos)!!.commentID)
                            editComment.requestFocus()
                        }
                    }

                }

                override fun onClickListner(position: Int, from: String, data: CommentData) {
                    userId = arrayComment!![position]!!.userID
                    arrayData = data
                    commentID = arrayComment!![position]!!.commentID
                    when (from) {
                        "connectionType" -> {
                            getPrivacy(
                                arrayComment!![position]!!.userFirstName,
                                arrayComment!![position]!!.userID,
                                data
                            )
                        }

                    }

                }

            }, "", arrayReplyComment
        )
        recyclerview.adapter = commentListAdapter
        commentListAdapter?.notifyDataSetChanged()

        if (MyUtils.isInternetAvailable(mActivity!!)) {
            pageNo = 0
            getCommentList()
        }
        // }
    }

    fun feedFavorite(
        favouriteimageview: AppCompatImageView,
        feedPosition: Int
    ) {
        favouriteimageview.isEnabled = false
        val action: String
        if (arrayComment!!.get(feedPosition)!!.isCommentLiked.equals("No", false)) {

            favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.comment_like_icon_selected))
            action = "LikePostCommnet"
            var count = 0
            try {
                count = Integer.valueOf(arrayComment!!.get(feedPosition)!!.postcommentLike) + 1
            } catch (e: java.lang.Exception) {
            }
            arrayComment!!.get(feedPosition)!!.postcommentLike = count.toString()
            arrayComment?.get(feedPosition)!!.isCommentLiked = ("Yes")
            commentListAdapter?.notifyItemChanged(feedPosition)
        } else {
            favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.comment_like_icon_unselected))
            action = "RemoveLikePostCommnet"
            var count = 0
            try {
                count = Integer.valueOf(arrayComment!!.get(feedPosition)!!.postcommentLike) - 1
            } catch (e: java.lang.Exception) {
            }
            arrayComment!!.get(feedPosition)!!.postcommentLike = count.toString()
            arrayComment?.get(feedPosition)!!.isCommentLiked = ("No")
            commentListAdapter?.notifyItemChanged(feedPosition)
        }


        val json = Gson().toJson(arrayComment?.get(feedPosition))
        val trendingFeedDatum =
            Gson().fromJson(json, CreatePostData::class.java)
        setCommentLike(
            arrayComment?.get(feedPosition)!!.commentID,
            action,
            feedPosition,
            trendingFeedDatum
        )
        favouriteimageview.isEnabled = true
    }

    private fun setCommentLike(
        commentID: String,
        action: String,
        feedPosition: Int,
        trendingFeedDatum: CreatePostData?
    ) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("commentID", commentID)
            when (action) {
                "LikePostCommnet" -> {
                    jsonObject.put("action", "Add")
                }
                "RemoveLikePostCommnet" -> {
                    jsonObject.put("action", "Delete")
                }
            }
            jsonObject.put("postID", postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        postViewModel.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            viewLifecycleOwner,
            { postlikePojos ->
                if (mActivity != null) {
                    if (postlikePojos?.get(0)?.status.equals("true", false)) {
                        when (action) {
                            "LikePostCommnet" -> {
                                arrayComment?.get(feedPosition)!!.isCommentLiked = "Yes"
                                for(j in 0 until feedData?.postCommentList?.size!!)
                                {
                                    if(feedData?.postCommentList!![j].commentID.equals(arrayComment?.get(feedPosition)!!.commentID))
                                    {
                                        feedData?.postCommentList!![j].isCommentLiked= arrayComment?.get(feedPosition)!!.isCommentLiked
                                        feedData?.postCommentList!![j].postcommentLike=(feedData?.postCommentList!![j].postcommentLike.toInt()+1).toString()
                                        break
                                    }
                                }
                            }
                            "RemoveLikePostCommnet" -> {
                                arrayComment?.get(feedPosition)!!.isCommentLiked = "No"
                                for(j in 0 until feedData?.postCommentList?.size!!)
                                {
                                    if(feedData?.postCommentList!![j].commentID.equals(arrayComment?.get(feedPosition)!!.commentID))
                                    {
                                        feedData?.postCommentList!![j].isCommentLiked= arrayComment?.get(feedPosition)!!.isCommentLiked
                                        feedData?.postCommentList!![j].postcommentLike=(feedData?.postCommentList!![j].postcommentLike.toInt()-1).toString()
                                        break
                                    }
                                }
                            }

                        }

                        if (notifyInterface != null) {
                            notifyInterface!!.notifyData(
                                feedData,
                                false,
                                false,
                                arrayComment?.get(feedPosition)!!.isCommentLiked,
                                arrayComment?.get(feedPosition)!!.commentID
                            )
                        }
                    } else {

                        MyUtils.showSnackbar(
                            mActivity!!,
                            postlikePojos?.get(0)?.message!!,
                            commentLayoutMain
                        )
                    }

                }
            })
    }


    fun feedSubCommentFavorite(
        favouriteimageview: AppCompatImageView,
        feedPosition: Int,
        replyPos: Int
    ) {
        favouriteimageview.isEnabled = false
        val action: String
        if (arrayComment!!.get(feedPosition)?.postcommentreply!![replyPos].isCommentLiked.equals(
                "No",
                false
            )
        ) {

            favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.comment_like_icon_selected))
            action = "LikePostCommnet"
            var count = 0
            try {
                count =
                    Integer.valueOf(arrayComment!!.get(feedPosition)?.postcommentreply!![replyPos].replylikedCount) + 1
            } catch (e: java.lang.Exception) {
            }
            arrayComment!!.get(feedPosition)?.postcommentreply!![replyPos].replylikedCount =
                count.toString()
            arrayComment!!.get(feedPosition)?.postcommentreply!![replyPos].isCommentLiked = ("Yes")
            commentListAdapter?.notifyItemChanged(feedPosition)
        } else {
            favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.comment_like_icon_unselected))
            action = "RemoveLikePostCommnet"
            var count = 0
            try {
                count =
                    Integer.valueOf(arrayComment!!.get(feedPosition)?.postcommentreply!![replyPos].replylikedCount) - 1
            } catch (e: java.lang.Exception) {
            }
            arrayComment!!.get(feedPosition)?.postcommentreply!![replyPos].replylikedCount =
                count.toString()
            arrayComment!!.get(feedPosition)?.postcommentreply!![replyPos].isCommentLiked = ("No")
            commentListAdapter?.notifyItemChanged(feedPosition)
        }
        val json = Gson().toJson(arrayComment?.get(feedPosition))
        val trendingFeedDatum =
            Gson().fromJson(json, CreatePostData::class.java)
        setSubCommentLike(
            arrayComment?.get(feedPosition)!!.commentID,
            action,
            feedPosition,
            trendingFeedDatum,
            arrayComment!!.get(feedPosition)?.postcommentreply!![replyPos].commentreplyID,
            replyPos
        )
        favouriteimageview.isEnabled = true
    }

    private fun setSubCommentLike(
        commentID: String,
        action: String,
        feedPosition: Int,
        trendingFeedDatum: CreatePostData?,
        commentreplyID: String,
        replyPos: Int
    ) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("commentID", commentID)
            jsonObject.put("commentreplyID", commentreplyID)
            when (action) {
                "LikePostCommnet" -> {
                    jsonObject.put("action", "Add")

                }
                "RemoveLikePostCommnet" -> {
                    jsonObject.put("action", "Delete")

                }
            }
            jsonObject.put("postID", postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        postSubCommentLikeModel.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@PostCommentListFragment,
            { postlikePojos ->
                if (mActivity != null) {
                    if (postlikePojos?.get(0)?.status.equals("true", false)) {
                        when (action) {
                            "LikePostCommnet" -> {
                                arrayComment!!.get(feedPosition)?.postcommentreply!![replyPos].isCommentLiked =
                                    "Yes"
                            }
                            "RemoveLikePostCommnet" -> {
                                arrayComment!!.get(feedPosition)?.postcommentreply!![replyPos].isCommentLiked =
                                    "No"
                            }
                        }
                        if (notifyInterface != null) {
                            notifyInterface!!.notifyData(
                                feedData,
                                false,
                                false,
                                feedData?.postComment,
                              ""
                            )
                        }
                        // (activity as MainActivity).showSnackBar(postlikePojos?.get(0)?.message!!)
                    } else {

                        MyUtils.showSnackbar(
                            mActivity!!,
                            postlikePojos?.get(0)?.message!!,
                            commentLayoutMain
                        )
                    }

                }
            })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        try {
            notifyInterface = activity as NotifyInterface
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement TextClicked"
            )
        }
    }

    fun getCommentList() {
        if (pageNo == 0) {
            setLayoutAsPerCondition(false, true, false, false, false)
            arrayComment!!.clear()
            commentListAdapter?.notifyDataSetChanged()
        } else {
            setLayoutAsPerCondition(false, false, true, false, false)
            arrayComment!!.add(null)
            commentListAdapter?.notifyItemInserted(arrayComment!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            if (sessionManager?.isLoggedIn()!!) jsonObject.put(
                "loginuserID",
                userData?.userID
            ) else jsonObject.put("loginuserID", "0")

            val lID = if (sessionManager?.getSelectedLanguage() == null) "1"
            else sessionManager?.getSelectedLanguage()?.languageID

            jsonObject.put("languageID", lID)
            jsonObject.put("action", "List")
            jsonObject.put("postID", postID)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiCall.apiFunction(mActivity!!, false, jsonArray.toString(), 0)
            .observe(viewLifecycleOwner,
                { response ->
                    if (!response.isNullOrEmpty()) {

                        //data found

                        isLoading = false
                        //   remove progress item
                        setLayoutAsPerCondition(false, false, true, false, false)

                        if (pageNo > 0) {
                            arrayComment!!.removeAt(arrayComment!!.size - 1)
                            commentListAdapter?.notifyItemRemoved(arrayComment!!.size)
                        }

                        if (response[0].status?.equals("true")!!) {
                            if (pageNo == 0)
                                arrayComment!!.clear()
                            if (arrayComment?.size!! < 1) {
                                editComment.requestFocus()
                            }


                            if (!response[0].data.isNullOrEmpty()) {
                                arrayComment!!.addAll(response[0].data!!)
                                commentListAdapter?.notifyDataSetChanged()
                                if (response[0].data[0].postComment.toInt() > 0) {
                                    ll_comment.visibility = View.VISIBLE
                                    tv_chat_count.text = response[0].data[0].postComment
                                } else {
                                    ll_comment.visibility = View.GONE

                                }
                                pageNo += 1
                                if (response[0].data!!.size < 10) {
                                    isLastpage = true
                                }
                            } else {
                                if (!arrayComment.isNullOrEmpty()) {
                                    setLayoutAsPerCondition(false, false, true, false, false)
                                } else setLayoutAsPerCondition(true, false, false, false, false)
                            }
                        } else {
                            if (arrayComment?.size == 0) {
                                showKeyboard()
                                editComment.requestFocus()
                            }
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                if (!arrayComment.isNullOrEmpty()) {
                                    setLayoutAsPerCondition(false, false, true, false, false)
                                } else setLayoutAsPerCondition(true, false, false, false, false)
                            } else {
                                setLayoutAsPerCondition(false, false, false, true, false)
                            }
                        }
                    } else {
                        //No internet and somthing rong
                        if (!MyUtils.isInternetAvailable(mActivity!!)) {
                            setLayoutAsPerCondition(false, false, false, true, true)

                        } else {
                            setLayoutAsPerCondition(false, false, false, true, false)
                        }
                    }
                })
    }

    fun addPostComment() {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("action", "Add")
            jsonObject.put("postID", postID)
            jsonObject.put("commentComment", Constant.encode(editComment.text.toString().trim()))
            when (feedData?.postMediaType) {
                "Photo" -> {
                    jsonObject.put("commentMediaType", "Image")
                }
                "Video" -> {
                    jsonObject.put("commentMediaType", "Video")
                }
                else -> {
                    jsonObject.put("commentMediaType", "Text")
                }
            }
            jsonObject.put("commentType", "Upload")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.d("ADDCOMMENT_OBJECT", jsonObject.toString())
        apiCall.apiFunction(mActivity!!, true, jsonArray.toString(), 1)
            .observe(viewLifecycleOwner,
                { response ->

                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true")) {
                            if (!response[0].data.isNullOrEmpty()) {
                                arrayComment?.add(response[0].data[0])
                                feedData?.postComment =
                                    (feedData?.postComment?.toInt()!! + 1).toString()
                                feedData?.postCommentList?.add(response[0].data[0])

                            }
                            commentListAdapter?.notifyDataSetChanged()
                            editComment.text?.clear()
                            checkCommentList()
                            if (notifyInterface != null) {
                                notifyInterface!!.notifyData(
                                    feedData,
                                    false,
                                    false,
                                    feedData?.postComment,""
                                )
                            }
                        } else {
                            //data not find
                            MyUtils.showSnackbar(
                                mActivity!!,
                                response[0].message,
                                commentLayoutMain!!
                            )

                        }

                    } else {
                        //No internet and somthing rong
                        if (!MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_common_network),
                                commentLayoutMain!!

                            )
                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_crash_error_message),
                                commentLayoutMain!!

                            )
                        }
                    }
                })
    }

    fun addReplyPostComment(commentID: String) {

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("postID", postID)
            jsonObject.put("action", "Add")
            jsonObject.put("commentID", commentID)
            when (feedData?.postMediaType) {
                "Photo" -> {
                    jsonObject.put("commentreplyMediaType", "Image")

                }
                "Video" -> {
                    jsonObject.put("commentreplyMediaType", "Video")
                }
                else -> {
                    jsonObject.put("commentreplyMediaType", "Text")

                }
            }
            jsonObject.put("commentreplyType", "Upload")
            jsonObject.put("commentreplyReply", Constant.encode(editComment.text.toString().trim()))
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        replyApiCall?.apiFunction(mActivity!!, true, jsonArray.toString(), 1)
            ?.observe(viewLifecycleOwner,
                Observer
                { response ->
                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true")) {
                            if (!response[0].data.isNullOrEmpty()) {
                                arrayReplyComment?.add(response[0].data[0])
                            }
                            commentListAdapter?.notifyDataSetChanged()
                            editComment.text?.clear()
                            setReply(false, "")
                            if (mPosition > -1) {
                                getReplyCommentList(mPosition, commentID)
                            }
                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                response[0].message,
                                commentLayoutMain!!
                            )

                        }
                    } else {
                        if (!MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_common_network),
                                commentLayoutMain!!
                            )
                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_crash_error_message),
                                commentLayoutMain!!
                            )
                        }
                    }
                })
    }

    private fun deleteReplyComment(
        commentreplyID: String,
        postionOfReplyComment: Int,
        pos: Int
    ) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("action", "Delete")
            jsonObject.put("commentreplyID", commentreplyID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        replyApiCall.apiFunction(mActivity!!, true, jsonArray.toString(), 3)
            .observe(this@PostCommentListFragment,
                { response ->

                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true", false)) {

                            arrayComment?.get(pos)!!.postcommentreply.removeAt(postionOfReplyComment)
                            commentListAdapter?.notifyItemRemoved(arrayComment?.get(pos)!!.postcommentreply!!.size)
                            commentListAdapter?.notifyDataSetChanged()

                            MyUtils.showSnackbar(
                                mActivity!!,
                                response[0].message,
                                commentLayoutMain!!
                            )

                        } else {
                            //data not find

                            MyUtils.showSnackbar(
                                mActivity!!,
                                response[0].message,
                                commentLayoutMain!!
                            )


                        }
                    } else {
                        //No internet and somthing rong
                        if (!MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_common_network),
                                commentLayoutMain!!
                            )
                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_crash_error_message),
                                commentLayoutMain!!
                            )
                        }
                    }
                })
    }

    private fun reportComment(commentID: String, postionOfComment: Int, reasonID: String) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("postID", postID)
            jsonObject.put("posterID", postUserID)
            jsonObject.put("reasonID", reasonID)
            jsonObject.put("commentID", commentID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiCall?.apiFunction(mActivity!!, true, jsonArray.toString(), 5)
            ?.observe(this@PostCommentListFragment,
                { response ->
                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true")) {
                            //data found
                            MyUtils.showSnackbar(
                                mActivity!!,
                                response[0].message,
                                commentLayoutMain!!
                            )

                            arrayComment?.removeAt(postionOfComment)
                            checkCommentList()
                            commentListAdapter?.notifyDataSetChanged()
                        } else {
                            //data not find
                            MyUtils.showSnackbar(
                                mActivity!!,
                                response[0].message,
                                commentLayoutMain!!
                            )

                        }
                    }
                })
    }

    fun getReasonList(cID: String, positionOFComment: Int) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        reasonModel?.apiFunction(mActivity!!, true, jsonArray.toString())
            ?.observe(this@PostCommentListFragment,
                { response ->
                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true")) {
                            //data found
                            objReason = response[0]
                            // openReasonListDialog(cID, positionOFComment)
                        } else {
                            //data not find
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                reportComment(cID, positionOFComment, "")
                            } else {
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    resources.getString(R.string.error_crash_error_message),
                                    commentLayoutMain!!

                                )
                            }
                        }
                    } else {
                        //No internet and somthing rong
                        if (!MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_common_network),
                                commentLayoutMain!!
                            )
                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_crash_error_message),
                                commentLayoutMain!!
                            )
                        }
                    }
                })
    }

    private fun checkCommentList() {
        if (arrayComment.isNullOrEmpty()) {
            pageNo = 0
            getCommentList()
        } else {
            setLayoutAsPerCondition(false, false, true, false, false)
        }
    }

    private fun getReplyCommentList(pos: Int, commentID: String) {
        if (progressBar != null) {
            progressBar?.visibility = View.VISIBLE

        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            if (sessionManager?.isLoggedIn()!!) jsonObject.put(
                "loginuserID",
                userData?.userID
            ) else jsonObject.put("loginuserID", "0")

            val lID = if (sessionManager?.getSelectedLanguage() == null) "1"
            else sessionManager?.getSelectedLanguage()?.languageID

            jsonObject.put("languageID", lID)
            jsonObject.put("action", "List")
            jsonObject.put("postID", postID)
            jsonObject.put("commentreplyID", "0")
            jsonObject.put("commentID", commentID)
            jsonObject.put("page", "0")
            jsonObject.put("pagesize", "100")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        replyApiCall?.apiFunction(mActivity!!, false, jsonArray.toString(), 0)
            ?.observe(this@PostCommentListFragment,
                Observer
                { response ->
                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true", false)) {
                            if (progressBar != null) {
                                progressBar?.visibility = View.GONE

                            }
                            if (!response[0].data.isNullOrEmpty()) {
                                arrayComment?.get(pos)!!.postcommentreply.clear()
                                arrayComment?.get(pos)!!.isVisibleComment = true
                                arrayComment?.get(pos)!!.postcommentreply.addAll(response[0].data)

                                commentListAdapter?.notifyDataSetChanged()

                            } else {
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    response[0].message,
                                    commentLayoutMain
                                )
                            }
                        } else {
                            if (progressBar != null) {
                                progressBar?.visibility = View.GONE
                            }

                            if (arrayReplyComment?.size == 0) {
                                arrayComment?.get(pos)!!.isVisibleComment = false
                                showKeyboard()
                                editComment.requestFocus()
                            }
                        }
                    } else {
                        if (progressBar != null) {
                            progressBar?.visibility = View.GONE
                        }
                        (activity as MainActivity).errorMethod()
                    }
                })

    }

    fun setLayoutAsPerCondition(
        noData: Boolean,
        progress: Boolean,
        datafound: Boolean,
        noInternet: Boolean,
        somthingROng: Boolean
    ) {

        if (noData) {
            commentLayoutMain?.visibility = View.VISIBLE

            generarerelLayoutMain?.visibility = View.VISIBLE
            recyclerview?.visibility = View.GONE

            relativeprogressBar?.visibility = View.GONE

            ll_no_data_found?.visibility = View.VISIBLE
            nodatafoundtextview?.visibility = View.VISIBLE
            nodatafoundtextview?.text = resources.getString(R.string.no_comment_list)
            nodatafoundtextview?.visibility = View.GONE

            nointernetMainRelativelayout?.visibility = View.GONE
            nointernetImageview?.visibility = View.VISIBLE
            nointernettextview?.visibility = View.VISIBLE

        } else if (progress) {
            commentLayoutMain?.visibility = View.VISIBLE

            generarerelLayoutMain?.visibility = View.VISIBLE
            recyclerview?.visibility = View.GONE

            relativeprogressBar?.visibility = View.VISIBLE

            ll_no_data_found?.visibility = View.GONE
            nodatafoundtextview?.visibility = View.VISIBLE
            nodatafoundtextview?.text = resources.getString(R.string.no_comment_list)

            nointernetMainRelativelayout?.visibility = View.GONE
            nointernetImageview?.visibility = View.VISIBLE
            nointernettextview?.visibility = View.VISIBLE
        } else if (datafound) {
            commentLayoutMain?.visibility = View.VISIBLE
            generarerelLayoutMain?.visibility = View.VISIBLE
            recyclerview?.visibility = View.VISIBLE
            relativeprogressBar?.visibility = View.GONE
            ll_no_data_found?.visibility = View.GONE
            nodatafoundtextview?.visibility = View.GONE
            nointernetMainRelativelayout?.visibility = View.GONE
            nointernetImageview?.visibility = View.VISIBLE
            nointernettextview?.visibility = View.VISIBLE
        } else if (noInternet) {
            commentLayoutMain?.visibility = View.VISIBLE

            generarerelLayoutMain?.visibility = View.VISIBLE
            recyclerview?.visibility = View.GONE

            relativeprogressBar?.visibility = View.GONE

            ll_no_data_found?.visibility = View.GONE
            nodatafoundtextview?.visibility = View.GONE

            nointernetMainRelativelayout?.visibility = View.VISIBLE
            nointernetImageview?.visibility = View.VISIBLE
            nointernetImageview?.setImageResource(R.drawable.no_internet_connection)
            nointernettextview?.visibility = View.VISIBLE
            nointernettextview?.text = resources.getString(R.string.error_common_network)
        } else if (somthingROng) {
            commentLayoutMain?.visibility = View.VISIBLE

            generarerelLayoutMain?.visibility = View.VISIBLE
            recyclerview?.visibility = View.GONE

            relativeprogressBar?.visibility = View.GONE

            ll_no_data_found?.visibility = View.GONE
            nodatafoundtextview?.visibility = View.GONE

            nointernetMainRelativelayout?.visibility = View.VISIBLE
            nointernetImageview?.visibility = View.VISIBLE
            nointernetImageview?.setImageResource(R.drawable.something_went_wrong)
            nointernettextview?.visibility = View.GONE
        }
    }

    fun showKeyboard() {
        val imm =
            mActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.SHOW_IMPLICIT
        )

    }

    fun setReply(isCommentReply: Boolean, replyName: String) {
        if (isCommentReply) {
            ll_comment_reply.visibility = View.VISIBLE
            tvCommentReplyName.text = "Reply to ${replyName} Comment"

            editComment.hint = "Reply to comment..."
        } else {
            ll_comment_reply.visibility = View.GONE
            editComment.hint = "Write a comment"


        }
    }

    @Throws(JSONException::class)
    private fun editComment(
        replyCommentId: String,
        replyCommentPos: Int,
        mPosition: Int
    ) {
        MyUtils.showProgressDialog(mActivity!!, "Wait..")

        val jsonObject = JSONObject()
        jsonObject.put("loginuserID", userData?.userID)
        jsonObject.put("languageID", userData?.languageID)
        jsonObject.put("postID", postID)
        jsonObject.put("action", "Edit")
        jsonObject.put("commentID", commentID)
        jsonObject.put("commentMediaType", "Text")
        jsonObject.put("commentType", feedData?.postType)
        jsonObject.put("commentreplyID", replyCommentId)
        jsonObject.put("commentreplyReply", Constant.encode(editComment.text.toString().trim()))
        jsonObject.put("apiType", RestClient.apiType)
        jsonObject.put("apiVersion", RestClient.apiVersion)
        val jsonArray = JSONArray()
        jsonArray.put(jsonObject)

        replyApiCall?.apiFunction(mActivity!!, true, jsonArray.toString(), 2)
            ?.observe(viewLifecycleOwner,
                Observer
                { response ->

                    if (!response.isNullOrEmpty()) {
                        editComment.setText("")
                        setReply(false, "")
                        if (response[0].status.equals("true", false)) {

                            arrayComment?.get(mPosition)!!.postcommentreply[replyCommentPos].commentreplyReply =
                                (response[0].data[0].commentreplyReply)
                            commentListAdapter?.notifyItemChanged(mPosition)

                            MyUtils.showSnackbar(
                                mActivity!!,
                                response[0].message,
                                commentLayoutMain!!
                            )


                        } else {
                            //data not find
                            MyUtils.showSnackbar(
                                mActivity!!,
                                response[0].message,
                                commentLayoutMain!!
                            )
                        }
                    } else {
                        //No internet and somthing rong
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_common_network),
                                commentLayoutMain!!
                            )
                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_crash_error_message),
                                commentLayoutMain!!
                            )
                        }
                    }
                })
    }

    private fun getPrivacy(userFirstName: String, userID: String, data: CommentData) {
        val privacyList: ArrayList<CreatePostPrivacyPojo> = ArrayList()
        privacyList.clear()
        if ((data.isYourFriend == "Yes") || (data.isYouSentRequest == "Yes")) {
            privacyList.add(CreatePostPrivacyPojo("Cancel Request", R.drawable.connection_icon))
        } else {
            privacyList.add(CreatePostPrivacyPojo("Add Network", R.drawable.connection_icon))
        }
        privacyList.add(
            CreatePostPrivacyPojo(
                "Send Message to " + userFirstName,
                R.drawable.popup_send_messages_icon
            )
        )
        privacyList.add(CreatePostPrivacyPojo("Share Via", R.drawable.popup_share_via_icon))
        privacyList.add(CreatePostPrivacyPojo("Report", R.drawable.popup_report_icon))

        openBottomSheet(privacyList)

    }

    private fun openBottomSheet(data: ArrayList<CreatePostPrivacyPojo>) {

        val bottomSheet = PrivacyBottomSheetFragment()
        val bundle = Bundle()
        bundle.putSerializable("data", data as Serializable)
        bottomSheet.arguments = bundle
        bottomSheet.show(childFragmentManager, "List")
    }

    private fun connectionListApi() {
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("searchWord", "")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        connectionListModel.getConnectionTypeList(mActivity!!, false, jsonArray.toString())
            .observe(this@PostCommentListFragment!!,
                { connectionListpojo ->
                    if (connectionListpojo != null && connectionListpojo.isNotEmpty()) {

                        if (connectionListpojo[0].status.equals("true", false)) {
                            MyUtils.dismissProgressDialog()

                            conneTypeList!!.clear()

                            for (i in connectionListpojo[0].data.indices) {
                                if (connectionListpojo[0].data[i].conntypeName.equals("All")) {
                                    conneTypeList!!.remove(connectionListpojo[0].data[i])
                                } else {
                                    conneTypeList!!.add(connectionListpojo[0].data[i])
                                }
                            }
                            openConnectionList(conneTypeList!!)

                        } else {
                            MyUtils.dismissProgressDialog()
                            MyUtils.showSnackbar(
                                mActivity!!,
                                connectionListpojo[0].message,
                                commentLayoutMain
                            )
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(commentLayoutMain)
                    }
                })


    }

    private fun openConnectionList(data: ArrayList<ConnectionListData>) {

        var bottomSheetData = ArrayList<String>()
        bottomSheetData.clear()
        for (i in 0 until data.size) {
            bottomSheetData.add(data[i]!!.conntypeName!!)
        }

        val bottomSheet = BottomSheetListFragment()
        val bundle = Bundle()
        bundle.putString("from", "ConneTypeList")
        bundle.putSerializable("data", bottomSheetData)
        bottomSheet.arguments = bundle
        bottomSheet.show(childFragmentManager!!, "List")
    }

    override fun setPrivacy(data: CreatePostPrivacyPojo, position: Int) {
        when (position) {
            0 -> {
                if (arrayData!!.isYourFriend == "Yes" || arrayData!!.isYouSentRequest == "Yes") {
                    cancleRequestApi(arrayData!!.userID)
                } else {
                    connectionListApi()
                }
            }
            1 -> {

                var userQuickBlockID = if (!arrayData!!.userQBoxID.isNullOrEmpty())
                    arrayData!!.userQBoxID!!
                else
                    return
                addToChatList(userQuickBlockID, arrayData!!.userID)

            }
            2 -> {
                shareData()
            }
            3 -> {
                var repostReasonFragment = RepostReasonFragment()
                Bundle().apply {
                    putString("fromReport", "commentReport")
                    putString("commentID", commentID)
                    putString("postID", postID)
                    repostReasonFragment.arguments = this
                }
                (activity as MainActivity).navigateTo(
                    repostReasonFragment,
                    repostReasonFragment::class.java.name,
                    true
                )

            }
        }
    }

    override fun onLanguageSelect(value: String, from: String) {

        for (i in 0 until conneTypeList!!.size) {
            if (value.equals(conneTypeList!![i]!!.conntypeName, false)) {
                if (!conneTypeID.equals(conneTypeList!![i]!!.conntypeID))
                    conneTypeID = conneTypeList!![i]!!.conntypeID
            }
        }
        setConnectionApi(conneTypeID, userId)
    }

    private fun setConnectionApi(conneTypeID: String, userId: String) {
        MyUtils.showProgressDialog(mActivity!!, "Wait")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("action", "sendrequest")
            jsonObject.put("userfriendSenderID", userData!!.userID)
            jsonObject.put("userfriendReceiverID", userId)
            jsonObject.put("conntypeID", conneTypeID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        friendListModel.getFriendList(mActivity!!, jsonArray.toString(), "friend_list")
            .observe(this@PostCommentListFragment!!,
                { connectionListpojo ->
                    if (connectionListpojo != null && connectionListpojo.isNotEmpty()) {

                        if (connectionListpojo[0].status.equals("true", false)) {
                            MyUtils.dismissProgressDialog()
                            commentListAdapter?.notifyDataSetChanged()

                        } else {
                            MyUtils.dismissProgressDialog()
                            MyUtils.showSnackbar(
                                mActivity!!,
                                connectionListpojo[0].message,
                                commentLayoutMain
                            )
                        }
                    } else {
                        MyUtils.dismissProgressDialog()
                        (activity as MainActivity).errorMethod()
                    }
                })
    }

    private fun cancleRequestApi(userId: String) {
        MyUtils.showProgressDialog(mActivity!!, "Wait")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("userfriendReceiverID", userId)
            jsonObject.put("userfriendSenderID", userData?.userID)
            jsonObject.put("action", "cancelrequest")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        friendListModel.getFriendList(mActivity!!, jsonArray.toString(), "friend_list")
            .observe(this@PostCommentListFragment!!,
                androidx.lifecycle.Observer { cancleRequestpojo ->
                    if (cancleRequestpojo != null && cancleRequestpojo.isNotEmpty()) {
                        if (cancleRequestpojo[0].status.equals("true", false)) {
                            MyUtils.dismissProgressDialog()

                            commentListAdapter?.notifyDataSetChanged()

                        } else {
                            MyUtils.dismissProgressDialog()
                            MyUtils.showSnackbar(
                                mActivity!!,
                                cancleRequestpojo[0].message,
                                commentLayoutMain
                            )
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        (activity as MainActivity).errorMethod()
                    }
                })
    }

    private fun shareData() {
        var uri =
            Uri.parse("https://play.google.com/store/apps/details?id=" + mActivity?.getPackageName());
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Download the app now - " + uri + "\n\n");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)))

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_clear -> {
                isCommentReply = false
                setReply(false, "")
            }
            R.id.btnRetry -> {
                if (MyUtils.isInternetAvailable(mActivity!!)) {
                    pageNo = 0
                    getCommentList()
                } else {
                    setLayoutAsPerCondition(false, false, false, true, false)

                }
            }
            R.id.send -> {
                if (isCommentReply) {

                    if (!editComment.text.toString().trim().isNullOrEmpty()) {
                        if (!replyCommentId.isEmpty() && !replyCommentId.isEmpty() && replyCommentPos > -1) {
                            editComment(replyCommentId, replyCommentPos, mPosition)
                        } else {
                            addReplyPostComment(commentID)

                        }
                    }
                } else {
                    if (!editComment.text.toString().isNullOrEmpty()) {
                        addPostComment()
                    }
                }
            }

        }
    }


    private fun addToChatList(userQuickBlockID: String, userID: String?) {
        MyUtils.showProgressDialog(mActivity!!, "Please wait")

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("otheruserID", userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

            jsonArray.put(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        friendListModel.getFriendList(mActivity!!, jsonArray.toString(), "chatTofriend")
            .observe(this@PostCommentListFragment,
                { loginPojo ->
                    MyUtils.dismissProgressDialog()
                    if (loginPojo != null) {

                        if (loginPojo[0].status.equals("true", true)) {

                            if (sessionManager?.getYesNoQBUser()!!) {

                                if (!MyUtils.isLoginForQuickBlock) {
                                    if (!MyUtils.isLoginForQuickBlockChat)
                                        (mActivity as MainActivity).loginForQuickBlockChat(
                                            sessionManager?.getQbUser()!!,
                                            userQuickBlockID
                                        )
                                    else
                                        (mActivity as MainActivity).getQBUser(userQuickBlockID, 1)
                                } else if (!MyUtils.isLoginForQuickBlockChat)
                                    (mActivity as MainActivity).loginForQuickBlockChat(
                                        sessionManager?.getQbUser()!!,
                                        userQuickBlockID
                                    )
                                else (mActivity as MainActivity).getQBUser(userQuickBlockID, 1)
                            }

                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                loginPojo[0].message,
                                nointernetMainRelativelayout
                            )
                        }
                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(nointernetMainRelativelayout)
                    }
                })

    }

}
