package com.nxplayr.fsl.ui.fragments.feedlikeviewlist.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.feedlikeviewlist.adapter.LikeAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.feedlikeviewlist.viewmodel.UserLikeListModel
import com.nxplayr.fsl.data.model.LikeListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_like_list.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LikeViewFragment : androidx.fragment.app.Fragment(),View.OnClickListener {

    var likeAdapter: LikeAdapter? = null
    var userlikeList: ArrayList<LikeListData?>? = ArrayList()
    private lateinit var linearLayoutManager: LinearLayoutManager
    var v: View? = null
    var activity: AppCompatActivity? = null
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
    private lateinit var  likeListModel: UserLikeListModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null)
            v = inflater.inflate(R.layout.fragment_like_list, container, false)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(activity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowCustomEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        setupViewModel()
        setupUI()

    }



    private fun setupUI() {
        tvToolbarTitle.setText(activity!!.resources.getString(R.string.likes))

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        linearLayoutManager = LinearLayoutManager(activity)
        likeAdapter =
            LikeAdapter(
                activity!!,
                object : LikeAdapter.OnItemClick {
                    override fun onClicklisneter(pos: Int, name: String) {

                    }
                },
                userlikeList
            )
        recyclerview.layoutManager = linearLayoutManager
        recyclerview.adapter = likeAdapter
        UserLikeListApi()
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
                        UserLikeListApi()
                    }
                }
            }
        })

        btnRetry.setOnClickListener(this)
    }

    private fun setupViewModel() {
         likeListModel = ViewModelProvider(this@LikeViewFragment).get(UserLikeListModel::class.java)
    }
    private fun UserLikeListApi() {

        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            userlikeList!!.clear()
            likeAdapter!!.notifyDataSetChanged()

        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
            userlikeList!!.add(null)
            likeAdapter!!.notifyItemInserted(userlikeList!!.size - 1)

        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("postID", userData?.userID)
            jsonObject.put("loginuserID", "1")
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("action", "List")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)

        likeListModel.getLikeList(activity as MainActivity, false, jsonArray.toString(), "user_likeList")
                .observe(viewLifecycleOwner, { likeListPojo ->


                    if (likeListPojo != null && likeListPojo.isNotEmpty()) {
                        isLoading = false
                        ll_no_data_found.visibility = View.GONE
                        nointernetMainRelativelayout.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE

                        if (pageNo > 0) {
                            userlikeList!!.removeAt(userlikeList!!.size - 1)
                            likeAdapter!!.notifyItemRemoved(userlikeList!!.size)
                        }


                        if (likeListPojo[0].status.equals("true")) {
                            recyclerview.visibility = View.VISIBLE

                            if (pageNo == 0) {
                                userlikeList?.clear()
                            }

                            userlikeList?.addAll(likeListPojo[0].data!!)
                            likeAdapter?.notifyDataSetChanged()
                            pageNo += 1

                            if (likeListPojo[0].data!!.size < 10) {
                                isLastpage = true
                            }


                            if (!likeListPojo[0].data!!.isNullOrEmpty()) {
                                if (likeListPojo[0].data!!.isNullOrEmpty()) {
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

                            if (userlikeList!!.isNullOrEmpty()) {
                                ll_no_data_found.visibility = View.VISIBLE
                                recyclerview.visibility = View.GONE

                            } else {
                                ll_no_data_found.visibility = View.GONE
                                recyclerview.visibility = View.VISIBLE

                            }
                        }
                    } else {
                        if (activity != null) {
                            errorMethod()
                        }

                    }
                })

    }

    private fun errorMethod() {
        relativeprogressBar.visibility = View.GONE
        try {
            nointernetMainRelativelayout.visibility = View.VISIBLE
            if (MyUtils.isInternetAvailable(activity!!)) {
                nointernetImageview.setImageDrawable(activity!!.getDrawable(R.drawable.ic_warning_black_24dp))
                nointernettextview.text = (this!!.getString(R.string.error_crash_error_message))
            } else {
                nointernetImageview.setImageDrawable(activity!!.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp))
                nointernettextview.text = (this!!.getString(R.string.error_common_network))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btnRetry->{
                pageNo = 0
                UserLikeListApi()
            }

        }
    }


}