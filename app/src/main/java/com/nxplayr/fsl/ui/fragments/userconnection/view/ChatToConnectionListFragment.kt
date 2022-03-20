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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.userconnection.adapter.AlphaBetAdapter
import com.nxplayr.fsl.ui.fragments.userconnection.adapter.ChatConnectionAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FriendListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.*
import com.nxplayr.fsl.ui.fragments.friendrequest.viewmodel.FriendListModel
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_connection_list.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException


class ChatToConnectionListFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var connection_list: ArrayList<FriendListData?>?  = ArrayList()
    var connectionAdapter: ChatConnectionAdapter? = null
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
    private var isLoading = false
    private var isLastpage = false
    private var l: CharArray? = charArrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')
    var y: Int = 0
    var idx = 0
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var userId = ""
    var blockPos: Int = 0
    var count = ""
    var from = ""
    var userIdConnect = ""
    var _areLecturesLoaded = false
    private lateinit var  connectionModel: FriendListModel

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

        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (arguments != null) {
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
            if (from.equals("profile",false)||from.equals("chat",false) ) {
                jsonObject.put("loginuserID", userData?.userID)
                jsonObject.put("userID", userData?.userID)
            }

            jsonObject.put("action", "friendlist")
            jsonObject.put("friendtype", "all")
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
            .observe(viewLifecycleOwner, { friendlistpojo ->

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
            })
    }

    private fun setupUI()
    {
        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }

        tvToolbarTitle1.setText(R.string.connections)
        add_icon_connection.visibility=View.GONE
        btnRetry.setOnClickListener(this)

        val arrayis: Array<String> = resources.getStringArray(R.array.alphabet)
        alphabetListData!!.clear()
        alphabetListData!!.addAll(arrayis)
        idx = l!!.size - 1

        linearLayoutManageralphabet = LinearLayoutManager(mActivity)
        linearLayoutManager = LinearLayoutManager(mActivity)
        //  if (connection_list == null) {
        connectionAdapter = ChatConnectionAdapter(mActivity!!, object : ChatConnectionAdapter.OnItemClick {
            override fun onClicklisneter(pos: Int, name: String) {

                var  userQuickBlockID =  if (!connection_list!![pos]?.userQBoxID.isNullOrEmpty())
                    connection_list!![pos]?.userQBoxID!!
                else
                    return
                addToChatList(userQuickBlockID,connection_list!![pos]?.userID)


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
         connectionModel = ViewModelProvider(this@ChatToConnectionListFragment).get(FriendListModel::class.java)

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
        connectionModel.getFriendList(mActivity!!, jsonArray.toString(), "chatTofriend").observe(this@ChatToConnectionListFragment,
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

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnRetry->{
                setupObserver()
            }
        }
    }


}


