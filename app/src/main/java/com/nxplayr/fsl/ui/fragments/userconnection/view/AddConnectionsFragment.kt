package com.nxplayr.fsl.ui.fragments.userconnection.view

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userconnection.adapter.AddConnectionListAdapter
import com.nxplayr.fsl.ui.fragments.userconnection.adapter.AlphaBetAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.ui.fragments.otheruserprofile.view.OtherUserProfileMainFragment
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.ui.fragments.userconnection.viewmodel.ConnectionListModel
import com.nxplayr.fsl.ui.fragments.ownprofile.view.ProfileMainFragment
import com.nxplayr.fsl.ui.fragments.friendrequest.viewmodel.FriendListModel
import com.nxplayr.fsl.ui.fragments.userconnection.viewmodel.UserContactListModel
import com.nxplayr.fsl.util.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_add_connections.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class AddConnectionsFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var list_connection: ArrayList<SuggestedFriendData?>? = null
    var addConnectionListAdapter: AddConnectionListAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var alphaBetAdapter: AlphaBetAdapter? = null
    var alphabetListData: ArrayList<String>? = ArrayList()
    var linearLayoutManageralphabet: LinearLayoutManager? = null
    var type: String = ""
    var tabPosition: Int = -1
    var mActivity: AppCompatActivity? = null
    var pageNo = 0
    var pageSize = 10
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var y: Int = 0
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    private var l: CharArray? = charArrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')
    var followersId = ""
    var idx = 0
    var conneTypeList: ArrayList<ConnectionListData>? = ArrayList()
    var connectionList: ArrayList<String>? = ArrayList()
    var connectionId = ""
    private lateinit var  followersModel: CommonStatusModel
    private lateinit var  connectionListModel: ConnectionListModel
    private lateinit var  userContactListModel: UserContactListModel
    private lateinit var  friendListModel: FriendListModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_add_connections, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mActivity = context as AppCompatActivity
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        setupViewModel()
        setupUI()
        setupObserver()
    }

    private fun setupObserver() {
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
                androidx.lifecycle.Observer { connectionListpojo ->
                    if (connectionListpojo != null && connectionListpojo.isNotEmpty()) {

                        if (connectionListpojo[0].status.equals("true", false)) {
//                                    MyUtils.dismissProgressDialog()

                            conneTypeList!!.clear()

                            for (i in 0 until connectionListpojo[0].data.size) {
                                if (connectionListpojo[0].data[i].conntypeName.equals("All")) {
                                    conneTypeList!!.remove(connectionListpojo[0].data[i])
                                } else {
                                    conneTypeList!!.add(connectionListpojo[0].data[i])
                                    connectionList = java.util.ArrayList()
                                    connectionList!!.clear()
                                    for (i in 0 until conneTypeList!!.size) {
                                        connectionList!!.add(conneTypeList!![i].conntypeName!!)
                                    }
                                }

                            }

                        } else {
                        }

                    } else {
                        ErrorUtil.errorMethod(ll_addConnection)
                    }
                })
    }

    private fun setupViewModel() {
        followersModel = ViewModelProvider(this@AddConnectionsFragment).get(CommonStatusModel::class.java)
        connectionListModel = ViewModelProvider(this@AddConnectionsFragment).get(ConnectionListModel::class.java)
        userContactListModel = ViewModelProvider(this@AddConnectionsFragment).get(
            UserContactListModel::class.java)
        friendListModel = ViewModelProvider(this@AddConnectionsFragment).get(FriendListModel::class.java)
    }

    private fun setupUI() {
        tvToolbarTitle.text = getString(R.string.add_connections)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }
        btnRetry.setOnClickListener(this)
        idx = l!!.size - 1
        setupObserver()
        linearLayoutManager = LinearLayoutManager(mActivity)
        if (list_connection == null) {
            list_connection = ArrayList()
            addConnectionListAdapter = AddConnectionListAdapter(activity as MainActivity, object : AddConnectionListAdapter.OnItemClick {

                override fun onClicklisneter(position: Int, name: String, connectionData: SuggestedFriendData?, view: View) {
                    when (name) {
                        "follow" -> {
                            followUserApi(position, connectionData!!.userID, connectionData)
                        }

                        "unfollow" -> {
                            userUnFollowApi(position, connectionData!!.userID, connectionData)
                        }
                        "connection" -> {
                            if(!userData?.userID?.equals(list_connection?.get(position)?.userID)!!)
                            {
                                var bundle = Bundle()
                                bundle.putString("userId",list_connection?.get(position)?.userID)
                                (activity as MainActivity).navigateTo(
                                    OtherUserProfileMainFragment(), bundle, OtherUserProfileMainFragment::class.java.name, true)

                            }
                            else
                            {
                                var bundle = Bundle()
                                bundle.putString("userId",list_connection?.get(position)?.userID)
                                (activity as MainActivity).navigateTo(ProfileMainFragment(), bundle, ProfileMainFragment::class.java.name, true)

                            }
                        }

                        "addFriend" -> {

                            PopupMenu(mActivity!!, view!!, connectionList!!).showPopUp(object :
                                PopupMenu.OnMenuSelectItemClickListener {
                                override fun onItemClick(item: String, pos: Int) {

                                    for (i in 0 until conneTypeList!!.size) {
                                        if (item.equals(conneTypeList!![i]!!.conntypeName, false)) {
                                            if (!connectionId.equals(conneTypeList!![i]!!.conntypeID))
                                                connectionId = conneTypeList!![i]!!.conntypeID
                                        }
                                    }
                                    addFriendApi(connectionId, connectionData!!.userID, position, connectionData)
                                }
                            })
                        }


                    }

                }

            }, list_connection
            )
            recyclerview.layoutManager = linearLayoutManager
            recyclerview.adapter = addConnectionListAdapter
            recyclerview.setHasFixedSize(true)
            recyclerview.addItemDecoration(DividerItemDecoration(activity as MainActivity, DividerItemDecoration.VERTICAL), Color.RED)

            val divider = DividerItemDecoration(recyclerview.getContext(), DividerItemDecoration.VERTICAL)
            divider.setDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.line_layout) }!!
            )
            recyclerview.addItemDecoration(divider)

            suggestedListApi()
        }

        try {
            val sectionItemDecoration = RecyclerSectionItemDecoration(resources.getDimensionPixelSize(R.dimen._30sdp), true,
                getSectionCallback(list_connection!!))
            recyclerview.addItemDecoration(sectionItemDecoration)

        } catch (e: ArrayIndexOutOfBoundsException) {

        }

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
                        suggestedListApi()
                    }
                }
            }
        })

        search_connection.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                addConnectionListAdapter!!.filter?.filter(p0.toString())
                addConnectionListAdapter?.notifyDataSetChanged()
            }

        })
    }

    fun getSectionCallback(people: ArrayList<SuggestedFriendData?>?): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            override fun isSection(position: Int): Boolean {
                return position == 0 || people?.get(position)?.userFirstName?.get(0)?.toUpperCase() != people?.get(position - 1)?.userFirstName?.get(0)?.toUpperCase()
            }

            override fun getSectionHeader(position: Int): CharSequence? {
                return people?.get(position)?.userFirstName?.toUpperCase()?.subSequence(0, 1)

            }
        }
    }

    fun suggestedListApi() {

        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            list_connection!!.clear()
            addConnectionListAdapter!!.notifyDataSetChanged()

        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
            list_connection!!.add(null)
            addConnectionListAdapter!!.notifyItemInserted(list_connection!!.size - 1)
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("userID", userData!!.userID)
            jsonObject.put("friendtype", "suggestions")
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("action", "friendlist")
            jsonObject.put("search_keyword", "")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        userContactListModel.getContactList(mActivity!!, false, jsonArray.toString(), "Contact_list")
                .observe(viewLifecycleOwner, Observer<List<SuggestedFreindListPojo>> { list_connectionPojo ->


                    if (list_connectionPojo != null && list_connectionPojo.isNotEmpty()) {
                        isLoading = false
                        ll_no_data_found.visibility = View.GONE
                        nointernetMainRelativelayout.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE

                        if (pageNo > 0) {
                            list_connection!!.removeAt(list_connection!!.size - 1)
                            addConnectionListAdapter!!.notifyItemRemoved(list_connection!!.size)
                        }

                        if (list_connectionPojo[0].status.equals("true", false)) {

                            recyclerview.visibility = View.VISIBLE
                            if (pageNo == 0) {
                                list_connection?.clear()
                            }

                            list_connection?.addAll(list_connectionPojo[0].data!!)
                            addConnectionListAdapter?.notifyDataSetChanged()
                            pageNo += 1

                            if (list_connectionPojo[0].data!!.size < 10) {
                                isLastpage = true
                            }

                            if (list_connectionPojo[0].data!!.isNullOrEmpty()) {
                                ll_no_data_found.visibility = View.VISIBLE
                                recyclerview.visibility = View.GONE
                            } else {
                                ll_no_data_found.visibility = View.GONE
                                recyclerview.visibility = View.VISIBLE
                            }

                        } else {
                            relativeprogressBar.visibility = View.GONE

                            if (list_connection!!.isNullOrEmpty()) {
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
                            ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)

                        }

                    }
                })
    }

    private fun followUserApi(position: Int, userID: String, connectionData: SuggestedFriendData) {

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

        followersModel.getCommonStatus(mActivity!!, false, jsonArray.toString(), "userFollow")
                .observe(this, Observer<List<CommonPojo>> { commonStatusPojo ->

                    MyUtils.dismissProgressDialog()

                    if (commonStatusPojo != null && commonStatusPojo.isNotEmpty()) {
                        if (commonStatusPojo[0].status.equals("true", true)) {

                            connectionData.isYouFollowing = "Yes"
                            addConnectionListAdapter?.notifyDataSetChanged()
                            MyUtils.showSnackbar(activity!!, commonStatusPojo[0].message, ll_addConnection)

                        } else {

                            MyUtils.showSnackbar(activity!!, commonStatusPojo[0].message, ll_addConnection)

                        }
                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }
                })
    }

    private fun userUnFollowApi(position: Int, userID: String, connectionData: SuggestedFriendData) {

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
                .observe(this, Observer<List<CommonPojo>> { commonStatusPojo ->

                    relativeprogressBar.visibility = View.GONE
                    recyclerview.visibility = View.VISIBLE

                    if (commonStatusPojo != null && commonStatusPojo.isNotEmpty()) {
                        if (commonStatusPojo[0].status.equals("true", true)) {

                            connectionData?.isYouFollowing = "No"
                            addConnectionListAdapter?.notifyDataSetChanged()

                            MyUtils.showSnackbar(activity!!, commonStatusPojo[0].message, ll_addConnection)


                        } else {
                            MyUtils.showSnackbar(activity!!, commonStatusPojo[0].message, ll_addConnection)
                        }
                    } else {
                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }
                })

    }

    private fun addFriendApi(connectionId: String, userID: String, position: Int, connectionData: SuggestedFriendData) {
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
                .observe(this@AddConnectionsFragment!!,
                        androidx.lifecycle.Observer { connectionListpojo ->
                            if (connectionListpojo != null && connectionListpojo.isNotEmpty()) {

                                if (connectionListpojo[0].status.equals("true", false)) {
                                    MyUtils.dismissProgressDialog()
//                                    list_connection?.remove(connectionData);
//                                    addConnectionListAdapter?.notifyDataSetChanged()

                                } else {
                                    MyUtils.dismissProgressDialog()
                                    MyUtils.showSnackbar(mActivity!!, connectionListpojo[0].message, ll_addConnection)
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
                suggestedListApi()
            }
        }
    }

}
