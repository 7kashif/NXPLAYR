package com.nxplayr.fsl.ui.fragments.collection.view

import android.content.Context
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
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.explorepost.adapter.ExploreVideoAdapater
import com.nxplayr.fsl.ui.fragments.explorepost.adapter.ExploreVideoListAdapater
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.explorepost.viewmodel.ExploreVideoModel
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.explorepost.view.ExploreVideoDetailFragment
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.explore_view_all_video_activity.*
import kotlinx.android.synthetic.main.explore_view_all_video_activity.img_Filter
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class AssignPostAlbumFragment : Fragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var v: View? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var mActivity: AppCompatActivity? = null

    var explore_video_list: ArrayList<CreatePostData?>? = ArrayList()
    var exploreVideo_adapter: ExploreVideoAdapater? = null
    var exploreVideoListadapter: ExploreVideoListAdapater? = null

    var swipeCount = 0

    var linearLayoutManagerTrick: LinearLayoutManager? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var y: Int = 0
    var pageNo = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false

    var albumId: String? = null
    var albumName: String? = null
    var subAlbumName: String? = null
    var subAlbumId: String? = null
    var postId: String? = "0"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.explore_view_all_video_activity, container, false)

        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(mActivity!!)
        userData = sessionManager?.get_Authenticate_User()

        if (arguments != null) {
            albumId = arguments?.getString("albumId")!!
            albumName = arguments?.getString("albumName")!!
            subAlbumId = arguments?.getString("subAlbumId")!!
            subAlbumName = arguments?.getString("subAlbumName")!!
            postId = arguments?.getString("postId")!!

        }
        img_Filter.visibility=View.GONE
        txt_video_type.setText(subAlbumName)

        txtViewAll.setOnClickListener(this)
        img_listView.setOnClickListener(this)
        img_expGridView.setOnClickListener(this)
        btnRetry.setOnClickListener(this)
        img_Filter.setOnClickListener(this)
        img_expGridView2.setOnClickListener(this)
        img_listView2.setOnClickListener(this)
        swipe_refresh_layout.setOnRefreshListener(this)
        getGridAssignPostAlbum()
    }

    override fun onRefresh() {
        swipeCount += 1;
        if (swipeCount > 0) {

            exploreVideo_adapter!!.notifyDataSetChanged()

            swipe_refresh_layout.isRefreshing = false

            pageNo = 0
            getAssignPostAlbumData(userData?.userID!!, pageNo, "Album", "", "", "", "", RestClient.apiVersion, "10",
                    "", "", "", "", subAlbumId!!, "", "",
                    "Video", "0", albumId!!, "0", RestClient.apiType)
        }
    }

    //Click Handle
    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.txtViewAll -> {
                (activity as MainActivity).onBackPressed()
              
            }

            R.id.img_listView -> {

                img_listView2.visibility = View.VISIBLE
                img_expGridView2.visibility = View.VISIBLE
                img_expGridView.visibility = View.GONE
                img_listView.visibility = View.GONE

                if (explore_video_list.isNullOrEmpty()) {

                    //Api call Funcation
                    pageNo = 0
                    getAssignPostAlbumData(userData?.userID!!, pageNo, "Album", "", "", "", "", RestClient.apiVersion, "10",
                            "", "", "", "", subAlbumId!!, "", "",
                            "Video", "0", albumId!!, "0", RestClient.apiType)
                } else {

                    getListAssignPostAlbum()
                }
            }

            R.id.img_expGridView2 -> {

                img_expGridView2.visibility = View.GONE
                img_listView2.visibility = View.GONE
                img_listView.visibility = View.VISIBLE
                img_expGridView.visibility = View.VISIBLE

                if (explore_video_list.isNullOrEmpty()) {

                    //Api call Funcation
                    pageNo = 0
                    getAssignPostAlbumData(userData?.userID!!, pageNo, "Album", "", "", "", "", RestClient.apiVersion, "10",
                            "", "", "", "", subAlbumId!!, "", "",
                            "Video", "0", albumId!!, "0", RestClient.apiType)
                } else {

                    getGridAssignPostAlbum()
                }

            }

            R.id.btnRetry -> {

                //Api call Funcation
                pageNo = 0
                getAssignPostAlbumData(userData?.userID!!, pageNo, "Album", "", "", "", "", RestClient.apiVersion, "10",
                        "", "", "", "", subAlbumId!!, "", "",
                        "Video", "0", albumId!!, "0", RestClient.apiType)
            }

            R.id.img_Filter -> {

            }
        }
    }

    //Type of View
    private fun getGridAssignPostAlbum() {
        rc_all_video.layoutManager = GridLayoutManager(mActivity!!, 2)
        if (explore_video_list.isNullOrEmpty() || pageNo == 0) {

            exploreVideo_adapter = ExploreVideoAdapater(mActivity!!, object : ExploreVideoAdapater.OnItemClick {
                override fun onClicklisneter(pos: Int) {

                    val exploreDetailFragment = ExploreVideoDetailFragment()
                    Bundle().apply {
                        putString("user_ID", explore_video_list!!.get(pos)?.userID)
                        putInt("pos", pos)
                        putString("username", explore_video_list!!.get(pos)?.userFirstName)
                        putString("like", explore_video_list!!.get(pos)?.postLike)
                        putString("view", explore_video_list!!.get(pos)?.postViews)
                        putString("userProfile", explore_video_list!!.get(pos)?.userProfilePicture)
                        putString("postID", explore_video_list!!.get(pos)?.postID)
                        putString("postLike", explore_video_list!!.get(pos)?.youpostLiked)
                        putString("posthashtag", explore_video_list!!.get(pos)?.posthashtag)
                        putString("exploreVideo", explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile)
                        putString("exploreThumbnail", explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail)
                        putString("albumID", explore_video_list!!.get(pos)?.postAlbum!![0].exalbumID)
                        putString("subAlbumAId", explore_video_list!!.get(pos)?.postAlbum!![0].exsubalbumID)
                        putString("collection", "Remove")
                        putSerializable("explore_video_list",explore_video_list)
                        putString("postType",explore_video_list?.get(pos)?.postType)
                        exploreDetailFragment.arguments = this
                    }
                    (activity as MainActivity).navigateTo(exploreDetailFragment,exploreDetailFragment::class.java.name,true)
                }

            }, explore_video_list!!, false)
        }
        rc_all_video.setHasFixedSize(true)
        rc_all_video.adapter = exploreVideo_adapter

        pageNo = 0
        getAssignPostAlbumData(userData?.userID!!, pageNo, "Album", "", "", "", "", RestClient.apiVersion, "10",
                "", "", "", "", subAlbumId!!, "", "",
                "Video", "0", albumId!!, "0", RestClient.apiType)
    }

    private fun getListAssignPostAlbum() {

        linearLayoutManagerTrick = LinearLayoutManager(mActivity!!, LinearLayoutManager.VERTICAL, false)

        rc_all_video.layoutManager = linearLayoutManagerTrick
        exploreVideoListadapter = ExploreVideoListAdapater(mActivity!!, object : ExploreVideoListAdapater.OnItemClick {
            override fun onClicklisneter(pos: Int) {

                val exploreDetailFragment = ExploreVideoDetailFragment()
                    Bundle().apply {
                        putString("user_ID", explore_video_list!!.get(pos)?.userID)
                        putInt("pos", pos)
                        putString("username", explore_video_list!!.get(pos)?.userFirstName)
                        putString("like", explore_video_list!!.get(pos)?.postLike)
                        putString("postComment", explore_video_list!!.get(pos)?.postComment)
                        putString("postDescription", explore_video_list!!.get(pos)?.postDescription)
                        putString("view", explore_video_list!!.get(pos)?.postViews)
                        putString("userProfile", explore_video_list!!.get(pos)?.userProfilePicture)
                        putString("postID", explore_video_list!!.get(pos)?.postID)
                        putString("postLike", explore_video_list!!.get(pos)?.youpostLiked)
                        putString("posthashtag", explore_video_list!!.get(pos)?.posthashtag)
                        putString("exploreVideo", explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile)
                        putString("exploreThumbnail", explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail)
                        putString("albumID", explore_video_list!!.get(pos)?.postAlbum!![0].exalbumID)
                        putString("subAlbumAId", explore_video_list!!.get(pos)?.postAlbum!![0].exsubalbumID)
                        putString("collection", "Remove")
                        putSerializable("explore_video_list",explore_video_list)
                        putString("postType",explore_video_list?.get(pos)?.postType)
                        exploreDetailFragment.arguments = this
                    }
                    (activity as MainActivity).navigateTo(exploreDetailFragment,exploreDetailFragment::class.java.name,true)
            }
        }, explore_video_list!!, false)

        rc_all_video.setHasFixedSize(true)
        rc_all_video.adapter = exploreVideoListadapter

    }

    //Api Calling Funcation
    private fun getAssignPostAlbumData(userid: String, page: Int, tabname: String, sortby: String, publicationTime: String, tag: String,
                                       gender: String, apiVersion: String, pagesize: String, footballType: String, footballLevel: String, pitchPosition: String,
                                       footballagecatID: String, exsubalbumID: String, countryID: String, postType: String,
                                       postMediaType: String, loginuserID: String?, exalbumID:
                                       String, postID: String, apiType: String) {

        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        if (pageNo == 0) {

            relativeprogressBar.visibility = View.VISIBLE
            explore_video_list?.clear()
            exploreVideo_adapter?.notifyDataSetChanged()

        } else {

            relativeprogressBar.visibility = View.GONE
            rc_all_video.visibility = View.VISIBLE
            explore_video_list!!.add(null)
            exploreVideo_adapter?.notifyItemInserted(explore_video_list!!.size - 1)
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("userID", userid)
            jsonObject.put("page", page)
            jsonObject.put("tabname", tabname)
            jsonObject.put("sortby", sortby)
            jsonObject.put("publicationTime", publicationTime)
            jsonObject.put("tag", tag)
            jsonObject.put("gender", gender)
            jsonObject.put("apiVersion", apiVersion)
            jsonObject.put("pagesize", pagesize)
            jsonObject.put("footballType", footballType)
            jsonObject.put("footballLevel", footballLevel)
            jsonObject.put("pitchPosition", pitchPosition)
            jsonObject.put("footballagecatID", footballagecatID)
            jsonObject.put("exsubalbumID", exsubalbumID)
            jsonObject.put("countryID", countryID)
            jsonObject.put("postType", postType)
            jsonObject.put("postMediaType", postMediaType)
            jsonObject.put("loginuserID", loginuserID)
            jsonObject.put("exalbumID", exalbumID)
            jsonObject.put("postID", postID)
            jsonObject.put("apiType", apiType)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("AssignPost_List", jsonObject.toString())
        jsonArray.put(jsonObject)
        var getAssignPostAlbumModel =
                ViewModelProviders.of(mActivity!!).get(ExploreVideoModel::class.java)
        getAssignPostAlbumModel.getExploreVList(mActivity!!, false, jsonArray.toString())
                .observe(mActivity!!,
                        Observer { exploreVidelistpojo ->

                            if (exploreVidelistpojo != null && exploreVidelistpojo.isNotEmpty()) {

                                isLoading = false
                                ll_no_data_found.visibility = View.GONE
                                nointernetMainRelativelayout.visibility = View.GONE
                                relativeprogressBar.visibility = View.GONE

                                if (pageNo > 0) {

                                    explore_video_list?.removeAt(explore_video_list!!.size - 1)
                                    exploreVideo_adapter?.notifyItemRemoved(explore_video_list!!.size)
                                }

                                if (exploreVidelistpojo[0].status.equals("true", true)) {

                                    rc_all_video.visibility = View.VISIBLE
                                    if (pageNo == 0) {
                                        explore_video_list?.clear()
                                    }

                                    explore_video_list?.addAll(exploreVidelistpojo!![0]!!.data!!)
                                    exploreVideo_adapter?.notifyDataSetChanged()
                                    exploreVideoListadapter?.notifyDataSetChanged()
                                    pageNo += 1
                                    if (exploreVidelistpojo[0].data!!.size < 10) {
                                        isLastpage = true
                                    }
                                    if (!exploreVidelistpojo[0].data!!.isNullOrEmpty()) {
                                        if (exploreVidelistpojo[0].data!!.isNullOrEmpty()) {
                                            ll_no_data_found.visibility = View.VISIBLE
                                            rc_all_video.visibility = View.GONE
                                        } else {
                                            ll_no_data_found.visibility = View.GONE
                                            rc_all_video.visibility = View.VISIBLE
                                        }


                                    } else {
                                        ll_no_data_found.visibility = View.VISIBLE
                                        rc_all_video.visibility = View.GONE
                                    }
                                } else {

                                    if (explore_video_list!!.size == 0) {

                                        ll_no_data_found.visibility = View.VISIBLE
                                        rc_all_video.visibility = View.GONE
                                    } else {

                                        ll_no_data_found.visibility = View.GONE
                                        rc_all_video.visibility = View.VISIBLE
                                    }

                                }

                            } else {

                                relativeprogressBar.visibility = View.GONE
                                ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                            }

                        })
    }

}