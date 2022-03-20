package com.nxplayr.fsl.ui.fragments.collection.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.collection.adapter.CreateAlbumAdapater
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.collection.viewmodel.CreateAlbumModel
import com.nxplayr.fsl.ui.fragments.collection.viewmodel.CteateAlbumListModel
import com.nxplayr.fsl.data.model.AlbumDatum
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.JsonParseException
import com.nxplayr.fsl.ui.fragments.postalbum.SubAlbumFragment
import kotlinx.android.synthetic.main.add_collection_activity.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class AddToCollectionFragment : Fragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var v: View? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var mActivity: AppCompatActivity? = null

    var popup: RelativeLayout? = null
    var b: AlertDialog? = null
    var swipeCount = 0

    var create_album_list: ArrayList<AlbumDatum?>? = ArrayList()
    var create_album_adapter: CreateAlbumAdapater? = null

    var postId: String? = null

    private var linearLayoutManager: LinearLayoutManager? = null
    private var y: Int = 0
    var pageNo = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var explore_video_list: ArrayList<CreatePostData?>? = java.util.ArrayList()
    var Add = ""
    var postType = ""
    var AlbumID = ""
    var SubAlbumID = ""
    var fromCome = ""
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.add_collection_activity, container, false)

        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (arguments != null) {
            postId = arguments?.getString("postId")
            Add = arguments?.getString("Add")!!
            postType = arguments?.getString("postType")!!
            explore_video_list = arguments?.getSerializable("explore_video_list") as ArrayList<CreatePostData?>?

            if (!arguments?.getString("fromCome").isNullOrEmpty()) {
                fromCome = arguments?.getString("fromCome")!!
                if (fromCome.equals("MoveToOther")) {
                    SubAlbumID = arguments?.getString("SubAlbumID")!!
                    AlbumID = arguments?.getString("AlbumID")!!
                }
            }

        }
        when (fromCome) {
            "MoveToOther" -> {
                tvTitile.text = "Move to another collection"
            }
            else -> {
                tvTitile.text = "Add to Collection"
            }
        }

        img_back_explore_all.setOnClickListener(this)
        txtAddAlbums.setOnClickListener(this)

        getAblumList()
        swipeLayout.setOnRefreshListener(this)
        btnRetry.setOnClickListener(this)

        rcAlbumCreate.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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
                        pageNo = 0
                        getCreatAlbumList(userData?.userID, pageNo, "10", RestClient.apiType, RestClient.apiVersion)
                    }
                }
            }
        })

    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.img_back_explore_all -> {
                (activity as MainActivity).onBackPressed()
            }
            R.id.txtAddAlbums -> {
                showAddAlbums()
            }
            R.id.btnRetry -> {

                getAblumList()
            }
        }
    }

    private fun showAddAlbums() {

        val dialogBuilder = AlertDialog.Builder(mActivity!!)
        val inflater = (this@AddToCollectionFragment).getLayoutInflater()

        @SuppressLint("InflateParams")
        val rv = inflater.inflate(R.layout.custom_dialog_add_ablums_collection, null)
        dialogBuilder.setView(rv)
        b = dialogBuilder.create()
        val txt_cancel = rv.findViewById(R.id.txt_cancel) as TextView
        val txt_okay = rv.findViewById(R.id.txt_okay) as TextView
        popup = rv.findViewById(R.id.popup) as RelativeLayout
        val edtAlbumName = rv.findViewById(R.id.edtAlbumName) as EditText

        txt_okay.setOnClickListener {

            val albumName = edtAlbumName!!.text.toString().trim()

            if (albumName.equals("")) {

                Toast.makeText(mActivity!!, "Please Enter Album Name", Toast.LENGTH_SHORT).show()
            } else {

                createAlbumName(userData?.userID, albumName, RestClient.apiType, RestClient.apiVersion)
            }

        }

        txt_cancel.setOnClickListener {
            b!!.dismiss()
        }

        dialogBuilder.setTitle("")
        dialogBuilder.setMessage("")
        b!!.setCanceledOnTouchOutside(false)
        b!!.setCancelable(false)
        b!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        b!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        b!!.show()

    }

    override fun onRefresh() {
        swipeCount += 1;
        if (swipeCount > 0) {

            create_album_adapter!!.notifyDataSetChanged()

            swipeLayout.isRefreshing = false

            pageNo = 0
            getCreatAlbumList(userData?.userID, pageNo, "10", RestClient.apiType, RestClient.apiVersion)
        }
    }

    private fun getAblumList() {

        linearLayoutManager = LinearLayoutManager(mActivity)
        if (create_album_list.isNullOrEmpty() || pageNo == 0) {

            create_album_adapter = CreateAlbumAdapater(mActivity!!, create_album_list, object : CreateAlbumAdapater.OnItemClick {

                override fun onClicklisneter(pos: Int) {

                }

                override fun onEditAlbum(posistion: Int) {

                }

                override fun onSubAlbum(posistion: Int) {

                    val subAlbum = SubAlbumFragment()
                    Bundle().apply {
                        this.putString("albumName", create_album_list!!.get(posistion)?.exalbumName)
                        this.putString("albumId", create_album_list!!.get(posistion)?.exalbumID)
                        this.putInt("albumPosition", posistion)
                        this.putString("postId", postId)
                        this.putString("subAlbum", "SubAlbumShow")
                        this.putString("Add", Add)
                        this.putString("postType", postType)
                        if (fromCome.equals("MoveToOther",false))
                        {
                            this.putString("fromCome", "MoveToOther")
                            this.putString("SubAlbumID", SubAlbumID)
                            this.putString("AlbumID", AlbumID)
                        }
                        putSerializable("explore_video_list", explore_video_list)
                        subAlbum.arguments = this
                    }

                    (activity as MainActivity).navigateTo(subAlbum, subAlbum::class.java.name, true)
                }

                override fun onAlbumOption(posistion: Int) {

                }

            }, false)
        }
        rcAlbumCreate.layoutManager = linearLayoutManager
        rcAlbumCreate.setHasFixedSize(true)
        rcAlbumCreate.adapter = create_album_adapter

        pageNo = 0
        getCreatAlbumList(userData?.userID, pageNo, "10", RestClient.apiType, RestClient.apiVersion)
    }

    //Api Calling Funcation
    private fun createAlbumName(useriD: String?, albumName: String, apiType: String, apiVersion: String) {
        relativeprogressBar.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {
            jsonObject.put("loginuserID", useriD)
            jsonObject.put("exalbumName", albumName)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }
        Log.d("CreatAlbumObject", jsonObject.toString())
        var getEmployementModel =
                ViewModelProviders.of(this@AddToCollectionFragment).get(CreateAlbumModel::class.java)
        getEmployementModel.apiCreateAlbum(mActivity!!, false, jsonArray.toString())
                .observe(this@AddToCollectionFragment,
                        Observer { albumCreatepojo ->

                            relativeprogressBar.visibility = View.GONE

                            if (albumCreatepojo != null && albumCreatepojo.isNotEmpty()) {

                                if (albumCreatepojo[0].status.equals("true", true)) {

                                    Toast.makeText(mActivity!!, albumCreatepojo[0].message, Toast.LENGTH_SHORT).show()
                                    b!!.dismiss()
                                    pageNo = 0
                                    getCreatAlbumList(userData?.userID, pageNo, "10", RestClient.apiType, RestClient.apiVersion)
                                } else {

                                    Toast.makeText(mActivity!!, albumCreatepojo[0].message, Toast.LENGTH_SHORT).show()
                                }

                            } else {

                                relativeprogressBar.visibility = View.GONE

                            }

                        })
    }

    private fun getCreatAlbumList(userID: String?, pageno: Int?, pagesize: String?, apiType: String?, apiVersion: String?) {

        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        if (pageNo == 0) {

            relativeprogressBar.visibility = View.VISIBLE
            create_album_list?.clear()
            create_album_adapter?.notifyDataSetChanged()

        } else {

            relativeprogressBar.visibility = View.GONE
            rcAlbumCreate.visibility = View.VISIBLE
            create_album_list!!.add(null)
            create_album_adapter?.notifyItemInserted(create_album_list!!.size - 1)
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("loginuserID", userID)
            jsonObject.put("page", pageno)
            jsonObject.put("pagesize", pagesize)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("ALBUM_LIST", jsonObject.toString())
        jsonArray.put(jsonObject)
        var getEmployementModel =
                ViewModelProviders.of(this@AddToCollectionFragment).get(CteateAlbumListModel::class.java)
        getEmployementModel.getAlbumDataList(mActivity!!, false, jsonArray.toString())
                .observe(this@AddToCollectionFragment,
                        Observer { albumListpojo ->

                            if (albumListpojo != null && albumListpojo.isNotEmpty()) {

                                isLoading = false
                                ll_no_data_found.visibility = View.GONE
                                nointernetMainRelativelayout.visibility = View.GONE
                                relativeprogressBar.visibility = View.GONE

                                if (pageNo > 0) {

                                    create_album_list?.removeAt(create_album_list!!.size - 1)
                                    create_album_adapter?.notifyItemRemoved(create_album_list!!.size)
                                }

                                if (albumListpojo[0].status.equals("true", true)) {

                                    rcAlbumCreate.visibility = View.VISIBLE
                                    if (pageNo == 0) {
                                        create_album_list?.clear()
                                    }
                                    pageNo += 1
                                    if (albumListpojo[0].data.size < 10) {
                                        isLastpage = true
                                    }
                                    create_album_list?.addAll(albumListpojo!![0]!!.data!!)
                                    create_album_adapter?.notifyDataSetChanged()
                                    if (!albumListpojo[0].data!!.isNullOrEmpty()) {
                                        if (albumListpojo[0].data!!.isNullOrEmpty()) {
                                            ll_no_data_found.visibility = View.VISIBLE
                                            rcAlbumCreate.visibility = View.GONE
                                        } else {
                                            ll_no_data_found.visibility = View.GONE
                                            rcAlbumCreate.visibility = View.VISIBLE
                                        }


                                    } else {
                                        ll_no_data_found.visibility = View.VISIBLE
                                        rcAlbumCreate.visibility = View.GONE
                                    }
                                } else {

                                    if (create_album_list!!.size == 0) {

                                        ll_no_data_found.visibility = View.VISIBLE
                                        rcAlbumCreate.visibility = View.GONE
                                    } else {

                                        ll_no_data_found.visibility = View.GONE
                                        rcAlbumCreate.visibility = View.VISIBLE
                                    }

                                }

                            } else {

                                relativeprogressBar.visibility = View.GONE
                                Toast.makeText(mActivity!!, R.string.error_common_network, Toast.LENGTH_SHORT).show()
                            }

                        })

    }

    override fun onDestroyView() {
        if (v != null) {
            val viewGroup = v?.parent as ViewGroup?
            viewGroup?.removeView(v);
        }
        super.onDestroyView()
    }
}