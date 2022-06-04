package com.nxplayr.fsl.ui.fragments.feed.view


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.ui.activity.main.view.MainActivity

import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.feed.adapter.SavedPostsAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SavePostListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel.CommonStatusModel
import com.nxplayr.fsl.ui.fragments.feed.viewmodel.SavePostListModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_privacy.*
import kotlinx.android.synthetic.main.fragment_receive_request.*
import kotlinx.android.synthetic.main.fragment_saved_posts.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SavedPostsFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var saveposts_list: ArrayList<SavePostListData?>? = null
    var savedPostsAdapter: SavedPostsAdapter? = null
    var pageNo = 0
    var pageSize = 10
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var y: Int = 0
    private lateinit var  linearLayoutManager: LinearLayoutManager
    private lateinit var  savePostListModel: SavePostListModel
    private lateinit var  commonStatusModel: CommonStatusModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_saved_posts, container, false)
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngSavedPosts.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngSavedPosts
            if (!sessionManager?.LanguageLabel?.lngNoDataFound.isNullOrEmpty())
                nodatafoundtextview.text = sessionManager?.LanguageLabel?.lngNoDataFound
            if (!sessionManager?.LanguageLabel?.lngCheckNoInternet.isNullOrEmpty())
                nointernettextview.text = sessionManager?.LanguageLabel?.lngCheckNoInternet
        }
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
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle.setText(R.string.saved_posts)
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        linearLayoutManager = LinearLayoutManager(mActivity)
        if (saveposts_list == null) {
            saveposts_list = ArrayList()
            savedPostsAdapter = SavedPostsAdapter(activity as MainActivity, saveposts_list, object : SavedPostsAdapter.OnItemClick {
                override fun onClicled(position: Int, from: String) {
                    when (from) {
                        "unsavePost" -> {
                            tv_unsave_post.visibility = View.VISIBLE
                            tv_unsave_post.setOnClickListener {
                                removesavePost(saveposts_list!![position]!!.postID, position)
                            }

                        }
                        else -> {
                            tv_unsave_post.visibility = View.GONE

                        }


                    }

                }

            })
            recyclerview.setHasFixedSize(true)
            recyclerview.layoutManager = linearLayoutManager
            recyclerview.adapter = savedPostsAdapter
            savePostListApi()
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
                        savePostListApi()
                    }
                }
            }
        })

        btnRetry.setOnClickListener {
            pageNo = 0
            savePostListApi()
        }
    }

    private fun setupViewModel() {
         savePostListModel = ViewModelProvider(this@SavedPostsFragment).get(SavePostListModel::class.java)
         commonStatusModel = ViewModelProvider(this@SavedPostsFragment).get(CommonStatusModel::class.java)
    }

    private fun savePostListApi() {

        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            saveposts_list!!.clear()
            savedPostsAdapter!!.notifyDataSetChanged()

        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
            saveposts_list!!.add(null)
            savedPostsAdapter!!.notifyItemInserted(saveposts_list!!.size - 1)
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        savePostListModel.getsavePostList(
            mActivity!!, false, jsonArray.toString(), "savePostList")
                .observe(viewLifecycleOwner, { savePostListPojo ->


                    if (savePostListPojo != null && savePostListPojo.isNotEmpty()) {
                        isLoading = false
                        ll_no_data_found.visibility = View.GONE
                        nointernetMainRelativelayout.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE

                        if (pageNo > 0) {
                            saveposts_list!!.removeAt(saveposts_list!!.size - 1)
                            savedPostsAdapter!!.notifyItemRemoved(saveposts_list!!.size)
                        }


                        if (savePostListPojo[0].status.equals("true", true)) {
                            recyclerview.visibility = View.VISIBLE

                            if (pageNo == 0) {
                                saveposts_list?.clear()
                            }

                            saveposts_list?.addAll(savePostListPojo[0].data!!)
                            savedPostsAdapter?.notifyDataSetChanged()
                            pageNo += 1

                            if (savePostListPojo[0].data!!.size < 10) {
                                isLastpage = true
                            }

                            if (!savePostListPojo[0].data!!.isNullOrEmpty()) {
                                if (savePostListPojo[0].data!!.isNullOrEmpty()) {
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

                            if (saveposts_list!!.isNullOrEmpty()) {
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

    private fun removesavePost(postID: String, position: Int) {
        tv_unsave_post.visibility = View.GONE
        MyUtils.showProgressDialog(mActivity!!, "Wait")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("postID", postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        commonStatusModel.getCommonStatus(mActivity!!, false, jsonArray.toString(), "removeSavePost")
                .observe(this@SavedPostsFragment!!,
                    { commonStatuspojo ->
                        if (commonStatuspojo != null && commonStatuspojo.isNotEmpty()) {
                            if (commonStatuspojo[0].status.equals("true", false)) {
                                MyUtils.dismissProgressDialog()
                                saveposts_list?.removeAt(position);
                                savedPostsAdapter?.notifyItemRemoved(position);
                                savedPostsAdapter?.notifyItemChanged(position, saveposts_list?.size);
                            } else {
                                MyUtils.dismissProgressDialog()
                                MyUtils.showSnackbar(mActivity!!, commonStatuspojo[0].message, ll_mainSavePost)
                            }

                        } else {
                            MyUtils.dismissProgressDialog()
                            (activity as MainActivity).errorMethod()
                        }
                    })
    }

}
