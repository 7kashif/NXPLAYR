package com.nxplayr.fsl.ui.fragments.explorepost.view

import android.content.Context
import android.content.Intent
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
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.filterfeed.view.FilterActivity
import com.nxplayr.fsl.ui.fragments.explorepost.adapter.ExploreVideoAdapater
import com.nxplayr.fsl.ui.fragments.explorepost.adapter.ExploreVideoListAdapater
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.explorepost.viewmodel.ExploreVideoModel
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.explore_view_all_video_activity.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ExploreViewAllVideoFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private var v: View? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var mActivity: AppCompatActivity? = null

    var userName: String? = null
    var getExploreType: String? = null
    var explore_video_list: ArrayList<CreatePostData?>? = ArrayList()
    var exploreVideo_adapter: ExploreVideoAdapater? = null
    var exploreVideoListadapter: ExploreVideoListAdapater? = null

    var swipeCount = 0

    private var linearLayoutManager: GridLayoutManager? = null
    private var y: Int = 0
    var pageNo = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    private var isBackFromB = false

    var footballLevel=""
    var countryID=""
    var sortby=""
    var pitchPosition=""
    var gender=""
    var footballagecatID=""
    var footballType=""
    var publicationTime=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.explore_view_all_video_activity, container, false)
        isBackFromB = false;
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(this.activity!!)
        sessionManager = context?.let { SessionManager(it) }
        userData = sessionManager?.get_Authenticate_User()

        if (arguments != null) {

            val ViewAllName = arguments?.getString("from_string", "")!!
            getExploreType = arguments?.getString("exploreType", "")!!

            txt_video_type.setText(ViewAllName)
        }

        getViewAllGridExploreData()

        rc_all_video.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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

                        getViewAllExploreDataList("trending", RestClient.apiType, userData?.userID, "ExploreVideos", getExploreType!!, "",
                                RestClient.apiVersion, "", "", pageNo, "0", "",
                                "", "0", "", "", "Video", "10")
                    }
                }
            }
        })

        swipe_refresh_layout.setOnRefreshListener(this)
        img_listView.setOnClickListener(this)
        img_expGridView.setOnClickListener(this)
        txtViewAll.setOnClickListener(this)
        btnRetry.setOnClickListener(this)
        img_expGridView2.setOnClickListener(this)
        img_Filter.setOnClickListener(this)

    }

    override fun onRefresh() {

        swipeCount += 1;
        if (swipeCount > 0) {

            exploreVideo_adapter!!.notifyDataSetChanged()

            swipe_refresh_layout.isRefreshing = false

            pageNo = 0
            getViewAllExploreDataList("trending", RestClient.apiType, userData?.userID, "ExploreVideos", getExploreType!!, "",
                    RestClient.apiVersion, "", "", pageNo, "0", "",
                    "", "0", "", "", "Video", "10")

        }
    }

    //Data View
    private fun getViewAllGridExploreData() {

        linearLayoutManager = GridLayoutManager(mActivity!!, 2)
        if (explore_video_list.isNullOrEmpty() || pageNo == 0) {

            exploreVideo_adapter = ExploreVideoAdapater(mActivity!!, object : ExploreVideoAdapater.OnItemClick {
                override fun onClicklisneter(pos: Int) {

                    if (explore_video_list!!.get(pos)?.postAlbum.isNullOrEmpty() || explore_video_list!!.get(pos)?.posthashtag.isNullOrEmpty()) {

                        var exploreDetailFragment = ExploreVideoDetailFragment()
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
                            putString("exploreVideo", explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile)
                            putString("exploreThumbnail", explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail)
                            putString("posthashtag", "null")
                            putString("albumID", "null")
                            putString("subAlbumAId", "null")
                            putString("collection", "Add")
                            putSerializable("explore_video_list",explore_video_list)
                            putString("postType",explore_video_list?.get(pos)?.postType)
                            exploreDetailFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(exploreDetailFragment, exploreDetailFragment::class.simpleName!!, true)
                    } else {

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
                            putString("AllVideo", "AddVideo")
                            putSerializable("explore_video_list",explore_video_list)
                            putString("postType",explore_video_list?.get(pos)?.postType)
                            exploreDetailFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(exploreDetailFragment, exploreDetailFragment::class.simpleName!!, true)
                    }

                }

            }, explore_video_list, false)
        }
        rc_all_video.layoutManager = linearLayoutManager
        rc_all_video.setHasFixedSize(true)
        rc_all_video.adapter = exploreVideo_adapter

        getViewAllExploreDataList("trending", RestClient.apiType, userData?.userID, "ExploreVideos", getExploreType!!, "",
                RestClient.apiVersion, "", "", pageNo, "0", "",
                "", "0", "", "", "Video", "10")

    }

    private fun getViewAllListExploreData() {

        rc_all_video.layoutManager = LinearLayoutManager(mActivity!!, LinearLayoutManager.VERTICAL, false)

        exploreVideoListadapter = ExploreVideoListAdapater(mActivity!!, object : ExploreVideoListAdapater.OnItemClick {
            override fun onClicklisneter(pos: Int) {

                if (explore_video_list!!.get(pos)?.postAlbum.isNullOrEmpty() || explore_video_list!!.get(pos)?.posthashtag.isNullOrEmpty()) {

                    var exploreDetailFragment = ExploreVideoDetailFragment()
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
                        putString("exploreVideo", explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile)
                        putString("exploreThumbnail", explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail)
                        putString("posthashtag", "null")
                        putString("albumID", "null")
                        putString("subAlbumAId", "null")
                        putString("collection", "Add")
                        putSerializable("explore_video_list",explore_video_list)
                        putString("postType",explore_video_list?.get(pos)?.postType)
                        exploreDetailFragment.arguments = this
                    }
                    (context as MainActivity).navigateTo(exploreDetailFragment, exploreDetailFragment::class.simpleName!!, true)

                } else {

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
                    (context as MainActivity).navigateTo(exploreDetailFragment, exploreDetailFragment::class.simpleName!!, true)

                }

            }

        }, explore_video_list!!, false)

        rc_all_video.setHasFixedSize(true)
        rc_all_video.adapter = exploreVideoListadapter

    }

    //Api Calling Funcation
    private fun getViewAllExploreDataList(trending: String, apiType: String, userID: String?, postType: String, sortby: String, publicationTime: String?,
                                          apiVersion: String, footballLevel: String, gender: String, pageno: Int, postID: String, footballType: String,
                                          agegroup: String, loginuserID: String, pitchPosition: String, tag: String,
                                          postMediaType: String, pagesize: String) {

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

            jsonObject.put("tabname", trending)
            jsonObject.put("apiType", apiType)
            jsonObject.put("userID", userID)
            jsonObject.put("postType", postType)
            jsonObject.put("sortby", sortby)
            jsonObject.put("publicationTime", publicationTime)
            jsonObject.put("apiVersion", apiVersion)
            jsonObject.put("footballLevel", footballLevel)
            jsonObject.put("gender", gender)
            jsonObject.put("page", pageno)
            jsonObject.put("postID", postID)
            jsonObject.put("footballType", footballType)
            jsonObject.put("agegroup", agegroup)
            jsonObject.put("loginuserID", loginuserID)
            jsonObject.put("pitchPosition", pitchPosition)
            jsonObject.put("tag", tag)
            jsonObject.put("postMediaType", postMediaType)
            jsonObject.put("pagesize", pagesize)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("VIEW_EXPLORE_LIST", jsonObject.toString())
        jsonArray.put(jsonObject)

        var getRisingModel =
                ViewModelProviders.of(mActivity!!).get(ExploreVideoModel::class.java)
        getRisingModel.getExploreVList(mActivity!!, false, jsonArray.toString())
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

                                    pageNo += 1
                                    if (exploreVidelistpojo[0].data!!.size < 10) {
                                        isLastpage = true
                                    }
                                    explore_video_list?.addAll(exploreVidelistpojo[0].data!!)
                                    exploreVideo_adapter?.notifyDataSetChanged()
                                    exploreVideoListadapter?.notifyDataSetChanged()

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

    //Click Handle
    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.txtViewAll -> {
                (context as MainActivity).onBackPressed()
            }

            R.id.img_listView -> {

                img_listView2.visibility = View.VISIBLE
                img_expGridView2.visibility = View.VISIBLE
                img_expGridView.visibility = View.GONE
                img_listView.visibility = View.GONE

                if (explore_video_list.isNullOrEmpty()) {

                    getViewAllExploreDataList("trending", RestClient.apiType, userData?.userID, "ExploreVideos", getExploreType!!, "",
                            RestClient.apiVersion, "", "", pageNo, "0", "",
                            "", "0", "", "", "Video", "10")

                } else {

                    getViewAllListExploreData()
                }

            }

            R.id.img_expGridView2 -> {

                img_expGridView2.visibility = View.GONE
                img_listView2.visibility = View.GONE
                img_listView.visibility = View.VISIBLE
                img_expGridView.visibility = View.VISIBLE

                if (explore_video_list.isNullOrEmpty()) {

                    getViewAllExploreDataList("trending", RestClient.apiType, userData?.userID, "ExploreVideos", getExploreType!!, "",
                            RestClient.apiVersion, "", "", pageNo, "0", "",
                            "", "0", "", "", "Video", "10")

                } else {

                    getViewAllGridExploreData()
                }
            }

            R.id.btnRetry -> {

                pageNo = 0
                getViewAllExploreDataList("trending", RestClient.apiType, userData?.userID, "ExploreVideos", getExploreType!!, "",
                        RestClient.apiVersion, "", "", pageNo, "0", "",
                        "", "0", "", "", "Video", "10")
            }

            R.id.img_Filter->{

                Intent(mActivity!!, FilterActivity::class.java).apply {
                    putExtra("footballLevel",footballLevel)
                    putExtra("countryID",countryID)
                    putExtra("sortby",sortby)
                    putExtra("pitchPosition",pitchPosition)
                    putExtra("gender",gender)
                    putExtra("footballagecatID",footballagecatID)
                    putExtra("footballType",footballType)
                    putExtra("publicationTime",publicationTime)
                    startActivityForResult(this,820)
                }
            }
        }
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
        getViewAllExploreDataList("trending", RestClient.apiType, userData?.userID, "ExploreVideos",sortby,publicationTime,
                RestClient.apiVersion, footballLevel, gender, pageNo, "0", footballType,
                "", "0", pitchPosition, "", "Video", "10")
    }

    override fun onResume() {
        super.onResume()
        getViewAllGridExploreData()
        isBackFromB = true;
    }

}