package com.nxplayr.fsl.ui.fragments.explorepost.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.UploadImagePojo
import com.nxplayr.fsl.ui.activity.filterfeed.view.FilterActivity
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.explorepost.adapter.ExploreAdapter
import com.nxplayr.fsl.ui.fragments.explorepost.viewmodel.ExploreVideoModel
import com.nxplayr.fsl.ui.fragments.feed.viewmodel.CreatePostModel
import com.nxplayr.fsl.util.*
import com.vincent.videocompressor.VideoController
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_explore_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ExploreFragment : Fragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var v: View? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var mActivity: AppCompatActivity? = null
    var swipeCount = 0
    var linearLayoutManager: LinearLayoutManager? = null

    var explore_video_list: ArrayList<CreatePostData?>? = null

    var oldExploreVideoAdapter: ExploreAdapter? = null
//    var exploreVideoTrickadapter: ExploreVideoListAdapater? = null

    private var y: Int = 0
    var pageNo = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var myUserId = ""
    var type = ""

    var footballLevel = ""
    var countryID = ""
    var sortby = ""
    var pitchPosition = ""
    var gender = ""
    var footballagecatID = ""
    var footballType = ""
    var publicationTime = ""
    var isVideos = false
    var isTricks = false
    var isListView = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_explore_main, container, false)
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
        if (arguments != null) {
            type = arguments?.getString("type")!!

        }
        when (type) {
            "MyExplore" -> {
                myUserId = userData?.userID!!
                ll_explore_header.visibility = View.GONE

            }
            "OtherExplore" -> {
                myUserId = if (arguments != null) {
                    arguments?.getString("userID")!!
                } else {
                    ""
                }
                ll_explore_header.visibility = View.GONE
            }
            else -> {
                myUserId = "0"
                ll_explore_header.visibility = View.VISIBLE
            }
        }
        txt_explore_all.setOnClickListener(this)
        btnRetry.setOnClickListener(this)
        txt_explore_videos.setOnClickListener(this)
        txt_explore_tricks.setOnClickListener(this)
        img_list.setOnClickListener(this)
        img_grid.setOnClickListener(this)
