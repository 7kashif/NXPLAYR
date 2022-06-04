package com.nxplayr.fsl.ui.fragments.friendrequest.view


import android.content.Context
import android.os.Bundle
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
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ConnectionListData
import com.nxplayr.fsl.data.model.FriendListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.bottomsheet.BottomSheetListFragment
import com.nxplayr.fsl.ui.fragments.friendrequest.adapter.RequestAdapter
import com.nxplayr.fsl.ui.fragments.friendrequest.viewmodel.FriendListModel
import com.nxplayr.fsl.ui.fragments.otheruserprofile.view.OtherUserProfileMainFragment
import com.nxplayr.fsl.ui.fragments.ownprofile.view.ProfileMainFragment
import com.nxplayr.fsl.ui.fragments.userconnection.viewmodel.ChatListModel
import com.nxplayr.fsl.util.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_receive_request.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ReceiveRequestFragment : Fragment(), BottomSheetListFragment.SelectLanguage,
    View.OnClickListener {

    var pageNo = 0
    var pageSize = 10
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var firstVisibleItemPosition: Int = 0
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var isLoading = false
    private var isLastpage = false
    var y: Int = 0
    var sessionManager: SessionManager? = null
    private var v: View? = null
    var requestAdapter: RequestAdapter? = null
    var activity1: AppCompatActivity? = null
    var tab_position: Int = 0
    var friendListData: ArrayList<FriendListData?>? = null
    var connectionListData: ArrayList<ConnectionListData>? = ArrayList()
    var userData: SignupData? = null
    var connectionId = ""
    var connectionTypeName = ""
    var uesrId = ""
    var posConne: Int = 0
    private lateinit var sendfriendlist: FriendListModel
    private lateinit var connectionListModel: ChatListModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_receive_request, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity1 = context as AppCompatActivity

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(activity1!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        if (arguments != null) {
            tab_position = arguments!!.getInt("position")
        }
        setupViewModel()
        setupUI()
        setupObserver()

        sendfriendlist.successFriend.observe(viewLifecycleOwner) { llfriendlistpojos ->

            if (llfriendlistpojos != null && llfriendlistpojos.isNotEmpty()) {
                isLoading = false
                ll_no_data_found.visibility = View.GONE
                nointernetMainRelativelayout.visibility = View.GONE
                relativeprogressBar.visibility = View.GONE

                if (pageNo > 0) {
                    friendListData!!.removeAt(friendListData!!.size - 1)
                    requestAdapter!!.notifyItemRemoved(friendListData!!.size)
                }


                if (llfriendlistpojos[0]!!.status.equals("true", true)) {
                    recyclerview.visibility = View.VISIBLE

                    if (pageNo == 0) {
                        friendListData?.clear()
                    }

                    friendListData?.addAll(llfriendlistpojos[0]!!.data!!)
                    requestAdapter?.notifyDataSetChanged()
                    pageNo += 1

                    (parentFragment as AcceptRequestFragment?)?.setTabtitle(
                        llfriendlistpojos[0]!!.count
                    )

                    if (llfriendlistpojos[0]!!.data!!.size < 10) {
                        isLastpage = true
                    }

                    if (!llfriendlistpojos[0]!!.data!!.isNullOrEmpty()) {
                        if (llfriendlistpojos[0]!!.data!!.isNullOrEmpty()) {
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

                    if (friendListData!!.isNullOrEmpty()) {
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
                    ErrorUtil.errorView(activity1!!, nointernetMainRelativelayout)
                }
            }
        }
    }

    private fun setupObserver() {
        try {
            ll_no_data_found.visibility = View.GONE
            nointernetMainRelativelayout.visibility = View.GONE
            if (pageNo == 0) {
                relativeprogressBar.visibility = View.VISIBLE
                friendListData?.clear()
                requestAdapter!!.notifyDataSetChanged()

            } else {
                relativeprogressBar.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE
                friendListData?.add(null)
                requestAdapter!!.notifyItemInserted(friendListData!!.size - 1)

            }
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            try {
                jsonObject.put("loginuserID", userData?.userID)
                when (tab_position) {
                    0 -> {
                        jsonObject.put("action", "pendingrequestlist")
                    }
                    1 -> {
                        jsonObject.put("action", "requestsendedlist")
                    }
                }
                jsonObject.put("userID", userData?.userID)
                jsonObject.put("search_keyword", "")
                jsonObject.put("friendtype", "all")
                jsonObject.put("page", pageNo)
                jsonObject.put("pagesize", "10")
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            jsonArray.put(jsonObject)
            sendfriendlist.friendApi(jsonArray.toString(), "friend_list")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupUI() {
        linearLayoutManager = LinearLayoutManager(activity)
        if (friendListData == null) {
            friendListData = ArrayList()
            requestAdapter = RequestAdapter(
                activity as MainActivity,
                friendListData,
                object : RequestAdapter.OnItemClick {

                    override fun onClicled(position: Int, from: String, view: View?) {

                        uesrId = friendListData!![position]!!.userID
                        posConne = position

                        when (from) {
                            "reqAccept" -> {
                                getAcceptFriend(position, friendListData?.get(position)?.userID)
                            }
                            "reqReject" -> {
                                getRejectFriend(position, friendListData?.get(position)?.userID)
                            }
                            "changeConneType" -> {
                                connectionListApi(view)
                            }
                            "otherserProfile" -> {
                                if (!userData?.userID?.equals(friendListData?.get(position)?.userID)!!) {
                                    var bundle = Bundle()
                                    bundle.putString(
                                        "userId",
                                        friendListData?.get(position)?.userID
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
                                        friendListData?.get(position)?.userID
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
                tab_position,
                sessionManager!!
            )
            recyclerview.layoutManager = linearLayoutManager
            recyclerview.setHasFixedSize(true)
            recyclerview.adapter = requestAdapter
            val divider =
                DividerItemDecoration(recyclerview.context, DividerItemDecoration.VERTICAL)
            divider.setDrawable(context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.line_layout
                )
            }!!)
            recyclerview.addItemDecoration(divider)
            pageNo = 0
            setupObserver()
            requestAdapter?.notifyDataSetChanged()

        }


        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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
                        setupObserver()
                    }
                }
            }
        })

        btnRetry.setOnClickListener(this)
    }

    private fun setupViewModel() {
        sendfriendlist = ViewModelProvider(this@ReceiveRequestFragment).get(FriendListModel::class.java)
        connectionListModel = ViewModelProvider(this@ReceiveRequestFragment).get(ChatListModel::class.java)
    }

    private fun getRejectFriend(position: Int, userID: String?) {
        MyUtils.showProgressDialog(activity1!!, "Wait")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("action", "rejectrequest")
            jsonObject.put("userfriendSenderID", userID)
            jsonObject.put("userfriendReceiverID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        sendfriendlist.friendApi(jsonArray.toString(), "friend_list")
        sendfriendlist.successFriend.observe(viewLifecycleOwner) { jobFunctionListpojo ->
            if (jobFunctionListpojo != null && jobFunctionListpojo.isNotEmpty()) {

                if (jobFunctionListpojo[0].status.equals("true", false)) {
                    MyUtils.dismissProgressDialog()
                    friendListData?.get(position)?.isYourRequestRejected = ("Yes");
                    friendListData?.removeAt(position);
                    requestAdapter?.notifyItemRemoved(position);
                    requestAdapter?.notifyItemChanged(position, friendListData?.size);
                } else {
                    MyUtils.dismissProgressDialog()
                    MyUtils.showSnackbar(
                        activity1!!,
                        jobFunctionListpojo[0].message,
                        llRecevie
                    )
                }

            } else {
                MyUtils.dismissProgressDialog()
                (activity as MainActivity).errorMethod()
            }
        }
    }

    private fun getAcceptFriend(position: Int, userID: String?) {
        MyUtils.showProgressDialog(activity1!!, "Wait")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("action", "acceptrequest")
            jsonObject.put("userfriendSenderID", userID)
            jsonObject.put("userfriendReceiverID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        sendfriendlist.friendApi(jsonArray.toString(), "friend_list")
        sendfriendlist.successFriend.observe(viewLifecycleOwner) { jobFunctionListpojo ->
            if (jobFunctionListpojo != null && jobFunctionListpojo.isNotEmpty()) {

                if (jobFunctionListpojo[0].status.equals("true", false)) {
                    MyUtils.dismissProgressDialog()
                    friendListData?.get(position)?.isYouSentRequest = ("Yes");
                    friendListData?.removeAt(position);
                    requestAdapter?.notifyItemRemoved(position);
                    requestAdapter?.notifyItemChanged(position, friendListData?.size);
                } else {
                    MyUtils.dismissProgressDialog()
                    MyUtils.showSnackbar(
                        activity1!!,
                        jobFunctionListpojo[0].message,
                        llRecevie
                    )
                }

            } else {
                MyUtils.dismissProgressDialog()
                (activity as MainActivity).errorMethod()
            }
        }
    }

    private fun connectionListApi(view: View?) {
        MyUtils.showProgressDialog(activity1!!, "Please wait...")
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

        connectionListModel.connectionList(jsonArray.toString())
        connectionListModel.connectionSuccess.observe(viewLifecycleOwner) { connectionListpojo ->

                if (connectionListpojo != null && connectionListpojo.isNotEmpty()) {

                    if (connectionListpojo[0].status.equals("true", false)) {
                        MyUtils.dismissProgressDialog()
                        connectionListData?.clear()
                        for (i in 0 until connectionListpojo[0].data.size) {

                            if (connectionListpojo[0].data[i].conntypeName.equals("All")) {
                                connectionListData!!.remove(connectionListpojo[0].data[i])
                            } else {
                                connectionListData!!.add(connectionListpojo[0].data[i])
                            }
                        }

                        displayFilter(connectionListData!!, userData?.userID!!, view)
//                            openConnectionList(connectionListData!!)
                    } else {
                        MyUtils.dismissProgressDialog()
                        MyUtils.showSnackbar(
                            activity1!!,
                            connectionListpojo[0].message,
                            llRecevie
                        )
                    }
                } else {
                    MyUtils.dismissProgressDialog()
                    ErrorUtil.errorMethod(llRecevie)
                }
            }


    }

    fun displayFilter(connections: ArrayList<ConnectionListData>, userID: String, v: View?) {
        val data = java.util.ArrayList<String>()
        for (i in 0 until connections.size) {
            data.add(connections[i].conntypeName)
        }

        val menupopup = Menupopup()
        val easyDialog: EasyDialog = menupopup.setMenuoption(context, data, v)
        menupopup.setListener { pos, menuname ->
            easyDialog.dismiss()

            friendListData!![posConne]!!.connectionType[0].conntypeName = menuname
            for (i in 0 until connectionListData!!.size) {
                if (menuname.equals(connectionListData!![i]!!.conntypeName, false)) {
                    if (!connectionId.equals(connectionListData!![i]!!.conntypeID)) {
                        connectionId = connectionListData!![i]!!.conntypeID
                        connectionTypeName = connectionListData!![i]!!.conntypeName
                    }
                }
            }
            setConnectionApi(connectionId, uesrId)

        }
    }

    private fun openConnectionList(data: ArrayList<ConnectionListData>) {
//        displayFilter(data, userData?.userID!!)
        var bottomSheetData = ArrayList<String>()
        bottomSheetData.clear()
        for (i in 0 until data.size) {
            bottomSheetData.add(data[i]!!.conntypeName!!)
        }
        val bottomSheet = BottomSheetListFragment()
        val bundle = Bundle()
        bundle.putString("from", "ConnectionList")
        bundle.putSerializable("data", bottomSheetData)
        bottomSheet.arguments = bundle
        bottomSheet.show(childFragmentManager!!, "List")
    }

    override fun onLanguageSelect(value: String, from: String) {
        friendListData!![posConne]!!.connectionType[0].conntypeName = value
        for (i in 0 until connectionListData!!.size) {
            if (value.equals(connectionListData!![i]!!.conntypeName, false)) {
                if (!connectionId.equals(connectionListData!![i]!!.conntypeID)) {
                    connectionId = connectionListData!![i]!!.conntypeID
                    connectionTypeName = connectionListData!![i]!!.conntypeName
                }
            }
        }
        setConnectionApi(connectionId, uesrId)
    }

    private fun setConnectionApi(connectioniD: String, uesrId: String) {
        MyUtils.showProgressDialog(activity1!!, "Wait")
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


        sendfriendlist.friendApi(jsonArray.toString(), "change_friendList")
        sendfriendlist.successFriend.observe(viewLifecycleOwner) { connectionListpojo ->
            if (connectionListpojo != null && connectionListpojo.isNotEmpty()) {

                if (connectionListpojo[0].status.equals("true", false)) {
                    MyUtils.dismissProgressDialog()
                    connectionTypeName = connectionListData!![0].conntypeName
//                                    friendListData!![0]?.connectionType = connectionListpojo[0].data?.get(0)?.connectionType!!
                    requestAdapter?.notifyDataSetChanged()

//                                    pageNo = 0
//                                    setupObserver()

                } else {
                    MyUtils.dismissProgressDialog()
                    MyUtils.showSnackbar(
                        activity1!!,
                        connectionListpojo[0].message,
                        llRecevie
                    )
                }
            } else {
                MyUtils.dismissProgressDialog()
                (activity as MainActivity).errorMethod()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id!!) {
            R.id.btnRetry -> {
                pageNo = 0
                setupObserver()

            }
        }
    }

}
