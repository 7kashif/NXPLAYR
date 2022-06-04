package com.nxplayr.fsl.ui.fragments.userfollowers.view


import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userfollowers.adapter.FollowersAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FollowingListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.otheruserprofile.view.OtherUserProfileMainFragment
import com.nxplayr.fsl.ui.fragments.ownprofile.view.ProfileMainFragment
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.FollowersModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.recyclerview
import kotlinx.android.synthetic.main.fragment_followers_list.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONObject


class FollowersListFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var mActivity: Activity? = null
    var list_followers: ArrayList<FollowingListData?>? = null
    var followersAdapter: FollowersAdapter? = null
    var tabPosition: Int = 0
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var pageNo = 0
    var pageSize = 10
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var y: Int = 0
    var userId = ""
    var fromFollow = ""
    private lateinit var followersModel: FollowersModel
    private lateinit var commonStatusModel: CommonStatusModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_followers_list, container, false)
        }
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
        if (arguments != null) {
            tabPosition = arguments!!.getInt("position")
            userId = arguments!!.getString("userID").toString()
            fromFollow = arguments!!.getString("fromData").toString()
        }
        setupViewModel()

    }

    override fun onResume() {
        super.onResume()
        if(sessionManager?.LanguageLabel!=null) {
            if (!sessionManager?.LanguageLabel?.lngNoInternet.isNullOrEmpty())
                nointernettextview.text = sessionManager?.LanguageLabel?.lngNoInternet
            if (!sessionManager?.LanguageLabel?.lngNoDataFound.isNullOrEmpty())
                nodatafoundtextview.text = sessionManager?.LanguageLabel?.lngNoDataFound.toString()
        }
        isLastpage = false
        isLoading = false
        pageNo = 0
        pageSize = 10
        visibleItemCount = 0
        totalItemCount = 0
        firstVisibleItemPosition = 0
        list_followers = null
        setupUI()
    }

    private fun setupObserver() {

        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            list_followers!!.clear()
            followersAdapter!!.notifyDataSetChanged()

        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
            list_followers!!.add(null)
            followersAdapter!!.notifyItemInserted(list_followers!!.size - 1)

        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            if (fromFollow.equals("profile")) {
                jsonObject.put("userID", userData?.userID)
            } else if (fromFollow.equals("otherUser")) {
                jsonObject.put("userID", userId)
            }
            when (tabPosition) {
                0 -> {
                    jsonObject.put("action", "followerlist")
                }
                1 -> {
                    jsonObject.put("action", "followinglist")
                }
            }
            jsonObject.put("search_keyword", "")
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        followersModel.getFollowerList(mActivity!!, false, jsonArray.toString(), "FollowingList")
            .observe(viewLifecycleOwner) { followerListPojo ->

                if (followerListPojo != null && followerListPojo.isNotEmpty()) {

                    isLoading = false
                    ll_no_data_found.visibility = View.GONE
                    nointernetMainRelativelayout.visibility = View.GONE
                    relativeprogressBar.visibility = View.GONE

                    if (pageNo > 0) {
                        list_followers!!.removeAt(list_followers!!.size - 1)
                        followersAdapter!!.notifyItemRemoved(list_followers!!.size)
                    }

                    if (followerListPojo[0].status.equals("true", false)) {
                        recyclerview.visibility = View.VISIBLE
                        if (pageNo == 0) {
                            list_followers?.clear()
                        }
                        list_followers?.addAll(followerListPojo[0].data)
                        (parentFragment as FollowersFragment?)?.setFollowingCount(followerListPojo[0].count)
                        followersAdapter?.notifyDataSetChanged()
                        pageNo += 1


                        if (followerListPojo[0].data.size < 10) {
                            isLastpage = true
                        }

                        if (!followerListPojo[0].data.isNullOrEmpty()) {
                            if (followerListPojo[0].data.isNullOrEmpty()) {
                                ll_no_data_found.visibility = View.VISIBLE
                                recyclerview.visibility = View.GONE
                            } else {
                                ll_no_data_found.visibility = View.GONE
                                recyclerview.visibility = View.VISIBLE
                            }

                        } else {
                            ll_no_data_found.visibility = View.VISIBLE
                            recyclerview.visibility = View.GONE
                        }

                    } else {
                        relativeprogressBar.visibility = View.GONE

                        if (list_followers!!.isNullOrEmpty()) {
                            ll_no_data_found.visibility = View.VISIBLE
                            recyclerview.visibility = View.GONE

                        } else {
                            ll_no_data_found.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE
                        }
                    }
                } else {
                    relativeprogressBar.visibility = View.GONE
                    ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                }
            }
    }

    private fun setupUI() {
        linearLayoutManager = LinearLayoutManager(mActivity!!)

        followingList()
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
                        setupObserver()
                    }
                }
            }
        })

        btnRetry.setOnClickListener(this)
    }

    private fun setupViewModel() {
        followersModel =
            ViewModelProvider(this@FollowersListFragment).get(FollowersModel::class.java)
        commonStatusModel =
            ViewModelProvider(this@FollowersListFragment).get(CommonStatusModel::class.java)

    }

    private fun followingList() {
        if (list_followers == null) {
            list_followers = ArrayList()
            followersAdapter = FollowersAdapter(
                activity as MainActivity,
                list_followers,
                object : FollowersAdapter.OnItemClick {
                    override fun onClicled(position: Int, from: String) {
                        when (from) {
                            "follow" -> {
                                followUserApi(position, list_followers!![position]!!.userID)
                            }
                            "unfollow" -> {
                                userUnFollowApi(position, list_followers!![position]!!.userID)
                            }
                            "otherserProfile" -> {
                                if (!userData?.userID?.equals(list_followers?.get(position)?.userID)!!) {
                                    var bundle = Bundle()
                                    bundle.putString(
                                        "userId",
                                        list_followers?.get(position)?.userID
                                    )
                                    (activity as MainActivity).navigateTo(
                                        OtherUserProfileMainFragment(),
                                        bundle,
                                        OtherUserProfileMainFragment::class.java.name,
                                        true
                                    )

                                } else {
                                    var bundle = Bundle()
                                    bundle.putString(
                                        "userId",
                                        list_followers?.get(position)?.userID
                                    )
                                    (activity as MainActivity).navigateTo(
                                        ProfileMainFragment(),
                                        bundle,
                                        ProfileMainFragment::class.java.name,
                                        true
                                    )

                                }
                            }
                        }
                    }
                },
                tabPosition
            )

            recyclerview.layoutManager = linearLayoutManager
            recyclerview.isNestedScrollingEnabled = true
            recyclerview.adapter = followersAdapter
            recyclerview.setHasFixedSize(true)
            setupObserver()
        }
    }

    private fun followUserApi(position: Int, userID: String) {

        MyUtils.showProgressDialog(activity!!, "Please wait..")
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

        commonStatusModel.getCommonStatus(mActivity!!, false, jsonArray.toString(), "userFollow")
            .observe(this@FollowersListFragment) { commonStatusPojo ->


                if (commonStatusPojo != null && commonStatusPojo.isNotEmpty()) {
                    if (commonStatusPojo[0].status.equals("true", true)) {
                        MyUtils.dismissProgressDialog()

                        list_followers?.get(position)?.isYouFollowing = "Yes"

                        setupObserver()
                        followersAdapter?.notifyDataSetChanged()

                    } else {
                        MyUtils.dismissProgressDialog()
                        MyUtils.showSnackbar(
                            activity!!,
                            commonStatusPojo[0].message,
                            ll_mainFollowersList
                        )

                    }
                } else {
                    MyUtils.dismissProgressDialog()

                    ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                }
            }

    }

    private fun userUnFollowApi(position: Int, userID: String) {

        MyUtils.showProgressDialog(activity!!, "Please wait..")
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
        commonStatusModel.getCommonStatus(mActivity!!, false, jsonArray.toString(), "userUnfollow")
            .observe(this@FollowersListFragment) { commonStatusPojo ->

                if (commonStatusPojo != null && commonStatusPojo.isNotEmpty()) {
                    if (commonStatusPojo[0].status.equals("true", true)) {

                        MyUtils.dismissProgressDialog()
                        list_followers?.get(position)?.isYouFollowing = "No"
                        setupObserver()
                        followersAdapter?.notifyDataSetChanged()
                    } else {
                        MyUtils.dismissProgressDialog()
                        MyUtils.showSnackbar(
                            activity!!,
                            commonStatusPojo[0].message,
                            ll_mainFollowersList
                        )
                    }
                } else {
                    MyUtils.dismissProgressDialog()
                    ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                }
            }

    }


    override fun onDestroyView() {
        if (v != null) {
            val parentViewGroup = v?.parent as ViewGroup?
            parentViewGroup?.removeAllViews();
        }
        super.onDestroyView()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRetry -> {
                setupObserver()
            }

        }
    }
}
