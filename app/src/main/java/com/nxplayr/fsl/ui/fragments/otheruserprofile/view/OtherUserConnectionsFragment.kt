package com.nxplayr.fsl.ui.fragments.otheruserprofile.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userconnection.adapter.ConnectionAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.ui.fragments.userconnection.viewmodel.ConnectionListModel
import com.nxplayr.fsl.ui.fragments.friendrequest.viewmodel.FriendListModel
import com.nxplayr.fsl.ui.fragments.userconnection.view.ConnectionsFragment
import com.nxplayr.fsl.util.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_other_user_connections.*
import kotlinx.android.synthetic.main.fragment_receive_request.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class OtherUserConnectionsFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var connection_list: ArrayList<FriendListData?>? = null
    var connectionAdapter: ConnectionAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    var alphabetListData: ArrayList<String>? = ArrayList()
    private lateinit var linearLayoutManageralphabet: LinearLayoutManager
    var pageNo = 0
    var pageSize = 10
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    private var l: CharArray? = charArrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')
    var y: Int = 0
    var idx = 0
    var from = ""
    var userId = ""
    var connectiontypeData: ArrayList<ConnectionListData>? = ArrayList()
    var connectionTypeList: ArrayList<String>? = ArrayList()
    var posConnection: Int = 0
    var connectionID = ""
    var connectionName = ""
    private lateinit var  friendListModel: FriendListModel
    private lateinit var  followersModel: CommonStatusModel
    private lateinit var  connectionListModel: ConnectionListModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_other_user_connections, container, false)
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
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        if (arguments != null) {
            from = arguments!!.getString("fromData").toString()
            userId = arguments!!.getString("userID").toString()
        }
        setupViewModel()
        setupUI()


    }

    private fun setupViewModel() {
         friendListModel =
            ViewModelProvider(this@OtherUserConnectionsFragment).get(FriendListModel::class.java)
         followersModel =
            ViewModelProvider(this@OtherUserConnectionsFragment).get(CommonStatusModel::class.java)
         connectionListModel =
            ViewModelProvider(this@OtherUserConnectionsFragment).get(ConnectionListModel::class.java)

    }

    private fun setupUI() {
        tvToolbarTitle.text = getString(R.string.connections)
        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }

        val arrayis: Array<String> = resources.getStringArray(R.array.alphabet)
        alphabetListData!!.clear()
        alphabetListData!!.addAll(arrayis)
        idx = l!!.size - 1

        connectionTypeApi()
        linearLayoutManageralphabet = LinearLayoutManager(mActivity)
        linearLayoutManager = LinearLayoutManager(mActivity)
        if (connection_list == null) {
            connection_list = ArrayList()
            connectionAdapter = ConnectionAdapter(activity as MainActivity, object : ConnectionAdapter.OnItemClick {
                override fun onClicklisneter(pos: Int, name: String, connectionData: FriendListData, view: View) {

                    posConnection = pos


                    when (name) {
                        "changeConnecType" -> {
                            PopupMenu(mActivity!!, view!!, connectionTypeList!!).showPopUp(object :
                                PopupMenu.OnMenuSelectItemClickListener {
                                override fun onItemClick(item: String, position: Int) {
                                    connectionData!!.connectionType[0].conntypeName = item
                                    for (i in 0 until connectiontypeData!!.size) {
                                        if (item.equals(connectiontypeData!![i]!!.conntypeName, false)) {
                                            if (!connectionID.equals(connectiontypeData!![i]!!.conntypeID))
                                                connectionID = connectiontypeData!![i]!!.conntypeID
                                        }
                                    }
                                    setConnectionApi(connectionID, connectionData!!.userID)
                                }
                            })
                        }

                        "addFriend" -> {
                            PopupMenu(mActivity!!, view!!, connectionTypeList!!).showPopUp(object :
                                PopupMenu.OnMenuSelectItemClickListener {
                                override fun onItemClick(item: String, position: Int) {
                                    for (i in 0 until connectiontypeData!!.size) {
                                        if (item.equals(connectiontypeData!![i]!!.conntypeName, false)) {
                                            if (!connectionID.equals(connectiontypeData!![i]!!.conntypeID))
                                                connectionID = connectiontypeData!![i]!!.conntypeID
                                        }
                                    }
                                    addFriendApi(connectionID, connectionData!!.userID, pos, connectionData)
                                }
                            })
                        }

                        "FollowUser" -> {
                            followUserApi(pos, connectionData!!.userID)
                        }
                        "UnfollowUser" -> {
                            userUnFollowApi(pos, connectionData!!.userID)
                        }
                    }

                }

            }, connection_list, 0, from)



            recyclerview.layoutManager = linearLayoutManager
            recyclerview.adapter = connectionAdapter

            recyclerview.addItemDecoration(DividerItemDecoration(activity as MainActivity, DividerItemDecoration.VERTICAL), Color.RED)

            val divider = DividerItemDecoration(recyclerview.getContext(),
                DividerItemDecoration.VERTICAL)
            divider.setDrawable(
                context?.let { ContextCompat.getDrawable(it, R.drawable.line_layout) }!!
            )
            recyclerview.addItemDecoration(divider)

            val sectionItemDecoration = RecyclerSectionItemDecoration(resources.getDimensionPixelSize(R.dimen._30sdp),
                true,
                getSectionCallback(connection_list!!))
            recyclerview.addItemDecoration(sectionItemDecoration)

            connectionListApi()
        }

        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                y = dy
                visibleItemCount = linearLayoutManager.childCount
                totalItemCount = linearLayoutManager.itemCount
                firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 10
                    ) {

                        isLoading = true
                        connectionListApi()
                    }
                }
            }
        })

        btnRetry.setOnClickListener(this)

        search_connections.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                connectionAdapter!!.filter?.filter(p0.toString())
                connectionAdapter?.notifyDataSetChanged()
            }

        })
    }

    private fun getSectionCallback(people: ArrayList<FriendListData?>): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            override fun isSection(position: Int): Boolean {
                return position == 0 || people?.get(position)?.userFirstName?.get(0)?.toUpperCase() != people?.get(position - 1)?.userFirstName?.get(0)?.toUpperCase()

//                return position == 0 || people[position]!!.userFirstName[0].toUpperCase() != people[position - 1]!!.userFirstName[0].toUpperCase()
            }

            override fun getSectionHeader(position: Int): CharSequence? {

                return people?.get(position)?.userFirstName?.toUpperCase()?.subSequence(0, 1)

            }
        }
    }

    private fun connectionListApi() {

        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            connection_list!!.clear()
            connectionAdapter!!.notifyDataSetChanged()
        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
            connection_list!!.add(null)
            connectionAdapter!!.notifyItemInserted(connection_list!!.size - 1)
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("userID", userId)
            jsonObject.put("friendtype", "all")
            jsonObject.put("action", "friendlist")
            jsonObject.put("search_keyword", "")
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        friendListModel.getFriendList(
                mActivity!!, jsonArray.toString(), "friend_list")
                .observe(viewLifecycleOwner) { friendlistpojo ->

                    if (friendlistpojo != null && friendlistpojo.isNotEmpty()) {
                        isLoading = false
                        ll_no_data_found.visibility = View.GONE
                        nointernetMainRelativelayout.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE

                        if (pageNo > 0) {
                            connection_list!!.removeAt(connection_list!!.size - 1)
                            connectionAdapter!!.notifyItemRemoved(connection_list!!.size)
                        }


                        if (friendlistpojo[0].status.equals("true", true)) {
                            recyclerview.visibility = View.VISIBLE

                            if (pageNo == 0) {
                                connection_list?.clear()
                            }

                            connection_list?.addAll(friendlistpojo[0].data!!)

                            (parentFragment as ConnectionsFragment?)?.setupTabIcons(
                                friendlistpojo[0].count?.get(
                                    0
                                )
                            )

                            connectionAdapter?.notifyDataSetChanged()
                            pageNo += 1

                            if (friendlistpojo[0].data!!.size < 10) {
                                isLastpage = true
                            }

                            if (!friendlistpojo[0].data!!.isNullOrEmpty()) {
                                if (friendlistpojo[0].data!!.isNullOrEmpty()) {
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

                            if (connection_list!!.isNullOrEmpty()) {
                                ll_no_data_found.visibility = View.VISIBLE
                                recyclerview.visibility = View.GONE

                            } else {
                                ll_no_data_found.visibility = View.GONE
                                recyclerview.visibility = View.VISIBLE

                            }
                        }
                    } else {
                        if (activity != null) {
                            relativeprogressBar.visibility = View.GONE
                            ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                        }
                    }
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
        followersModel.getCommonStatus(activity!!, false, jsonArray.toString(), "userFollow")
                .observe(this, { commonStatusPojo ->

                    MyUtils.dismissProgressDialog()

                    if (commonStatusPojo != null && commonStatusPojo.isNotEmpty()) {
                        if (commonStatusPojo[0].status.equals("true", true)) {

                            connection_list?.get(position)?.isYouFollowing = "Yes"
                            connectionAdapter?.notifyItemChanged(position)
                            connectionAdapter?.notifyItemChanged(position, connection_list!!.size)

                            MyUtils.showSnackbar(activity!!, commonStatusPojo[0].message, ll_mainOtherConne)

                        } else {

                            MyUtils.showSnackbar(activity!!, commonStatusPojo[0].message, ll_mainOtherConne)

                        }
                    } else {
                        MyUtils.dismissProgressDialog()

                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }
                })

    }

    private fun userUnFollowApi(position: Int, userID: String) {

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
        followersModel.getCommonStatus(activity!!, false, jsonArray.toString(), "userUnfollow")
                .observe(this, { commonStatusPojo ->

                    relativeprogressBar.visibility = View.GONE
                    recyclerview.visibility = View.VISIBLE

                    if (commonStatusPojo != null && commonStatusPojo.isNotEmpty()) {
                        if (commonStatusPojo[0].status.equals("true", true)) {

                            connection_list?.get(position)?.isYouFollowing = "No"
                            connectionAdapter?.notifyItemChanged(position)
                            connectionAdapter?.notifyItemChanged(position, connection_list!!.size)

                            MyUtils.showSnackbar(activity!!, commonStatusPojo[0].message, ll_mainOtherConne)

                        } else {
                            MyUtils.showSnackbar(activity!!, commonStatusPojo[0].message, ll_mainOtherConne)
                        }
                    } else {
                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }
                })
    }

    private fun connectionTypeApi() {
//        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
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
                                connectiontypeData!!.clear()

                                for (i in 0 until connectionListpojo[0].data.size) {
                                    if (connectionListpojo[0].data[i].conntypeName.equals("All")) {
                                        connectiontypeData!!.remove(connectionListpojo[0].data[i])
                                    } else {
                                        connectiontypeData!!.add(connectionListpojo[0].data[i])
                                        connectionTypeList = java.util.ArrayList()
                                        connectionTypeList!!.clear()
                                        for (i in 0 until connectiontypeData!!.size) {
                                            connectionTypeList!!.add(connectiontypeData!![i].conntypeName!!)
                                        }
                                    }

                                }

                            } else {
//                                    MyUtils.dismissProgressDialog()
                                MyUtils.showSnackbar(mActivity!!, connectionListpojo[0].message, ll_mainOtherConne)
                            }
                        } else {
//                                MyUtils.dismissProgressDialog()
                            ErrorUtil.errorMethod(ll_mainOtherConne)
                        }
                    })
    }

    private fun addFriendApi(connectionId: String, userID: String, position: Int, connectionData: FriendListData) {
        MyUtils.showProgressDialog(mActivity!!, "Wait")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("action", "sendrequest")
            jsonObject.put("userfriendSenderID", userData!!.userID)
            jsonObject.put("userfriendReceiverID", userID)
            jsonObject.put("conntypeID", connectionId)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        friendListModel.getFriendList(mActivity!!, jsonArray.toString(), "friend_list")
                .observe(this@OtherUserConnectionsFragment!!,
                    { connectionListpojo ->
                        if (connectionListpojo != null && connectionListpojo.isNotEmpty()) {

                            if (connectionListpojo[0].status.equals("true", false)) {
                                MyUtils.dismissProgressDialog()

                                connectionAdapter?.notifyDataSetChanged()

                            } else {
                                MyUtils.dismissProgressDialog()
                                MyUtils.showSnackbar(mActivity!!, connectionListpojo[0].message, ll_mainOtherConne)
                            }
                        } else {
                            MyUtils.dismissProgressDialog()
                            (activity as MainActivity).errorMethod()
                        }
                    })
    }

    private fun setConnectionApi(connectioniD: String, uesrId: String) {
        MyUtils.showProgressDialog(mActivity!!, "Wait")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("userfriendID", uesrId)
            jsonObject.put("conntypeID", connectioniD)
            jsonObject.put("edit", "Yes")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        friendListModel.getFriendList(mActivity!!, jsonArray.toString(), "change_friendList")
                .observe(this@OtherUserConnectionsFragment!!,
                    { connectionListpojo ->
                        if (connectionListpojo != null && connectionListpojo.isNotEmpty()) {

                            if (connectionListpojo[0].status.equals("true", false)) {
                                MyUtils.dismissProgressDialog()
                                connectionName = connectiontypeData!![0].conntypeName
                                connectionAdapter?.notifyDataSetChanged()

                            } else {
                                MyUtils.dismissProgressDialog()
                                MyUtils.showSnackbar(mActivity!!, connectionListpojo[0].message, ll_mainOtherConne)
                            }
                        } else {
                            MyUtils.dismissProgressDialog()
                            (activity as MainActivity).errorMethod()
                        }
                    })
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btnRetry->{
                connectionListApi()

            }
        }
    }


}