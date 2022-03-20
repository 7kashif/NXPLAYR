package com.nxplayr.fsl.ui.fragments.otheruserprofile.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.ui.fragments.userconnection.viewmodel.ConnectionListModel
import com.nxplayr.fsl.ui.fragments.friendrequest.viewmodel.FriendListModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.PopupMenu
import com.nxplayr.fsl.util.SessionManager
import com.google.android.material.tabs.TabLayout
import com.nxplayr.fsl.data.model.CommonPojo
import com.nxplayr.fsl.data.model.ConnectionListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.explorepost.view.ExploreFragment
import com.nxplayr.fsl.ui.fragments.feed.view.PostGridViewListFragment
import com.nxplayr.fsl.ui.fragments.otheruserprofile.adapter.OtherUserDetailViewPagerAdapter
import com.nxplayr.fsl.ui.fragments.ownprofile.view.UserFeedListsFragment
import com.nxplayr.fsl.ui.fragments.userfollowers.view.FollowersFragment
import kotlinx.android.synthetic.main.fragment_other_user_profile.*
import kotlinx.android.synthetic.main.fragment_other_user_profile.layout_connection
import kotlinx.android.synthetic.main.fragment_other_user_profile.layout_followers
import kotlinx.android.synthetic.main.fragment_other_user_profile.layout_following
import kotlinx.android.synthetic.main.fragment_other_user_profile.tv_following
import kotlinx.android.synthetic.main.fragment_other_user_profile.tv_friend
import kotlinx.android.synthetic.main.fragment_other_user_profile.tv_unfriend
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.util.*


class OtherUserProfileFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var otherUserData: SignupData? = null
    var adapter: OtherUserDetailViewPagerAdapter? = null
    var userId = ""
    var conneTypeList: ArrayList<ConnectionListData>? = ArrayList()
    var connectionList: ArrayList<String>? = ArrayList()
    var tabPosition: Int = 0
    var connectionId = ""
    var userQuickBlockID=""

    private lateinit var  signupModel: SignupModel
    private lateinit var  friendListModel: FriendListModel
    private lateinit var  connectionListModel: ConnectionListModel
    private lateinit var  commonStatusModel: CommonStatusModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_other_user_profile, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onDestroyView() {
        if (v != null) {
            val parentViewGroup = v?.parent as ViewGroup?
            parentViewGroup?.removeAllViews();
        }
        super.onDestroyView()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (arguments != null) {
            userId = arguments!!.getString("userID").toString()
            tabPosition = arguments!!.getInt("position")
        }
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        getOtherUserProfileData()
        connectionListApi()

        if (tabPosition == 0)
        {
            ll_mainOtherUserData.visibility = View.VISIBLE
            tv_ratingCount.visibility = View.GONE
            ll_mainConnect.visibility = View.VISIBLE
            ll_mainFollowFollowing.visibility = View.VISIBLE
            ll_mainProfileContact.visibility = View.GONE
            ll_mainGridLayout.visibility = View.VISIBLE
            baselineFollow.visibility = View.VISIBLE
            baselineConnect.visibility = View.VISIBLE
            main_contentCoordinator.visibility = View.VISIBLE
            ll_mainGridLayout.visibility = View.VISIBLE

        }
        else if (tabPosition == 2)
        {
            ll_mainOtherUserData.visibility = View.VISIBLE
            tv_ratingCount.visibility = View.GONE
            ll_mainConnect.visibility = View.GONE
            ll_mainFollowFollowing.visibility = View.GONE
            ll_mainGridLayout.visibility = View.GONE
            ll_mainProfileContact.visibility = View.VISIBLE
            baselineFollow.visibility = View.GONE
            baselineConnect.visibility = View.GONE
            main_contentCoordinator.visibility = View.VISIBLE
            ll_mainGridLayout.visibility = View.GONE
            appbar_layout.stopNestedScroll()
            main_contentCoordinator.stopNestedScroll()
        }

        setupViewPager(viewpagerOtherUser, "Grid")
        tablayout_OtherUserPro.setupWithViewPager(viewpagerOtherUser)
        tablayout_OtherUserPro.tabMode = TabLayout.MODE_SCROLLABLE

        layout_connection.setOnClickListener(this)
        layout_followers.setOnClickListener (this)
        layout_following.setOnClickListener (this)
        post_grid_Icon.setOnClickListener (this)
        post_list_Icon.setOnClickListener (this)
        tv_friend.setOnClickListener (this)
        tv_unfriend.setOnClickListener (this)
        tv_chat.setOnClickListener (this)
        btnRetry.setOnClickListener (this)
        tv_follow.setOnClickListener (this)
        followingTV.setOnClickListener (this)
    }

    private fun setupViewModel()
    {
        signupModel = ViewModelProvider(this@OtherUserProfileFragment).get(SignupModel::class.java)
        connectionListModel = ViewModelProvider(this@OtherUserProfileFragment).get(ConnectionListModel::class.java)
        friendListModel = ViewModelProvider(this@OtherUserProfileFragment).get(FriendListModel::class.java)
        commonStatusModel = ViewModelProvider(this@OtherUserProfileFragment).get(CommonStatusModel::class.java)
    }

    private fun addToChatList(userQuickBlockID: String, userID: String?) {
        MyUtils.showProgressDialog(mActivity!!,"Please wait")

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
        friendListModel.getFriendList(mActivity!!, jsonArray.toString(), "chatTofriend").observe(this@OtherUserProfileFragment,
            { loginPojo ->
                MyUtils.dismissProgressDialog()
                if (loginPojo != null) {

                    if (loginPojo[0].status.equals("true", true)) {

                        if (sessionManager?.getYesNoQBUser()!!){

                            if (!MyUtils.isLoginForQuickBlock){
                                if (!MyUtils.isLoginForQuickBlockChat)
                                    (mActivity as MainActivity).loginForQuickBlockChat(sessionManager?.getQbUser()!!, userQuickBlockID)
                                else
                                    (mActivity as MainActivity).getQBUser(userQuickBlockID, 1)
                            } else if(!MyUtils.isLoginForQuickBlockChat)
                                (mActivity as MainActivity).loginForQuickBlockChat(sessionManager?.getQbUser()!!, userQuickBlockID)
                            else (mActivity as MainActivity).getQBUser(userQuickBlockID, 1)
                        }
                    } else {
                        MyUtils.showSnackbar(mActivity!!,loginPojo[0].message,nointernetMainRelativelayout)
                    }
                } else {
                    MyUtils.dismissProgressDialog()
                    ErrorUtil.errorMethod(nointernetMainRelativelayout)
                }
            })

    }

    private fun getOtherUserProfileData() {
        relativeprogressBar?.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("otherUserID", userId)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        signupModel.userRegistration(mActivity!!, false, jsonArray.toString(), "other_userProfile").observe(viewLifecycleOwner,
            { loginPojo ->

                if (loginPojo != null) {
                    if (loginPojo[0].status.equals("true", true)) {
                        if (loginPojo[0].data.isNotEmpty()) {
                            relativeprogressBar?.visibility = View.GONE
                            main_contentCoordinator.visibility = View.VISIBLE
                            otherUserData = loginPojo[0].data[0]
                            if (otherUserData != null) {

                                setOtherUserData(otherUserData!!)
                            }
                        }
                    }
                } else {
                    relativeprogressBar?.visibility = View.GONE
                    main_contentCoordinator.visibility = View.GONE
                    ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                }
            })

    }

    private fun setupViewPager(viewpager: ViewPager, from: String) {
        adapter = OtherUserDetailViewPagerAdapter(childFragmentManager, userId, "OtherUserPro")
        if (from.equals("Grid", false))
        {
            adapter?.addFragment(PostGridViewListFragment(), "All")
            adapter?.addFragment(ExploreFragment(), "ProSkills")
            adapter?.addFragment(PostGridViewListFragment(), "Photos")
            adapter?.addFragment(PostGridViewListFragment(), "Videos")
            adapter?.addFragment(PostGridViewListFragment(), "Documents")
            adapter?.addFragment(PostGridViewListFragment(), "Links")
            viewpager.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
        else if (from.equals("List", false)) {
            adapter?.addFragment(UserFeedListsFragment(), "All")
            adapter?.addFragment(ExploreFragment(), "ProSkills")
            adapter?.addFragment(UserFeedListsFragment(), "Photos")
            adapter?.addFragment(UserFeedListsFragment(), "Videos")
            adapter?.addFragment(UserFeedListsFragment(), "Documents")
            adapter?.addFragment(UserFeedListsFragment(), "Links")
            viewpager.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

     @JvmName("setOtherUserData1")
     private fun setOtherUserData(otherUserData: SignupData) {

        proflie_other_userName.text = otherUserData.userFirstName + " " + otherUserData.userLastName
        tv_userProile.text = otherUserData.userBio
        Tv_connections.text = otherUserData.totalFriendCount
        Tv_followers.text = otherUserData.totalFollowerCount
        tv_following.text = otherUserData.totalFollowingCount
        profile_otheruserImage.setImageURI(RestClient.image_base_url_users + otherUserData.userProfilePicture)
        image_otherUserCover.setImageURI(RestClient.image_base_url_users + otherUserData.userCoverPhoto)
        tv_mobileNum.text = otherUserData.userCountryCode + " " + otherUserData.userMobile
        tv_emailAddress.text = otherUserData.userEmail
        if (!otherUserData?.userQBoxID.isNullOrEmpty())
            userQuickBlockID = otherUserData?.userQBoxID!!

        var mDrawable = resources.getDrawable(R.drawable.friend_icon_big_connection)
        mDrawable.setColorFilter(ContextCompat.getColor(mActivity!!, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
        tv_friend.setCompoundDrawablesRelativeWithIntrinsicBounds(mDrawable, null, null, null)
        tv_unfriend.setCompoundDrawablesRelativeWithIntrinsicBounds(mDrawable, null, null, null)

        if (otherUserData!!.isYourFriend == "Yes" ) {
            tv_unfriend.visibility = View.VISIBLE
            tv_friend.visibility = View.GONE
            tv_chat.visibility=View.VISIBLE
        } else {
            tv_unfriend.visibility = View.GONE
            tv_friend.visibility = View.VISIBLE
            tv_chat.visibility=View.GONE
        }
        if (otherUserData.isYouFollowing == "Yes") {
            followingTV.visibility = View.VISIBLE
            tv_follow.visibility = View.GONE
        } else {
            tv_follow.visibility = View.VISIBLE
            followingTV.visibility = View.GONE

        }

        tv_ratingCount.text = "(0)"

    }

    private fun connectionListApi() {
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
                .observe(viewLifecycleOwner,
                    { connectionListpojo ->
                        if (connectionListpojo != null && connectionListpojo.isNotEmpty()) {

                            if (connectionListpojo[0].status.equals("true", false)) {
                                conneTypeList!!.clear()

                                for (i in connectionListpojo[0].data.indices) {
                                    if (connectionListpojo[0].data[i].conntypeName.equals("All")) {
                                        conneTypeList!!.remove(connectionListpojo[0].data[i])
                                    } else {
                                        conneTypeList!!.add(connectionListpojo[0].data[i])
                                        connectionList = ArrayList()
                                        connectionList!!.clear()
                                        for (i in 0 until conneTypeList!!.size) {
                                            connectionList!!.add(conneTypeList!![i].conntypeName!!)
                                        }
                                    }

                                }

                            }

                        } else {
                            ErrorUtil.errorMethod(ll_mainOtherUser)
                        }
                    })

    }

    private fun addFriendApi(connectionId: String, userId: String) {
        MyUtils.showProgressDialog(mActivity!!, "Wait")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("action", "sendrequest")
            jsonObject.put("userfriendSenderID", userData!!.userID)
            jsonObject.put("userfriendReceiverID", userId)
            jsonObject.put("conntypeID", connectionId)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        friendListModel.getFriendList(mActivity!!, jsonArray.toString(), "friend_list")
                .observe(this@OtherUserProfileFragment!!,
                    { connectionListpojo ->
                        if (connectionListpojo != null && connectionListpojo.isNotEmpty()) {

                            if (connectionListpojo[0].status.equals("true", false)) {
                                MyUtils.dismissProgressDialog()
                                tv_unfriend.visibility = View.VISIBLE
                                tv_friend.visibility = View.GONE

                            } else {
                                MyUtils.dismissProgressDialog()
                                MyUtils.showSnackbar(mActivity!!, connectionListpojo[0].message, ll_mainOtherUser)
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
            jsonObject.put("userID", userId)
            jsonObject.put("action", "unfriend")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        friendListModel.getFriendList(mActivity!!, jsonArray.toString(), "friend_list")
                .observe(this@OtherUserProfileFragment!!,
                    { cancleRequestpojo ->
                        if (cancleRequestpojo != null && cancleRequestpojo.isNotEmpty()) {
                            if (cancleRequestpojo[0].status.equals("true", false)) {
                                MyUtils.dismissProgressDialog()
                                tv_unfriend.visibility = View.GONE
                                tv_friend.visibility = View.VISIBLE

                            } else {
                                MyUtils.dismissProgressDialog()
                                MyUtils.showSnackbar(mActivity!!, cancleRequestpojo[0].message, ll_mainOtherUser)
                            }

                        } else {
                            MyUtils.dismissProgressDialog()
                            (activity as MainActivity).errorMethod()
                        }
                    })
    }

    private fun followUserApi(userID: String) {

        MyUtils.showProgressDialog(mActivity!!, "Please wait..")
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
                .observe(this@OtherUserProfileFragment, Observer<List<CommonPojo>> { commonStatusPojo ->

                    MyUtils.dismissProgressDialog()

                    if (commonStatusPojo != null && commonStatusPojo.isNotEmpty()) {
                        if (commonStatusPojo[0].status.equals("true", true)) {

                            tv_follow.visibility = View.GONE
                            followingTV.visibility = View.VISIBLE

                            MyUtils.showSnackbar(mActivity!!, commonStatusPojo[0].message, ll_mainOtherUser)

                        } else {

                            MyUtils.showSnackbar(mActivity!!, commonStatusPojo[0].message, ll_mainOtherUser)

                        }
                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                    }
                })

    }

    private fun unfollowUserApi(userID: String) {
        MyUtils.showProgressDialog(mActivity!!, "Please wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("action", "unfollow")
            jsonObject.put("userfollowerFollowingID",userID)
            jsonObject.put("userfollowerFollowerID", userData!!.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        commonStatusModel.getCommonStatus(mActivity!!, false, jsonArray.toString(), "userFollow")
                .observe(this@OtherUserProfileFragment, Observer { commonStatusPojo ->

                    MyUtils.dismissProgressDialog()

                    if (commonStatusPojo != null && commonStatusPojo.isNotEmpty()) {
                        if (commonStatusPojo[0].status.equals("true", true)) {

                            followingTV.visibility = View.GONE
                            tv_follow.visibility = View.VISIBLE

                            MyUtils.showSnackbar(mActivity!!, commonStatusPojo[0].message, ll_mainOtherUser)

                        } else {

                            MyUtils.showSnackbar(mActivity!!, commonStatusPojo[0].message, ll_mainOtherUser)

                        }
                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                    }
                })

    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.layout_connection->{
                if (otherUserData!!.isYourFriend == "Yes") {
                    var bundle = Bundle()
                    bundle.putString("fromData", "otherUser")
                    bundle.putString("userID", userId)
                    (activity as MainActivity).navigateTo(OtherUserConnectionsFragment(), bundle, OtherUserConnectionsFragment::class.java.name, true)
                }
            }
            R.id.layout_followers->{
                var bundle = Bundle()
                bundle.putString("fromData", "otherUser")
                bundle.putString("userID", userId)
                (activity as MainActivity).navigateTo(FollowersFragment(), bundle, FollowersFragment::class.java.name, true)

            }
            R.id.layout_following->{
                var bundle = Bundle()
                bundle.putInt("tabposition", 1)
                bundle.putString("fromData", "otherUser")
                bundle.putString("userID", userId)
                (activity as MainActivity).navigateTo(FollowersFragment(), bundle, FollowersFragment::class.java.name, true)

            }
            R.id.post_grid_Icon->{
                setupViewPager(viewpagerOtherUser, "Grid")
                post_grid_Icon.setImageResource(R.drawable.thumb_view_selected)
                post_list_Icon.setImageResource(R.drawable.list_view_unselected)

            }
            R.id.post_list_Icon->{
                setupViewPager(viewpagerOtherUser, "List")
                post_grid_Icon.setImageResource(R.drawable.thumb_view_unselected)
                post_list_Icon.setImageResource(R.drawable.list_view_selected)

            }
            R.id.tv_friend->{

                PopupMenu(mActivity!!, tv_friend!!, connectionList!!).showPopUp(object :
                    PopupMenu.OnMenuSelectItemClickListener {
                    override fun onItemClick(item: String, pos: Int) {

                        for (i in 0 until conneTypeList!!.size) {
                            if (item.equals(conneTypeList!![i]!!.conntypeName, false)) {
                                if (!connectionId.equals(conneTypeList!![i]!!.conntypeID))
                                    connectionId = conneTypeList!![i]!!.conntypeID
                            }
                        }
                        addFriendApi(connectionId, userId)
                    }
                })
            }
            R.id.tv_unfriend->{
                cancleRequestApi(userId)

            }
            R.id.tv_chat->{
                addToChatList(userQuickBlockID,userId!!)

            }
            R.id.btnRetry->{
                getOtherUserProfileData()
            }
            R.id.tv_follow->{
                followUserApi(userId)
            }
            R.id.followingTV->{
                unfollowUserApi(userId)
            }

        }
    }

}