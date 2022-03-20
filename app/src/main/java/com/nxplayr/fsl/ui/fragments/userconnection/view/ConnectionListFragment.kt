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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userconnection.adapter.AlphaBetAdapter
import com.nxplayr.fsl.ui.fragments.userconnection.adapter.ConnectionAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.ui.fragments.otheruserprofile.view.OtherUserProfileMainFragment
import com.nxplayr.fsl.ui.fragments.bottomsheet.PrivacyBottomSheetFragment
import com.nxplayr.fsl.ui.fragments.postcomment.view.RepostReasonFragment
import com.nxplayr.fsl.ui.fragments.ownprofile.view.ProfileMainFragment
import com.nxplayr.fsl.ui.fragments.blockeduser.viewmodel.BlockUserListModel
import com.nxplayr.fsl.ui.fragments.friendrequest.viewmodel.FriendListModel
import com.nxplayr.fsl.util.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_connection_list.*
import kotlinx.android.synthetic.main.fragment_other_user_profile.*
import kotlinx.android.synthetic.main.fragment_receive_request.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.text.ParseException


class ConnectionListFragment : Fragment(), PrivacyBottomSheetFragment.selectPrivacy,View.OnClickListener {

    private var v: View? = null
    var connection_list: ArrayList<FriendListData?>?  = ArrayList()
    var connectionAdapter: ConnectionAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    var list_bottomsheet: ArrayList<String>? = ArrayList()
    var alphaBetAdapter: AlphaBetAdapter? = null
    var alphabetListData: ArrayList<String>? = ArrayList()
    private lateinit var linearLayoutManageralphabet: LinearLayoutManager
    var type: String = ""
    var mActivity: AppCompatActivity? = null
    var pageNo = 0
    var pageSize = 10
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var firstVisibleItemPosition: Int = 0
    var y: Int = 0
    var idx = 0
    private var l: CharArray? = charArrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')
    private var isLoading = false
    private var isLastpage = false
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var tab_position: Int = 0
    var userId = ""
    var blockPos: Int = 0
    var count = ""
    var from = ""
    var userIdConnect = ""
    var _areLecturesLoaded = false
    private lateinit var connectionModel : FriendListModel
    private lateinit var blockUserListModel : BlockUserListModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_connection_list, container, false)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.visibility=View.GONE
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (arguments != null) {
            tab_position = arguments!!.getInt("position", 0)
            from = arguments!!.getString("fromData").toString()
            userIdConnect = arguments!!.getString("userID").toString()
        }

        setupViewModel()
        setupUI()
        setupObserver()
    }

    private fun setupObserver() {

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
            if (from.equals("profile")) {
                jsonObject.put("loginuserID", userData?.userID)
                jsonObject.put("userID", userData?.userID)
            }
            when (tab_position) {
                0 -> {
                    jsonObject.put("friendtype", "all")
                }
                1 -> {
                    jsonObject.put("friendtype", "Friends")
                    jsonObject.put("conntypeID", "3")
                }
                2 -> {
                    jsonObject.put("friendtype", "Acquaintances")
                    jsonObject.put("conntypeID", "4")
                }
                3 -> {
                    jsonObject.put("friendtype", "Professionals")
                    jsonObject.put("conntypeID", "2")
                }
            }
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
        connectionModel.getFriendList(
            mActivity!!, jsonArray.toString(), "friend_list")
            .observe(viewLifecycleOwner,{ friendlistpojo ->

                if (friendlistpojo != null && friendlistpojo.isNotEmpty()) {
                    isLoading = false
                    ll_no_data_found.visibility = View.GONE
                    nointernetMainRelativelayout.visibility = View.GONE
                    relativeprogressBar.visibility = View.GONE

                    if (pageNo > 0) {
                        connection_list?.removeAt(connection_list!!.size - 1)
                        connectionAdapter?.notifyItemRemoved(connection_list!!.size)
                    }

                    if (friendlistpojo[0].status.equals("true", true)) {
                        recyclerview.visibility = View.VISIBLE

                        if (pageNo == 0) {
                            connection_list?.clear()
                        }

                        connection_list?.addAll(friendlistpojo[0].data!!)
                        connectionAdapter?.notifyDataSetChanged()
                        (parentFragment as ConnectionsFragment?)?.setupTabIcons(friendlistpojo!![0]!!.count.get(0))

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
                        (parentFragment as ConnectionsFragment?)?.setupTabIcons(friendlistpojo!![0].count?.get(0))

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
            })
    }

    private fun setupUI() {
        val arrayis: Array<String> = resources.getStringArray(R.array.alphabet)
        alphabetListData!!.clear()
        alphabetListData!!.addAll(arrayis)
        idx = l!!.size - 1

        linearLayoutManageralphabet = LinearLayoutManager(mActivity)
        linearLayoutManager = LinearLayoutManager(mActivity)
        //  if (connection_list == null) {
        connectionAdapter = ConnectionAdapter(activity as MainActivity, object : ConnectionAdapter.OnItemClick {
            override fun onClicklisneter(pos: Int, name: String, connectionData: FriendListData, v: View) {
                when (name) {
                    "profile" -> {
                        if (!userData?.userID?.equals(connection_list?.get(pos)?.userID)!!) {
                            var bundle = Bundle()
                            bundle.putString("userId", connection_list?.get(pos)?.userID)
                            (activity as MainActivity).navigateTo(OtherUserProfileMainFragment(), bundle, OtherUserProfileMainFragment::class.java.name, true)

                        } else {
                            var bundle = Bundle()
                            bundle.putString("userId", connection_list?.get(pos)?.userID)
                            (activity as MainActivity).navigateTo(ProfileMainFragment(), bundle, ProfileMainFragment::class.java.name, true)

                        }
                    }
                    "connectionType" -> {
                        displayFilter(v, connection_list?.get(0)?.userID!!, pos)
                    }
                    else -> {
                        getPrivacy()
                        userId = connectionData.userID
                        blockPos = pos
                    }

                }


            }

        }, connection_list, tab_position, from)

        recyclerview.layoutManager = linearLayoutManager
        recyclerview.adapter = connectionAdapter

        recyclerview.addItemDecoration(DividerItemDecoration(activity as MainActivity, DividerItemDecoration.VERTICAL), Color.RED)
        val divider = DividerItemDecoration(recyclerview.getContext(),
            DividerItemDecoration.VERTICAL)
        divider.setDrawable(
            context?.let { ContextCompat.getDrawable(it, R.drawable.line_layout) }!!
        )
        recyclerview.addItemDecoration(divider)
        if(connection_list?.isNullOrEmpty()!!)
        {
            val sectionItemDecoration = RecyclerSectionItemDecoration(resources.getDimensionPixelSize(R.dimen._30sdp),
                true,
                getSectionCallback(connection_list!!))
            recyclerview.addItemDecoration(sectionItemDecoration)
        }

        if (connection_list.isNullOrEmpty() || pageNo == 0) {
            pageNo = 0
            isLastpage = false
            isLoading = false
            setupObserver()
        }

        //  }

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
                        setupObserver()
                    }
                }
            }
        })
        btnRetry.setOnClickListener (this)
        search_connection.addTextChangedListener(object : TextWatcher {
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

    private fun setupViewModel() {
         connectionModel = ViewModelProvider(this@ConnectionListFragment).get(FriendListModel::class.java)
         blockUserListModel = ViewModelProvider(this@ConnectionListFragment).get(BlockUserListModel::class.java)

    }

    override fun onDestroyView() {
        if (v != null) {
            val parentViewGroup = v?.parent as ViewGroup?
            parentViewGroup?.removeAllViews();
        }
        super.onDestroyView()
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

    private fun getPrivacy() {
        val privacyList: ArrayList<CreatePostPrivacyPojo> = ArrayList()
        privacyList.clear()
        privacyList.add(CreatePostPrivacyPojo(resources.getString(R.string.send_message), R.drawable.popup_send_messages_icon))
        privacyList.add(CreatePostPrivacyPojo(resources.getString(R.string.disconnect), +R.drawable.connection_popup_disconnect))
        privacyList.add(CreatePostPrivacyPojo(resources.getString(R.string.block_this_user), R.drawable.connection_popup_block))
        privacyList.add(CreatePostPrivacyPojo(resources.getString(R.string.report), R.drawable.popup_report_icon))
        openBottomSheet(privacyList)
    }

    private fun openBottomSheet(data: ArrayList<CreatePostPrivacyPojo>) {

        val bottomSheet = PrivacyBottomSheetFragment()
        val bundle = Bundle()
        bundle.putSerializable("data", data as Serializable)
        bottomSheet.arguments = bundle
        bottomSheet.show(childFragmentManager, "List")
    }

    override fun setPrivacy(data: CreatePostPrivacyPojo, position: Int) {
        when (position) {
            0 -> {
                var  userQuickBlockID =  if (!connection_list!![position]?.userQBoxID.isNullOrEmpty())
                    connection_list!![position]?.userQBoxID!!
                else
                    return

                       addToChatList(userQuickBlockID,connection_list?.get(position)?.userID!!)
            }
            1 -> {
                disconnectUserApi(userId)
            }
            2 -> {
                blockUserApi(userId)
            }
            3 -> {
                var repostReasonFragment = RepostReasonFragment()
                Bundle().apply {
                    putString("fromReport", "friendReport")
                    putString("userfriendId", userId)
                    repostReasonFragment.arguments = this
                }
                (activity as MainActivity).navigateTo(repostReasonFragment, repostReasonFragment::class.java.name, true)

            }

        }
    }

    fun displayFilter(v: View?, userID: String, pos1: Int) {
        val data = java.util.ArrayList<String>()
        data.add("Friends")
        data.add("Acquaintances")
        data.add("Professionals")

        val menupopup = Menupopup()
        val easyDialog: EasyDialog = menupopup.setMenuoption(mActivity, data, v)
        menupopup.setListener { pos, menuname ->
            easyDialog.dismiss()
            when(menuname)
            {

                "Friends" -> {
                    setConnectionApi("3", userID, pos1, menuname)

                }
                "Acquaintances" -> {
                    setConnectionApi("4", userID, pos1, menuname)

                }
                "Professionals" -> {
                    setConnectionApi("2", userID, pos1, menuname)

                }
            }
        }
    }

    private fun blockUserApi(userId: String) {
        MyUtils.showProgressDialog(mActivity!!, "Wait")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("userID", userId)
            jsonObject.put("action", "add")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        blockUserListModel.getBlockUserList(mActivity!!, false, jsonArray.toString())
                .observe(this@ConnectionListFragment!!,
                        androidx.lifecycle.Observer { disconnectUserpojo ->
                            if (disconnectUserpojo != null && disconnectUserpojo.isNotEmpty()) {
                                if (disconnectUserpojo[0].status.equals("true", false)) {
                                    MyUtils.dismissProgressDialog()
                                    pageNo = 0
                                    setupObserver()
                                } else {
                                    MyUtils.dismissProgressDialog()
                                    MyUtils.showSnackbar(mActivity!!, disconnectUserpojo[0].message, ll_mainConnectionList)
                                }

                            } else {
                                MyUtils.dismissProgressDialog()
                                (activity as MainActivity).errorMethod()
                            }
                        })
    }

    private fun disconnectUserApi(userId: String) {
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

        connectionModel.getFriendList(mActivity!!, jsonArray.toString(), "friend_list")
                .observe(this@ConnectionListFragment!!,
                    { disconnectUserpojo ->
                        if (disconnectUserpojo != null && disconnectUserpojo.isNotEmpty()) {
                            if (disconnectUserpojo[0].status.equals("true", false)) {
                                MyUtils.dismissProgressDialog()
                                pageNo = 0
                                setupObserver()
                            } else {
                                MyUtils.dismissProgressDialog()
                                MyUtils.showSnackbar(mActivity!!, disconnectUserpojo[0].message, ll_mainConnectionList)
                            }

                        } else {
                            MyUtils.dismissProgressDialog()
                            (activity as MainActivity).errorMethod()
                        }
                    })
    }

    private fun setConnectionApi(connectioniD: String, uesrId: String, pos: Int, menuname: String)
    {
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

        connectionModel.getFriendList(mActivity!!, jsonArray.toString(), "change_friendList")
                .observe(this@ConnectionListFragment!!,
                        androidx.lifecycle.Observer { connectionListpojo ->
                            if (connectionListpojo != null && connectionListpojo.isNotEmpty()) {

                                if (connectionListpojo[0].status.equals("true", false)) {
                                    MyUtils.dismissProgressDialog()
                                    if (parentFragment != null && parentFragment is ConnectionsFragment) {
                                        (parentFragment as ConnectionsFragment?)?.setupTabIcons(connectionListpojo!![0]!!.counts!!.get(0).all!![0])
                                    }
                                    when (tab_position) {
                                        0 -> {
                                            connection_list?.get(pos)?.connectionType = connectionListpojo!![0].data!![0]!!.connectionType
                                            connectionAdapter?.notifyItemChanged(pos)
                                        }
                                        1 -> {
                                            if (connection_list?.get(pos)?.connectionType?.equals(connectionListpojo!![0].data!![0]!!.connectionType)!!) {
                                                connection_list?.get(pos)?.connectionType = connectionListpojo!![0].data!![0]!!.connectionType
                                                connectionAdapter?.notifyItemChanged(pos)


                                            } else {
                                                if (connection_list?.size!! > 1) {
                                                    connection_list?.removeAt(pos);
                                                    connectionAdapter?.notifyItemRemoved(pos);
                                                    connectionAdapter?.notifyItemChanged(pos, connection_list?.size);

                                                } else {
                                                    pageNo = 0
                                                    setupObserver()
                                                }
                                                if (parentFragment != null && parentFragment is ConnectionsFragment) {
                                                    pageNo = 0
                                                    (parentFragment as ConnectionsFragment).updateViewPager()
                                                }
                                            }


                                        }
                                        2 -> {
                                            if (connection_list?.get(pos)?.connectionType?.equals(connectionListpojo!![0].data!![0]!!.connectionType)!!) {
                                                connection_list?.get(pos)?.connectionType = connectionListpojo!![0].data!![0]!!.connectionType
                                                connectionAdapter?.notifyItemChanged(pos)

                                            } else {
                                                if (connection_list?.size!! > 1) {
                                                    connection_list?.removeAt(pos);
                                                    connectionAdapter?.notifyItemRemoved(pos);
                                                    connectionAdapter?.notifyItemChanged(pos, connection_list?.size);

                                                } else {
                                                    pageNo = 0
                                                    setupObserver()
                                                }
                                                if (parentFragment != null && parentFragment is ConnectionsFragment) {
                                                    pageNo = 0
                                                    (parentFragment as ConnectionsFragment).updateViewPager()
                                                }
                                            }
                                        }
                                        3 -> {
                                            if (connection_list?.get(pos)?.connectionType?.equals(connectionListpojo!![0].data!![0]!!.connectionType)!!) {
                                                connection_list?.get(pos)?.connectionType = connectionListpojo!![0].data!![0]!!.connectionType
                                                connectionAdapter?.notifyItemChanged(pos)

                                            } else {
                                                if (connection_list?.size!! > 1) {
                                                    connection_list?.removeAt(pos);
                                                    connectionAdapter?.notifyItemRemoved(pos);
                                                    connectionAdapter?.notifyItemChanged(pos, connection_list?.size);

                                                } else {
                                                    pageNo = 0
                                                    setupObserver()
                                                }

                                                if (parentFragment != null && parentFragment is ConnectionsFragment) {
                                                    pageNo = 0
                                                    (parentFragment as ConnectionsFragment).updateViewPager()
                                                }
                                            }
                                        }
                                    }


                                } else {
                                    MyUtils.dismissProgressDialog()
                                    MyUtils.showSnackbar(mActivity!!, connectionListpojo[0].message, llRecevie)
                                }
                            } else {
                                MyUtils.dismissProgressDialog()
                                (activity as MainActivity).errorMethod()
                            }
                        })
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        // super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && v != null) {

            pageNo = 0
            isLastpage = false
            isLoading = false
            setupObserver()

        }

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

        var signupModel = ViewModelProviders.of(this@ConnectionListFragment).get(FriendListModel::class.java)
        signupModel.getFriendList(mActivity!!, jsonArray.toString(), "chatTofriend").observe(this@ConnectionListFragment,
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
                                (mActivity as MainActivity).loginForQuickBlockChat(sessionManager?.getQbUser()!!,userQuickBlockID)
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

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btnRetry->{
                setupObserver()
            }
        }
    }

}


