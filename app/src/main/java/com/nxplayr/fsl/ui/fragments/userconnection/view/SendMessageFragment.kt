package com.nxplayr.fsl.ui.fragments.userconnection.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ChatListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.friendrequest.viewmodel.FriendListModel
import com.nxplayr.fsl.ui.fragments.userconnection.adapter.SendMessageAdapter
import com.nxplayr.fsl.ui.fragments.userconnection.viewmodel.ChatListModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_send_message.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException

class SendMessageFragment : androidx.fragment.app.Fragment() {

    var sendMessageAdapter: SendMessageAdapter? = null
    var sendMessageListData: ArrayList<ChatListData?>? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    var v: View? = null
    var mActivity: Activity? = null
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
    private lateinit var chatListModel: ChatListModel
    private lateinit var connectionModel: FriendListModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //  if (v == null)
        v = inflater.inflate(R.layout.fragment_send_message, container, false)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        setupViewModel()
        setupUI()


    }

    private fun setupUI() {
        linearLayoutManager = LinearLayoutManager(mActivity)
        if (sendMessageListData == null) {
            sendMessageListData = ArrayList()
            sendMessageAdapter =
                SendMessageAdapter(mActivity!!, object : SendMessageAdapter.OnItemClick {
                    override fun onClicklisneter(pos: Int, name: String) {
                        var userQuickBlockID =
                            if (!sendMessageListData!![pos]?.userQBoxID.isNullOrEmpty())
                                sendMessageListData!![pos]?.userQBoxID!!
                            else
                                return
                        addToChatList(userQuickBlockID, sendMessageListData!![pos]?.userID)
                    }

                }, sendMessageListData)

            recyclerview.layoutManager = linearLayoutManager
            recyclerview.adapter = sendMessageAdapter

            val divider =
                DividerItemDecoration(recyclerview.getContext(), DividerItemDecoration.VERTICAL)
            divider.setDrawable(context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.line_layout
                )
            }!!)
            recyclerview.addItemDecoration(divider)

            sendMessageListApi()
        } else {
            sendMessageAdapter =
                SendMessageAdapter(mActivity!!, object : SendMessageAdapter.OnItemClick {
                    override fun onClicklisneter(pos: Int, name: String) {
                        var userQuickBlockID =
                            if (!sendMessageListData!![pos]?.userQBoxID.isNullOrEmpty())
                                sendMessageListData!![pos]?.userQBoxID!!
                            else
                                return
                        addToChatList(userQuickBlockID, sendMessageListData!![pos]?.userID)
                    }

                }, sendMessageListData)

            recyclerview.layoutManager = linearLayoutManager
            recyclerview.adapter = sendMessageAdapter

            val divider =
                DividerItemDecoration(recyclerview.getContext(), DividerItemDecoration.VERTICAL)
            divider.setDrawable(context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.line_layout
                )
            }!!)
            recyclerview.addItemDecoration(divider)
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
                        sendMessageListApi()
                    }
                }
            }
        })

        btnRetry.setOnClickListener {
            sendMessageListApi()
        }
        floating_btn_connection.setOnClickListener {

            var bundle = Bundle()
            bundle.putString("fromData", "chat")
            (activity as MainActivity).navigateTo(
                ChatToConnectionListFragment(),
                bundle,
                ChatToConnectionListFragment::class.java.name,
                true
            )

        }
    }

    private fun setupViewModel() {
        chatListModel = ViewModelProvider(this@SendMessageFragment).get(ChatListModel::class.java)
        connectionModel =
            ViewModelProvider(this@SendMessageFragment).get(FriendListModel::class.java)
    }

    private fun sendMessageListApi(searchWord: String = "") {

        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            sendMessageListData!!.clear()
            sendMessageAdapter!!.notifyDataSetChanged()
        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
            sendMessageListData!!.add(null)
            sendMessageAdapter!!.notifyItemInserted(sendMessageListData!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", "1")
            jsonObject.put("searchWord", searchWord)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        chatListModel.getChatList(jsonArray.toString())
        chatListModel.chatList.observe(viewLifecycleOwner) { friendlistpojo ->
            if (friendlistpojo != null && friendlistpojo.isNotEmpty()) {
                isLoading = false
                ll_no_data_found.visibility = View.GONE
                nointernetMainRelativelayout.visibility = View.GONE
                relativeprogressBar.visibility = View.GONE

                if (pageNo > 0) {
                    sendMessageListData!!.removeAt(sendMessageListData!!.size - 1)
                    sendMessageAdapter!!.notifyItemRemoved(sendMessageListData!!.size)
                }


                if (friendlistpojo[0].status.equals("true", true)) {
                    recyclerview.visibility = View.VISIBLE

                    if (pageNo == 0) {
                        sendMessageListData?.clear()
                    }

                    sendMessageListData?.addAll(friendlistpojo[0].data!!)


                    sendMessageAdapter?.notifyDataSetChanged()
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

                    if (sendMessageListData!!.isNullOrEmpty()) {
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
        }
    }

    private fun addToChatList(userQuickBlockID: String, userID: String?) {
        MyUtils.showProgressDialog(mActivity!!, "Please wait")

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
        connectionModel.friendApi(jsonArray.toString(), "chatTofriend")
        connectionModel.successFriend.observe(viewLifecycleOwner) { loginPojo ->
                MyUtils.dismissProgressDialog()
                if (loginPojo != null) {

                    if (loginPojo[0].status.equals("true", true)) {

                        if (sessionManager?.getYesNoQBUser()!!) {

                            if (!MyUtils.isLoginForQuickBlock) {
                                if (!MyUtils.isLoginForQuickBlockChat)
                                    (activity as MainActivity).loginForQuickBlockChat(
                                        sessionManager?.getQbUser()!!,
                                        userQuickBlockID
                                    )
                                else
                                    (activity as MainActivity).getQBUser(userQuickBlockID, 1)
                            } else if (!MyUtils.isLoginForQuickBlockChat)
                                (activity as MainActivity).loginForQuickBlockChat(
                                    sessionManager?.getQbUser()!!,
                                    userQuickBlockID
                                )
                            else (activity as MainActivity).getQBUser(userQuickBlockID, 1)
                        }

                    } else {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            loginPojo[0].message,
                            nointernetMainRelativelayout
                        )
                    }
                } else {
                    MyUtils.dismissProgressDialog()
                    ErrorUtil.errorMethod(nointernetMainRelativelayout)
                }
            }

    }

    fun applySearch(searchKeyword: String) {
        pageNo = 0
        sendMessageListApi(searchKeyword)
    }


}