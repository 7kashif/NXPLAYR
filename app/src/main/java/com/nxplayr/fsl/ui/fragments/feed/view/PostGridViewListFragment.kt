package com.nxplayr.fsl.ui.fragments.feed.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.*
import com.nxplayr.fsl.ui.activity.post.view.DocumentActivity
import com.nxplayr.fsl.ui.activity.fullscreenvideo.view.FullScreenVideo
import com.nxplayr.fsl.ui.activity.post.view.LinkWebViewActivity
import com.nxplayr.fsl.ui.activity.fullscreenvideo.view.PhotoGallaryView
import com.nxplayr.fsl.ui.fragments.feed.adapter.PostGridViewAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.feed.viewmodel.CreatePostModel
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_post_grid_view_list.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable


class PostGridViewListFragment : Fragment() {

    var v: View? = null
    var mActivity: Activity? = null
    var postList: ArrayList<CreatePostData?>? = null
    var postGridViewAdapter: PostGridViewAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var gridViewManager: LinearLayoutManager? = null
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
    var tabPos: Int = 0
    var userID = ""
    var from = ""
    private lateinit var  createPostModel: CreatePostModel

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_post_grid_view_list, container, false)
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
            userID = arguments?.getString("userID").toString()
            from = arguments?.getString("from").toString()
        }
        setupViewModel()
        setupUI()


    }

    private fun setupUI() {
        gridViewManager = GridLayoutManager(mActivity!!, 3, GridLayoutManager.VERTICAL, false)

        if (postList == null) {
            postList = ArrayList()
            postGridViewAdapter = PostGridViewAdapter(mActivity!!, postList!!, object : PostGridViewAdapter.OnItemClick {
                override fun onClicled(position: Int, from: String) {
                    when(postList!![position]!!.postMediaType)
                    {
                        "Video"->{
                            val i = Intent(mActivity, FullScreenVideo::class.java)
                            i.putExtra("videouri", RestClient.image_base_url_posts + postList?.get(position)!!.postSerializedData.get(
                                0
                            ).albummedia[0].albummediaFile)
                            mActivity?.startActivity(i)
                            mActivity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                        }
                        "Photo"->{
                            Intent(mActivity, PhotoGallaryView::class.java).apply {
                                putExtra("photoUri",postList!![position]!!.postSerializedData[0].albummedia as Serializable)
                                mActivity?.startActivity(this)
                            }
                        }
                        "Document"->{
                            Intent(mActivity, DocumentActivity::class.java).apply {
                                putExtra("file", RestClient.image_base_url_posts + postList?.get(position)?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile)
                                mActivity?.startActivity(this)
                            }
                        }
                        "Link"->{
                            var des= if(!postList?.get(position)?.postDescription.isNullOrEmpty()){

                                postList?.get(position)?.postDescription?.split("||")?.toTypedArray()
                            }else{
                                null
                            }
                            var uri=""
                            if (des?.size!! > 1){
                                uri = if (!des!![1]?.isNullOrEmpty())
                                {
                                    Uri.parse(des[1]).toString()
                                }
                                else
                                {
                                    ""
                                }
                            }
                            if (des?.size!! > 1) {
                                Intent(context, LinkWebViewActivity::class.java).apply {
                                    putExtra("file", uri.toString())
                                    startActivity(this)
                                }
                            }

                        }
                    }

                }
            })
            recyclerview.layoutManager = gridViewManager
            recyclerview.adapter = postGridViewAdapter
            getPostList()
        }

        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                y = dy
                visibleItemCount = gridViewManager!!.childCount
                totalItemCount = gridViewManager!!.itemCount
                firstVisibleItemPosition = gridViewManager!!.findFirstVisibleItemPosition()
                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 10
                    ) {

                        isLoading = true
                        getPostList()
                    }
                }
            }
        })
        btnRetry.setOnClickListener {
            pageNo = 0
            getPostList()
        }
    }

    private fun setupViewModel() {
         createPostModel = ViewModelProvider(this@PostGridViewListFragment).get(CreatePostModel::class.java)

    }

    fun getPostList() {

        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            postList!!.clear()
            postGridViewAdapter!!.notifyDataSetChanged()

        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
            postList!!.add(null)
            postGridViewAdapter!!.notifyItemInserted(postList!!.size - 1)

        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("postID", "0")
            if (from.equals("followersDetails")) {
                jsonObject.put("loginuserID", userData!!.userID)
            } else if (from.equals("OtherUserPro")) {

                        jsonObject.put("loginuserID", userID)

            }
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            when (tabPos) {
                0 -> {
                    jsonObject.put("tabname", "my")
                    jsonObject.put("postType", "Social")
                }
                2-> {
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
                .observe(viewLifecycleOwner,{ postListPojo ->

                    if (postListPojo != null && postListPojo.isNotEmpty()) {
                        isLoading = false
                        ll_no_data_found.visibility = View.GONE
                        nointernetMainRelativelayout.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE

                        if (pageNo > 0) {
                            postList!!.removeAt(postList!!.size - 1)
                            postGridViewAdapter!!.notifyItemRemoved(postList!!.size)
                        }

                        if (postListPojo[0].status.equals("true")) {

                            recyclerview.visibility = View.VISIBLE

                            if (pageNo == 0) {
                                postList?.clear()
                            }
                            postList?.addAll(postListPojo[0].data!!)
                            postGridViewAdapter?.notifyDataSetChanged()
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
                         ErrorUtil.errorView(mActivity!!,ll_grid_viewpost)
                    }
                })

    }
}

