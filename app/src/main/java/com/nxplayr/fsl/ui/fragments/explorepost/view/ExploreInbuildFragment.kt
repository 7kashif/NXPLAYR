package com.nxplayr.fsl.ui.fragments.explorepost.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nxplayr.fsl.*
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.explorepost.viewmodel.ExploreBannerModel
import com.nxplayr.fsl.ui.fragments.explorepost.viewmodel.ExploreVideoModel
import com.nxplayr.fsl.data.model.Banner
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.explorepost.adapter.*
import com.nxplayr.fsl.util.AutoScrollViewPager
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_explore_all.*
import org.json.JSONArray
import org.json.JSONObject


class ExploreInbuildFragment : androidx.fragment.app.Fragment(), SwipeRefreshLayout.OnRefreshListener{

    private var v: View? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var mActivity: AppCompatActivity? = null

    var banner_adapter: BannerAdapter? = null
    var banner_list: ArrayList<Banner>? = ArrayList()

    var explore_feature_list: ArrayList<CreatePostData?>? = ArrayList()
    var explore_popular_list: ArrayList<CreatePostData?>? = ArrayList()
    var explore_risng_list: ArrayList<CreatePostData?>? = ArrayList()
    var explore_discover_list: ArrayList<CreatePostData?>? = ArrayList()

    var exploreFeature_adapter: ExploreFeatureAdapater? = null
    var oldExplorePopular_adapter: OldExploreVideoAdapater? = null
    var exploreRising_adapter: ExploreRisingAdapater? = null
    var exploreDiscovered_adapter: ExploreCoveredAdapater? = null

    var swipeCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       if(v==null)
       {
           v = inflater.inflate(R.layout.fragment_explore_all, container, false)
       }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if(sessionManager?.get_Authenticate_User()!=null)
        {
            userData = sessionManager?.get_Authenticate_User()
        }
        viewAllExplore()
            getBannerDataList()
            FeatureData()
            MostPopular()
            RisingStar()
            DisccoveredData()




        val viewPagerOffer = v!!.findViewById(R.id.viewPagerOffer) as AutoScrollViewPager

        banner_adapter = BannerAdapter(mActivity!!, object : BannerAdapter.OnItemClick {
            override fun onClicklisneter(pos: Int) {

            }

        }, banner_list!!)
        viewPagerOffer.adapter = banner_adapter
        viewPagerOffer.startAutoScroll();

