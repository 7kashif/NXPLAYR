package com.nxplayr.fsl.ui.fragments.explorepost.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.gson.JsonParseException
import com.nxplayr.fsl.R
import com.nxplayr.fsl.application.MyApplication
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CommentPojo
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.ThreedotsBottomPojo
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.activity.post.view.CreatePostActivityTwo
import com.nxplayr.fsl.ui.fragments.bottomsheet.ThreeDotsBottomSheetFragment
import com.nxplayr.fsl.ui.fragments.collection.view.BottomSheetExplore
import com.nxplayr.fsl.ui.fragments.explorepost.adapter.ExploreVideoDetailAdapter
import com.nxplayr.fsl.ui.fragments.explorepost.viewmodel.ExploreVideoModelV2
import com.nxplayr.fsl.ui.fragments.explorepost.viewmodel.LikePostModel
import com.nxplayr.fsl.ui.fragments.feed.view.PostViewLikeListFragment
import com.nxplayr.fsl.ui.fragments.feed.viewmodel.CreatePostModelV2
import com.nxplayr.fsl.ui.fragments.postcomment.view.PostCommentListFragment
import com.nxplayr.fsl.ui.fragments.postcomment.viewmodel.CommentModel
import com.nxplayr.fsl.util.Constant
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.explore_video_detail_page_activity.*
import kotlinx.android.synthetic.main.fragment_explore_view_pager.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ExploreVideoDetailFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var mActivity: AppCompatActivity? = null
    var list_bottomsheet: ArrayList<ThreedotsBottomPojo>? = ArrayList()
    var linearLayoutManager: LinearLayoutManager? = null
    var exoPlayer: SimpleExoPlayer? = null
    private var mViewPagerAdapter: ExploreVideoDetailAdapter? = null
    private var isMuteing = false
    var explore_video_list: java.util.ArrayList<CreatePostData?>? = java.util.ArrayList()
    var pos = 0
    var postType = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        try {
            if (v == null) {
                v = inflater.inflate(R.layout.fragment_explore_view_pager, container, false)

            }
        } catch (e: InflateException) {
        }
        // v = inflater.inflate(R.layout.fragment_explore_view_pager, container, false)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(this.requireActivity())

        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (arguments != null) {
            pos = arguments?.getInt("pos", 0)!!
            postType = arguments?.getString("postType")!!
            explore_video_list =
                arguments?.getSerializable("explore_video_list") as java.util.ArrayList<CreatePostData?>?
        }

        getPostList(
            explore_video_list?.get(pos)?.postID!!,
            explore_video_list?.get(pos)?.userID!!,
            "",
            postType
        )

        img_back.setOnClickListener(this)

        mViewPagerAdapter =
            ExploreVideoDetailAdapter(mActivity!!, object : ExploreVideoDetailAdapter.OnItemClick {

                override fun onClicklisneter(pos: Int) {
                    val likeFragment = PostViewLikeListFragment()
                    Bundle().apply {
                        putString("postId", explore_video_list?.get(pos)?.postID)
                        putString("from", "Like")
                        likeFragment.arguments = this
                    }
                    (activity as MainActivity).navigateTo(
                        likeFragment,
                        likeFragment::class.java.name,
                        true
                    )
                }

                override fun onClickCommnetlisneter(pos: Int) {

                    val commnetFragment = PostCommentListFragment()
                    Bundle().apply {
                        putString("postId", explore_video_list?.get(pos)?.postID)
                        putString("postUserID", userData?.userID)
                        commnetFragment.arguments = this
                    }
                    (activity as MainActivity).navigateTo(
                        commnetFragment,
                        commnetFragment::class.java.name,
                        true
                    )

                }

                override fun onClickViewlisneter(pos: Int) {

                    val viewFragment = PostViewLikeListFragment()
                    Bundle().apply {
                        putString("postId", explore_video_list?.get(pos)?.postID)
                        putString("from", "View")
                        viewFragment.arguments = this
                    }
                    (activity as MainActivity).navigateTo(
                        viewFragment,
                        viewFragment::class.java.name,
                        true
                    )
                }

                override fun onClickSharelisneter(pos: Int) {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Hey check out post on FSL" + "\n\nhttps://play.google.com/store/apps/details?id=${mActivity!!.packageName}"
                    )
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "NXPLAYR")
                    startActivity(Intent.createChooser(shareIntent, "Share post via"))

                    //  openBottomSheetForShare(pos)
                }

                override fun onClickMorelisneter(pos: Int) {

                    val bottomSheet = BottomSheetExplore()
                    Bundle().apply {
                        putString("postId", explore_video_list?.get(pos)?.postID)
                        if (explore_video_list!!.get(pos)?.postAlbum.isNullOrEmpty()) {
                            putString("collection", "Add")
                            putString("postType", explore_video_list!!.get(pos)?.postType)
                            putSerializable("explore_video_list", explore_video_list)

                        } else {
                            putString("collection", "Remove")
                            putString(
                                "albumID",
                                explore_video_list!!.get(pos)?.postAlbum!![0].exalbumID
                            )
                            putString(
                                "subAlbumAId",
                                explore_video_list!!.get(pos)?.postAlbum!![0].exsubalbumID
                            )
                            putString("postType", explore_video_list!!.get(pos)?.postType)
                            putSerializable("explore_video_list", explore_video_list)
                        }
                        putString("user_ID", explore_video_list?.get(pos)?.userID)
                        bottomSheet.arguments = this
                    }
                    bottomSheet.show(mActivity?.supportFragmentManager!!, "BottomSheetExplore")
                    bottomSheet.setOnclickLisner(object : BottomSheetExplore.BottomSheetListener {
                        override fun onOptionClick(text: String) {
                            when (text) {
                                "RemoveAPi" -> {
                                    explore_video_list!!.get(pos)?.postAlbum?.clear()
                                    MyUtils.CollecationData = ""
                                }
                                "MoveTo" -> {
                                    bottomSheet.dismiss()
                                    editPost(pos)
                                }
                                "EditPost" -> {
                                    bottomSheet.dismiss()

                                    Intent(mActivity!!, CreatePostActivityTwo::class.java).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        putExtra("postData", explore_video_list?.get(pos))
                                        putExtra("fromType", "editPost")
                                        putExtra("type1", "MyExploreVideo")
                                        startActivity(this)
                                    }
                                }
                            }
                        }

                    })
                }

                override fun onClickUnselectlisneter(pos: Int) {

                    getListPost(
                        explore_video_list?.get(pos)?.postID,
                        userData?.userID,
                        "Add",
                        RestClient.apiType,
                        RestClient.apiVersion
                    )
                    val intLike: Int? = explore_video_list?.get(pos)?.postLike!!.toInt()
                    explore_video_list?.get(pos)?.postLike = (intLike!! + 1).toString()
                    txt_like.text = explore_video_list?.get(pos)?.postLike
                }

                override fun onClickSelectlisneter(pos: Int) {

                    getUnLikePost(
                        explore_video_list?.get(pos)?.postID,
                        userData?.userID,
                        "Remove",
                        RestClient.apiType,
                        RestClient.apiVersion
                    )
                    var subLike = (explore_video_list?.get(pos)?.postLike?.toInt()!!) - 1
                    explore_video_list?.get(pos)?.postLike = subLike.toString()
                    txt_like.text = explore_video_list?.get(pos)?.postLike.toString()
                }

                override fun onClickPlaylisneter(pos: Int, videofile: String) {
                }

                override fun onComment(
                    pos: Int,
                    from: String,
                    editText: EditText,
                    commentcomment: String
                ) {
                    MyUtils.hideKeyboard1(mActivity!!)
                    sendPostComment(
                        explore_video_list!![pos]!!.postID,
                        explore_video_list!![pos]!!.postMediaType,
                        commentcomment,
                        pos,
                        editText
                    )

                }

            }, explore_video_list!!, false)
        viewPager.adapter = mViewPagerAdapter
        viewPager.setCurrentItem(pos, true)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (mViewPagerAdapter != null) {
                    mViewPagerAdapter?.stopPlayer()
                    mViewPagerAdapter?.pausePlayer()
                }
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

    }

    fun editPost(positionPost: Int) {
        MyUtils.showProgressDialog(mActivity!!, resources.getString(R.string.wait))

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
            jsonObject.put(
                "postDescription",
                explore_video_list?.get(positionPost)?.postDescription
            )
            jsonObject.put("postLanguage", "en")
            jsonObject.put("postID", explore_video_list?.get(positionPost)?.postID)
            jsonObject.put("postCategory", "")
            jsonObject.put(
                "postPrivacyType",
                explore_video_list?.get(positionPost)?.postPrivacyType
            )
            jsonObject.put("postUploadDate", formattedDate)
            jsonObject.put("connectiontypeIDs", "")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        when (explore_video_list?.get(positionPost)?.postMediaType) {

            "Video" -> {
                if (!explore_video_list?.get(positionPost)?.postLocation.isNullOrEmpty()) {
                    jsonObject.put(
                        "postLocation",
                        explore_video_list?.get(positionPost)?.postLocation
                    )

                } else {
                    // jsonObject.put("postLocation", MyUtils.currentLocation.toString())
                    jsonObject.put("postLocation", "")

                }
                if (!explore_video_list?.get(positionPost)?.postLatitude.isNullOrEmpty()) {
                    jsonObject.put(
                        "postLatitude",
                        explore_video_list?.get(positionPost)?.postLatitude
                    )

                } else {
                    jsonObject.put("postLatitude", MyUtils.currentLattitude.toString())

                }
                if (!explore_video_list?.get(positionPost)?.postLongitude.isNullOrEmpty()) {
                    jsonObject.put(
                        "postLongitude",
                        explore_video_list?.get(positionPost)?.postLongitude
                    )

                } else {
                    jsonObject.put("postLongitude", MyUtils.currentLongtiude.toString())

                }
                when (explore_video_list?.get(positionPost)?.postType) {
                    "ExploreVideos" -> {
                        jsonObject.put("postType", "ExploreTricks")

                    }
                    "ExploreTricks" -> {
                        jsonObject.put("postType", "ExploreVideos")

                    }
                }
                jsonObject.put("postMediaType", "Video")
                try {
                    jsonObjectpostSerializedData.put("albumName", "")
                    jsonObjectpostSerializedData.put("albumType", "Media")
                    val jsonArrayAlbummedia = JSONArray()
                    if (!explore_video_list?.get(positionPost)?.postSerializedData!![0].albummedia.isNullOrEmpty()) {
                        for (i in explore_video_list?.get(positionPost)?.postSerializedData!![0].albummedia.indices) {
                            val jsonObjectAlbummedia = JSONObject()
                            try {
                                jsonObjectAlbummedia.put(
                                    "albummediaFile",
                                    explore_video_list?.get(positionPost)?.postSerializedData!![0].albummedia[i].albummediaFile
                                )
                                jsonObjectAlbummedia.put(
                                    "albummediaThumbnail",
                                    explore_video_list?.get(positionPost)?.postSerializedData!![0].albummedia[i].albummediaThumbnail
                                )
                                jsonObjectAlbummedia.put("albummediaFileType", "Video")
                                jsonObjectAlbummedia.put(
                                    "albummediaFileSize",
                                    explore_video_list?.get(positionPost)?.postSerializedData!![0].albummedia[i].albummediaFileSize
                                )
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
            }

        }
        jsonArray.put(jsonObject)


        val createPostModel = ViewModelProviders.of(this@ExploreVideoDetailFragment).get(
            CreatePostModelV2::class.java
        )
        createPostModel.postFunction(jsonArray.toString(), "editPost")
        createPostModel.postSuccessLiveData
            .observe(this@ExploreVideoDetailFragment,
                androidx.lifecycle.Observer { response ->
                    if (!response.isNullOrEmpty()) {
                        MyUtils.dismissProgressDialog()
                        if (response[0].status.equals("true", true)) {
                            MyUtils.hideKeyboard1(mActivity!!)
                            MyUtils.showSnackbar(
                                mActivity!!,
                                response[0].message,
                                ll_video_details
                            )
                            (activity as MainActivity).onBackPressed()


                        } else {
                            //No data and no internet
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                if (!response[0].message.isNullOrEmpty()) {
                                    MyUtils.showSnackbar(
                                        mActivity!!,
                                        response[0].message,
                                        ll_video_details
                                    )
                                }

                            } else {
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    resources.getString(R.string.error_common_network),
                                    ll_video_details
                                )
                            }
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_crash_error_message),
                                ll_video_details
                            )
                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_common_network), ll_video_details
                            )
                        }
                    }
                })
    }


    private fun sendPostComment(
        postID: String,
        postMediaType: String,
        commentComment: String,
        position: Int,
        view1: EditText
    ) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("postID", postID)
            jsonObject.put("action", "Add")
            if (postMediaType.equals("Photo")) {
                jsonObject.put("commentMediaType", "Image")
            } else {
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
        val apiCall =
            ViewModelProviders.of(this@ExploreVideoDetailFragment!!).get(CommentModel::class.java)
        apiCall?.apiFunction(mActivity!!, true, jsonArray.toString(), 1)
            ?.observe(this@ExploreVideoDetailFragment,
                Observer<List<CommentPojo>?>
                { response ->

                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true")) {
                            if (!response[0].data.isNullOrEmpty()) {
                                explore_video_list!![position]!!.postCommentList?.add(
                                    0,
                                    response[0].data[0]
                                )
                                mViewPagerAdapter?.setupCommentAdapter(
                                    position,
                                    explore_video_list!!
                                )
//                                mViewPagerAdapter?.addComment(response[0].data[0])
                                explore_video_list!![position]!!.postComment =
                                    (explore_video_list!![position]!!.postComment.toInt() + 1).toString()
                            }
                            mViewPagerAdapter?.notifyDataSetChanged()
                            view1?.text.clear()
                        } else {
                            //data not find
                            MyUtils.showSnackbar(
                                mActivity!!,
                                response[0].message,
                                viewPager!!
                            )
                        }
                    } else {
                        if (!MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_common_network),
                                viewPager!!

                            )
                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_crash_error_message),
                                viewPager!!

                            )
                        }
                    }
                })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.img_back -> {

                (activity as MainActivity).onBackPressed()

            }
        }
    }

    private fun getUnLikePost(
        postID: String?,
        userID: String?,
        action: String,
        apiType: String,
        apiVersion: String
    ) {

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {

            jsonObject.put("postID", postID)
            jsonObject.put("loginuserID", userID)
            jsonObject.put("action", action)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }
        Log.d("LIKE_OBJECT", jsonObject.toString())
        var postLikeModel =
            ViewModelProviders.of(this@ExploreVideoDetailFragment).get(LikePostModel::class.java)
        postLikeModel.getPostLikeApi(mActivity!!, false, jsonArray.toString())
            .observe(this@ExploreVideoDetailFragment,
                Observer { postLikepojo ->

                    if (postLikepojo != null && postLikepojo.isNotEmpty()) {

                        if (postLikepojo[0].status.equals("true", true)) {

                            img_unselected.visibility = View.VISIBLE
                            img_selected.visibility = View.GONE
                        } else {

                        }

                    } else {

                    }

                })
    }

    private fun getListPost(
        postID: String?,
        userID: String?,
        action: String,
        apiType: String,
        apiVersion: String
    ) {

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {

            jsonObject.put("postID", postID)
            jsonObject.put("loginuserID", userID)
            jsonObject.put("action", action)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }
        Log.d("LIKE_OBJECT", jsonObject.toString())
        var postLikeModel =
            ViewModelProviders.of(this@ExploreVideoDetailFragment).get(LikePostModel::class.java)
        postLikeModel.getPostLikeApi(mActivity!!, false, jsonArray.toString())
            .observe(this@ExploreVideoDetailFragment,
                Observer { postLikepojo ->

                    if (postLikepojo != null && postLikepojo.isNotEmpty()) {

                        if (postLikepojo[0].status.equals("true", true)) {

                            img_unselected.visibility = View.GONE
                            img_selected.visibility = View.VISIBLE
                        } else {

                        }

                    } else {

                    }

                })
    }

    private fun openBottomSheetForShare(pos: Int) {
        list_bottomsheet = ArrayList()
        list_bottomsheet!!.clear()
        list_bottomsheet!!.add(
            ThreedotsBottomPojo(
                R.drawable.popup_share_in_post_icon,
                resources.getString(R.string.share_in_a_post)
            )
        )
        list_bottomsheet!!.add(
            ThreedotsBottomPojo(
                R.drawable.popup_send_messages_icon,
                resources.getString(R.string.send_in_a_private_message)
            )
        )

        val bottomSheet = ThreeDotsBottomSheetFragment()
        val bundle = Bundle()
        bundle.putSerializable("data", list_bottomsheet)
        bundle.putString("from", "ShareList")
        bottomSheet.arguments = bundle
        bottomSheet.show(mActivity!!.supportFragmentManager, "list")
        bottomSheet.setOnclickLisner(object : ThreeDotsBottomSheetFragment.SelectList {
            override fun onOptionSelect(value: Int, from: String) {
                bottomSheet.dismiss()
                when (from) {
                    resources.getString(R.string.share_in_a_post) -> {
                        /* Intent(mActivity!!, CreatePostActivityTwo::class.java).apply {
                            putExtra("postDataShare", explore_video_list?.get(pos))
                            putExtra("fromType", "postShare")
                            startActivity(this)
                        }*/
                        val shareIntent = Intent()
                        shareIntent.action = Intent.ACTION_SEND
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            "Hey check out post on FSL" + "\n\nhttps://play.google.com/store/apps/details?id=${mActivity!!.packageName}"
                        )
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "NXPLAYR")
                        startActivity(Intent.createChooser(shareIntent, "Share post via"))

                    }
                    resources.getString(R.string.send_in_a_private_message) -> {
                    }
                }
            }

        })
    }

    private fun getPostList(postId: String, userId: String, from: String, postType: String) {
        MyUtils.showProgressDialog(mActivity!!, "Please Wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("tag", "")
            jsonObject.put("loginuserID", "0")
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("postType", postType)
            jsonObject.put("postMediaType", "Video")
            jsonObject.put("footballLevel", "")
            jsonObject.put("countryID", "")
            jsonObject.put("sortby", "")
            jsonObject.put("userID", userData?.userID)
            jsonObject.put("pitchPosition", "")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("page", "0")
            jsonObject.put("gender", "")
            jsonObject.put("tabname", "trending")
            jsonObject.put("footballagecatID", "")
            jsonObject.put("footballType", "")
            jsonObject.put("postID", postId)
            jsonObject.put("publicationTime", "")


        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        var getEmployementModel =
            ViewModelProviders.of(this@ExploreVideoDetailFragment)
                .get(ExploreVideoModelV2::class.java)
        getEmployementModel.getVideos(jsonArray.toString())
        getEmployementModel.exploreSuccessLiveData
            .observe(viewLifecycleOwner,
                Observer { exploreVidelistpojo ->

                    MyUtils.dismissProgressDialog()

                    if (exploreVidelistpojo != null && exploreVidelistpojo.isNotEmpty()) {

                        if (exploreVidelistpojo[0].status.equals("true", true)) {
                            for (i in 0 until explore_video_list?.size!!) {
                                if (!postId.equals(exploreVidelistpojo[0].data?.get(0)?.postID, false)) {
                                    explore_video_list?.add(
                                        i,
                                        exploreVidelistpojo[0].data?.get(0)
                                    )
                                    break
                                }
                            }
                            if (mViewPagerAdapter != null) {
                                mViewPagerAdapter?.notifyDataSetChanged()
                            }
                        } else {
                            (activity as MainActivity).showSnackBar(exploreVidelistpojo!![0]!!.message!!)
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        try {
                            if (!MyUtils.isInternetAvailable(MyApplication.instance)) {
                                (activity as MainActivity).showSnackBar(resources.getString(R.string.error_common_network))
                            } else {
                                (activity as MainActivity).showSnackBar(resources.getString(R.string.error_crash_error_message))
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                })
    }

    override fun onStop() {
        super.onStop()
        if (mViewPagerAdapter != null) {
            mViewPagerAdapter?.stopPlayer()
            mViewPagerAdapter?.pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mViewPagerAdapter != null) {
            mViewPagerAdapter?.stopPlayer()
            mViewPagerAdapter?.pausePlayer()
        }

    }

}