//        img_grid_2.setOnClickListener(this)
        swipe_refresh.setOnClickListener(this)
        img_Filter.setOnClickListener(this)

        isVideos = true
        getExploreVideo()
        swipe_refresh.setOnRefreshListener(this)
    }

    //Click Handle
    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.txt_explore_all -> {

                (activity as MainActivity).navigateTo(
                    ExploreAllFragment(),
                    ExploreAllFragment::class.java.name, true
                )
            }

            R.id.btnRetry -> {

                pageNo = 0

                getGridVideoDataList(
                    "",
                    myUserId,
                    "10",
                    RestClient.apiVersion,
                    "ExploreVideos",
                    "Video",
                    footballLevel,
                    countryID,
                    sortby,
                    userData?.userID!!,
                    pitchPosition,
                    RestClient.apiType,
                    pageNo,
                    gender,
                    "trending",
                    footballagecatID,
                    footballType,
                    "0",
                    publicationTime
                )
            }

            R.id.txt_explore_videos -> {
                isVideos = true
                isTricks = false
                txt_explore_videos.setBackgroundResource(R.drawable.background_bv_curve)
                txt_explore_tricks.setBackgroundResource(android.R.color.transparent);

                txt_explore_videos.setTextColor(getResources().getColor(R.color.black));
                txt_explore_tricks.setTextColor(getResources().getColor(R.color.colorPrimary));

                getGridVideoDataList(
                    "",
                    myUserId,
                    "10",
                    RestClient.apiVersion,
                    "ExploreVideos",
                    "Video",
                    footballLevel,
                    countryID,
                    sortby,
                    userData?.userID!!,
                    pitchPosition,
                    RestClient.apiType,
                    pageNo,
                    gender,
                    "trending",
                    footballagecatID,
                    footballType,
                    "0",
                    publicationTime
                )
            }

            R.id.txt_explore_tricks -> {
                isVideos = false
                isTricks = true
                txt_explore_tricks.setBackgroundResource(R.drawable.background_bv_curve)
                txt_explore_videos.setBackgroundResource(android.R.color.transparent);

                txt_explore_tricks.setTextColor(getResources().getColor(R.color.black));
                txt_explore_videos.setTextColor(getResources().getColor(R.color.colorPrimary));

                linearLayoutManager = GridLayoutManager(mActivity!!, 2)
                if (explore_video_list.isNullOrEmpty() || pageNo == 0) {

                    rc_explore_videos.layoutManager = linearLayoutManager
                    oldExploreVideoAdapter = ExploreAdapter(
                        mActivity!!,
                        object : ExploreAdapter.OnItemClick {
                            override fun onClickLisneter(pos: Int) {

                                if (explore_video_list!!.get(pos)?.postAlbum.isNullOrEmpty()) {

                                    var exploreDetailFragment = ExploreVideoDetailFragment()
                                    Bundle().apply {
                                        putString("user_ID", explore_video_list!!.get(pos)?.userID)
                                        putInt("pos", pos)
                                        putString(
                                            "username",
                                            explore_video_list!!.get(pos)?.userFirstName
                                        )
                                        putString("like", explore_video_list!!.get(pos)?.postLike)
                                        putString(
                                            "postComment",
                                            explore_video_list!!.get(pos)?.postComment
                                        )
                                        putString(
                                            "postDescription",
                                            explore_video_list!!.get(pos)?.postDescription
                                        )
                                        putString("view", explore_video_list!!.get(pos)?.postViews)
                                        putString(
                                            "userProfile",
                                            explore_video_list!!.get(pos)?.userProfilePicture
                                        )
                                        putString("postID", explore_video_list!!.get(pos)?.postID)
                                        putString(
                                            "postLike",
                                            explore_video_list!!.get(pos)?.youpostLiked
                                        )
                                        putString(
                                            "exploreVideo",
                                            explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile
                                        )
                                        putString(
                                            "exploreThumbnail",
                                            explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail
                                        )
                                        putSerializable("explore_video_list", explore_video_list)
                                        putString(
                                            "postType",
                                            explore_video_list!!.get(pos)?.postType
                                        )
                                        putString("posthashtag", "null")
                                        putString("albumID", "null")
                                        putString("subAlbumAId", "null")
                                        putString("collection", "Add")
                                        putSerializable("explore_video_list", explore_video_list)
                                        putString(
                                            "postType",
                                            explore_video_list?.get(pos)?.postType
                                        )

                                        exploreDetailFragment.arguments = this
                                    }
                                    (activity as MainActivity).navigateTo(
                                        exploreDetailFragment,
                                        exploreDetailFragment::class.simpleName!!,
                                        true
                                    )

                                } else {

                                    var exploreDetailFragment = ExploreVideoDetailFragment()
                                    Bundle().apply {
                                        putInt("pos", pos)
                                        putString("user_ID", explore_video_list!!.get(pos)?.userID)
                                        putString(
                                            "username",
                                            explore_video_list!!.get(pos)?.userFirstName
                                        )
                                        putString("like", explore_video_list!!.get(pos)?.postLike)
                                        putString(
                                            "postComment",
                                            explore_video_list!!.get(pos)?.postComment
                                        )
                                        putString(
                                            "postDescription",
                                            explore_video_list!!.get(pos)?.postDescription
                                        )
                                        putString("view", explore_video_list!!.get(pos)?.postViews)
                                        putString(
                                            "userProfile",
                                            explore_video_list!!.get(pos)?.userProfilePicture
                                        )
                                        putString("postID", explore_video_list!!.get(pos)?.postID)
                                        putString(
                                            "postLike",
                                            explore_video_list!!.get(pos)?.youpostLiked
                                        )
                                        putString(
                                            "posthashtag",
                                            explore_video_list!!.get(pos)?.posthashtag
                                        )
                                        putString(
                                            "exploreVideo",
                                            explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile
                                        )
                                        putString(
                                            "exploreThumbnail",
                                            explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail
                                        )
                                        putString(
                                            "albumID",
                                            explore_video_list!!.get(pos)?.postAlbum!![0].exalbumID
                                        )
                                        putString(
                                            "subAlbumAId",
                                            explore_video_list!!.get(pos)?.postAlbum!![0].exsubalbumID
                                        )
                                        putString("collection", "Remove")
                                        putSerializable("explore_video_list", explore_video_list)
                                        putString(
                                            "postType",
                                            explore_video_list!!.get(pos)?.postType
                                        )
                                        exploreDetailFragment.arguments = this

                                    }
                                    (activity as MainActivity).navigateTo(
                                        exploreDetailFragment,
                                        exploreDetailFragment::class.simpleName!!,
                                        true
                                    )

                                    getListVideoTrickDataList(
                                        RestClient.apiType,
                                        myUserId,
                                        "0",
                                        "Video",
                                        footballagecatID,
                                        footballType,
                                        "",
                                        userData?.userID!!,
                                        RestClient.apiVersion,
                                        pageNo!!,
                                        sortby,
                                        publicationTime,
                                        gender,
                                        "trending",
                                        footballLevel,
                                        "ExploreTricks",
                                        "6",
                                        countryID,
                                        pitchPosition
                                    )
                                }

                            }


                        },
                        explore_video_list!!,
                        false
                    )
                }
                rc_explore_videos.setHasFixedSize(true)
                rc_explore_videos.adapter = oldExploreVideoAdapter

                getListVideoTrickDataList(
                    RestClient.apiType,
                    myUserId,
                    "0",
                    "Video",
                    footballagecatID,
                    footballType,
                    "",
                    userData?.userID!!,
                    RestClient.apiVersion,
                    pageNo!!,
                    sortby,
                    publicationTime,
                    gender,
                    "trending",
                    footballLevel,
                    "ExploreTricks",
                    "10",
                    countryID,
                    pitchPosition
                )
            }

            R.id.img_list -> {
                isListView = true
                img_grid.setImageResource(R.drawable.thumb_view_unselected)
                img_list.setImageResource(R.drawable.list_selected)
                linearLayoutManager =
                    LinearLayoutManager(mActivity!!, LinearLayoutManager.VERTICAL, false)
                rc_explore_videos.layoutManager = linearLayoutManager
                oldExploreVideoAdapter!!.changeView(isListView)
            }

            R.id.img_grid -> {
                isListView = false
                img_grid.setImageResource(R.drawable.thumb_view_selected)
                img_list.setImageResource(R.drawable.list_icon_selected)
                linearLayoutManager = GridLayoutManager(mActivity!!, 2)
                rc_explore_videos.layoutManager = linearLayoutManager
                oldExploreVideoAdapter!!.changeView(isListView)
            }

            R.id.img_Filter -> {
                Intent(mActivity!!, FilterActivity::class.java).apply {
                    putExtra("footballLevel", footballLevel)
                    putExtra("countryID", countryID)
                    putExtra("sortby", sortby)
                    putExtra("pitchPosition", pitchPosition)
                    putExtra("gender", gender)
                    putExtra("footballagecatID", footballagecatID)
                    putExtra("footballType", footballType)
                    putExtra("publicationTime", publicationTime)
                    startActivityForResult(this, 820)
                }

            }
        }
    }

    private fun getExploreVideo() {

        if (!isListView)
            linearLayoutManager = GridLayoutManager(mActivity!!, 2)
        else
            linearLayoutManager =
                LinearLayoutManager(mActivity!!, LinearLayoutManager.VERTICAL, false)

        explore_video_list = ArrayList()
        rc_explore_videos.layoutManager = linearLayoutManager
        oldExploreVideoAdapter =
            ExploreAdapter(mActivity!!, object : ExploreAdapter.OnItemClick {
                override fun onClickLisneter(pos: Int) {

                    if (explore_video_list!!.get(pos)?.postAlbum.isNullOrEmpty()) {

                        var exploreDetailFragment = ExploreVideoDetailFragment()
                        Bundle().apply {
                            putInt("pos", pos)
                            putString("user_ID", explore_video_list!!.get(pos)?.userID)
                            putString("username", explore_video_list!!.get(pos)?.userFirstName)
                            putString("like", explore_video_list!!.get(pos)?.postLike)
                            putString("postComment", explore_video_list!!.get(pos)?.postComment)
                            putString(
                                "postDescription",
                                explore_video_list!!.get(pos)?.postDescription
                            )
                            putString("view", explore_video_list!!.get(pos)?.postViews)
                            putString(
                                "userProfile",
                                explore_video_list!!.get(pos)?.userProfilePicture
                            )
                            putString("postID", explore_video_list!!.get(pos)?.postID)
                            putString("postLike", explore_video_list!!.get(pos)?.youpostLiked)
                            putString(
                                "exploreVideo",
                                explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile
                            )
                            putString(
                                "exploreThumbnail",
                                explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail
                            )
                            putString("posthashtag", "null")
                            putString("albumID", "null")
                            putString("subAlbumAId", "null")
                            putString("collection", "Add")
                            putSerializable("explore_video_list", explore_video_list)
                            putString("postType", explore_video_list!!.get(pos)?.postType)
                            exploreDetailFragment.arguments = this

                        }
                        (context as MainActivity).navigateTo(
                            exploreDetailFragment,
                            exploreDetailFragment::class.simpleName!!,
                            true
                        )

                    } else {

                        var exploreDetailFragment = ExploreVideoDetailFragment()
                        Bundle().apply {
                            putInt("pos", pos)
                            putString("user_ID", explore_video_list!!.get(pos)?.userID)
                            putString("username", explore_video_list!!.get(pos)?.userFirstName)
                            putString("like", explore_video_list!!.get(pos)?.postLike)
                            putString("postComment", explore_video_list!!.get(pos)?.postComment)
                            putString(
                                "postDescription",
                                explore_video_list!!.get(pos)?.postDescription
                            )
                            putString("view", explore_video_list!!.get(pos)?.postViews)
                            putString(
                                "userProfile",
                                explore_video_list!!.get(pos)?.userProfilePicture
                            )
                            putString("postID", explore_video_list!!.get(pos)?.postID)
                            putString("postLike", explore_video_list!!.get(pos)?.youpostLiked)
                            putString("posthashtag", explore_video_list!!.get(pos)?.posthashtag)
                            putString(
                                "exploreVideo",
                                explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile
                            )
                            putString(
                                "exploreThumbnail",
                                explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail
                            )
                            putString(
                                "albumID",
                                explore_video_list!!.get(pos)?.postAlbum!![0].exalbumID
                            )
                            putString(
                                "subAlbumAId",
                                explore_video_list!!.get(pos)?.postAlbum!![0].exsubalbumID
                            )
                            putString("collection", "Remove")
                            putSerializable("explore_video_list", explore_video_list)
                            putString("postType", explore_video_list!!.get(pos)?.postType)
                            exploreDetailFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            exploreDetailFragment,
                            exploreDetailFragment::class.simpleName!!,
                            true
                        )

                    }
                }

            }, explore_video_list!!, false)
        //  }
        rc_explore_videos.setHasFixedSize(true)
        rc_explore_videos.adapter = oldExploreVideoAdapter
        getGridVideoDataList(
            "", myUserId, "10", RestClient.apiVersion, "ExploreVideos", "Video",
            "", "", "", userData?.userID!!, "", RestClient.apiType, pageNo, "", "trending",
            "", "", "0", ""
        )
    }

    private fun getTrickExploreVideo() {

        if (!isListView)
            linearLayoutManager = GridLayoutManager(mActivity!!, 2)
        else
            linearLayoutManager =
                LinearLayoutManager(mActivity!!, LinearLayoutManager.VERTICAL, false)

        rc_explore_videos.layoutManager = linearLayoutManager
        oldExploreVideoAdapter =
            ExploreAdapter(mActivity!!, object : ExploreAdapter.OnItemClick {
                override fun onClickLisneter(pos: Int) {

                    if (explore_video_list!!.get(pos)?.postAlbum.isNullOrEmpty()) {

                        var exploreDetailFragment = ExploreVideoDetailFragment()
                        Bundle().apply {
                            putString("user_ID", explore_video_list!!.get(pos)?.userID)
                            putInt("pos", pos)
                            putString("username", explore_video_list!!.get(pos)?.userFirstName)
                            putString("like", explore_video_list!!.get(pos)?.postLike)
                            putString("postComment", explore_video_list!!.get(pos)?.postComment)
                            putString(
                                "postDescription",
                                explore_video_list!!.get(pos)?.postDescription
                            )
                            putString("view", explore_video_list!!.get(pos)?.postViews)
                            putString(
                                "userProfile",
                                explore_video_list!!.get(pos)?.userProfilePicture
                            )
                            putString("postID", explore_video_list!!.get(pos)?.postID)
                            putString("postLike", explore_video_list!!.get(pos)?.youpostLiked)
                            putString(
                                "exploreVideo",
                                explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile
                            )
                            putString(
                                "exploreThumbnail",
                                explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail
                            )
                            putString("posthashtag", "null")
                            putString("albumID", "null")
                            putString("subAlbumAId", "null")
                            putString("collection", "Add")
                            putSerializable("explore_video_list", explore_video_list)
                            putString("postType", explore_video_list!!.get(pos)?.postType)
                            exploreDetailFragment.arguments = this
                        }
                        (activity as MainActivity).navigateTo(
                            exploreDetailFragment,
                            exploreDetailFragment::class.simpleName!!,
                            true
                        )

                    } else {

                        var exploreDetailFragment = ExploreVideoDetailFragment()
                        Bundle().apply {
                            putString("user_ID", explore_video_list!!.get(pos)?.userID)
                            putInt("pos", pos)
                            putString("username", explore_video_list!!.get(pos)?.userFirstName)
                            putString("like", explore_video_list!!.get(pos)?.postLike)
                            putString("postComment", explore_video_list!!.get(pos)?.postComment)
                            putString(
                                "postDescription",
                                explore_video_list!!.get(pos)?.postDescription
                            )
                            putString("view", explore_video_list!!.get(pos)?.postViews)
                            putString(
                                "userProfile",
                                explore_video_list!!.get(pos)?.userProfilePicture
                            )
                            putString("postID", explore_video_list!!.get(pos)?.postID)
                            putString("postLike", explore_video_list!!.get(pos)?.youpostLiked)
                            putString("posthashtag", explore_video_list!!.get(pos)?.posthashtag)
                            putString(
                                "exploreVideo",
                                explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile
                            )
                            putString(
                                "exploreThumbnail",
                                explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail
                            )
                            putString(
                                "albumID",
                                explore_video_list!!.get(pos)?.postAlbum!![0].exalbumID
                            )
                            putString(
                                "subAlbumAId",
                                explore_video_list!!.get(pos)?.postAlbum!![0].exsubalbumID
                            )
                            putString("collection", "Remove")
                            putSerializable("explore_video_list", explore_video_list)
                            putString("postType", explore_video_list!!.get(pos)?.postType)
                            exploreDetailFragment.arguments = this
                        }
                        (activity as MainActivity).navigateTo(
                            exploreDetailFragment,
                            exploreDetailFragment::class.simpleName!!,
                            true
                        )

                    }

                }

            }, explore_video_list!!, false)

        rc_explore_videos.setHasFixedSize(true)
        rc_explore_videos.adapter = oldExploreVideoAdapter
    }

    //Api Calling
    private fun getGridVideoDataList(
        tag: String,
        userLoginiD: String?,
        pagesize: String,
        apiVersion: String,
        postType: String,
        postMediaType: String,
        footballLevel: String,
        countryID: String,
        sortby: String,
        userID: String,
        pitchPosition: String,
        apiType: String,
        page: Int,
        gender: String,
        tabname: String,
        footballagecatID: String,
        footballType: String,
        postiD: String,
        publicationTime: String,
        searchKeyword: String = ""
    ) {

        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        rc_explore_videos.visibility = View.GONE
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("tag", tag)
            jsonObject.put("loginuserID", userLoginiD)
            jsonObject.put("pagesize", pagesize)
            jsonObject.put("apiVersion", apiVersion)
            jsonObject.put("postType", postType)
            jsonObject.put("postMediaType", postMediaType)
            jsonObject.put("footballLevel", footballLevel)
            jsonObject.put("countryID", countryID)
            jsonObject.put("sortby", sortby)
            jsonObject.put("pitchPosition", pitchPosition)
            jsonObject.put("apiType", apiType)
            jsonObject.put("page", page)
            jsonObject.put("gender", gender)
            jsonObject.put("searchKeyword", searchKeyword)
            if (type.equals("MyExplore", false) || type.equals("OtherExplore", false)) {
                jsonObject.put("tabname", "my")

            } else {
                jsonObject.put("tabname", tabname)
                jsonObject.put("userID", userID)

            }
            jsonObject.put("footballagecatID", footballagecatID)
            jsonObject.put("footballType", footballType)
            jsonObject.put("postID", postiD)
            jsonObject.put("publicationTime", publicationTime)


        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("VIDEO_LIST_GRID", jsonObject.toString())
        jsonArray.put(jsonObject)
        Log.d("LIST_GRID1111", jsonArray.toString())

        var getEmployementModel =
            ViewModelProviders.of(this@ExploreFragment).get(ExploreVideoModel::class.java)
        getEmployementModel.getExploreVList(mActivity!!, false, jsonArray.toString())
            .observe(viewLifecycleOwner,
                Observer { exploreVidelistpojo ->

                    relativeprogressBar.visibility = View.GONE

                    if (exploreVidelistpojo != null && exploreVidelistpojo.isNotEmpty()) {

                        if (exploreVidelistpojo[0].status.equals("true", true)) {
                            rc_explore_videos.visibility = View.VISIBLE
                            explore_video_list?.clear()
                            explore_video_list?.addAll(exploreVidelistpojo[0].data!!)
                            if (isListView) {
                                linearLayoutManager =
                                    LinearLayoutManager(
                                        mActivity!!,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                rc_explore_videos.layoutManager = linearLayoutManager
                                oldExploreVideoAdapter!!.changeView(isListView)
                            }
                            oldExploreVideoAdapter?.notifyDataSetChanged()
                            if (explore_video_list!!.size == 0) {

                                ll_no_data_found.visibility = View.VISIBLE
                                rc_explore_videos.visibility = View.GONE
                            } else {

                                ll_no_data_found.visibility = View.GONE
                                rc_explore_videos.visibility = View.VISIBLE
                            }

                        } else {
                            if (pageNo == 0) {
                                explore_video_list?.clear()
                                oldExploreVideoAdapter?.notifyDataSetChanged()
                            }
                            if (explore_video_list!!.size == 0) {

                                ll_no_data_found.visibility = View.VISIBLE
                                rc_explore_videos.visibility = View.GONE
                            } else {

                                ll_no_data_found.visibility = View.GONE
                                rc_explore_videos.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        rc_explore_videos.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE
                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }

                })
    }

    private fun getListVideoTrickDataList(
        apiType: String,
        useriD: String,
        postiD: String,
        postMediaType: String,
        footballagecatID: String,
        footballType: String,
        taga: String,
        userID: String,
        apiVersion: String,
        page: Int,
        sortby: String,
        publicationTime: String,
        gender: String,
        tabname: String,
        footballLevel: String,
        postType: String,
        pagesize: String,
        countryID: String,
        pitchPosition: String
    ) {

        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        rc_explore_videos.visibility = View.GONE
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("apiType", apiType)
            jsonObject.put("loginuserID", useriD)
            jsonObject.put("postID", postiD)
            jsonObject.put("postMediaType", postMediaType)
            jsonObject.put("footballagecatID", footballagecatID)
            jsonObject.put("footballType", footballType)
            jsonObject.put("tag", taga)
            jsonObject.put("apiVersion", apiVersion)
            jsonObject.put("page", page)
            jsonObject.put("sortby", sortby)
            jsonObject.put("publicationTime", publicationTime)
            jsonObject.put("gender", gender)
            if (type.equals("MyExplore", false) || type.equals("OtherExplore", false)) {
                jsonObject.put("tabname", "my")

            } else {
                jsonObject.put("tabname", tabname)
                jsonObject.put("userID", userID)

            }
            jsonObject.put("footballLevel", footballLevel)
            jsonObject.put("postType", postType)
            jsonObject.put("pagesize", pagesize)
            jsonObject.put("countryID", countryID)
            jsonObject.put("pitchPosition", pitchPosition)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("VIDEO_LIST_123", jsonObject.toString())
        jsonArray.put(jsonObject)

        var getEmployementModel =
            ViewModelProviders.of(this@ExploreFragment).get(ExploreVideoModel::class.java)
        getEmployementModel.getExploreVList(mActivity!!, false, jsonArray.toString())
            .observe(this@ExploreFragment!!,
                Observer { exploreVidelistpojo ->

                    relativeprogressBar.visibility = View.GONE

                    if (exploreVidelistpojo != null && exploreVidelistpojo.isNotEmpty()) {

                        if (exploreVidelistpojo[0].status.equals("true", true)) {
                            rc_explore_videos.visibility = View.VISIBLE
                            explore_video_list?.clear()
                            explore_video_list?.addAll(exploreVidelistpojo!![0]!!.data!!)
                            if (isListView) {
                                linearLayoutManager =
                                    LinearLayoutManager(
                                        mActivity!!,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                rc_explore_videos.layoutManager = linearLayoutManager
                                oldExploreVideoAdapter!!.changeView(isListView)
                            }
                            oldExploreVideoAdapter?.notifyDataSetChanged()
                            if (explore_video_list!!.size == 0) {
                                ll_no_data_found.visibility = View.VISIBLE
                                rc_explore_videos.visibility = View.GONE
                            } else {
                                rc_explore_videos.visibility = View.VISIBLE
                                ll_no_data_found.visibility = View.GONE
                            }

                        } else {
                            if (pageNo == 0) {
                                explore_video_list?.clear()
                                oldExploreVideoAdapter?.notifyDataSetChanged()
                            }

                            if (explore_video_list!!.size == 0) {

                                ll_no_data_found.visibility = View.VISIBLE
                                rc_explore_videos.visibility = View.GONE
                            } else {
                                rc_explore_videos.visibility = View.VISIBLE
                                ll_no_data_found.visibility = View.GONE
                            }

                        }

                    } else {
                        rc_explore_videos.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE
                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }

                })
    }

    override fun onRefresh() {
        swipeCount += 1;
        if (swipeCount > 0) {

            oldExploreVideoAdapter!!.notifyDataSetChanged()

            swipe_refresh.isRefreshing = false

            if (isVideos) {
                getExploreVideo()
            } else {
                pageNo = 0
                getListVideoTrickDataList(
                    RestClient.apiType,
                    myUserId,
                    "0",
                    "Video",
                    footballagecatID,
                    footballType,
                    "",
                    userData?.userID!!,
                    RestClient.apiVersion,
                    pageNo!!,
                    sortby,
                    publicationTime,
                    gender,
                    "trending",
                    footballLevel,
                    "ExploreTricks",
                    "6",
                    countryID,
                    pitchPosition
                )
            }
        }
    }

    fun isProgress(isVisible: Boolean) {
        if (isVisible) {
            ll_explore_header_progress.visibility = View.VISIBLE
        } else {
            ll_explore_header_progress.visibility = View.GONE
        }
    }

    fun createPost(
        from: String,
        datumList: ArrayList<CreatePostPhotoPojo>,
        stringExtraDes: String,
        stringExtraPrivcy: String,
        Location: String?,
        latitude: String?,
        longitude: String?,
        tag: String?,
        VideoThumb: ArrayList<CreatePostPhotoPojo>,
        radioText: String?,
        connectionTypeIDs: String?
    ) {
        //if (homeRadioButton.isChecked() || socialRadioButton.isChecked()){
        if (datumList != null && datumList?.size!! > 0) {
            /*val imageVideolist1:ArrayList<AddImages> =ArrayList()
            for (i in numberOfImage?.indices!!.reversed()) {
                if (numberOfImage?.get(i)!!.fromServer.equals("No",false)) {
                    imageVideolist1.add(numberOfImage!!.get(i)!!)
                    numberOfImage?.removeAt(i)
                }
            }*/

            ll_explore_header_progress.visibility = View.VISIBLE
            if (datumList?.size!! > 0) {
                MyAsyncVideoCompressTask(
                    from,
                    datumList,
                    stringExtraDes,
                    stringExtraPrivcy,
                    Location,
                    latitude,
                    longitude,
                    tag,
                    VideoThumb,
                    radioText,
                    connectionTypeIDs
                ).execute()

            } else {
                ll_explore_header_progress.visibility = View.GONE
                Log.e("CreatePost", "CreatePost")
                //createPost(s, btnSave, numberOfImage!!)
            }


        } else {

            MyUtils.showSnackbar(
                mActivity!!,
                resources.getString(R.string.exo_track_selection_title_video),
                ll_explore_header
            )
        }
    }

    inner class MyAsyncVideoCompressTask(
        var from: String,
        var imageList: ArrayList<CreatePostPhotoPojo>,
        var stringExtraDes: String,
        var stringExtraPrivcy: String,
        var Location: String?,
        var latitude: String?,
        var longitude: String?,
        var tag: String?,
        var VideoThumb: ArrayList<CreatePostPhotoPojo>,
        var radioText: String?,
        var connectionTypeIDs: String?
    ) : AsyncTask<Int?, Int?, String>() {

        override fun onPostExecute(result: String?) {
            if (result.equals("Task Completed.", false)) {
                val jsonArray = JSONArray()
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("loginuserID", userData?.userID)
                    jsonObject.put("apiType", RestClient.apiType)
                    jsonObject.put("apiVersion", RestClient.apiVersion)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                jsonArray.put(jsonObject)

                UploadExploreVideo().uploadFileVideo(
                    mActivity!!,
                    jsonArray.toString(),
                    "post",
                    imageList,
                    false,
                    object : UploadExploreVideo.OnUploadFileListener {
                        override fun onSuccessUpload(datumList: UploadImagePojo?) {

                            createExplore(
                                from,
                                imageList,
                                stringExtraDes,
                                stringExtraPrivcy,
                                Location,
                                latitude,
                                longitude,
                                tag,
                                VideoThumb,
                                radioText,
                                connectionTypeIDs
                            )
                        }

                        override fun onFailureUpload(
                            msg: String,
                            datumList: List<UploadImagePojo>?
                        ) {
                            ErrorUtil.errorMethod(ll_explore_header)
                        }

                    })
            }

        }

        override fun onPreExecute() {
            super.onPreExecute()
            ll_explore_header_progress.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg p0: Int?): String {
            val fileName: String = MyUtils.createFileName(Date(), "Video")!!
            val file =
                ImageSaver(mActivity!!).setExternal(true).setFileName(fileName).createFile()
            val isCompress: Boolean = VideoController.getInstance().convertVideo(
                imageList!![0]!!.imagePath,
                file.absolutePath,
                VideoController.COMPRESS_QUALITY_MEDIUM
            ) { percent ->
                Log.d("percent video compress", "" + percent)
                progressBar.progress = percent.toInt()
            }
            if (isCompress) {
                var width = 0
                var height = 0
                try {
                    var retriever = MediaMetadataRetriever()
                    retriever.setDataSource(file.absolutePath)
                    width =
                        Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
                    height =
                        Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val fileName1 = MyUtils.createFileName(Date(), "Video", height, width)
                imageList[0]!!.imageName = (fileName1!!)
                imageList[0]!!.imagePath = file.absolutePath


                imageList[0].isCompress = true

                if (VideoThumb != null && VideoThumb.size > 0) {
                    MyUtils.showSnackbar(
                        mActivity!!,
                        "Please wait post uploading...",
                        ll_main_explore
                    )

                    val jsonArray = JSONArray()
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("loginuserID", "0")
                        jsonObject.put("apiType", RestClient.apiType)
                        jsonObject.put("apiVersion", RestClient.apiVersion)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    jsonArray.put(jsonObject)
                    Log.e("jsonArray", "" + Gson().toJson(jsonArray))

                    var compressedImage =
                        compressFiles(VideoThumb, fileName1?.replace(".mp4", "_thumb.jpg"))
                    Log.e("compressedImage", compressedImage.toString())
                    val filePart = MultipartBody.Part.createFormData(
                        "FileField", fileName1?.replace(".mp4", "_thumb.jpg"),
                        RequestBody.create("image*//*".toMediaTypeOrNull(), compressedImage!!)
                    )
                    val call = RestClient.get()!!.uploadAttachment(
                        filePart,
                        RequestBody.create("text/plain".toMediaTypeOrNull(), "post"),
                        RequestBody.create("text/plain".toMediaTypeOrNull(), jsonArray.toString())
                    )
                    call.enqueue(object : RestCallback<List<UploadImagePojo>>(mActivity!!) {
                        override fun Success(response: Response<List<UploadImagePojo>>) {
                            Log.e("response", "data" + response.body().toString())
                            if (compressedImage != null)
                                compressedImage!!.delete()
                            if (response.body() != null && response.body()!!.isNotEmpty()) {
                                if (response.body()!![0].status.toString().equals("true", true)) {
                                    Log.e("Response", "" + response.body())
                                } else {
                                    MyUtils.showSnackbar(
                                        mActivity!!,
                                        response.body()!![0].message!!,
                                        ll_explore_header
                                    )

                                }
                            } else {
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    response.body()!![0].message!!,
                                    ll_explore_header
                                )

                            }
                        }

                        override fun failure() {
                            if (compressedImage != null)
                                compressedImage!!.delete()
                            ErrorUtil.errorMethod(ll_explore_header)
                        }
                    })

                }


            }

            return "Task Completed."
        }
    }

    fun compressFiles(VideoThumb: ArrayList<CreatePostPhotoPojo>, replace: String): File {
        var file: File? = null
        for (i in VideoThumb?.indices!!) {
            if (!VideoThumb!![i]!!.isCompress) {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                val file1 = File(VideoThumb[i]!!.imagePath.toString())
                try {
                    file = Compressor(mActivity)
                        .setQuality(90)
                        .setMaxHeight(225)
                        .setMaxWidth(720)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .compressToFile(file1)

                    VideoThumb[i]!!.imageName = (replace!!)
                    VideoThumb[i]!!.imagePath = file.absolutePath
                    VideoThumb[i]!!.isCompress = true
                } catch (e: IOException) {
                    e.printStackTrace()
                }


            } else {
                file = null
            }
        }

        return file!!
    }

    private fun createExplore(
        from: String,
        datumList: ArrayList<CreatePostPhotoPojo>,
        stringExtraDes: String,
        stringExtraPrivcy: String,
        location: String?,
        latitude: String?,
        longitude: String?,
        tag: String?,
        videoThumb: ArrayList<CreatePostPhotoPojo>,
        radioText: String?,
        connectionTypeIDs: String?
    ) {
        ll_explore_header_progress.visibility = View.GONE
        var c = Calendar.getInstance()

        var df = SimpleDateFormat("yyyy-MM-dd")
        var formattedDate = df.format(c.time)

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        val jsonObjectpostSerializedData = JSONObject()
        val jsonArraypostSerializedData = JSONArray()


        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("postDescription", Constant.encode(stringExtraDes))
            jsonObject.put("postLanguage", "en")
            jsonObject.put("postCategory", "Progress Picture")
            jsonObject.put("postPrivacyType", stringExtraPrivcy)
            jsonObject.put("postUploadDate", formattedDate)
            jsonObject.put("connectiontypeIDs", connectionTypeIDs)
            jsonObject.put("postHashTags", tag)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonObject.put("postLatitude", latitude)
        jsonObject.put("postLongitude", longitude)
        jsonObject.put("postLocation", location)
        when (radioText) {
            "Videos" -> {
                jsonObject.put("postType", "ExploreVideos")

            }
            "Tricks" -> {
                jsonObject.put("postType", "ExploreTricks")
            }
        }
        jsonObject.put("postMediaType", "Video")

        try {
            jsonObjectpostSerializedData.put("albumName", "")
            jsonObjectpostSerializedData.put("albumType", "Media")
            val jsonArrayAlbummedia = JSONArray()
            if (!datumList.isNullOrEmpty()) {
                for (i in 0 until datumList.size) {
                    val jsonObjectAlbummedia = JSONObject()
                    try {
                        jsonObjectAlbummedia.put("albummediaFile", datumList[i].imageName)
                        jsonObjectAlbummedia.put("albummediaThumbnail", videoThumb[0].imageName)
                        jsonObjectAlbummedia.put("albummediaFileType", "Video")
                        jsonArrayAlbummedia.put(i, jsonObjectAlbummedia)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            jsonObjectpostSerializedData.put("albummedia", jsonArrayAlbummedia)
            jsonArraypostSerializedData.put(jsonObjectpostSerializedData)
            jsonObject.put("postSerializedData", jsonArraypostSerializedData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        Log.e("jsonArray", "" + Gson().toJson(jsonArray))
        val createPostModel =
            ViewModelProviders.of(this@ExploreFragment).get(CreatePostModel::class.java)
        createPostModel.apiFunction(mActivity!!, jsonArray.toString(), "")
            .observe(this@ExploreFragment,
                androidx.lifecycle.Observer
                { response ->
                    if (!response.isNullOrEmpty()) {
                        MyUtils.dismissProgressDialog()
                        if (response[0].status.equals("true", true)) {
                            MyUtils.hideKeyboard1(mActivity!!)
                            //MyUtils.showSnackbar(mActivity!!, response[0].message!!, ll_explore_header)
                            if (isVideos) {

                                pageNo = 0
                                getGridVideoDataList(
                                    "",
                                    myUserId,
                                    "10",
                                    RestClient.apiVersion,
                                    "ExploreVideos",
                                    "Video",
                                    "",
                                    "",
                                    "",
                                    userData?.userID!!,
                                    "",
                                    RestClient.apiType,
                                    pageNo,
                                    "",
                                    "trending",
                                    "",
                                    "",
                                    "0",
                                    ""
                                )

                            } else if (isTricks) {
                                pageNo = 0
                                getListVideoTrickDataList(
                                    RestClient.apiType,
                                    myUserId,
                                    "0",
                                    "Video",
                                    footballagecatID,
                                    footballType,
                                    "",
                                    userData?.userID!!,
                                    RestClient.apiVersion,
                                    pageNo!!,
                                    sortby,
                                    publicationTime,
                                    gender,
                                    "trending",
                                    footballLevel,
                                    "ExploreTricks",
                                    "6",
                                    countryID,
                                    pitchPosition
                                )
                            }

                        } else {
                            //No data and no internet
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                if (!response[0].message!!.isNullOrEmpty()) {
                                    MyUtils.showSnackbar(
                                        mActivity!!,
                                        response[0].message!!,
                                        ll_explore_header
                                    )
                                }

                            } else {
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    resources.getString(R.string.error_common_network),
                                    ll_explore_header
                                )
                            }
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_crash_error_message),
                                ll_explore_header
                            )
                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_common_network),
                                ll_explore_header
                            )
                        }
                    }
                })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 820 && data != null) {
            if (data.hasExtra("footballLevel"))
                footballLevel = data.getStringExtra("footballLevel")!!
            if (data.hasExtra("countryID"))
                countryID = data.getStringExtra("countryID")!!
            if (data.hasExtra("sortby"))
                sortby = data.getStringExtra("sortby")!!
            if (data.hasExtra("pitchPosition"))
                pitchPosition = data.getStringExtra("pitchPosition")!!
            if (data.hasExtra("gender"))
                gender = data.getStringExtra("gender")!!
            if (data.hasExtra("footballagecatID"))
                footballagecatID = data.getStringExtra("footballagecatID")!!
            if (data.hasExtra("footballType"))
                footballType = data.getStringExtra("footballType")!!
            if (data.hasExtra("publicationTime"))
                publicationTime = data.getStringExtra("publicationTime")!!

            applyFilter()


        }

    }

    fun applyFilter() {
        pageNo = 0
        isLastpage = false
        isLoading = false
        getGridVideoDataList(
            "",
            myUserId,
            "10",
            RestClient.apiVersion,
            "ExploreVideos",
            "Video",
            footballLevel,
            countryID,
            sortby,
            userData?.userID!!,
            pitchPosition,
            RestClient.apiType,
            pageNo,
            gender,
            "trending",
            footballagecatID,
            footballType,
            "0",
            publicationTime
        )
    }

    fun applySearch(searchKeyword: String) {
        pageNo = 0
        isLastpage = false
        isLoading = false
        getGridVideoDataList(
            "",
            myUserId,
            "10",
            RestClient.apiVersion,
            "ExploreVideos",
            "Video",
            footballLevel,
            countryID,
            sortby,
            userData?.userID!!,
            pitchPosition,
            RestClient.apiType,
            pageNo,
            gender,
            "trending",
            footballagecatID,
            footballType,
            "0",
            publicationTime,
            searchKeyword
        )
    }
}