        swipe_layout.setOnRefreshListener(this)
    }

    override fun onRefresh() {

        swipeCount += 1;
        if (swipeCount > 0) {

            oldExplorePopular_adapter!!.notifyDataSetChanged()
            exploreRising_adapter!!.notifyDataSetChanged()
            exploreDiscovered_adapter!!.notifyDataSetChanged()

            swipe_layout.isRefreshing = false

            getPopularDataList()
            getRisingDataList()
            getDiscoveredDataList()
            getBannerDataList()
        }
    }

    private fun FeatureData() {
        rc_featured.layoutManager = LinearLayoutManager(mActivity!!, LinearLayoutManager.HORIZONTAL, false)
        exploreFeature_adapter = ExploreFeatureAdapater(mActivity!!, object : ExploreFeatureAdapater.OnItemClick {
            override fun onClicklisneter(pos: Int) {

                if (explore_feature_list!!.get(pos)?.postAlbum.isNullOrEmpty() || explore_feature_list!!.get(pos)?.posthashtag.isNullOrEmpty()) {

                    var iVideoDetail =  ExploreVideoDetailFragment()
                    Bundle().apply {
                        this.putString("user_ID", explore_feature_list!!.get(pos)?.userID)
                        this.putString("username", explore_feature_list!!.get(pos)?.userFirstName)
                        this.putString("like", explore_feature_list!!.get(pos)?.postLike)
                        this.putString("postComment", explore_feature_list!!.get(pos)?.postComment)
                        this.putString("postDescription", explore_feature_list!!.get(pos)?.postDescription)
                        this.putString("view", explore_feature_list!!.get(pos)?.postViews)
                        this.putString("userProfile", explore_feature_list!!.get(pos)?.userProfilePicture)
                        this.putString("postID", explore_feature_list!!.get(pos)?.postID)
                        this.putString("postLike", explore_feature_list!!.get(pos)?.youpostLiked)
                        this.putString("exploreVideo", explore_feature_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile)
                        this.putString("exploreThumbnail", explore_feature_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail)
                        this.putString("posthashtag", "null")
                        this.putString("albumID", "null")
                        this.putString("subAlbumAId", "null")
                        this.putString("collection", "Add")
                        putSerializable("explore_video_list",explore_feature_list)
                        putString("postType", explore_feature_list!!.get(pos)?.postType)
                        iVideoDetail.arguments=this
                    }
                    (activity as MainActivity).navigateTo(iVideoDetail,iVideoDetail::class.java.name,true)

                } else {

                    var iVideoDetail =  ExploreVideoDetailFragment()
                    Bundle().apply {
                        this.putString("user_ID", explore_feature_list!!.get(pos)?.userID)
                        this.putString("username", explore_feature_list!!.get(pos)?.userFirstName)
                        this.putString("like", explore_feature_list!!.get(pos)?.postLike)
                        this.putString("postComment", explore_feature_list!!.get(pos)?.postComment)
                        this.putString("postDescription", explore_feature_list!!.get(pos)?.postDescription)
                        this.putString("view", explore_feature_list!!.get(pos)?.postViews)
                        this.putString("userProfile", explore_feature_list!!.get(pos)?.userProfilePicture)
                        this.putString("postID", explore_feature_list!!.get(pos)?.postID)
                        this.putString("postLike", explore_feature_list!!.get(pos)?.youpostLiked)
                        this.putString("posthashtag", explore_feature_list!!.get(pos)?.posthashtag)
                        this.putString("exploreVideo", explore_feature_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile)
                        this.putString("exploreThumbnail", explore_feature_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail)
                        this.putString("albumID", explore_feature_list!!.get(pos)?.postAlbum!![0].exalbumID)
                        this.putString("subAlbumAId", explore_feature_list!!.get(pos)?.postAlbum!![0].exsubalbumID)
                        this.putString("collection", "Remove")
                        putSerializable("explore_video_list",explore_feature_list)
                        putString("postType",explore_feature_list?.get(pos)?.postType)
                        iVideoDetail.arguments=this
                    }
                    (activity as MainActivity).navigateTo(iVideoDetail,iVideoDetail::class.java.name,true)

                }

            }

        }, explore_feature_list!!, false)
        rc_featured.setHasFixedSize(true)
        rc_featured.adapter = exploreFeature_adapter

        getFeatureDataList()
    }

    private fun MostPopular() {
        rc_most_popular.layoutManager = LinearLayoutManager(mActivity!!, LinearLayoutManager.HORIZONTAL, false)
        oldExplorePopular_adapter = OldExploreVideoAdapater(mActivity!!, object : OldExploreVideoAdapater.OnItemClick {
            override fun onClicklisneter(pos: Int) {
                if (explore_popular_list!!.get(pos)?.postAlbum.isNullOrEmpty() || explore_popular_list!!.get(pos)?.posthashtag.isNullOrEmpty()) {

                    var exploreDetailFragment = ExploreVideoDetailFragment()
                    Bundle().apply {
                        putString("user_ID", explore_popular_list!!.get(pos)?.userID)
                        putInt("pos", pos)
                        putString("username", explore_popular_list!!.get(pos)?.userFirstName)
                        putString("like", explore_popular_list!!.get(pos)?.postLike)
                        putString("postComment", explore_popular_list!!.get(pos)?.postComment)
                        putString("postDescription", explore_popular_list!!.get(pos)?.postDescription)
                        putString("view", explore_popular_list!!.get(pos)?.postViews)
                        putString("userProfile", explore_popular_list!!.get(pos)?.userProfilePicture)
                        putString("postID", explore_popular_list!!.get(pos)?.postID)
                        putString("postLike", explore_popular_list!!.get(pos)?.youpostLiked)
                        putString("exploreVideo", explore_popular_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile)
                        putString("exploreThumbnail", explore_popular_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail)
                        putString("posthashtag", "null")
                        putString("albumID", "null")
                        putString("subAlbumAId", "null")
                        putString("collection", "Add")
                        putSerializable("explore_video_list",explore_popular_list)
                        putString("postType", explore_popular_list!!.get(pos)?.postType)
                        exploreDetailFragment.arguments = this
                    }
                    (mActivity as MainActivity).navigateTo(exploreDetailFragment, exploreDetailFragment::class.simpleName!!, true)

                } else {

                    val exploreDetailFragment = ExploreVideoDetailFragment()
                    Bundle().apply {
                        putString("user_ID", explore_popular_list!!.get(pos)?.userID)
                        putInt("pos", pos)
                        putString("username", explore_popular_list!!.get(pos)?.userFirstName)
                        putString("like", explore_popular_list!!.get(pos)?.postLike)
                        putString("postComment", explore_popular_list!!.get(pos)?.postComment)
                        putString("postDescription", explore_popular_list!!.get(pos)?.postDescription)
                        putString("view", explore_popular_list!!.get(pos)?.postViews)
                        putString("userProfile", explore_popular_list!!.get(pos)?.userProfilePicture)
                        putString("postID", explore_popular_list!!.get(pos)?.postID)
                        putString("postLike", explore_popular_list!!.get(pos)?.youpostLiked)
                        putString("posthashtag", explore_popular_list!!.get(pos)?.posthashtag)
                        putString("exploreVideo", explore_popular_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile)
                        putString("exploreThumbnail", explore_popular_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail)
                        putString("albumID", explore_popular_list!!.get(pos)?.postAlbum!![0].exalbumID)
                        putString("subAlbumAId", explore_popular_list!!.get(pos)?.postAlbum!![0].exsubalbumID)
                        putString("collection", "Remove")
                        putSerializable("explore_video_list",explore_popular_list)
                        putString("postType", explore_popular_list!!.get(pos)?.postType)
                        exploreDetailFragment.arguments = this
                    }
                    (mActivity as MainActivity).navigateTo(exploreDetailFragment, exploreDetailFragment::class.simpleName!!, true)

                }
            }

        }, explore_popular_list, false)
        rc_most_popular.setHasFixedSize(true)
        rc_most_popular.adapter = oldExplorePopular_adapter

        getPopularDataList()
    }

    private fun DisccoveredData() {
        rc_discovered.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        exploreDiscovered_adapter = ExploreCoveredAdapater(mActivity!!, object : ExploreCoveredAdapater.OnItemClick {
            override fun onClicklisneter(pos: Int) {
                if (explore_discover_list!!.get(pos)?.postAlbum.isNullOrEmpty() || explore_discover_list!!.get(pos)?.posthashtag.isNullOrEmpty()) {

                    var exploreDetailFragment = ExploreVideoDetailFragment()
                    Bundle().apply {
                        putString("user_ID", explore_discover_list!!.get(pos)?.userID)
                        putInt("pos", pos)
                        putString("username", explore_discover_list!!.get(pos)?.userFirstName)
                        putString("like", explore_discover_list!!.get(pos)?.postLike)
                        putString("postComment", explore_discover_list!!.get(pos)?.postComment)
                        putString("postDescription", explore_discover_list!!.get(pos)?.postDescription)
                        putString("view", explore_discover_list!!.get(pos)?.postViews)
                        putString("userProfile", explore_discover_list!!.get(pos)?.userProfilePicture)
                        putString("postID", explore_discover_list!!.get(pos)?.postID)
                        putString("postLike", explore_discover_list!!.get(pos)?.youpostLiked)
                        putString("exploreVideo", explore_discover_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile)
                        putString("exploreThumbnail", explore_discover_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail)
                        putString("posthashtag", "null")
                        putString("albumID", "null")
                        putString("subAlbumAId", "null")
                        putString("collection", "Add")
                        putSerializable("explore_video_list",explore_discover_list)
                        putString("postType", explore_discover_list!!.get(pos)?.postType)
                        exploreDetailFragment.arguments = this
                    }
                    (mActivity as MainActivity).navigateTo(exploreDetailFragment, exploreDetailFragment::class.simpleName!!, true)

                } else {
                    val exploreDetailFragment = ExploreVideoDetailFragment()
                    Bundle().apply {
                        putString("user_ID", explore_discover_list!!.get(pos)?.userID)
                        putInt("pos", pos)
                        putString("username", explore_discover_list!!.get(pos)?.userFirstName)
                        putString("like", explore_discover_list!!.get(pos)?.postLike)
                        putString("postComment", explore_discover_list!!.get(pos)?.postComment)
                        putString("postDescription", explore_discover_list!!.get(pos)?.postDescription)
                        putString("view", explore_discover_list!!.get(pos)?.postViews)
                        putString("userProfile", explore_discover_list!!.get(pos)?.userProfilePicture)
                        putString("postID", explore_discover_list!!.get(pos)?.postID)
                        putString("postLike", explore_discover_list!!.get(pos)?.youpostLiked)
                        putString("posthashtag", explore_discover_list!!.get(pos)?.posthashtag)
                        putString("exploreVideo", explore_discover_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile)
                        putString("exploreThumbnail", explore_discover_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail)
                        putString("albumID", explore_discover_list!!.get(pos)?.postAlbum!![0].exalbumID)
                        putString("subAlbumAId", explore_discover_list!!.get(pos)?.postAlbum!![0].exsubalbumID)
                        putString("collection", "Remove")
                        putSerializable("explore_video_list",explore_discover_list)
                        exploreDetailFragment.arguments = this
                    }
                    (mActivity as MainActivity).navigateTo(exploreDetailFragment, exploreDetailFragment::class.simpleName!!, true)

                }

            }

        }, explore_discover_list!!, false)
        rc_discovered.setHasFixedSize(true)
        rc_discovered.adapter = exploreDiscovered_adapter

        getDiscoveredDataList()
    }

    private fun RisingStar() {

        rc_rising_start.layoutManager = LinearLayoutManager(mActivity!!, LinearLayoutManager.HORIZONTAL, false)
        rc_rising_start.setHasFixedSize(true)
        exploreRising_adapter = ExploreRisingAdapater(mActivity!!, object : ExploreRisingAdapater.OnItemClick {
            override fun onClicklisneter(pos: Int) {
                if (explore_risng_list!!.get(pos)?.postAlbum.isNullOrEmpty() || explore_risng_list!!.get(pos)?.posthashtag.isNullOrEmpty()) {

                    var exploreDetailFragment = ExploreVideoDetailFragment()
                    Bundle().apply {
                        putString("user_ID", explore_risng_list!!.get(pos)?.userID)
                        putInt("pos", pos)
                        putString("username", explore_risng_list!!.get(pos)?.userFirstName)
                        putString("like", explore_risng_list!!.get(pos)?.postLike)
                        putString("postComment", explore_risng_list!!.get(pos)?.postComment)
                        putString("postDescription", explore_risng_list!!.get(pos)?.postDescription)
                        putString("view", explore_risng_list!!.get(pos)?.postViews)
                        putString("userProfile", explore_risng_list!!.get(pos)?.userProfilePicture)
                        putString("postID", explore_risng_list!!.get(pos)?.postID)
                        putString("postLike", explore_risng_list!!.get(pos)?.youpostLiked)
                        putString("exploreVideo", explore_risng_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile)
                        putString("exploreThumbnail", explore_risng_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail)
                        putString("posthashtag", "null")
                        putString("albumID", "null")
                        putString("subAlbumAId", "null")
                        putString("collection", "Add")
                        putSerializable("explore_video_list",explore_risng_list)
                        putString("postType", explore_risng_list!!.get(pos)?.postType)
                        exploreDetailFragment.arguments = this
                    }
                    (activity as MainActivity).navigateTo(exploreDetailFragment, exploreDetailFragment::class.simpleName!!, true)

                } else {

                    val exploreDetailFragment = ExploreVideoDetailFragment()

                    Bundle().apply {
                        putString("user_ID", explore_risng_list!!.get(pos)?.userID)
                        putInt("pos", pos)
                        putString("username", explore_risng_list!!.get(pos)?.userFirstName)
                        putString("like", explore_risng_list!!.get(pos)?.postLike)
                        putString("postComment", explore_risng_list!!.get(pos)?.postComment)
                        putString("postDescription", explore_risng_list!!.get(pos)?.postDescription)
                        putString("view", explore_risng_list!!.get(pos)?.postViews)
                        putString("userProfile", explore_risng_list!!.get(pos)?.userProfilePicture)
                        putString("postID", explore_risng_list!!.get(pos)?.postID)
                        putString("postLike", explore_risng_list!!.get(pos)?.youpostLiked)
                        putString("posthashtag", explore_risng_list!!.get(pos)?.posthashtag)
                        putString("exploreVideo", explore_risng_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile)
                        putString("exploreThumbnail", explore_risng_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail)
                        putString("albumID", explore_risng_list!!.get(pos)?.postAlbum!![0].exalbumID)
                        putString("subAlbumAId", explore_risng_list!!.get(pos)?.postAlbum!![0].exsubalbumID)
                        putString("collection", "Remove")
                        putSerializable("explore_video_list",explore_risng_list)
                        putString("postType", explore_risng_list!!.get(pos)?.postType)
                        exploreDetailFragment.arguments = this
                    }
                    (activity as MainActivity).navigateTo(exploreDetailFragment, exploreDetailFragment::class.simpleName!!, true)


                }

            }

        }, explore_risng_list!!, false)
        rc_rising_start.adapter = exploreRising_adapter

        getRisingDataList()
    }

    //View All Clicks
    private fun viewAllExplore() {

        txt_view_featured.setOnClickListener {

            var exploreDetailFragment = ExploreViewAllVideoFragment()
            Bundle().apply {
                putString("from_string", "featured")
                putString("exploreType", "featured")
                exploreDetailFragment.arguments = this
            }
            (activity as MainActivity).navigateTo(exploreDetailFragment, exploreDetailFragment::class.simpleName!!, true)

        }
        txt_view_most_popular.setOnClickListener {

            var exploreDetailFragment = ExploreViewAllVideoFragment()
            Bundle().apply {
                putString("from_string", "Most Popular")
                putString("exploreType", "mostviewed")
                exploreDetailFragment.arguments = this
            }
            (activity as MainActivity).navigateTo(exploreDetailFragment, exploreDetailFragment::class.simpleName!!, true)

        }
        txt_view_rising_start.setOnClickListener {
            var exploreDetailFragment = ExploreViewAllVideoFragment()
            Bundle().apply {
                putString("from_string", "Rising Stars")
                putString("exploreType", "risingstar")
                exploreDetailFragment.arguments = this
            }
            (activity as MainActivity).navigateTo(exploreDetailFragment, exploreDetailFragment::class.simpleName!!, true)
        }
        txt_view_discovered.setOnClickListener {
            var exploreDetailFragment = ExploreViewAllVideoFragment()
            Bundle().apply {
                putString("from_string", "Undiscovered/Just Discovered")
                putString("exploreType", "justdiscovered")
                exploreDetailFragment.arguments = this
            }
            (activity as MainActivity).navigateTo(exploreDetailFragment, exploreDetailFragment::class.simpleName!!, true)
        }
    }

    //Api Call Funcations
    private fun getFeatureDataList() {
        feature_progress.visibility = View.VISIBLE
        featurenodatafoundtextview.visibility = View.GONE
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("page", "0")
            jsonObject.put("sortby", "featured")
            jsonObject.put("postType", "ExploreVideos")
            jsonObject.put("pagesize", "5")
            jsonObject.put("pitchPosition", "")
            jsonObject.put("gender", "")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("tag", "")
            jsonObject.put("postMediaType", "Video")
            jsonObject.put("footballLevel", "")
            jsonObject.put("agegroup", "")
            jsonObject.put("tabname", "trending")
            jsonObject.put("userID", userData?.userID)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("postID", "0")
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("footballType", "")
            jsonObject.put("publicationTime", "")

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("FEATURE_LIST", jsonObject.toString())
        jsonArray.put(jsonObject)

        var getPopularModel =
                ViewModelProviders.of(this@ExploreInbuildFragment).get(ExploreVideoModel::class.java)
        getPopularModel.getExploreVList(mActivity!!, false, jsonArray.toString())
                .observe(viewLifecycleOwner,
                        Observer { exploreVidelistpojo ->

                            feature_progress.visibility = View.GONE
                            if (exploreVidelistpojo != null && exploreVidelistpojo.isNotEmpty()) {
                                if (exploreVidelistpojo[0].status.equals("true", true)) {
                                    explore_feature_list?.clear()
                                    explore_feature_list?.addAll(exploreVidelistpojo!![0]!!.data!!)
                                    exploreFeature_adapter?.notifyDataSetChanged()
                                    if (explore_feature_list!!.size == 0) {

                                        featurenodatafoundtextview.visibility = View.VISIBLE
                                        rc_featured.visibility = View.GONE
                                    } else {
                                        rc_featured.visibility = View.VISIBLE
                                        featurenodatafoundtextview.visibility = View.GONE
                                    }
                                } else {

                                    if (explore_feature_list!!.size == 0) {

                                        featurenodatafoundtextview.visibility = View.VISIBLE
                                        rc_featured.visibility = View.GONE
                                    } else {
                                        rc_featured.visibility = View.VISIBLE
                                        featurenodatafoundtextview.visibility = View.GONE
                                    }

                                }

                            } else {

                                feature_progress.visibility = View.GONE
                                rc_featured.visibility = View.GONE
                                try {
                                    featurenodatafoundtextview.visibility = View.VISIBLE
                                    if (MyUtils.isInternetAvailable(mActivity!!)) {
                                        featurenodatafoundtextview.text = mActivity!!.resources.getString(R.string.error_crash_error_message)
                                    } else {
                                        featurenodatafoundtextview.text = mActivity!!.resources.getString(R.string.error_common_network)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                        })
    }

    private fun getPopularDataList() {
        most_popular_progress.visibility = View.VISIBLE
        most_popularnodatafoundtextview.visibility = View.GONE
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("postID", "0")
            jsonObject.put("agegroup", "")
            jsonObject.put("loginuserID", "0")
            jsonObject.put("postMediaType", "Video")
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("userID", userData?.userID)
            jsonObject.put("sortby", "mostviewed")
            jsonObject.put("pagesize", "5")
            jsonObject.put("footballLevel", "")
            jsonObject.put("publicationTime", "")
            jsonObject.put("tag", "")
            jsonObject.put("page", "0")
            jsonObject.put("postType", "ExploreVideos")
            jsonObject.put("gender", "")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("footballType", "")
            jsonObject.put("tabname", "trending")
            jsonObject.put("pitchPosition", "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("POPULAR_LIST", jsonObject.toString())
        jsonArray.put(jsonObject)

        var getPopularModel =
                ViewModelProviders.of(this@ExploreInbuildFragment).get(ExploreVideoModel::class.java)
        getPopularModel.getExploreVList(mActivity!!, false, jsonArray.toString())
                .observe(viewLifecycleOwner,
                        Observer { exploreVidelistpojo ->
                            most_popular_progress.visibility = View.GONE
                            if (exploreVidelistpojo != null && exploreVidelistpojo.isNotEmpty()) {

                                if (exploreVidelistpojo[0].status.equals("true", true)) {
                                    explore_popular_list?.clear()
                                    explore_popular_list?.addAll(exploreVidelistpojo!![0]!!.data!!)
                                    oldExplorePopular_adapter?.notifyDataSetChanged()
                                    if (explore_popular_list!!.size == 0) {

                                        most_popularnodatafoundtextview.visibility = View.VISIBLE
                                       rc_most_popular.visibility = View.GONE
                                    } else {

                                        most_popularnodatafoundtextview.visibility = View.GONE
                                        rc_most_popular.visibility = View.VISIBLE
                                    }
                                } else {

                                    if (explore_popular_list!!.size == 0) {

                                        most_popularnodatafoundtextview.visibility = View.VISIBLE
                                        rc_most_popular.visibility = View.GONE
                                    } else {
                                        rc_most_popular.visibility = View.VISIBLE
                                        most_popularnodatafoundtextview.visibility = View.GONE
                                    }

                                }

                            } else {
                                rc_most_popular.visibility = View.GONE
                                most_popular_progress.visibility = View.GONE
                                try {
                                    most_popularnodatafoundtextview.visibility = View.VISIBLE
                                    if (MyUtils.isInternetAvailable(mActivity!!)) {
                                        most_popularnodatafoundtextview.text = mActivity!!.resources.getString(R.string.error_crash_error_message)
                                    } else {
                                        most_popularnodatafoundtextview.text = mActivity!!.resources.getString(R.string.error_common_network)
                                    }
                                } catch (e: Exception) {

                                }
                            }
                        })
    }

    private fun getRisingDataList() {

        rising_start_progress.visibility = View.VISIBLE
        rising_startnodatafoundtextview.visibility = View.GONE
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("postID", "")
            jsonObject.put("agegroup", "")
            jsonObject.put("loginuserID", "0")
            jsonObject.put("postMediaType", "Video")
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("userID", userData?.userID)
            jsonObject.put("sortby", "risingstar")
            jsonObject.put("pagesize", "5")
            jsonObject.put("footballLevel", "")
            jsonObject.put("publicationTime", "")
            jsonObject.put("tag", "")
            jsonObject.put("page", "0")
            jsonObject.put("postType", "ExploreVideos")
            jsonObject.put("gender", "")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("tabname", "trending")
            jsonObject.put("pitchPosition", "")

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("RISING_LIST", jsonObject.toString())
        jsonArray.put(jsonObject)

        var getRisingModel =
                ViewModelProviders.of(this@ExploreInbuildFragment).get(ExploreVideoModel::class.java)
        getRisingModel.getExploreVList(mActivity!!, false, jsonArray.toString())
                .observe(viewLifecycleOwner,
                        Observer { exploreVidelistpojo ->

                            rising_start_progress.visibility = View.GONE

                            if (exploreVidelistpojo != null && exploreVidelistpojo.isNotEmpty()) {

                                if (exploreVidelistpojo[0].status.equals("true", true)) {

                                    explore_risng_list?.clear()
                                    explore_risng_list?.addAll(exploreVidelistpojo!![0]!!.data!!)
                                    exploreRising_adapter?.notifyDataSetChanged()
                                    if (explore_risng_list!!.size == 0) {

                                        rising_startnodatafoundtextview.visibility = View.VISIBLE
                                        rc_rising_start.visibility=View.GONE
                                    } else {

                                        rising_startnodatafoundtextview.visibility = View.GONE
                                        rc_rising_start.visibility=View.VISIBLE
                                    }
                                } else {

                                    if (explore_risng_list!!.size == 0) {

                                        rising_startnodatafoundtextview.visibility = View.VISIBLE
                                        rc_rising_start.visibility=View.GONE
                                    } else {

                                        rising_startnodatafoundtextview.visibility = View.GONE
                                        rc_rising_start.visibility=View.VISIBLE
                                    }

                                }

                            } else {

                                rising_start_progress.visibility = View.GONE
                                rc_rising_start.visibility=View.GONE
                                try {
                                    rising_startnodatafoundtextview.visibility = View.VISIBLE
                                    if (MyUtils.isInternetAvailable(mActivity!!)) {
                                        rising_startnodatafoundtextview.text = mActivity!!.resources.getString(R.string.error_crash_error_message)
                                    } else {
                                        rising_startnodatafoundtextview.text = mActivity!!.resources.getString(R.string.error_common_network)
                                    }
                                } catch (e: Exception) {

                                }
                            }

                        })
    }

    private fun getDiscoveredDataList() {

        discovered_progress.visibility = View.VISIBLE
        discoverednodatafoundtextview.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("tabname", "trending")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("userID", userData?.userID)
            jsonObject.put("postType", "ExploreVideos")
            jsonObject.put("sortby", "justdiscovered")
            jsonObject.put("publicationTime", "")
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("footballLevel", "")
            jsonObject.put("gender", "")
            jsonObject.put("page", "0")
            jsonObject.put("postID", "")
            jsonObject.put("footballType", "")
            jsonObject.put("agegroup", "")
            jsonObject.put("loginuserID", "0")
            jsonObject.put("pitchPosition", "")
            jsonObject.put("tag", "")
            jsonObject.put("postMediaType", "Video")
            jsonObject.put("pagesize", "5")

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("DISCOVERED_LIST", jsonObject.toString())
        jsonArray.put(jsonObject)

        var getRisingModel =
                ViewModelProviders.of(this@ExploreInbuildFragment).get(ExploreVideoModel::class.java)
        getRisingModel.getExploreVList(mActivity!!, false, jsonArray.toString())
                .observe(viewLifecycleOwner,
                        Observer { exploreVidelistpojo ->

                            discovered_progress.visibility = View.GONE

                            if (exploreVidelistpojo != null && exploreVidelistpojo.isNotEmpty()) {

                                if (exploreVidelistpojo[0].status.equals("true", true)) {

                                    explore_discover_list?.clear()
                                    explore_discover_list?.addAll(exploreVidelistpojo!![0]!!.data!!)
                                    exploreDiscovered_adapter?.notifyDataSetChanged()
                                    if (explore_discover_list!!.size == 0) {
                                        discoverednodatafoundtextview.visibility = View.VISIBLE
                                        rc_discovered.visibility=View.GONE
                                    } else {
                                        discoverednodatafoundtextview.visibility = View.GONE
                                        rc_discovered.visibility=View.VISIBLE
                                    }
                                } else {

                                    if (explore_discover_list!!.size == 0) {
                                        discoverednodatafoundtextview.visibility = View.VISIBLE
                                        rc_discovered.visibility=View.GONE
                                    } else {
                                        discoverednodatafoundtextview.visibility = View.GONE
                                        rc_discovered.visibility=View.VISIBLE
                                    }

                                }

                            } else {
                                discovered_progress.visibility = View.GONE
                                rc_discovered.visibility=View.GONE
                                try {
                                    discoverednodatafoundtextview.visibility = View.VISIBLE
                                    if (MyUtils.isInternetAvailable(mActivity!!)) {
                                        discoverednodatafoundtextview.text = mActivity!!.resources.getString(R.string.error_crash_error_message)
                                    } else {
                                        discoverednodatafoundtextview.text = mActivity!!.resources.getString(R.string.error_common_network)
                                    }
                                } catch (e: Exception) {

                                }
                            }

                        })
    }

    private fun getBannerDataList() {
        viewPager_progress.visibility = View.VISIBLE
        viewPagerodatafoundtextview.visibility = View.GONE
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("languageID", "1")
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("BANNER_LIST", jsonObject.toString())
        jsonArray.put(jsonObject)
        var getEmployementModel =
                ViewModelProviders.of(this@ExploreInbuildFragment).get(ExploreBannerModel::class.java)
        getEmployementModel.getBannerList(mActivity!!, false, jsonArray.toString())
                .observe(viewLifecycleOwner,
                        Observer { bannerlistpojo ->
                            viewPager_progress.visibility = View.GONE
                            if (bannerlistpojo != null && bannerlistpojo.isNotEmpty()) {
                                if (bannerlistpojo[0].status.equals("true", true)) {
                                    banner_list?.clear()
                                    banner_list?.addAll(bannerlistpojo!![0]!!.data!!)
                                    banner_adapter?.notifyDataSetChanged()
                                    if (banner_list!!.size == 0) {
                                        viewPagerodatafoundtextview.visibility = View.VISIBLE
                                        viewPagerOffer?.visibility=View.GONE
                                    } else {
                                        viewPagerodatafoundtextview.visibility = View.GONE
                                        viewPagerOffer?.visibility=View.VISIBLE

                                    }
                                } else {

                                    if (banner_list!!.size == 0) {
                                        viewPagerodatafoundtextview.visibility = View.VISIBLE
                                        viewPagerOffer?.visibility=View.GONE
                                    } else {
                                        viewPagerodatafoundtextview.visibility = View.GONE
                                        viewPagerOffer?.visibility=View.VISIBLE

                                    }

                                }
                            } else {

                                viewPager_progress.visibility = View.GONE
                                viewPagerOffer?.visibility=View.GONE
                                try {
                                    viewPagerodatafoundtextview.visibility = View.VISIBLE
                                    if (MyUtils.isInternetAvailable(mActivity!!)) {
                                        viewPagerodatafoundtextview.text = mActivity!!.resources.getString(R.string.error_crash_error_message)
                                    } else {
                                        viewPagerodatafoundtextview.text = mActivity!!.resources.getString(R.string.error_common_network)
                                    }
                                } catch (e: Exception) {

                                }
                            }
                        })

    }
}